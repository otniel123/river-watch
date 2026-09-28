# 🔐 Security checklist

> Generic questions the reviewer asks about security. Project-specific decisions live in `docs/review/PATTERNS.md`.
> Load on **every PR**. Any confirmed item in section 1 is 🔴 **Blocking**.

## 1. Secrets (always check — blocking)

- [ ] No passwords, API keys, client secrets, JWT signing keys, Telegram bot tokens or ANA credentials in code, `application*.yml`, tests, fixtures, `docs/samples/`, screenshots or commit messages.
- [ ] Secrets come from environment variables; `.env` stays gitignored; `.env.example` updated with **placeholder** values only.
- [ ] Saved API responses (`docs/samples/`, test resources) are scrubbed of tokens/headers.
- [ ] Secrets never appear in logs, exception messages, ProblemDetail bodies or URLs that get logged (the Telegram token goes in the URL — make sure that URL is never logged).
- [ ] If a secret was committed: removing it in a new commit is **not enough** — it must be rotated. Say so explicitly.

## 2. Authentication & authorization (from M6 on)

- [ ] Identity comes from the validated JWT (`@AuthenticationPrincipal Jwt` → `sub`), never from a path/query/body id.
- [ ] "My" resources live under `/me/...`; no `/users/{id}` for self-service.
- [ ] **Ownership check on every operation** (read, update, delete) — queries filtered by the token's user (`findByIdAndUserId`); absent → **404** (don't reveal existence). (IDOR/BOLA)
- [ ] Authorization rules: specific matchers before `anyRequest()`; `/api/v1/admin/**` requires ADMIN; debug endpoints are ADMIN-only and marked temporary.
- [ ] Correct 401 (not authenticated) vs 403 (authenticated, no permission).
- [ ] JWT: HS256 secret ≥ 32 chars from env, `exp` set (1h), nothing sensitive in claims (payload is readable); token delivered in the URL **fragment**, and the frontend clears it with `history.replaceState`.
- [ ] `roles` claim mapped with the `ROLE_` prefix.

## 3. Input handling

- [ ] Request bodies use dedicated DTOs with `@Valid` + constraints; never bind directly to entities (mass assignment).
- [ ] Business-rule validation in the service (e.g. `from > to` → 400).
- [ ] SQL: parameters always bound (`:param` in `JdbcClient`, JPA params); **no string concatenation** into SQL or PL/pgSQL `EXECUTE`.
- [ ] Pagination `size` has an upper bound (avoid dumping the whole table).
- [ ] Telegram messages escape user/station-provided text for the chosen `parse_mode`.

## 4. Data exposure

- [ ] Entities never serialized; DTOs expose only needed fields (no `provider_id`, internal flags, raw payloads unless intended).
- [ ] Error responses (ProblemDetail) don't leak stack traces, SQL or internal class names.
- [ ] Logs don't contain personal data beyond what's needed (email, chat_id) — prefer ids.

## 5. Web / frontend

- [ ] CORS configured in the backend with explicit origins (e.g. `http://localhost:4200`) — never `*` together with credentials.
- [ ] No secrets in Angular code or `environment*.ts` (everything shipped to the browser is public).
- [ ] Token storage in `localStorage` is an accepted, documented MVP trade-off (XSS risk) — don't block on it, but flag new XSS vectors (`innerHTML`, `bypassSecurityTrust*`).
- [ ] Auth interceptor only attaches the Bearer token to **our API** URLs, not to third-party requests.
- [ ] Guards are UX only — the backend must enforce every rule too.

## 6. Dependencies & config

- [ ] New dependencies are well-known and maintained; no unnecessary starters.
- [ ] Actuator: only `health` (and `info`) exposed publicly.

## Review output hints

- Secrets, IDOR, missing authz, SQL concatenation → 🔴 Blocking.
- Missing input bounds, overly verbose errors/logs → 🟡.
- Always explain the attack in one sentence ("an attacker changes the id in the URL and deletes someone else's subscription").
