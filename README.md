# e-doctorat

Platform for managing doctoral (PhD) admissions and examination workflows: subject publication, candidate registration and applications, jury/commission grading, and result publication — across separate portals for candidates, professors (including lab directors and CED directors), and the registrar's office (scolarité).

## Structure

```
backend/    Spring Boot 3 (Java 17) REST API
frontend/   React 19 + TypeScript + Vite SPA
```

## Backend

- **Stack:** Spring Boot 3.1.5, Spring Security, Spring Data JPA, MySQL, JWT (jjwt) + Spring Session (JDBC) hybrid auth, Google OAuth2, Spring Mail.
- **Layout:** `backend/src/main/java/com/edoc/{config,controllers,dto,exception,models,repository,security,services}`
- **Uploaded files:** stored under `backend/uploads/`, served at `/uploads/**`.

### Setup

1. Create a MySQL database (default expected name: `testauth`).
2. Copy `backend/src/main/resources/application.properties.example` to `application.properties` and fill in your own values:
   - `spring.datasource.url` / `username` / `password`
   - `jwt.secret` / `jwt.expiration`
   - `google.client.id` (Google OAuth2)
   - `spring.mail.*` (SMTP credentials for notification emails)
3. Run the API:
   ```
   cd backend
   ./mvnw spring-boot:run
   ```
   Serves on `http://localhost:8080`.

## Frontend

- **Stack:** React 19, TypeScript, Vite, Tailwind CSS v4, Radix UI (shadcn-style components), TanStack Query & Table, React Router, React Hook Form + Zod, Zustand, jsPDF.
- **Portals:** `frontend/src/portals/{candidat,professeur,scolarite,shared}`
  - `candidat` — registration, applications, diplomas, profile.
  - `professeur` — subjects, enrolled candidates, grading, results, examination commissions, and (for lab directors) laboratory management.
  - `scolarite` — registrar/admin oversight of inscriptions.

### Setup

```
cd frontend
npm install
npm run dev
```

Serves on Vite's default dev port and proxies `/api` and `/uploads` to the backend at `http://localhost:8080` (see `vite.config.ts`).

Other scripts: `npm run build`, `npm run lint`, `npm run preview`.

## Running locally

1. Start MySQL and the backend (`http://localhost:8080`).
2. Start the frontend dev server (`npm run dev` in `frontend/`).
3. Open the app at the URL Vite prints; API calls are proxied to the backend automatically.
