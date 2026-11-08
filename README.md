# Loja — laboratório de CI/CD

Aplicação Java 17 / Spring Boot 4.0.6 que calcula compras por peso, com Maven Wrapper 3.9.14.

## Executar localmente

No PowerShell, com um JDK 17 ou superior configurado:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress verify
.\mvnw.cmd spring-boot:run
```

No Linux, use `bash ./mvnw` no lugar de `.\mvnw.cmd`.

- Compra: `http://localhost:8081/loja/compra?produto=FRANGO&quantidade=2`
- Saúde: `http://localhost:8081/actuator/health`
- A variável `PORT` substitui a porta local 8081 quando definida.

Os testes verificam preços, desconto acima de 10 kg, entradas inválidas, integração HTTP e saúde.

## Pipeline

Arquivo: `.github/workflows/ci-cd.yml`.

1. Push em `main` e PR destinada a `main`: checkout, Java 17, cache Maven, `verify`, testes do script de deploy, build Docker e armazenamento dos relatórios/JAR.
2. Apenas push em `main`, após sucesso de toda a CI: o job CD envia à API do Render o SHA exato validado, aguarda o deploy ficar `live`, confere o commit e verifica saúde e compra pela URL pública.
3. Erros de build, publicação, commit divergente, timeout ou smoke test fazem o CD falhar. PRs não recebem credenciais de deploy.

O JAR guardado é uma evidência do build. O Render recompila o mesmo commit pelo Dockerfile; não utiliza esse artefato do Actions. A etapa Docker usa `-DskipTests` porque os testes já são executados pela CI antes da publicação.

## Estratégia de deployment

Usamos um Web Service Docker no Render, pois a aplicação precisa de um processo Java em execução. O Dockerfile possui etapas de build e runtime e executa com usuário sem privilégios. O blueprint `render.yaml` define a porta e o health check. Deploys automáticos do próprio Render estão desligados para que a publicação de novas versões seja controlada pelo GitHub Actions após a CI.

## Configuração pela interface — responsável: aluno

Antes de mesclar a primeira PR:

1. Envie a branch `feat/github-actions-ci-cd` ao GitHub e abra uma PR com destino a `main`. Aguarde a CI; não haverá CD nessa PR.
2. No Render, crie um Web Service vinculado ao repositório. Para o cadastro inicial, selecione esta branch e runtime Docker, usando `Dockerfile` na raiz, variável `PORT=10000` e Health Check Path `/actuator/health`. Use o plano Free se disponível na sua conta. A criação pode disparar um primeiro deploy de provisionamento; registre-o separadamente dos deploys da pipeline.
3. Após o cadastro, configure a branch do serviço como `main` e Auto-Deploy como **Off**. Não mantenha a branch de desenvolvimento como origem do serviço. O `render.yaml` registra a configuração final; também pode ser usado como Blueprint quando estiver na `main`.
4. Copie o Service ID (`srv-...`) e a URL HTTPS pública. Crie uma API key no Render com acesso para publicar e consultar esse serviço.
5. No GitHub, em **Settings → Environments**, crie `production`. Para deploy automático, deixe sem aprovação manual obrigatória. Nesse environment, em **Environment secrets**, adicione `RENDER_API_KEY`. Em **Environment variables**, adicione `RENDER_SERVICE_ID` e `APP_URL` (ex.: `https://seu-servico.onrender.com`). Não coloque a chave em arquivos ou no chat.
6. Mescle a PR. Confira a execução originada pelo push em `main`: CI e CD precisam terminar com sucesso. Abra a URL pública de compra e o endpoint de saúde.

O serviço deve estar conectado a este mesmo repositório. É necessário permitir ao Render acessar o repositório. Se o plano não permitir criar o serviço, revise a hospedagem antes de contratar algo.

## Evidências e relatório

- PR com CI aprovada; confirmação de que o CD não executou na PR.
- Execução na `main` com CI e CD aprovados e SHA do commit publicado.
- Relatórios em `resultados-testes` e JAR nos artifacts do Actions.
- Tela do Render com deploy concluído e aplicação acessível.
- Para demonstrar bloqueio por falha, faça uma PR de teste com uma asserção incorreta, registre a CI vermelha e depois corrija. Não mescle o erro.
- Registre prompts, resultados, erros e alterações manuais em `docs/registro-ia.md`. Não invente execuções ou evidências. A entrega final é um PDF único conforme o enunciado.

## Referências

- [Deploys e commits específicos no Render](https://render.com/docs/deploys)
- [API para iniciar deploy](https://api-docs.render.com/reference/create-deploy)
- [API para consultar deploy](https://api-docs.render.com/reference/retrieve-deploy)
- [Blueprint do Render](https://render.com/docs/blueprint-spec)
