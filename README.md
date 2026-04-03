# Finance Data Processing and Access Control Backend

A Spring Boot backend for finance data management with JWT authentication, role-based access control, finance record CRUD, and dashboard analytics.

## What Reviewers Can Verify

This project is organized around three roles:

| Role | Access |
| --- | --- |
| `VIEWER` | Read finance records and dashboard summary |
| `ANALYST` | Read finance records and dashboard summary |
| `ADMIN` | Full access to users and finance records |

Core backend features:

- Public registration
- JWT login
- Admin-only user management
- Finance record create, read, update, and soft delete
- Pagination and filtering
- Dashboard summary analytics
- Seeded demo data for immediate review
- Automated tests

## Assignment Coverage

### User and role management

- Public self-registration with default `VIEWER` role
- Admin-only user creation, listing, lookup, and update
- Active and inactive user support
- Backend-enforced role restrictions

### Financial records

- Create, read, update, and delete APIs
- Amount, type, category, transaction date, and notes
- Filtering by type, category, and date range
- Paginated listing

### Dashboard summary APIs

- Total income
- Total expense
- Net balance
- Category totals
- Monthly trends
- Recent activity

### Validation and reliability

- Bean validation on request payloads
- Centralized exception handling
- Consistent HTTP status codes

## Default Setup

The application uses PostgreSQL by default.

Default runtime values in [`application.yaml`](/home/somu/Documents/demo/src/main/resources/application.yaml):

```bash
DB_URL=jdbc:postgresql://localhost:5432/zorvyn
DB_USERNAME=somu
DB_PASSWORD=somu1234
DB_DRIVER=org.postgresql.Driver
```

If your local PostgreSQL setup differs, export your own values before starting the app.

## Quick Start

### 1. Create the database

```sql
CREATE DATABASE zorvyn;
```

### 2. Start the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 3. Optional Docker setup

Build and start the app with PostgreSQL:

```bash
docker compose up --build
```

Stop the stack:

```bash
docker compose down
```

### 4. Seeded demo users

On first startup, the app seeds three users and sample finance records:

- `admin@finance.local` / `Admin@123`
- `analyst@finance.local` / `Analyst@123`
- `viewer@finance.local` / `Viewer@123`

## Live Demo for Reviewers

This project now includes Swagger/OpenAPI documentation so reviewers can inspect and test the endpoints from a browser instead of relying only on curl commands.

### Swagger UI

After starting the application, open:

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

How to use it:

1. Call `POST /api/v1/auth/login` with one of the seeded users.
2. Copy the returned JWT token.
3. Click `Authorize` in Swagger UI.
4. Paste `Bearer <your-token>` into the value field.
5. Open the protected endpoints and click `Try it out`.

This lets reviewers check request bodies, query parameters, response models, and role-protected APIs directly from Swagger.

### Curl-based review flow

This section is meant to let a reviewer verify the same APIs from the terminal.

### Review flow

1. Start the app.
2. Copy the commands below in order.
3. Verify successful responses for auth, users, records, and dashboard.
4. Verify access control by comparing admin and viewer behavior.

### Reviewer prerequisites

- `curl`
- `jq`
- Running API on `http://localhost:8080`

### Set the base URL

```bash
export BASE_URL=http://localhost:8080/api/v1
```

### 1. Register a public user

This proves public registration works and that non-admin signup is available.

```bash
curl -s "$BASE_URL/auth/register" \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName": "Public Demo User",
    "email": "public-demo@finance.local",
    "password": "Viewer@123"
  }' | jq
```

Expected result: a created user with role `VIEWER`.

### 2. Log in with seeded users

```bash
export ADMIN_TOKEN=$(curl -s "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "admin@finance.local",
    "password": "Admin@123"
  }' | jq -r '.token')

export ANALYST_TOKEN=$(curl -s "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "analyst@finance.local",
    "password": "Analyst@123"
  }' | jq -r '.token')

export VIEWER_TOKEN=$(curl -s "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "viewer@finance.local",
    "password": "Viewer@123"
  }' | jq -r '.token')
```

Optional check:

```bash
echo "$ADMIN_TOKEN" | cut -c1-25
echo "$ANALYST_TOKEN" | cut -c1-25
echo "$VIEWER_TOKEN" | cut -c1-25
```

### 3. Verify dashboard summary API

Admin, analyst, and viewer should all be allowed to read the dashboard summary.

```bash
curl -s "$BASE_URL/dashboard/summary" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq
```

Filtered summary example:

```bash
curl -s "$BASE_URL/dashboard/summary?startDate=2026-03-01&endDate=2026-04-30" \
  -H "Authorization: Bearer $ANALYST_TOKEN" | jq
```

Expected result: totals, counts, category totals, monthly trends, and recent activity.

### 4. Verify finance record listing and filters

List all records:

```bash
curl -s "$BASE_URL/records/all?page=0&size=10" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq
```

Filter by type and category:

```bash
curl -s "$BASE_URL/records/all?type=EXPENSE&category=Rent&page=0&size=5" \
  -H "Authorization: Bearer $ANALYST_TOKEN" | jq
```

Filter by date range:

```bash
curl -s "$BASE_URL/records/all?startDate=2026-03-01&endDate=2026-04-30&page=0&size=5" \
  -H "Authorization: Bearer $VIEWER_TOKEN" | jq
```

Expected result: paginated data under `content`.

### 5. Create a finance record as admin

```bash
curl -s -X POST "$BASE_URL/records/create" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "amount": 199.99,
    "type": "EXPENSE",
    "category": "Software",
    "transactionDate": "2026-04-03",
    "notes": "Subscription renewal"
  }' | jq
```

Expected result: `201 Created` with the new record payload.

### 6. Read a specific finance record

Use one of the ids returned from the list endpoint. Example with id `1`:

```bash
curl -s "$BASE_URL/records/get/1" \
  -H "Authorization: Bearer $VIEWER_TOKEN" | jq
```

### 7. Update a finance record as admin

```bash
curl -s -X PUT "$BASE_URL/records/update/1" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "amount": 5400.00,
    "type": "INCOME",
    "category": "Salary",
    "transactionDate": "2026-03-15",
    "notes": "Adjusted salary entry for review"
  }' | jq
```

### 8. Delete a finance record as admin

Use a record id that can be safely removed during review:

```bash
curl -i -X DELETE "$BASE_URL/records/delete/1" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected result: `204 No Content`.

### 9. Create a user as admin

```bash
curl -s -X POST "$BASE_URL/users/create" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName": "Finance Intern",
    "email": "intern@finance.local",
    "password": "Intern@123",
    "role": "VIEWER",
    "active": true
  }' | jq
```

Expected result: `201 Created` with the new user.

### 10. List all users as admin

```bash
curl -s "$BASE_URL/users/all" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq
```

### 11. Get a single user as admin

```bash
curl -s "$BASE_URL/users/get/1" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq
```

### 12. Update a user as admin

```bash
curl -s -X PUT "$BASE_URL/users/update/2" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName": "Insight Analyst Updated",
    "role": "ANALYST",
    "active": true
  }' | jq
```

### 13. Verify access control

Viewer can read dashboard:

```bash
curl -s "$BASE_URL/dashboard/summary" \
  -H "Authorization: Bearer $VIEWER_TOKEN" | jq
```

Viewer can read records:

```bash
curl -s "$BASE_URL/records/all?page=0&size=5" \
  -H "Authorization: Bearer $VIEWER_TOKEN" | jq
```

Viewer cannot create records:

```bash
curl -i -X POST "$BASE_URL/records/create" \
  -H "Authorization: Bearer $VIEWER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "amount": 10.00,
    "type": "EXPENSE",
    "category": "Blocked",
    "transactionDate": "2026-04-03",
    "notes": "Should be rejected"
  }'
```

Expected result: `403 Forbidden`.

Viewer cannot access admin-only users API:

```bash
curl -i "$BASE_URL/users/all" \
  -H "Authorization: Bearer $VIEWER_TOKEN"
```

Expected result: `403 Forbidden`.

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

- Controllers handle HTTP concerns
- Services contain business logic
- DTOs define request and response contracts
- Repositories manage persistence
- Security is enforced with JWT and method-level authorization

## Running Tests

```bash
./mvnw test
```

## Notes

- Public registration always creates `VIEWER` userss.
- Elevated roles stay admin-managed.
- Sample data is seeded only when the user table is empty.
