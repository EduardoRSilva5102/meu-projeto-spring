# Registro de uso da IA

Este registro acompanha a atividade. A quantidade final de prompts e as evidências devem ser consolidadas após a execução real no GitHub e no Render.

## Interações até o início da implementação

1. **Leitura do enunciado.** O aluno solicitou apenas a leitura do PDF, informou que o uso de IA era previsto e definiu que ações pela interface GitHub seriam realizadas por ele; código seria produzido pela IA. Resultado: identificação dos requisitos de CI, CD e relatório.
2. **Varredura do projeto.** O aluno forneceu o caminho local e solicitou análise da estrutura. Resultado: identificação de Spring Boot, Java 17, Maven, endpoint de compra, teste de contexto e ausência de pipeline/deploy.
3. **Implementação inicial.** Prompt do aluno:

   > Não existem alterações pendentes, o projeto foi apenas clonado, agora, já podemos começar a etapa de desenvolvimento.
   > Tudo que for relacionado a código e não depender de ferramentas do GitHub ou acessos, já integre no projeto local, crie uma branch dedicada antes disso, para já termos a 1° PR (dê o nome seguindo os padrões de branch, seja eles feat, chore, etc).

   Alterações: branch `feat/github-actions-ci-cd`, testes de negócio/HTTP, Actuator health, porta por variável, Dockerfile, blueprint Render, pipeline CI/CD, script de deploy e documentação.

As descrições dos prompts 1 e 2 são resumos; use o histórico do chat para transcrever os textos completos no relatório. São três solicitações do aluno até esta versão inicial, não uma contagem final da atividade. Chamadas de ferramentas e ajustes internos da IA não foram contados como novos prompts do aluno.

## Pendências de validação externa

- Envio da branch, PR, configuração de hospedagem, secrets e variáveis.
- Execução real do Actions em PR e push na `main`.
- Publicação e verificação da URL pública.
- Registro das ações manuais do aluno e das capturas de tela.

## Verificação local da implementação inicial — 08/11/2026

- `mvnw.cmd --batch-mode --no-transfer-progress verify`: BUILD SUCCESS, 16 testes Java, nenhuma falha/erro, JAR gerado.
- Java disponível localmente: JDK 21, compilando com `release 17`. A execução efetiva no JDK 17 está configurada para a CI e para o Docker.
- Testes Python: 5 aprovados; cobrem configuração ausente, espera pelo deploy, falha, commit divergente e timeout. São testes simulados, não uma publicação real.
- YAML da pipeline e blueprint parseados; conferidos triggers para `main`, dependência de CD em CI e Auto-Deploy `off`.
- `git diff --check`: sem erros de whitespace.
- Docker instalado, mas daemon desligado. Build da imagem não foi executado localmente; está previsto na CI.
- Smoke test do JAR tentado em processo temporário com `PORT=18091`. O log confirmou a porta, mas o Tomcat não iniciou por `Unable to establish loopback connection` / `SocketException: Invalid argument: connect` no JDK local do Windows. Repetir com preferência IPv4 não resolveu. Os processos temporários foram encerrados; a validação HTTP real permanece pendente. Os testes HTTP via MockMvc passaram.
- A API do Render foi conferida na documentação oficial; nenhuma credencial foi usada e nenhum serviço foi publicado pela IA.

Não confundir essas verificações locais com evidência de funcionamento do GitHub Actions ou de CD em produção.

## Próximas interações

Para cada interação relevante, registre: prompt completo; resultado; problema (se houver); alteração; responsável (IA ou aluno); evidência e SHA quando disponível. Nunca registre secrets.
