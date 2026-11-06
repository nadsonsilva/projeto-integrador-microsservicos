# Projeto Integrador — Microsserviços com Java

**Curso:** Bacharelado em Engenharia de Software — IFAM, Campus Parintins  
**Disciplina:** Projeto Integrador  
**Atividade:** Reprodução prática da videoaula *Construindo Microsserviços com Java*  
**Data de referência:** 03/11/2026

## 1. Descrição

Projeto didático de dois serviços Spring Boot. O **microsserviço de eventos** cadastra eventos, lista eventos, consulta os próximos e registra participantes, persistindo os dados em um banco H2. Depois de uma inscrição, comunica-se via **Spring Cloud OpenFeign (HTTP/REST)** com o **microsserviço de e-mail**.

**Fidelidade à videoaula:** o fluxo e as rotas principais seguem o exemplo da aula de Fernanda Kipper, incluindo o serviço de eventos com Spring Boot, Spring Data JPA, H2 e Feign. A aula integra um serviço de e-mail separado que usa AWS SES. Para permitir testes locais sem credenciais ou envio real, o segundo serviço deste pacote é **uma implementação de simulação (mock)**, feita para esta atividade. Portanto, **não se trata de uma transcrição literal de todo o código do vídeo**, e o serviço de e-mail de demonstração não envia mensagens verdadeiras.

## 2. Tecnologias

- Java 21
- Spring Boot 3.2.0
- Spring Web (API REST)
- Spring Data JPA e banco H2 em memória
- Spring Cloud OpenFeign 2023.0.0 (versão estável correspondente à linha mostrada na aula)
- Maven e testes com JUnit / Mockito

## 3. Estrutura

```text
Projeto_Integrador_Microsservicos_Java/
├── events-microservice/
│   ├── pom.xml
│   └── src/
│       ├── main/java/br/edu/ifam/microsservicos/eventos/
│       │   ├── EventosApplication.java
│       │   ├── client/EmailServiceClient.java
│       │   ├── controller/EventController.java
│       │   ├── dto/...
│       │   ├── exception/...
│       │   ├── model/...
│       │   ├── repository/...
│       │   └── service/EventService.java
│       └── main/resources/application.properties
├── email-mock-microservice/
│   ├── pom.xml
│   └── src/main/...
├── exemplos/
├── scripts/
└── README.md
```

## 4. Pré-requisitos

- Instalar o **JDK 21** e o **Apache Maven** (ou abrir os dois projetos em uma IDE que inclua suporte ao Maven, como IntelliJ IDEA ou VS Code com Java Extension Pack).
- Dispor de conexão com a internet **na primeira compilação**, para baixar as dependências Maven.
- Deixar as portas **8080** (eventos) e **8090** (simulador de e-mail) livres.
- Não são necessárias credenciais AWS para esta versão de demonstração.

Verificação no terminal:
```bash
java -version
mvn -version
```

## 5. Como executar

Abra **dois terminais** no diretório do repositório.

**Terminal 1 — E-mail (porta 8090):**
```bash
cd email-mock-microservice
mvn spring-boot:run
```

**Terminal 2 — Eventos (porta 8080):**
```bash
cd events-microservice
mvn spring-boot:run
```

No Windows PowerShell, os comandos são os mesmos. Como alternativa, abra cada pasta como projeto Maven e execute a classe `EmailMockApplication` e depois `EventosApplication` na IDE.

> Ordem importante: primeiro suba o serviço de e-mail, pois o de eventos usa Feign para fazer uma requisição HTTP ao registrar participantes.

## 6. Endpoints

| Serviço | Método | URL | Função |
|---|---|---|---|
| Eventos | `GET` | `http://localhost:8080/events` | Lista eventos cadastrados |
| Eventos | `GET` | `http://localhost:8080/events/upcoming` | Lista eventos com data de hoje em diante |
| Eventos | `POST` | `http://localhost:8080/events` | Cadastra evento |
| Eventos | `POST` | `http://localhost:8080/events/{id}` | Inscreve participante no evento |
| E-mail simulado | `POST` | `http://localhost:8090/api/email/send` | Recebe notificação de e-mail |
| E-mail simulado | `GET` | `http://localhost:8090/api/email/history` | Consulta notificações recebidas |

### 6.1. Cadastrar evento

Exemplo no Postman/Insomnia ou via terminal:
```bash
curl -X POST http://localhost:8080/events \
  -H "Content-Type: application/json" \
  -d '{"maxParticipants":20,"registeredParticipants":0,"date":"30/12/2026","title":"Encontro de Tecnologia","description":"Evento acadêmico de Engenharia de Software"}'
```

O resultado contém o campo `id` do evento. **Copie esse ID** para a próxima requisição.

### 6.2. Listar eventos
```bash
curl http://localhost:8080/events
curl http://localhost:8080/events/upcoming
```

### 6.3. Registrar participante
**Substitua** `ID_DO_EVENTO` pelo ID retornado no cadastro:
```bash
curl -X POST http://localhost:8080/events/ID_DO_EVENTO \
  -H "Content-Type: application/json" \
  -d '{"participantEmail":"aluno@exemplo.com"}'
```

**Verificar a integração entre os microsserviços:**
```bash
curl http://localhost:8090/api/email/history
```

O histórico exibirá o e-mail simulado; ele **não foi enviado para a internet**.

> **Windows PowerShell:** para enviar JSON sem problemas de aspas, use `Invoke-RestMethod` ou o arquivo `scripts/testar.ps1`. Para Linux/macOS, utilize `scripts/testar.sh`.

## 7. Banco de dados H2

- Armazenamento **em memória**: os dados são apagados quando o serviço é reiniciado.
- Console local (somente para estudo): <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:eventosdb`
- User: `sa` / Password: *(vazio)*.

## 8. Testes automatizados

Dentro de `events-microservice`:
```bash
mvn test
```
Os testes unitários verificam criação de eventos, lotação e comunicação esperada com o cliente de e-mail simulado.

**Importante:** este pacote foi preparado com revisão estática dos arquivos, mas **não foi compilado/executado no ambiente em que foi gerado**, pois nele não há Maven nem acesso à resolução de dependências. É necessário realizar `mvn test`, iniciar os dois serviços e executar os exemplos em seu computador antes de entregar.

## 9. Comportamentos e limites

- Ao atingir `maxParticipants`, a API responde com **HTTP 409** (`EVENTO_LOTADO`).
- Eventos inexistentes geram **HTTP 404**.
- A data recebida é no formato brasileiro `dd/MM/yyyy`.
- Se o serviço de e-mail não estiver disponível durante uma inscrição, a API informa indisponibilidade. Como a operação é transacional, o cadastro no banco de eventos é desfeito.
- O exemplo não inclui autenticação, envio real de e-mails ou proteção de produção. O H2 Console está ligado apenas para uso didático.

## 10. Publicação no GitHub

Crie um **repositório público** e envie **o conteúdo desta pasta inteira**, mantendo `README.md`, os dois projetos e seus códigos `src`. Não envie as pastas `target/` nem arquivos com senhas ou chaves.

Pelo Git na pasta raiz:
```bash
git init
git add .
git commit -m "Projeto Integrador: microsservicos Java"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
git push -u origin main
```

Troque `SEU_USUARIO` e `SEU_REPOSITORIO` pelos dados do seu GitHub. O professor pediu o **link público do repositório** e as respostas da **Atividade 1** no grupo de WhatsApp da turma, até **06/11/2026**.

## 11. Referências

- Estudo Dirigido de Projeto Integrador, IFAM Campus Parintins, 03/11/2026, disponibilizado pelo professor.
- Videoaula: *Construindo Microsserviços com Java*: <https://www.youtube.com/watch?v=yACzWg9gUGM>.
- Projeto de referência associado à aula (Fernanda Kipper): <https://github.com/Fernanda-Kipper/microservice-java>.
- Serviço de e-mail original referenciado na aula: <https://github.com/Fernanda-Kipper/desafio-backend-uber> (usa AWS SES e exige configuração adicional).

**Observação acadêmica:** esta é uma implementação de estudo baseada no exemplo apresentado. Leia e compreenda os arquivos, execute os testes e registre suas próprias evidências de execução.
