# Resume Analyzer Platform

An end-to-end resume analysis application that combines a React web client with Spring Boot microservices. Users create an account, upload a resume, provide a target role or job description, and receive an ATS-style score, extracted skills, missing skills, education details, rule-based feedback, and an AI-generated improvement plan.

## Highlights

- JWT-based registration and login with BCrypt password hashing.
- API Gateway as the single entry point for frontend requests.
- PDF text extraction with Apache PDFBox.
- Rule-based ATS scoring using skill, education, and resume-length signals.
- Event-driven processing with Apache Kafka so upload, ATS analysis, and AI generation are decoupled.
- Gemini integration for role-aware resume recommendations, with a deterministic fallback when the external API is unavailable.
- React dashboard with protected routes, drag-and-drop upload, polling for asynchronous results, animated feedback, and charts.

## Architecture

```text
React/Vite frontend (5173)
          |
          v
API Gateway (8080)
   |          |          |
   v          v          v
Auth (8081) Resume (8082) AI (8083)
                |          ^
                | Kafka     |
                +----------+

MySQL databases: auth_db, resume_db, ai_db
Kafka topics: resume-uploaded, ai-suggestion
```

### Services

| Service | Port | Responsibility |
| --- | ---: | --- |
| `api-gateway` | 8080 | Routes `/api/auth`, `/api/resume`, and `/api/ai` requests and applies CORS policy. |
| `auth-service` | 8081 | User registration/login, BCrypt credential storage, JWT issuance and validation. |
| `resume-service` | 8082 | Validates and parses PDF uploads, persists resumes, computes ATS analysis, and publishes Kafka events. |
| `ai-service` | 8083 | Consumes analysis events, calls Gemini, persists suggestions, and exposes suggestion retrieval. |

## Request flow

1. The client registers or logs in through the gateway and stores the returned JWT.
2. The authenticated client uploads a PDF, target position, and optional job description.
3. `resume-service` extracts text, stores the resume with `PENDING` status, and publishes a `resume-uploaded` event.
4. A Kafka consumer performs skill extraction, required-skill matching, education detection, ATS scoring, and feedback generation.
5. The service stores the analysis, marks the resume `COMPLETED`, and publishes an `ai-suggestion` event.
6. `ai-service` consumes the event, requests a concise Gemini coaching response, stores it, and exposes it through `/api/ai/suggestion/{resumeId}`.
7. The frontend polls for the analysis and AI suggestion, then renders the results dashboard.

## Technology stack

- **Frontend:** React, Vite, React Router, Axios, React Hook Form, React Dropzone, Tailwind CSS, Framer Motion, Recharts
- **Backend:** Java 21, Spring Boot 3.3.5, Spring Security, Spring Data JPA, Spring Cloud Gateway MVC
- **Messaging:** Apache Kafka 3.7+
- **Persistence:** MySQL 8+
- **Document processing:** Apache PDFBox 3.0.3
- **AI:** Google Gemini API via Spring WebClient
- **Build tools:** Maven 3.9+, npm

## Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+
- MySQL 8+
- Apache Kafka 3.7+ (with ZooKeeper, matching the current local startup script)
- A Gemini API key for AI suggestions

## Configuration

Create a local `application.properties` for each backend service from its example file:

- `resume-analyzer-platform/auth-service/src/main/resources/application.properties.example`
- `resume-analyzer-platform/resume-service/src/main/resources/application.properties.example`
- `resume-analyzer-platform/api-gateway/src/main/resources/application.properties.example`

The AI service currently has a local `application.properties`; keep credentials out of source control and provide `gemini.api.key`, database credentials, and JWT settings through a local or environment-specific configuration.

The default local ports and databases are:

| Component | Default |
| --- | --- |
| Frontend | `http://localhost:5173` |
| Gateway | `http://localhost:8080` |
| Auth DB | `auth_db` |
| Resume DB | `resume_db` in the example configuration |
| AI DB | `ai_db` |
| Kafka | `localhost:9092` |

## Run locally

### 1. Start infrastructure

Start MySQL, ZooKeeper, and Kafka. The repository includes a Windows convenience script that assumes Kafka is installed at `C:\kafka\kafka_2.13-3.7.0`:

```bat
start-all.bat
```

If your installation paths differ, start each process manually or update the paths in `start-all.bat`.

### 2. Start the backend

From `resume-analyzer-platform`:

```bash
mvn clean install
mvn -pl auth-service spring-boot:run
mvn -pl api-gateway spring-boot:run
mvn -pl resume-service spring-boot:run
mvn -pl ai-service spring-boot:run
```

### 3. Start the frontend

```bash
cd resume-analyzer-frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## API overview

All calls are made through `http://localhost:8080`.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Create a user and return a JWT. |
| `POST` | `/api/auth/login` | Authenticate a user and return a JWT. |
| `POST` | `/api/resume/upload` | Upload a PDF as multipart form data (`file`, `userEmail`, optional `targetPosition`, `jobDescription`). |
| `GET` | `/api/resume/analysis/{resumeId}` | Retrieve ATS analysis and resume status. |
| `GET` | `/api/ai/suggestion/{resumeId}` | Retrieve the persisted AI suggestion. |

Send the JWT returned by registration/login as:

```http
Authorization: Bearer <token>
```

## Scoring model

The current ATS score is intentionally transparent and heuristic:

- Up to 60 points for matching a fixed required-skill set (`java`, `spring boot`, `mysql`, `git`, `rest api`).
- 20 points when education-related keywords are detected.
- Up to 20 points based on extracted resume word count.
- Feedback is generated from score bands and missing required skills.

This is a baseline analyzer, not a replacement for a commercial ATS or recruiter review.

## Verification

Run backend tests from `resume-analyzer-platform`:

```bash
mvn test
```

Run frontend lint/build checks:

```bash
cd resume-analyzer-frontend
npm run lint
npm run build
```

## Current limitations and next steps

- Resume parsing is implemented for text-based PDFs; scanned/image-only PDFs are rejected when no text can be extracted.
- The backend upload endpoint accepts PDF only. The frontend currently advertises DOCX in its dropzone and should be aligned with the backend before claiming DOCX support.
- Kafka and MySQL are configured for local development; deployment automation, service discovery, observability, retries, and production secret management remain future work.
- Resume ownership is currently passed as `userEmail`; authorization checks should be added so users can only retrieve their own analyses.
- JWT enforcement is implemented in the auth service and propagated by the client; downstream resume/AI services should also validate the token and principal before production use.
- The frontend results page contains presentation fallbacks for fields not yet returned by the backend (for example recruiter match/readiness/quality).

## Repository layout

```text
resume-analyzer-frontend/      React/Vite client
resume-analyzer-platform/      Maven parent and Spring Boot services
  api-gateway/
  auth-service/
  resume-service/
  ai-service/
start-all.bat                  Local Windows startup helper
```
