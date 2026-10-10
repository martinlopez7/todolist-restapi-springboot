# AGENTS.md

Spring Boot 4.0.2 / Java 21 / Maven REST API for a per-user todo list, secured with JWT.
There is no README; this file and the code are the sources of truth.

## Commands

Use the Maven wrapper (never a system `mvn`):

- Run app: `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`)
- Build jar: `./mvnw clean package`
- All tests: `./mvnw test`
- Single test class: `./mvnw test -Dtest=TaskServiceTest`
- Single test method: `./mvnw test -Dtest=TaskServiceTest#shouldCreateTaskSuccessfully`

There is no lint/formatter/typecheck step configured.

## Database (required for run and full test suite)

PostgreSQL is mandatory. Start it with `docker compose up postgres-db`.

Config gotcha: `src/main/resources/application.properties` points at database
`todo_list`, but `docker-compose.yml` creates `todo_db`. Running locally with
`spring-boot:run` against the compose database fails unless you create `todo_list`
(or override the datasource). The compose `app-backend` service avoids this by
overriding `SPRING_DATASOURCE_URL` to `todo_db`.

- `spring.jpa.hibernate.ddl-auto=create-drop`: schema is recreated on start and
  dropped on shutdown, so all data is lost every restart. No Flyway/Liquibase.
- `TodoListApplicationTests` (`@SpringBootTest`) boots the full context, so
  `./mvnw test` needs Postgres reachable, not just the isolated unit tests.

## Architecture

- Base package is `com.martin.todo_list` (note the underscore, not a dot).
- Layers: `controllers` -> `services` -> `repositories` (Spring Data JPA);
  `entities` (JPA), `dto` (API payloads), `auth` (request/response + auth service),
  `config` (security, JWT filter/service, beans), `exceptions` (`@RestControllerAdvice`).
- `TaskService` owns ownership checks: every task operation verifies
  `task.getUser().getId()` matches the authenticated user, else returns null/false
  (controller maps that to 404). Keep this pattern for new task operations.
- `Task.createdAt` and initial `status=PENDING` are set in `@PrePersist`, not in the service.

## Security / API

- `/auth/register` and `/auth/login` are public; all other routes need
  `Authorization: Bearer <jwt>`. CSRF disabled, stateless sessions.
- JWT signing secret is hardcoded in `config/JwtService.java`; tokens last 24h.
- Endpoints: `POST /auth/register`, `POST /auth/login`, and `GET/POST/PUT/PATCH/DELETE /tasks`
  (PATCH takes `?status=` with enum `TaskStatus`: PENDING, IN_PROGRESS, COMPLETED).
- OpenAPI UI (springdoc) is available at `/swagger-ui.html`.

## Testing conventions

- Use Spring Boot 4's `@MockitoBean`, not the older `@MockBean`.
- Controller tests use `@WebMvcTest` + `@WithMockUser`; service tests use plain
  Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks`).
- `TaskControllerTest` calls `.with(csrf())` on MockMvc requests; keep this if you
  add secured controller tests.

## Docker

`docker-compose.yml` builds the backend from `Dockerfile` (multi-stage Maven build,
`-DskipTests`) and runs it against the `postgres-db` service on port 8080.
