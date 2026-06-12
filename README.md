# AI-Powered Resume Analyzer Platform

Microservices-based resume analysis platform with ATS scoring and AI suggestions.

## Tech Stack
- Java 21 / Spring Boot 3.3.5
- Spring Cloud Gateway 2023.0.3
- Spring Security + JWT
- Apache Kafka
- Apache PDFBox
- MySQL
- React.js + Tailwind CSS
- Gemini API
- Docker + Docker Compose

## Microservices

| Service | Port | Description |
|---|---|---|
| api-gateway | 8080 | Single entry point, routes requests |
| auth-service | 8081 | JWT authentication, user management |
| resume-service | 8082 | PDF parsing, ATS scoring |
| ai-service | 8083 | Gemini AI suggestions |

## Setup Instructions

### Prerequisites
- Java 21+
- Maven 3.9+
- MySQL 8.x
- Apache Kafka 3.7+

### Configuration
Each service has an `application.properties.example` file.
Copy it and rename to `application.properties`, then fill your values.

## Run Services
### Start Zookeeper

```bash
bin/windows/zookeeper-server-start.bat config/zookeeper.properties
```

### Start Kafka

```bash
bin/windows/kafka-server-start.bat config/server.properties
```

### Start Services

```bash
cd auth-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd resume-service && mvn spring-boot:run
```

## Recommended Startup Order

1. Zookeeper
2. Kafka
3. Auth Service
4. API Gateway
5. Resume Service
## Progress
- [x] Day 1 — Auth service + API Gateway
- [x] Day 2 — Resume service + PDF parsing + Kafka (Phase 2)
- [x] Day 3 — ATS scoring completion
- [x] Day 4 — Kafka Integration and setup
- [x] Day 5 — AI service
- [ ] Day 6 — Frontend