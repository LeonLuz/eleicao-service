# eleicao-service

Back-end do sistema de simulação de eleição eletrônica, trabalho da disciplina de Engenharia de Software 2.

## Como rodar

Copie o `.env.example` para `.env` e suba o banco e a aplicação:

```bash
docker compose up -d --build
```

A documentação da API fica em http://localhost:8080/swagger-ui.html.

## Testes

```bash
./mvnw test     # unitários
./mvnw verify   # unitários e de integração (precisa do Docker)
```

A explicação da arquitetura, da modularização e da modelagem do banco está no documento técnico entregue junto com o trabalho.