# UPIQ AI API Reference

This reference is derived from the current Spring controllers in `backend/src/main/java/com/upiq`. The backend base URL is `http://localhost:8080`; API routes use `/api` except the root health route. The frontend default base URL is `http://localhost:8080/api`.

Unless marked public, endpoints require a JWT bearer token:

```http
Authorization: Bearer <token>
```

`SecurityConfig` permits `/api/auth/**` and `/health/**`; all other routes require authentication. Most JSON controller responses use `{ "success": boolean, "data": ..., "message": "..." }`. The report endpoint returns PDF bytes instead. Invalid DTOs return a validation error object; other application errors commonly use the API wrapper. Spring Security may reject unauthenticated requests before a controller is called.

## Authentication

### `POST /api/auth/register` — public

Request:

```json
{
  "email": "member@example.test",
  "userName": "member",
  "password": "a-local-password",
  "role": "USER"
}
```

`userName` and `role` are optional. Password must be at least six characters; email must be valid. The service defaults an omitted role to `USER`, but currently accepts `ADMIN` when supplied. **This is an authorization issue:** public registration must not permit self-assigned admin role before public deployment.

Success is HTTP 201 with `data.message`. Existing email/name or invalid role returns an unsuccessful API response (typically 400).

### `POST /api/auth/login` — public

Request: `{ "email": "member@example.test", "password": "a-local-password" }`.

Success is HTTP 200 with `{ "success": true, "data": { "token": "<jwt>" }, "message": "Login successful" }`. Invalid credentials/disabled account returns 401 and an unsuccessful wrapper.

## Transactions

All routes below require authentication. The user ID is obtained from the principal; callers do not provide an owner ID.

### `POST /api/transactions`

Create a transaction. Request fields:

```json
{
  "amount": 125.50,
  "type": "expense",
  "category": "Uncategorized",
  "description": "Cafe",
  "paymentMethod": "UPI",
  "date": "2026-08-18T12:30:00"
}
```

`amount` and `type` are required; type must be `income` or `expense`. Category, description, payment method and date are optional. A blank/missing/`Uncategorized` category enters the existing deterministic categorization flow. A confident category is persisted, and the user's category record is ensured. Explicit categories are preserved. Returns HTTP 201 with `ApiResponse<TransactionResponse>`.

### `GET /api/transactions`

Returns the authenticated user's transactions ordered by date descending in `data`.

### `GET /api/transactions/category/{category}`

Returns only the authenticated user's matching category transactions (case-insensitive category lookup).

### `POST /api/transactions/categorize-uncategorized`

Processes that user's uncategorized transactions using the existing deterministic rules. The response data includes `transactionsProcessed`, `categorized`, and `remainingUncategorized` counts.

### `GET /api/transactions/{id}`

Returns one transaction only if it belongs to the authenticated user. An ID belonging to another user is rejected by service ownership checks.

### `PUT /api/transactions/{id}`

Updates amount, type, category, description, payment method and optional date for a transaction owned by the authenticated user. Request shape is the create shape; amount/type remain required. Missing/blank/`Uncategorized` category uses deterministic categorization.

### `DELETE /api/transactions/{id}`

Deletes one transaction only if owned by the authenticated user. Success is HTTP 204 with no body.

### `DELETE /api/transactions`

Deletes all transactions for the authenticated user. Returns an API response wrapper; `data` is null.

`TransactionResponse` fields are `id`, `userId`, `amount`, `type`, `category`, `description`, `date`, and `paymentMethod`.

## Categories

All category routes require authentication. The principal's user ID is used when creating, listing, updating, and deleting records. Individual category updates/deletes are looked up by both category ID and owner ID.

### `POST /api/categories`

Request: `{ "name": "Food", "type": "expense", "description": "Optional", "color": "#...", "icon": "🍔" }`. Name and type are required; type is `income` or `expense`. Returns HTTP 201 with `CategoryResponse`.

### `GET /api/categories`

Returns the authenticated user's categories.

### `GET /api/categories/type/{type}`

Returns that user's categories of the supplied type (`income` or `expense`).

### `GET /api/categories/{id}`

Returns one category only from the authenticated user's scope.

### `PUT /api/categories/{id}`

Updates name/type/description/color/icon on a category owned by the principal. The request uses the create-category fields.

### `DELETE /api/categories/{id}`

Deletes a category owned by the principal. Returns an API response wrapper.

### `GET /api/categories/health`

Returns a service health message. Despite the health name, this path is under `/api` and is **not** in the public allowlist, so it requires authentication.

`CategoryResponse` fields are `id`, `name`, `type`, `description`, `color`, and `icon`.

## Statement parsing

### `POST /api/pdf/upload`

Requires authentication. Multipart form data with a file field named `file`:

```http
Content-Type: multipart/form-data
```

The backend accepts PDF or CSV, validates/parses the file, and returns `ApiResponse<ParsingResponse>`. `ParsingResponse` includes `totalTransactions`, `successfulParses`, `failedParses`, `transactions`, `errors`, and `message`. Each returned candidate contains amount, type, category (when found in CSV; PDF extraction may omit it), description, date, and payment method. **This endpoint parses but does not save transactions.** The frontend previews PDF results and calls `POST /api/transactions` for each row the user saves. The current frontend upload screen only accepts PDF files, even though this controller/parser path also accepts CSV.

### `GET /api/pdf/health`

Returns a parser health message. This `/api` path is protected by the global authenticated-route rule.

## Financial dashboard

### `GET /api/financial/dashboard`

Requires authentication; selects data using the principal's user ID. The response is `ApiResponse<FinancialDashboardResponse>` with:

- `hasTransactionData`
- `latestAvailableMonth` and `previousAvailableMonth` (year, month, label)
- `currentMonthSummary` (income, expense, net balance, savings rate, counts)
- `monthlyExpenseComparison` (period totals, absolute/percentage difference, denominator flag, counts)
- `categorySpending` (category totals/counts/percentage)

The latest month is the month of that user's newest transaction. An account with no transactions receives `hasTransactionData=false`, empty category spending, and absent summary/month fields. Calculated amounts do not carry a currency code.

## Financial Assistant

### `POST /api/financial-chat`

Requires authentication. The authenticated user ID determines the transaction scope; there is no user ID in the request.

Request: `{ "question": "How much did I spend on Food in August?" }`. The question must be nonblank. The bounded resolver selects supported financial calculation intents. A supported request invokes the deterministic financial engine and then System C/Ollama using the resulting verified FinancialContext.

Response data (`FinancialChatResponse`) includes `answer`, optional `calculationType`, `financialContext`, `model`, and `latencyMs`, plus `supported` and `error` booleans. Unsupported questions are returned as a handled response with supported=false; no-data and Ollama failure conditions are represented in the response rather than by a dedicated streaming/chat endpoint. Ollama must be configured and running for successful supported natural-language answers; this endpoint does not download models.

## Financial report

### `GET /api/financial/report`

Requires authentication. The principal's user ID is the only scope selector; no user ID query parameter is consumed. The response is raw `application/pdf` with an attachment filename such as `upiq-financial-report-2026-08.pdf`.

The latest available transaction month is selected using `FinancialDashboardService`. Report totals, expense comparison, category data and top five transactions come from existing dashboard/calculation services. PDF generation does not call Ollama. Empty-data accounts receive a valid PDF explaining that no transaction data is available.

## User endpoints

These routes require authentication under the global security configuration, but current controller/service authorization is not limited to an admin role or caller-only data:

| Method | Path | Current behavior |
|---|---|---|
| `GET` | `/api/v1/users` | Returns all users. No admin-role check exists. |
| `GET` | `/api/v1/users/email/{email}` | Looks up and returns a user by supplied email. No caller/admin scope check exists. |
| `GET` | `/api/v1/users/username/{username}` | Looks up and returns a user by supplied username. No caller/admin scope check exists. |
| `GET` | `/api/v1/users/me` | Looks up the authenticated principal name (email) and returns that user's DTO. |

`UserDTO` omits the password. The broad list and lookup behavior is still a privacy/authorization concern and should be fixed before deployment to untrusted users.

## Health

### `GET /health/health` — public

Returns the service health message in an `ApiResponse<String>`. This is the only non-auth API-style health route allowed by the inspected `SecurityConfig` matcher (`/health/**`).

## Error and response notes

- Successful JSON endpoints normally use `{ "success", "data", "message" }`.
- Bean validation errors use HTTP 400 with `timestamp`, `status`, `error`, `message`, `errors` (field-to-message map), and `path`.
- `IllegalArgumentException` is mapped to HTTP 400 with the common API error wrapper.
- Generic exceptions are mapped to HTTP 500 with a generic wrapper message. Spring Security authentication/authorization responses may be produced before controller exception handling.
- There is no OpenAPI/Swagger controller or generated API schema in the repository.

## Not present as REST endpoints

No REST endpoint was found for starting a research experiment, querying a benchmark, fetching research metrics, managing persisted budgets, fraud detection, investment recommendations, or voice/notification features. The research runner is an opt-in `ApplicationRunner` configured by Spring properties, not an API.
