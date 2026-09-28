# 🧪 Testing checklist

> Generic questions the reviewer asks about tests. Project-specific decisions live in `docs/review/PATTERNS.md`.
> Load on **every PR** that changes code.

## 1. When tests are required (project policy)

The Definition of Done requires **manual testing + green CI**, not automated tests. Automated tests are phased in along the roadmap:

| Phase | Rule | Severity if missing |
|---|---|---|
| **Any story** whose acceptance criteria ask for a test | The test must exist and pass | 🔴 Blocking |
| **Before US-106** (M0, MA, US-100…US-105) | No automated tests expected (except ArchUnit in US-056) | — don't ask |
| **After US-106 is merged** | New service logic (rules, validations, error paths) should have unit tests even if the issue doesn't ask | 🟡 Suggestion |
| **Security-critical behaviour** (ownership/IDOR, authz rules, token handling) after US-106 | Should have an automated test | 🟡 strong suggestion (explain why) |
| **Frontend (Angular)** | No automated tests required in the MVP | — optional nitpick at most |

Stories whose acceptance criteria explicitly require automated tests:

| Story | Required test |
|---|---|
| US-006 | CI fails when a test fails |
| US-056 | 2–3 ArchUnit rules |
| US-106 | `StationService` unit test (Mockito) + `@WebMvcTest` for 200 and 404 |
| US-202 | Token cache: second call doesn't re-auth; expired token forces renewal |
| US-203 | ANA JSON → domain mapping using saved fixtures (no network) |
| US-401 | Open-Meteo parsing using a saved JSON |

Implicit / optional: US-102 (test **or** `CommandLineRunner`), US-501 (test **or** `/debug` endpoint), US-403 (mirror US-401 → parsing test expected), US-604 (tip: generate the JWT in an isolated unit test), US-702 ("touching someone else's subscription → 404" — recommend automated).

Stories validated **manually** (don't demand automation): US-205 (run job twice), US-502 (DBeaver scenarios), US-605 (Postman 401/403), US-802 (manual INSERT).

> To decide the phase, check whether US-106 (issue #13) is closed / its code exists in `backend/src/test`.

## 2. Quality of the tests that exist

- [ ] **AAA** structure (Arrange / Act / Assert) is clear; one behaviour per test.
- [ ] Test names describe behaviour (`shouldThrowNotFoundWhenStationDoesNotExist`), not the method name.
- [ ] Asserts are meaningful — not just `assertNotNull`; assert status **and** body/ProblemDetail `code` in controller tests.
- [ ] **Error paths** covered, not only the happy path (404, 400, empty list, null fields).
- [ ] Right slice: unit test with Mockito for services; `@WebMvcTest` (+ `@MockBean`) for controllers; don't boot the full context when a slice is enough.
- [ ] **No network** in tests: external APIs tested with saved JSON fixtures loaded from the classpath.
- [ ] **Deterministic**: no dependency on `Instant.now()` without an injectable `Clock`, no `Thread.sleep`, no test order dependence, no random data without a seed.
- [ ] Mocks verify interactions only when the interaction *is* the behaviour (e.g. "does not re-authenticate" → `verify(..., times(1))`).
- [ ] Fixtures contain **no real secrets or tokens**.
- [ ] Tests run in CI (`mvn verify`) — not disabled or `@Disabled` without an explanation.

## 3. Review output hints

- Missing required test → 🔴, cite the acceptance criterion.
- Missing test for new logic after US-106 → 🟡, propose the concrete test (name + scenario).
- Flaky/non-deterministic test → 🔴 (it erodes trust in CI).
- Link the "📚 What to study" item of the issue when relevant (e.g. AAA, Mockito, `@WebMvcTest`).
