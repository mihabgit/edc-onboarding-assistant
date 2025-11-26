# EDC Onboarding Assistant

A Spring Boot REST API service for simplifying EDC (Eclipse Dataspace Connector) asset registration and management.

## Features

- **Asset Registration**: Simplified interface for registering assets in EDC
- **Asset Discovery**: Get all the registered assets
- **Health Monitoring**: Check EDC connector health and system status
- **Asset Validation**: Verify asset registration and policy attachment (upcoming)

## Prerequisites

- Java 17 or higher
- Gradle 7.6+
- EDC Connector instance

## Quick Start

1. **Clone and build**
```
git clone https://github.com/mihabgit/edc-onboarding-assistant
cd edc-onboarding-assistant
./gradlew build
```

2. **Run the application**
```
./gradlew bootRun
```

3. **Access the API**
```
Application: http://localhost:8090
Swagger UI: http://localhost:8090/swagger-ui/index.html
```

## API Endpoints

### Asset Management

- ```POST /api/v1/assets``` - Register a new asset
- ```GET /api/v1/assets``` - List all assets
- ```GET /api/v1/assets/{assetId}``` - Get asset details

### Health

- ```GET /api/v1/health``` - System health check

## Example Usage

```
curl -X POST http://localhost:8090/api/v1/assets \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Vehicle Telematics Data",
      "description": "Real-tiem vehicle sensor data",
      "contentType": "application/json",
      "dataAddress": {
        "type": "HttpData",
        "baseUrl": "https://supplier.example.com/api/telematics"
      },
      "accessPolicy": {
        "allowedCompanies": ["BPNL000000000001", "BPNL0000000001"],
        "usagePurpose": "quality.analysis"
      }
    }'
```

```
curl -X 'GET' \
  'http://localhost:8090/api/v1/assets/123' \
  -H 'accept: */*'
```