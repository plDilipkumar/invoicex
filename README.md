# InvoiceX — Invoice and Client Management API

InvoiceX is a Spring Boot REST API for account-based invoice and client management. It includes JWT authentication, client and invoice workflows, invoice search/pagination, PDF generation, and optional email-related flows.

## Stack

- Java 17 and Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security
- MySQL-compatible relational database
- JWT authentication
- Docker deployment on Render
- React frontend: [invoicex-frontend](https://github.com/plDilipkumar/invoicex-frontend)

## Environment configuration

Configure these as environment variables in the backend host. Do not commit credentials or connection strings to Git.

| Variable | Purpose |
| --- | --- |
| `PORT` | HTTP port provided by the host; defaults to 8080 locally |
| `SPRING_DATASOURCE_URL` | JDBC URL for the provisioned MySQL database |
| `SPRING_DATASOURCE_USERNAME` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | Strong secret used to sign JWTs |
| `MAIL_USERNAME` | Optional SMTP account for email features |
| `MAIL_PASSWORD` | Optional SMTP credential or app password |
| `DB_POOL_SIZE` | Optional JDBC pool size (defaults to 5) |

For Aiven MySQL, use the service's connection details and its required TLS/SSL mode in `SPRING_DATASOURCE_URL`. Use the database/schema provided by the service, not a guessed host or password. Keep credentials only in the Render environment settings.

## Database initialization

The application uses JPA with `spring.jpa.hibernate.ddl-auto=update`, so Hibernate can create/update entity tables once the target database exists and the configured database user has the necessary privileges. This does **not** provision a deleted Aiven service or create a missing database server. Verify that the database service is running, the schema exists, and the Render service can connect before testing registration and invoice workflows.

The main persisted entities include users, clients, invoices, and their user/client relationships. For a production system with valuable customer data, replace automatic schema updates with reviewed versioned migrations before handling real business records.

## Run locally

Requirements: Java 17 and Maven.

```bash
mvn spring-boot:run
```

Set the required database and JWT environment variables first. The API then listens on the configured `PORT` (or 8080 by default).

## Main API routes

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `/api/clients` — client CRUD
- `/api/invoices` — invoice CRUD, search, and PDF download

Authenticated routes require a valid JWT. Do not put real customer or payment information in a public demo.

## Deployment

- Backend service: [Render](https://invoicex-i3y4.onrender.com)
- Frontend: [Vercel](https://invoicex-frontend.vercel.app)
- Source: [Backend repository](https://github.com/plDilipkumar/invoicex) · [Frontend repository](https://github.com/plDilipkumar/invoicex-frontend)

These URLs identify the configured deployments; database connectivity and end-to-end workflows still need to be verified before describing the application as production-ready.
