"""Publica o commit validado no Render e aguarda sua disponibilidade."""
import json
import os
import sys
import time
from urllib.error import HTTPError, URLError
from urllib.parse import quote, urlparse
from urllib.request import Request, urlopen


def request_json(url, token=None, data=None):
    headers = {"Accept": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    if data is not None:
        headers["Content-Type"] = "application/json"
    request = Request(url, headers=headers,
                      data=json.dumps(data).encode() if data is not None else None)
    with urlopen(request, timeout=30) as response:
        return json.load(response)


def wait_for_deploy(url, token, commit, timeout=1200):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        deploy = request_json(url, token)
        status = deploy["status"]
        print(f"Render: {status}", flush=True)
        if status == "live":
            if deploy.get("commit", {}).get("id") != commit:
                raise RuntimeError("O commit publicado difere do commit validado pela CI.")
            return
        if status in {"build_failed", "update_failed", "pre_deploy_failed",
                      "canceled", "deactivated"}:
            raise RuntimeError(f"Deploy terminou sem sucesso: {status}")
        time.sleep(15)
    raise TimeoutError("Render não concluiu o deploy em 20 minutos.")


def smoke_test(base_url):
    for attempt in range(12):
        try:
            if request_json(base_url + "/actuator/health").get("status") != "UP":
                raise RuntimeError("Health check não retornou UP.")
            with urlopen(base_url + "/loja/compra?produto=FRANGO&quantidade=2", timeout=30) as response:
                body = response.read().decode("utf-8")
            if "Produto: FRANGO" not in body or "Total: R$ 20.00" not in body.replace(",", "."):
                raise RuntimeError("A compra de teste não retornou o resultado esperado.")
            print("Smoke test: saúde e compra confirmadas.", flush=True)
            return
        except (HTTPError, URLError, TimeoutError, RuntimeError):
            if attempt == 11:
                raise
            time.sleep(10)


def main():
    names = ("RENDER_API_KEY", "RENDER_SERVICE_ID", "APP_URL", "GITHUB_SHA")
    missing = [name for name in names if not os.environ.get(name, "").strip()]
    if missing:
        raise RuntimeError("Configure secrets/variables: " + ", ".join(missing))
    base_url = os.environ["APP_URL"].rstrip("/")
    parsed = urlparse(base_url)
    if parsed.scheme != "https" or not parsed.netloc or parsed.username or parsed.query or parsed.fragment:
        raise RuntimeError("APP_URL deve ser a URL HTTPS pública da aplicação, sem credenciais ou parâmetros.")
    service = quote(os.environ["RENDER_SERVICE_ID"], safe="")
    api = f"https://api.render.com/v1/services/{service}/deploys"
    token = os.environ["RENDER_API_KEY"]
    commit = os.environ["GITHUB_SHA"]
    deploy = request_json(api, token, {"commitId": commit})
    deploy_id = quote(deploy["id"], safe="")
    print(f"Deploy iniciado: {deploy_id} | commit: {commit}", flush=True)
    wait_for_deploy(f"{api}/{deploy_id}", token, commit)
    smoke_test(base_url)


if __name__ == "__main__":
    try:
        main()
    except HTTPError as error:
        # Não imprime headers, token ou corpo da resposta autenticada.
        print(f"Falha HTTP no deploy: {error.code}", file=sys.stderr)
        sys.exit(1)
    except (RuntimeError, TimeoutError, URLError, KeyError, ValueError) as error:
        print(f"Falha no deploy: {error}", file=sys.stderr)
        sys.exit(1)
