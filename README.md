# Cadastro de Clientes com ViaCEP

Aplicação full stack de cadastro de clientes que preenche o endereço automaticamente a partir do CEP, consultando a API pública do [ViaCEP](https://viacep.com.br). O projeto foi desenvolvido como desafio do **Bootcamp DIO & CI&T**, no módulo *Design Patterns com Java: Dos Clássicos (GoF) ao Spring Framework*.

A base é o lab [lab-padroes-projeto-spring](https://github.com/digitalinnovationone/lab-padroes-projeto-spring), da DIO. A partir dele, o backend foi migrado para versões atuais, bugs foram corrigidos e uma interface web em Angular foi adicionada.

<!-- Sugestão: salve um print da tela em docs/tela.png e descomente a linha abaixo -->
<!-- ![Tela do sistema](docs/tela.png) -->

## Funcionalidades

- Cadastrar, listar, editar e excluir clientes (CRUD completo)
- Preenchimento automático do endereço (logradouro, bairro, cidade e UF) a partir do CEP
- Validação do CEP: formato (8 dígitos) e existência na base do ViaCEP
- Cache local de endereços: um CEP já consultado não chama o ViaCEP de novo
- Respostas de erro padronizadas (RFC 9457 – *Problem Details*), exibidas na interface
- Documentação interativa da API com Swagger

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 21, Spring Boot 4.0.8, Spring Data JPA, Spring Web MVC |
| Integração | Spring Cloud OpenFeign (Spring Cloud 2025.1.1) |
| Banco de dados | H2 (em memória) |
| Documentação da API | springdoc-openapi 3.0.3 (Swagger UI) |
| Frontend | Angular 19, TypeScript, HttpClient, FormsModule |
| Ícones | Material Icons |

## Estrutura do repositório

```
cadastro-clientes-viacep/
├── backend/    # API REST em Spring Boot
└── frontend/   # Interface web em Angular
```

## Padrões de projeto aplicados

| Padrão | Onde | Como |
|---|---|---|
| **Singleton** | Beans do Spring (`@Service`, `@RestController`, repositórios) | O container do Spring cria uma única instância de cada bean e a compartilha. |
| **Strategy** | `ClienteService` → `ClienteServiceImpl` | O controller depende da interface, e a implementação pode ser trocada sem alterar quem a usa. |
| **Facade** | `ClienteServiceImpl` / `ClienteRestController` | Uma interface simples (cadastrar cliente) esconde a orquestração entre o banco H2 e a API do ViaCEP. |
| **Repository** | `ClienteRepository`, `EnderecoRepository` | O acesso a dados é tratado como uma coleção de objetos de domínio (Spring Data). |
| **Proxy** | Spring Data e OpenFeign | O Spring gera em tempo de execução as implementações das interfaces de repositório e do `ViaCepService`. |

Princípios aplicados: **Inversão de Controle** e **Injeção de Dependência via construtor**, no backend (Spring) e no frontend (`inject()` do Angular), e **Inversão de Dependência (DIP)**, com o controller dependendo da abstração `ClienteService`.

## Melhorias em relação ao lab original

**Atualização**
- Migração do Spring Boot 2.5 / Java 11 para Spring Boot 4.0.8 / Java 21 (`javax.persistence` → `jakarta.persistence`)
- Swagger atualizado para `springdoc-openapi-starter-webmvc-ui`

**Correções de bugs**
- `PUT /clientes/{id}` criava um novo registro em vez de atualizar o existente
- `GET`, `PUT` e `DELETE` com id inexistente retornavam 500 ou 200; agora retornam **404**
- CEP inválido ou inexistente quebrava o cadastro (500); agora retorna **400** com mensagem clara
- CEP era salvo em formatos diferentes (`01001000` e `01001-000`); agora é padronizado só com dígitos
- `id` enviado no `POST` podia gerar erro; agora é ignorado e o cadastro sempre cria um novo cliente

**Qualidade**
- Injeção de dependência via construtor no lugar de `@Autowired` em atributos
- Tratamento centralizado de exceções com `@RestControllerAdvice` e `ProblemDetail`
- CORS liberado apenas para a origem do frontend (`http://localhost:4200`)

**Novo**
- Interface web em Angular 19

## Endpoints da API

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `GET` | `/clientes` | Lista todos os clientes | 200 |
| `GET` | `/clientes/{id}` | Busca um cliente pelo id | 200, 404 |
| `POST` | `/clientes` | Cadastra um cliente | 200, 400 |
| `PUT` | `/clientes/{id}` | Atualiza um cliente | 200, 400, 404 |
| `DELETE` | `/clientes/{id}` | Exclui um cliente | 200, 404 |

Corpo esperado no `POST` e no `PUT`. Os demais campos do endereço são preenchidos pelo ViaCEP:

```json
{
  "nome": "Maria da Silva",
  "endereco": { "cep": "01001-000" }
}
```

Exemplo de resposta de erro:

```json
{
  "title": "Bad Request",
  "status": 400,
  "detail": "CEP não encontrado: 99999999",
  "instance": "/clientes"
}
```

## Como executar

### Pré-requisitos

- Java 21+
- Node.js e Angular CLI 19 (`npm install -g @angular/cli@19`)
- Conexão com a internet (para consultar o ViaCEP)

### 1. Backend

```bash
cd backend
./mvnw spring-boot:run
```

No Windows (PowerShell ou CMD), use `mvnw spring-boot:run`. Também é possível executar a classe `Application` pela IDE.

- API: http://localhost:8080/clientes
- Swagger UI: http://localhost:8080/swagger-ui.html

O H2 é um banco em memória: os dados são apagados a cada reinício da aplicação.

### 2. Frontend

Em outro terminal:

```bash
cd frontend
npm install
ng serve
```

Acesse http://localhost:4200. O backend precisa estar rodando.

## Fluxo de validação do CEP

```
CEP informado
  → remove caracteres não numéricos e valida 8 dígitos   (senão: 400)
  → já existe no banco local?   sim → usa o endereço salvo
                                não → consulta o ViaCEP
                                        → CEP não existe?  400
                                        → salva o endereço no banco e vincula ao cliente
```

## Histórico de commits

O projeto segue o padrão [Conventional Commits](https://www.conventionalcommits.org/pt-br/) (`feat`, `fix`, `refactor`, `build`, `chore`, `docs`), com os escopos `backend` e `frontend`.

## Créditos

- Projeto base: [lab-padroes-projeto-spring](https://github.com/digitalinnovationone/lab-padroes-projeto-spring), de Venilton FalvoJr (DIO)
- Dados de endereço: [ViaCEP](https://viacep.com.br)

## Autor

**André Luiz da Câmara Muniz** · [GitHub](https://github.com/engenheiromuniz)