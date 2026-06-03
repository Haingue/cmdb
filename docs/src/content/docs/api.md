---
title: API Reference
description: CMDB API documentation
---

# API Reference

This document provides an overview of the CMDB API endpoints. For detailed API documentation, see the OpenAPI/Swagger specifications for each service.

## Base URLs

| Service | Base URL | Port | Protocol |
|---------|----------|------|----------|
| BFF (Production) | `https://api.cmdb.example.com` | 443 | HTTPS |
| BFF (Development) | `http://localhost:8080` | 8080 | HTTP |
| Inventory Service | `http://localhost:8081` | 8081 | HTTP |

## Authentication

The CMDB API uses token-based authentication.

### Authentication Methods

#### 1. Bearer Token (JWT)

Include the token in the `Authorization` header:

```
Authorization: Bearer <your-token>
```

#### 2. API Key

Include the API key in the `X-API-Key` header:

```
X-API-Key: <your-api-key>
```

### Authentication Flow

```
1. Client sends credentials to /auth/login
2. Server returns JWT token
3. Client includes token in subsequent requests
4. Server validates token on each request
```

## BFF API Endpoints

The BFF (Backend For Frontend) provides the main API for the frontend application.

### GraphQL API

**Endpoint**: `/graphql`

**Methods**: `POST`

**Content-Type**: `application/json`

#### Example Query

```graphql
query GetEntities($first: Int, $after: String) {
  entities(first: $first, after: $after) {
    pageInfo {
      hasNextPage
      endCursor
    }
    edges {
      node {
        id
        uuid
        name
        type
        description
        version
        createdAt
        updatedAt
        components {
          id
          name
          type
        }
        events {
          id
          type
          description
          timestamp
        }
      }
    }
  }
}
```

#### Variables

```json
{
  "first": 10,
  "after": null
}
```

#### Response

```json
{
  "data": {
    "entities": {
      "pageInfo": {
        "hasNextPage": false,
        "endCursor": "..."
      },
      "edges": [
        {
          "node": {
            "id": "...",
            "uuid": "550e8400-e29b-41d4-a716-446655440000",
            "name": "Production Server 1",
            "type": "HARDWARE",
            "description": "Main production server",
            "version": "1.0",
            "createdAt": "2025-01-01T10:00:00Z",
            "updatedAt": "2025-06-01T15:30:00Z",
            "components": [...],
            "events": [...]
          }
        }
      ]
    }
  }
}
```

### REST API

#### Entities

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/entities` | List all entities |
| `GET` | `/api/v1/entities/{id}` | Get a specific entity |
| `POST` | `/api/v1/entities` | Create a new entity |
| `PUT` | `/api/v1/entities/{id}` | Update an entity |
| `DELETE` | `/api/v1/entities/{id}` | Delete an entity |
| `GET` | `/api/v1/entities/search` | Search entities |

#### Projects

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/projects` | List all projects |
| `GET` | `/api/v1/projects/{id}` | Get a specific project |
| `POST` | `/api/v1/projects` | Create a new project |
| `PUT` | `/api/v1/projects/{id}` | Update a project |
| `DELETE` | `/api/v1/projects/{id}` | Delete a project |

#### Environments

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/environments` | List all environments |
| `GET` | `/api/v1/environments/{id}` | Get a specific environment |
| `POST` | `/api/v1/environments` | Create a new environment |
| `PUT` | `/api/v1/environments/{id}` | Update an environment |
| `DELETE` | `/api/v1/environments/{id}` | Delete an environment |

#### Components

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/components` | List all components |
| `GET` | `/api/v1/components/{id}` | Get a specific component |
| `POST` | `/api/v1/components` | Create a new component |
| `PUT` | `/api/v1/components/{id}` | Update a component |
| `DELETE` | `/api/v1/components/{id}` | Delete a component |

#### Technologies

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/technologies` | List all technologies |
| `GET` | `/api/v1/technologies/{id}` | Get a specific technology |
| `POST` | `/api/v1/technologies` | Create a new technology |
| `PUT` | `/api/v1/technologies/{id}` | Update a technology |
| `DELETE` | `/api/v1/technologies/{id}` | Delete a technology |

### Aggregators API

#### GitHub Analyzer

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/aggregators/github/repos` | List analyzed repositories |
| `GET` | `/api/v1/aggregators/github/repos/{owner}/{repo}` | Get repository details |
| `POST` | `/api/v1/aggregators/github/sync` | Trigger synchronization |
| `GET` | `/api/v1/aggregators/github/stats` | Get analyzer statistics |

#### Syslog Server

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/aggregators/syslog/status` | Get server status |
| `GET` | `/api/v1/aggregators/syslog/logs` | View recent logs |
| `POST` | `/api/v1/aggregators/syslog/test` | Send a test message |

## Request/Response Examples

### Create Entity

**Request**
```http
POST /api/v1/entities HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Development Server",
  "type": "HARDWARE",
  "description": "Development server for testing",
  "environment": {
    "type": "DEV",
    "location": "Paris"
  },
  "metadata": {
    "ipAddress": "192.168.1.100",
    "hostname": "dev-server-01"
  }
}
```

**Response**
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": "...",
  "uuid": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Development Server",
  "type": "HARDWARE",
  "description": "Development server for testing",
  "version": 1,
  "environment": {
    "type": "DEV",
    "location": "Paris"
  },
  "metadata": {
    "ipAddress": "192.168.1.100",
    "hostname": "dev-server-01"
  },
  "createdAt": "2025-06-03T10:00:00Z",
  "updatedAt": "2025-06-03T10:00:00Z"
}
```

### Update Entity

**Request**
```http
PUT /api/v1/entities/550e8400-e29b-41d4-a716-446655440001 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Development Server",
  "description": "Development server for testing - Updated",
  "metadata": {
    "ipAddress": "192.168.1.100",
    "hostname": "dev-server-01",
    "os": "Ubuntu 22.04"
  }
}
```

**Response**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": "...",
  "uuid": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Development Server",
  "type": "HARDWARE",
  "description": "Development server for testing - Updated",
  "version": 2,
  "environment": {
    "type": "DEV",
    "location": "Paris"
  },
  "metadata": {
    "ipAddress": "192.168.1.100",
    "hostname": "dev-server-01",
    "os": "Ubuntu 22.04"
  },
  "createdAt": "2025-06-03T10:00:00Z",
  "updatedAt": "2025-06-03T10:30:00Z"
}
```

### Search Entities

**Request**
```http
GET /api/v1/entities/search?name=server&type=HARDWARE&page=0&size=20 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "content": [
    {
      "id": "...",
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Production Server 1",
      "type": "HARDWARE",
      "description": "Main production server",
      "version": 1
    },
    {
      "id": "...",
      "uuid": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Development Server",
      "type": "HARDWARE",
      "description": "Development server for testing",
      "version": 2
    }
  ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

## Error Responses

### Standard Error Format

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": {
      "field": "fieldName",
      "rejectedValue": "invalidValue",
      "message": "Field-specific error message"
    },
    "timestamp": "2025-06-03T10:00:00Z",
    "path": "/api/v1/entities"
  }
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Authentication required |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `CONFLICT` | 409 | Resource already exists |
| `INTERNAL_ERROR` | 500 | Internal server error |
| `SERVICE_UNAVAILABLE` | 503 | Service temporarily unavailable |

## Rate Limiting

API endpoints may be rate limited to prevent abuse.

**Rate Limit Headers**:

- `X-RateLimit-Limit`: Maximum requests per period
- `X-RateLimit-Remaining`: Requests remaining in current period
- `X-RateLimit-Reset`: Time when rate limit resets (Unix timestamp)

**Rate Limit Response** (when limit exceeded):

```http
HTTP/1.1 429 Too Many Requests
Content-Type: application/json
Retry-After: 60

{
  "error": {
    "code": "RATE_LIMITED",
    "message": "Too many requests",
    "retryAfter": 60
  }
}
```

## Pagination

Most list endpoints support pagination using the following parameters:

| Parameter | Type | Description | Default |
|-----------|------|-------------|---------|
| `page` | integer | Page number (zero-based) | 0 |
| `size` | integer | Page size | 20 |
| `sort` | string | Sort field and direction (e.g., `name,asc` or `createdAt,desc`) | - |

**Response Format**:

```json
{
  "content": [...],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

## Versioning

The API uses **semantic versioning** in the URL path:

```
/api/v1/...  # Version 1 (current)
/api/v2/...  # Version 2 (future)
```

**Version Support Policy**:
- Current version is always v1
- Previous versions are supported for at least 6 months after a new major version is released
- Deprecation notices will be provided in advance

## OpenAPI Specification

Each service provides an OpenAPI (Swagger) specification:

- **BFF**: `/v3/api-docs` or `/swagger-ui.html`
- **Inventory Service**: `/inventory/v3/api-docs`
- **GitHub Analyzer**: `/github/v3/api-docs`
- **Syslog Server**: `/syslog/v3/api-docs`

You can use these specifications to:
- Explore the API interactively using Swagger UI
- Generate client SDKs
- Generate server stubs
- Integrate with API documentation tools

## SDKs and Client Libraries

### JavaScript/TypeScript

A TypeScript client library is available:

```bash
npm install @cmdb/client
```

**Usage**:

```typescript
import { CMDBClient } from '@cmdb/client';

const client = new CMDBClient({
  baseUrl: 'https://api.cmdb.example.com',
  token: 'your-token'
});

// Get entities
const entities = await client.entities.list();

// Create entity
const newEntity = await client.entities.create({
  name: 'My Server',
  type: 'HARDWARE'
});
```

### Java

A Java client library is also available:

**Maven**:
```xml
<dependency>
    <groupId>com.haingue.cmdb</groupId>
    <artifactId>cmdb-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Usage**:
```java
CMDBClient client = new CMDBClient("https://api.cmdb.example.com", "your-token");

// Get entities
List<Entity> entities = client.getEntities();

// Create entity
Entity newEntity = client.createEntity(new EntityCreateRequest("My Server", EntityType.HARDWARE));
```

## Webhooks

The CMDB supports webhooks for event-driven integrations.

### Event Types

- `entity.created`
- `entity.updated`
- `entity.deleted`
- `project.created`
- `project.updated`
- `project.deleted`

### Webhook Configuration

```json
{
  "url": "https://your-server.com/webhook",
  "events": ["entity.created", "entity.updated"],
  "secret": "your-secret-key",
  "active": true
}
```

### Webhook Payload

```json
{
  "event": "entity.created",
  "timestamp": "2025-06-03T10:00:00Z",
  "data": {
    "entity": {
      "id": "...",
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "name": "New Entity",
      "type": "HARDWARE",
      "createdAt": "2025-06-03T10:00:00Z"
    }
  },
  "signature": "sha256-hash-of-payload-with-secret"
}
```
