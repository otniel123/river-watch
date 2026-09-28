# 🧹 Maintainability checklist

> Generic questions the reviewer asks about code health. Project-specific decisions live in `docs/review/PATTERNS.md`.
> Load on **every PR** that changes code. Most findings here are 🟡 or 🟢 — escalate to 🔴 only for boundary violations or code that is clearly wrong.

## 1. Structure & boundaries

- [ ] Code is in the right **module** (`station, ingestion, weather, risk, alert, subscription, auth, common`) and **layer** (`api → application → domain`).
- [ ] `domain` doesn't import Spring Web, JPA infra or external-API types; modules don't reach into another module's internal `domain`. (🔴 — it's an architecture rule; suggest an ArchUnit rule if not covered.)
- [ ] External API DTOs stay inside their adapter; the rest of the code sees only our records/interfaces.
- [ ] Frontend: `core/` / `shared/` / `features/` respected; features don't import each other; dumb components don't inject data services.
- [ ] `common` isn't becoming a dumping ground — shared code has a real second user.

## 2. Readability

- [ ] Names reveal intent and use the **domain language** (station, reading, level_cm, attention/alert/flood, outbox…); units in names when relevant (`levelCm`, `rainfallMm`).
- [ ] Methods do one thing and are short enough to read without scrolling; deep nesting replaced with guard clauses.
- [ ] No magic numbers/strings — constants, enums or configuration (`application.yml`) for thresholds, intervals, weights.
- [ ] Comments explain **why**, not what; no commented-out code.
- [ ] Consistent formatting and style with sibling files.

## 3. Design

- [ ] Single responsibility: controllers only translate HTTP; services hold rules; repositories only access data.
- [ ] No duplication that will drift (copy-pasted mapping, validation or SQL) — but don't abstract after just one use (rule of three).
- [ ] Dependencies injected via constructor; no static state or singletons by hand.
- [ ] Immutability where possible: records, `final` fields, unmodifiable collections.
- [ ] Configuration externalized and typed (`@ConfigurationProperties`) rather than scattered `@Value`s.
- [ ] Error handling is explicit: no swallowed exceptions (`catch (Exception e) {}`), no `null` returns where `Optional`/exception is clearer.
- [ ] Time via injectable `Clock` when logic depends on "now" (also improves testability).

## 4. Scope & hygiene

- [ ] PR does what its story says — no unrelated refactors or features mixed in (suggest a separate PR/issue).
- [ ] No dead code, unused imports, leftover `System.out.println` / `console.log`, debug endpoints left behind without a "temporary" note + follow-up issue.
- [ ] TODOs reference an issue (`// TODO(#42): ...`).
- [ ] Temporary code from a story (e.g. `/me` debug in US-602, `/debug/telegram` in US-801) is tracked for removal.

## 5. Documentation

- [ ] Docs affected by the change are updated in the same PR: `data-model.md` (schema changes), `api-guidelines.md`/README (new conventions), ADR for significant decisions, `.env.example` (new env vars).
- [ ] Public/non-obvious classes (ports, adapters, jobs) have a one-line Javadoc explaining their role.
- [ ] Commit messages follow Conventional Commits and describe the change.

## Review output hints

- Always suggest the concrete alternative (renamed identifier, extracted method, where the class should move).
- Prefer 1–3 high-value maintainability comments over a long list of nitpicks; group repeated nitpicks into one comment.
- If a finding reflects a **new project convention**, propose it for `PATTERNS.md`.
