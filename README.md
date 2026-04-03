# Finance Data Processing and Access Control Backend

A Spring Boot backend built for the assignment. The project focuses on backend design quality: clear APIs, strict role-based access control, finance record processing, dashboard analytics, validation, and maintainable service boundaries.

## What You Can Demo Quickly

This backend is built around three roles:

| Role | What it can do |
| --- | --- |
| `VIEWER` | Read records and dashboard summaries |
| `ANALYST` | Read records and analytics insights |
| `ADMIN` | Full access to users and finance records |

Core features:

- Public registration
- JWT login
- User management
- Finance record CRUD
- Pagination and filtering
- Soft delete
- Dashboard summary analytics
- Seed data for instant demo
- Integration tests

## Assignment Coverage

### User and role management

- Public self-registration with default `VIEWER` access
- Admin-only user creation, listing, lookup, and updates
- Active/inactive users
- Role-based restrictions enforced in the backend

### Financial records

- Create, read, update, delete
- Amount, type, category, transaction date, notes
- Filters by type, category, and date range
- Paginated listing

### Dashboard summary APIs

- Total income
- Total expense
- Net balance
- Category totals
- Monthly trends
- Recent activity

### Validation and reliability

- Request validation with useful error payloads
- Consistent HTTP status codes
- Centralized exception handling

## Default Setup

PostgreSQL is the default database.

Default connection values:

```bash
DB_URL=jdbc:postgresql://localhost:5432/finance_dashboard
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_DRIVER=org.postgresql.Driver
```

If your local PostgreSQL values are different, export them before starting the app.

## Quick Start

### 1. Create the database

```sql
CREATE DATABASE finance_dashboard;
```

### 2. Start the app

```bash
./mvnw spring-boot:run
```

### Docker setup

Build and start the app with PostgreSQL:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080` and PostgreSQL at `localhost:5432`.

To stop the stack:

```bash
docker compose down
```

### 3. Use seeded demo accounts

- `admin@finance.local` / `Admin@123`
- `analyst@finance.local` / `Analyst@123`
- `viewer@finance.local` / `Viewer@123`

## Interactive API Walkthrough

### Step 1: Register a public user

```bash
curl -s http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName":"Public Demo User",
    "email":"public-demo@finance.local",
    "password":"Viewer@123"
  }'
```

This creates a user with the `VIEWER` role by default.

### Step 2: Log in as admin

```bash
curl -s http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "email":"admin@finance.local",
    "password":"Admin@123"
  }'
```

Example response:

```json
{
  "token": "eyJ...",
  "user": {
    "id": 1,
    "fullName": "System Admin",
    "email": "admin@finance.local",
    "role": "ADMIN",
    "active": true
  }
}
```

### Step 3: Save the admin token

```bash
TOKEN=$(curl -s http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@finance.local","password":"Admin@123"}' | jq -r .token)
```

### Step 4: View dashboard summary

```bash
curl -s http://localhost:8080/api/v1/dashboard/summary \
  -H "Authorization: Bearer $TOKEN"
```

### Step 5: List records with filters

```bash
curl -s "http://localhost:8080/api/v1/records/all?type=EXPENSE&category=Rent&page=0&size=5" \
  -H "Authorization: Bearer $TOKEN"
```

### Step 6: Create a finance record

```bash
curl -s http://localhost:8080/api/v1/records/create \
  -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "amount": 199.99,
    "type": "EXPENSE",
    "category": "Software",
    "transactionDate": "2026-04-03",
    "notes": "Subscription renewal"
  }'
```

### Step 7: Create another user as admin

```bash
curl -s http://localhost:8080/api/v1/users/create \
  -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName": "Finance Intern",
    "email": "intern@finance.local",
    "password": "Intern@123",
    "role": "VIEWER",
    "active": true
  }'
```

### Step 8: List all users as admin

```bash
curl -s http://localhost:8080/api/v1/users/all \
  -H "Authorization: Bearer $TOKEN"
```

### Step 9: Verify access control

Log in as viewer:

```bash
VIEWER_TOKEN=$(curl -s http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"viewer@finance.local","password":"Viewer@123"}' | jq -r .token)
```

Viewer can read:

```bash
curl -s http://localhost:8080/api/v1/dashboard/summary \
  -H "Authorization: Bearer $VIEWER_TOKEN"
```

Viewer cannot create:

```bash
curl -i http://localhost:8080/api/v1/records/all \
  -X POST \
  -H "Authorization: Bearer $VIEWER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "amount": 10,
    "type": "EXPENSE",
    "category": "Blocked",
    "transactionDate": "2026-04-03"
  }'
```

Expected result: `403 Forbidden`

## API Reference

### Authentication

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Users

- `POST /api/v1/users/create` (`ADMIN`)
- `GET /api/v1/users/all` (`ADMIN`)
- `GET /api/v1/users/get/{id}` (`ADMIN`)
- `PUT /api/v1/users/update/{id}` (`ADMIN`)

### Finance Records

- `POST /api/v1/records/create` (`ADMIN`)
- `GET /api/v1/records/all` (`ADMIN`, `ANALYST`, `VIEWER`)
- `GET /api/v1/records/get/{id}` (`ADMIN`, `ANALYST`, `VIEWER`)
- `PUT /api/v1/records/update/{id}` (`ADMIN`)
- `DELETE /api/v1/records/delete/{id}` (`ADMIN`)

Supported query parameters on `GET /api/v1/records/all`:

- `type`
- `category`
- `startDate`
- `endDate`
- `page`
- `size`

### Dashboard

- `GET /api/v1/dashboard/summary` (`ADMIN`, `ANALYST`, `VIEWER`)

Optional query parameters:

- `startDate`
- `endDate`

## Project Structure

- Controllers handle HTTP concerns only
- Services contain business logic
- DTOs define API contracts
- Repositories manage persistence
- Security is enforced through JWT and method-level authorization

## Running Tests

```bash
./mvnw test
```

## Notes

- Public registration creates `VIEWER` users by default. Elevated roles remain admin-managed.
- PostgreSQL is the default because it looks stronger for submission and matches realistic backend usage.
- If you want an in-memory evaluation mode later, it can be added back as a dedicated Spring profile instead of being the default.
