# 🧭 River Watch — Project Patterns

> Living document maintained by the `code-reviewer` agent (`.github/agents/code-reviewer.agent.md`) and by humans.
> A pattern lands here when it is **established**: decided in an issue / ADR / architecture doc, or implemented in approved code.
> Every entry states the *rule*, the *why*, and its *source*. Changing a pattern requires justification (ideally an ADR).

Status legend: **📐 Planned** (decided in issues/docs, not yet in code) · **✅ Established** (in approved code) · **🔄 Changed**

---

## 1. Workflow

| Rule | Why | Source | Status |
|---|---|---|---|
| One branch + PR per story, `Closes #N` in the PR | Trains the real workflow; traceability | README / DoD | 📐 |
| Branch name `feat/us-XXX-short-desc` | Links branch to story | README | 📐 |
| Conventional Commits in English | Readable history, changelog | DoD | 📐 |
| CI green before merge | Breaking the build is a blocker | US-006 | 📐 |
| No secrets committed; `.env` gitignored, `.env.example` updated | Security | DoD, US-002 | 📐 |

## 2. Backend architecture

- **Package by feature**: `station, ingestion, weather, risk, alert, subscription, auth, common`. — US-052 📐
- Inside a module: `api → application → domain`. `domain` imports neither Spring Web nor infra. — US-052 📐
- Modules communicate through public services/interfaces, never another module's internal `domain`. — US-052 📐
- **Ports & adapters**: domain interface (`WeatherProvider`), infra implementation (`OpenMeteoWeatherProvider`); external JSON DTOs never leave the adapter. — US-203, US-401 📐
- Architecture rules enforced by **ArchUnit** tests. — US-056 📐

## 3. REST API

- `/api/v1` prefix, plural nouns, camelCase JSON, ISO-8601 UTC dates. — US-054 📐
- Pagination with `page/size/sort`, returning `Page<XxxDTO>`. — US-103 📐
- DTOs are Java **records**; entities never serialized; **MapStruct** for mapping. — US-103 📐
- Errors as **ProblemDetail** (RFC 7807) via `@RestControllerAdvice`, with extra `code` (e.g. `STATION_NOT_FOUND`). Custom unchecked exceptions thrown in the **service**. — US-104 📐
- Business-rule validation (e.g. `from > to`) in the service → 400. Defaults (e.g. last 48h) applied in the service. — US-207 📐
- Unique-constraint conflicts → 409 ProblemDetail. — US-702 📐

## 4. Persistence

- Flyway `V###__desc.sql` for schema/data; **applied migrations are never edited**. — US-004 📐
- `R__` repeatable migrations for functions/procedures/triggers (`CREATE OR REPLACE`). — US-501, US-802 📐
- `timestamptz` always; river levels as **integer cm**; `jsonb` for raw payloads and factors. — US-100, US-101, US-204 📐
- FK columns indexed; `CHECK` for enumerated text; conscious `ON DELETE`. — US-101 📐
- Idempotency via composite `UNIQUE` + `INSERT ... ON CONFLICT` (`DO NOTHING` for immutable facts, `DO UPDATE` for replaceable forecasts). — US-204, US-205, US-402 📐
- JPA: `@Enumerated(STRING)`, `fetch = LAZY`, `ddl-auto: validate`. `JdbcClient` for upserts and `CALL`s. — US-102, US-205, US-503 📐
- Triggers are fast and local: only INSERT into `alert_outbox`. — US-802 📐
- Every schema change updates `docs/architecture/data-model.md`. — US-100 📐

## 5. Integrations & jobs

- `RestClient` with connect/read timeouts; credentials from env via `@ConfigurationProperties`/`@Value`. — US-202, US-208 📐
- Retry only transient errors (5xx/timeout) with exponential backoff; never 4xx. — US-208 📐
- Scheduled jobs: idempotent, interval configurable in `application.yml`, try/catch **per item**, run recorded in `ingestion_log` (`SUCCESS/PARTIAL/FAILED`). — US-205, US-206 📐
- Adapter parsing tested with saved JSON fixtures (no network). — US-203, US-401 📐
- Outbox consumer: fetch pending → process → mark; failure keeps item pending; per-recipient try/catch; at-least-once delivery. — US-800, US-803 📐

## 6. Security

- Identity always from the JWT (`@AuthenticationPrincipal Jwt` → `sub`); "my" resources under `/me`, never `/users/{id}`. — US-701 📐
- Ownership check on every operation (`findByIdAndUserId`); absent → **404**. — US-702 📐
- Own JWT HS256, secret ≥32 chars from env, `exp` 1h; token delivered in URL **fragment**. — US-604 📐
- Roles → authorities with `ROLE_` prefix; specific rules before `anyRequest()`. — US-605 📐
- Severity enums carry an explicit business weight; never `ordinal()`. — US-803 📐

## 7. Frontend (Angular)

- `core/` · `shared/` · `features/`; features never import each other. — US-053 📐
- Standalone components, `inject()`, signals for state, `@if/@for` with `track`. — US-053, US-504 📐
- Smart pages inject services; dumb components only receive inputs. — US-504 📐
- Typed `HttpClient` + interfaces mirroring DTOs; `apiUrl` in `environment`. — US-301 📐
- `switchMap` for request streams that can be superseded. — US-304 📐
- Functional interceptors (errors → toast with ProblemDetail `detail`; auth → Bearer, 401 → logout) and functional guards returning `UrlTree`. — US-305, US-606, US-607 📐
- Every data component handles loading / error / success. — US-305 📐

## 8. Testing

- JUnit 5 + AAA; Mockito for services; `@WebMvcTest` for controllers (200 + error cases). — US-106 📐
- New logic ships with tests; risk procedure validated against the hand-computed scenarios of US-500. — US-502 📐

## 9. Logging

- SLF4J with placeholders, never string concatenation; never log secrets or tokens. — US-206 📐

---

## Common review findings

_Recurring mistakes found in reviews (the agent appends here)._

- _(none yet)_

---

## Review log

| Date | PR | Story | Patterns added/changed |
|---|---|---|---|
| 2026-09-28 | — | — | Initial seed from milestones M0–M8 issues |
