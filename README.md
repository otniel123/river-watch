# 🌊 River Watch

A **river level monitoring and flood alert system**, initially focused on the **Itajaí Valley (Santa Catarina, Brazil)**.

River Watch ingests hydrological station data from **ANA (Brazil's National Water Agency)** plus weather and hydrological forecasts from **Open-Meteo / Flood API (GloFAS)**, computes a **risk level per region**, and dispatches **Telegram alerts** to subscribed users.

> ⚠️ Active development — MVP (v0.1.0) in progress. See the [issues](https://github.com/otniel123/river-watch/issues) for the detailed roadmap.

---

## 🎯 Goals

- Periodically ingest readings from real river-gauge and rain-gauge stations
- Persist historical series **idempotently** and auditably
- Compute flood risk per region combining **accumulated rainfall**, **river level**, and **forecast**
- Notify subscribed users when a threshold is crossed
- Serve as a deep learning project on architecture, databases, and external integrations

---

## 🏗️ Architecture

The project follows a **layered ports & adapters architecture**, with the dependency rule enforced automatically by **ArchUnit** tests.

```
┌─────────────┐     ┌──────────────────┐     ┌────────────────────┐
│  Angular    │────▶│  Spring Boot API │────▶│ PostgreSQL/PostGIS │
│  (frontend) │     │   (backend)      │     │  + PL/pgSQL        │
└─────────────┘     └────────┬─────────┘     └─────────┬──────────┘
                             │                         │
                    ┌────────┴────────┐        ┌───────┴────────┐
                    │ External APIs   │        │ outbox table   │
                    │ ANA / Open-Meteo│        │  + trigger     │
                    │ GloFAS          │        └───────┬────────┘
                    └─────────────────┘                │
                                               ┌───────┴────────┐
                                               │ Telegram worker│
                                               └────────────────┘
```

Architectural decisions are captured as **ADRs**, alongside **C4 (levels 1 and 2)** diagrams in the docs folder.

---

## 🧰 Tech stack

| Layer | Technologies |
|---|---|
| **Backend** | Java, Spring Boot, Spring Security (JWT + OAuth2), JPA/Hibernate, MapStruct |
| **Database** | PostgreSQL + PostGIS, Flyway (migrations), PL/pgSQL (functions, procedures, triggers) |
| **Frontend** | Angular (routing, guards, time-series charts) |
| **Integrations** | ANA API (Hidroweb), Open-Meteo, Open-Meteo Flood API (GloFAS), Telegram Bot API |
| **Infra** | Docker Compose, GitHub Actions (CI) |

---

## 🚀 Running locally

> Prerequisites: Docker + Docker Compose, JDK, Node.js / Angular CLI.

```bash
# 1. Clone the repository
git clone https://github.com/otniel123/river-watch.git
cd river-watch

# 2. Set up environment variables
cp .env.example .env
# edit .env with database credentials, ANA credentials, Telegram bot token, etc.

# 3. Start the database (PostgreSQL + PostGIS)
docker compose up -d

# 4. Run the backend (Flyway migrations are applied on boot)
./mvnw spring-boot:run

# 5. Run the frontend
cd frontend
npm install
npm start
```

🔐 **Never commit secrets.** `.env` is gitignored; use `.env.example` as the reference.

---

## 🗺️ Roadmap (milestones)

Work is organized into numbered user stories (`US-XXX`) grouped by milestone:

| Milestone | Theme | Key deliverables |
|---|---|---|
| **M0** | Foundation | Docker Compose, Spring Boot skeleton, first Flyway migration, Angular skeleton, CI, ANA API access |
| **M0.5** | Architecture | C4 diagrams, backend modules and the dependency rule, frontend conventions, REST API design guide, ADRs, ArchUnit |
| **M1** | Station domain | Data model (ER), `station` table, JPA entities, paginated `GET /api/v1/stations` with DTO + MapStruct, standardized errors (ProblemDetail), seed of real Itajaí Valley stations, first tests |
| **M2** | ANA ingestion | Auth client with token caching, fetch and map readings, idempotent `reading` table, scheduled job, `ingestion_log`, `GET /stations/{id}/readings`, retry |
| **M3** | Data frontend | Station list, detail page with routing, 48-hour river-level chart, period selector (24h/48h/7d), loading and global errors |
| **M4** | Weather & forecast | `WeatherProvider` port + Open-Meteo adapter, persist forecast per region, river discharge (Flood API / GloFAS) |
| **M5** | Risk calculation | Risk calculation spec, PL/pgSQL accumulated-rain function, `sp_recalc_region_risk` procedure, risk endpoints, dashboard |
| **M6** | Authentication | Login sequence diagram, OAuth apps (Google and GitHub), user upsert, own JWT issuance, API protection, Angular login flow, guards and navbar |
| **M7** | User & subscriptions | Profile with Telegram `chat_id`, region subscriptions CRUD |
| **M8** | Alerts | Alert pipeline design + ADR-004, Telegram bot, threshold trigger + outbox table, worker consuming the outbox, anti-spam, alert history, MVP demo and v0.1.0 release |

---

## 🏷️ Repository conventions

- **Branch + Pull Request** for every story (even when working solo)
- **Conventional Commits** in English (`feat:`, `fix:`, `chore:`, `docs:`...)
- **Green CI** required before merging
- Labels in use: `backend`, `frontend`, `database`, `infra`, `external-api`, `size:S`, `size:M`

### Definition of Done
- [ ] Own branch + Pull Request
- [ ] CI green
- [ ] Manually tested; all acceptance criteria checked
- [ ] No secrets committed
- [ ] Commits in English following Conventional Commits

---

## 🤝 Contributing

1. Pick an open issue
2. Create a branch off `main` (e.g. `feat/us-103-stations-endpoint`)
3. Implement it, satisfying every acceptance criterion in the issue
4. Open a PR referencing the issue (`Closes #16`)

---

## 📚 Data sources

- [ANA — Hidroweb / hydrological data API](https://www.snirh.gov.br/hidroweb/)
- [Open-Meteo Weather API](https://open-meteo.com/)
- [Open-Meteo Flood API (GloFAS)](https://open-meteo.com/en/docs/flood-api)

---

## 📄 License

Not yet defined.
