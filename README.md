# Sistema de Simulação de Eleição Eletrônica

API REST de back-end para simular uma eleição eletrônica: cadastro de eleitores, candidatos, partidos e eleições, abertura e encerramento da votação, registro de votos, acompanhamento da apuração e resultado final.

Trabalho da disciplina de Programação Modular.

## Tecnologias

- Java 21 e Spring Boot 4 (Web MVC, Data JPA, Security, Validation)
- MySQL 8.4 (Docker)
- JWT para autenticação, BCrypt para senhas
- springdoc-openapi (Swagger UI) para a documentação da API
- JUnit 5, Mockito e Testcontainers para os testes

## Como executar

Pré-requisitos: JDK 21 e Docker.

1. Copie o arquivo de variáveis de ambiente e ajuste os valores se quiser:

   ```bash
   cp .env.example .env
   ```

   | Variável | Descrição |
   |---|---|
   | `DB_USER`, `DB_PASSWORD`, `DB_ROOT_PASSWORD` | Credenciais do MySQL |
   | `ADMIN_USERNAME`, `ADMIN_PASSWORD` | Chefe da Seção criado automaticamente na primeira execução |
   | `JWT_SECRET` | Chave de assinatura do token (mínimo de 32 caracteres) |
   | `JWT_EXPIRATION` | Validade do token em milissegundos |

2. Suba tudo com Docker (banco e aplicação):

   ```bash
   docker compose up -d --build
   ```

   Ou suba só o banco e rode a aplicação pela IDE ou pelo Maven:

   ```bash
   docker compose up -d mysql
   ./mvnw spring-boot:run
   ```

O banco é criado pelo script `docker/mysql/init/01-schema.sql`, que também cadastra os cinco cargos. O script só roda quando o volume do MySQL é criado; para recriar o banco do zero, use `docker compose down -v`.

A API fica em `http://localhost:8080`.

## Documentação da API

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI (JSON): http://localhost:8080/v3/api-docs

Para testar rotas protegidas no Swagger, faça login em `POST /auth/login`, copie o `token` da resposta e cole no botão **Authorize**.

## Perfis e endpoints

| Método | Rota | Quem pode usar |
|---|---|---|
| POST | `/auth/login` | Público |
| GET, PUT | `/api/usuarios/me` | Qualquer usuário autenticado |
| POST | `/api/usuarios/chefe-secao` | Chefe da Seção |
| POST, GET, PUT, DELETE | `/api/eleitores` | Chefe da Seção |
| POST, PUT, DELETE | `/api/candidatos` | Chefe da Seção |
| GET | `/api/candidatos`, `/api/candidatos/eleicao/{idEleicao}/cargo/{idCargo}` | Qualquer usuário autenticado |
| POST | `/api/partidos` | Chefe da Seção |
| GET | `/api/partidos`, `/api/cargos` | Qualquer usuário autenticado |
| POST | `/api/eleicoes` | Chefe da Seção |
| PATCH | `/api/eleicoes/{id}/abrir`, `/api/eleicoes/{id}/encerrar` | Chefe da Seção |
| GET | `/api/eleicoes`, `/api/eleicoes/{id}` | Qualquer usuário autenticado |
| POST | `/api/votacao/votar` | Eleitor |
| GET | `/api/votacao/eleicao/{idEleicao}/acompanhamento` | Eleitor (todos os cargos) e Candidato (só o próprio cargo) |
| GET | `/api/votacao/eleicao/{idEleicao}/resultado-final` | Qualquer usuário autenticado, após o encerramento |

Requisições sem token recebem `401`; usuários sem o perfil exigido recebem `403`; violações de regra de negócio (votar com a eleição fechada, votar duas vezes no mesmo cargo, número de candidato repetido etc.) recebem `409`.

## Roteiro de demonstração

1. `POST /auth/login` com o usuário administrador do `.env` para obter o token do Chefe da Seção.
2. `POST /api/partidos` e `POST /api/eleicoes` (a eleição nasce `AGENDADA`).
3. `GET /api/cargos` para ver os ids dos cargos, depois `POST /api/candidatos` e `POST /api/eleitores`.
4. `PATCH /api/eleicoes/{id}/abrir`.
5. Login como eleitor, `GET /api/candidatos/eleicao/{id}/cargo/{idCargo}` e `POST /api/votacao/votar`. Votar de novo no mesmo cargo retorna `409`.
6. `GET /api/votacao/eleicao/{id}/acompanhamento` como eleitor e como candidato.
7. `PATCH /api/eleicoes/{id}/encerrar` e `GET /api/votacao/eleicao/{id}/resultado-final`.

## Testes

```bash
./mvnw test     # testes unitários (*Test): services isolados com Mockito
./mvnw verify   # unitários + integração (*IT): API completa contra um MySQL real via Testcontainers
```

Os testes de integração precisam do Docker em execução.

## Estrutura do projeto

O código é organizado por funcionalidade. Cada módulo em `feature/` tem suas próprias camadas, e o que é compartilhado fica em `core/`:

```
src/main/java/com/example/eleicao_service
├── core/
│   ├── config/      administrador inicial e Swagger
│   ├── security/    Spring Security e JWT
│   └── handler/     tratamento centralizado de erros
└── feature/
    ├── auth/  usuario/  eleitor/  candidato/  partido/  cargo/  eleicao/  votacao/
    │   ├── controller/       interface com rotas, permissões e documentação
    │   ├── controller/impl/
    │   ├── service/          interface com as regras de negócio
    │   ├── service/impl/
    │   ├── repository/
    │   └── model/            entidades JPA e DTOs
```

Fluxo de uma requisição: `Cliente -> Spring Security (JWT) -> Controller -> Service -> Repository -> MySQL`.

## Banco de dados

Oito tabelas: `usuario`, `eleitor`, `candidato`, `partido`, `cargo`, `eleicao`, `controle_voto` e `voto`. Eleitor e candidato usam o id do usuário como chave primária. Para manter o voto secreto, `controle_voto` registra que o eleitor já votou em um cargo (com `UNIQUE (eleição, eleitor, cargo)`) e `voto` guarda o candidato escolhido sem identificar o eleitor.

A explicação completa da arquitetura, da modularização e da modelagem (com o dicionário de dados) está no documento técnico entregue junto com o trabalho.
