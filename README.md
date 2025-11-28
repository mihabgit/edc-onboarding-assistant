# EDC Onboarding Assistant

A Spring Boot REST API service for simplifying EDC (Eclipse Dataspace Connector) asset registration and management.

## Features

- **Asset Registration**: Simplified interface for registering assets in EDC
- **Asset Discovery**: Get all the registered assets
- **Health Monitoring**: Check EDC connector health and system status
- **Asset Validation**: Verify asset registration and policy attachment (upcoming)

## Prerequisites

- Java 25
- Gradle 7.6+
- Git
- Docker

## Quick Start (On Docker)

1. **Step 1:**
Clone and build edc-connector repository
```
git clone https://github.com/mihabgit/edc-connector.git
cd edc-connector
```
Make sure docker is running on your computer

Run docker build command: ```docker build -t edc-connector:latest .```

After successfully build the edc-connector follow below steps

2. **Clone and build the edc-onboarding-assistant app**
```
git clone https://github.com/mihabgit/edc-onboarding-assistant
cd edc-onboarding-assistant
```
Run run this command: ```docker compose up --build```

Both application should running.

3. **Access the API**

You can access the API here: [Swagger UI](http://localhost:8090/swagger-ui/index.html)

## API Endpoints

### Asset Management

- ```POST /api/v1/assets``` - Register a new asset
- ```GET /api/v1/assets``` - List all assets
- ```GET /api/v1/assets/{assetId}``` - Get asset details

### Health Check

- ```GET /api/v1/health``` - System health check

## Example Usage

### Create Asset

Request:
```
curl -X 'POST' \
  'http://localhost:8090/api/v1/assets' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "string",
  "description": "string",
  "contentType": "string",
  "dataAddress": {
    "type": "string",
    "baseUrl": "https://abcd.com"
  },
  "accessPolicy": {
    "allowedCompanies": [
      "string"
    ],
    "usagePurpose": "string"
  }
}'
```
Response:

```
{
  "assetId": "asset-366c062e",
  "status": "published",
  "catalogUrl": "http://localhost:8090/api/catalog",
  "message": "Asset successfully registered and published"
}
```
### Get all asset list:

Request:
```
curl -X 'GET' \
  'http://localhost:8090/api/v1/assets?page=0&limit=50' \
  -H 'accept: */*'
```
Response
```
{
  "assets": [
    {
      "properties": {
        "id": "asset-366c062e"
      },
      "@id": "asset-366c062e",
      "@type": "Asset",
      "dataAddress": {
        "@type": "DataAddress",
        "type": "HttpData",
        "baseUrl": "https://abcd.com"
      },
      "@context": {
        "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
        "edc": "https://w3id.org/edc/v0.0.1/ns/",
        "odrl": "http://www.w3.org/ns/odrl/2/"
      }
    }
  ],
  "total": 1
}
```
### Get asset by assetId

Request
```
curl -X 'GET' \
  'http://localhost:8090/api/v1/assets/asset-366c062e' \
  -H 'accept: */*'
```

Response
```
{
  "properties": {
    "id": "asset-366c062e"
  },
  "@id": "asset-366c062e",
  "@type": "Asset",
  "dataAddress": {
    "@type": "DataAddress",
    "type": "HttpData",
    "baseUrl": "https://abcd.com"
  },
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  }
}
```

### Health Check
Request
```
curl -X 'GET' \
  'http://localhost:8090/api/v1/health' \
  -H 'accept: */*'
```

Response
```
{
  "status": "healthy",
  "timestamp": "2025-11-28T08:45:51.715105195",
  "checks": {
    "database": "up",
    "edcManagementStatus": "up",
    "edcDataPlane": "up"
  },
  "statistics": {
    "totalAssets": 1,
    "totalPolicies": 1
  }
}
```

Hope, everything works well.