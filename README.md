
# API Lifecycle Manager

A centralized API lifecycle management system that helps organizations manage API versions, control consumer access, monitor API usage, and handle API deprecation and retirement.

## Overview

As applications evolve, APIs need to be updated to support new requirements. However, immediately replacing older API versions can break applications that still depend on them.

API Lifecycle Manager addresses this problem by providing a centralized platform to register API versions, manage their lifecycle, onboard consumers, and facilitate controlled migration between versions.

## Key Features

- **API Registration:** Register APIs with details such as API name, version, and owner.
- **Version Management:** Maintain and manage multiple versions of the same API.
- **API Deprecation:** Mark API versions as deprecated, specify sunset dates, and identify replacement versions.
- **API Retirement:** Retire deprecated API versions while enforcing sunset-date validation.
- **Consumer Management:** Register applications that consume the available APIs.
- **Access Control:** Grant consumers access to specific API versions.
- **API Key Authentication:** Validate consumer API keys when accessing runtime endpoints.
- **Usage Monitoring:** Track API usage through consumer activity and usage reports.
- **Deprecation Notifications:** Return HTTP response headers containing deprecation status, sunset dates, and replacement-version links.

## Application Workflow

1. An administrator registers an API and its version.
2. Consumers register with the platform.
3. The administrator grants consumers access to specific API versions.
4. When a newer version becomes available, the administrator can mark the older version as deprecated.
5. A sunset date and replacement version are specified.
6. Runtime responses communicate deprecation information through HTTP headers.
7. Consumers can migrate to the replacement version.
8. The administrator can retire the older version after its sunset date.

## Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React.js |
| Backend | Java, Spring Boot |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Security | Spring Security, HTTP Basic Authentication, API Key Authentication |
| Database Migration | Flyway |
| API Documentation | Springdoc OpenAPI / Swagger |
| Deployment | Render |

## Architecture

The application follows a layered backend architecture.

- **Controller Layer:** Handles incoming HTTP requests and returns responses.
- **Service Layer:** Contains business logic and lifecycle validation.
- **Repository Layer:** Handles database interactions using Spring Data JPA.
- **Entity Layer:** Represents database entities and their relationships.
- **Frontend:** Provides an interface for API registration, lifecycle operations, consumer management, and usage monitoring.

## API Lifecycle

```text
       API Registration
              |
              v
           ACTIVE
              |
              v
         DEPRECATED
              |
       Sunset Date Reached
              |
              v
           RETIRED
```

## Security and Access Management

- Administrative operations are protected using role-based authorization.
- Consumer access is managed at the individual API-version level.
- Runtime endpoints validate consumer API keys.
- CORS configuration controls permitted frontend origins.

## Running Locally

### Prerequisites

- Java 21
- Node.js and npm
- PostgreSQL
- Maven

### 1. Clone the repository

```bash
git clone https://github.com/abhilashab4/Api-Lifecycle-Manager.git
cd Api-Lifecycle-Manager
```

### 2. Configure the backend

Configure the following environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5432/apilifecycle
DB_USERNAME=postgres
DB_PASSWORD=your_database_password
ADMIN_PASSWORD=your_admin_password
```

### 3. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend starts on:

```text
http://localhost:8080
```

### 4. Start the frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on the Vite development server, typically:

```text
http://localhost:5173
```

## API Documentation

Once the backend is running, access Swagger UI at:

```text
http://localhost:8080/swagger-ui.html
```

## Project Objective

The objective of this project is to demonstrate centralized API governance and lifecycle management, enabling organizations to manage API versions, control consumer access, monitor usage, and facilitate structured migration from deprecated versions.
