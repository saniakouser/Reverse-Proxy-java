# Custom Reverse Proxy — Java + Spring Boot

A learning project implementing a custom HTTP reverse proxy without Spring Cloud Gateway.

## Features

- Configurable path-prefix routing
- Longest-prefix route matching
- Thread-safe round-robin upstream selection
- HTTP method, query string, request body and header forwarding
- Upstream response status, body and headers forwarding
- Basic upstream timeout and error handling
- JUnit tests for route resolution and upstream selection
- Dockerfile and Docker Compose example

## Requirements

- JDK 21
- Maven (or Maven Wrapper added by your IDE/Initializr)
- Node.js for the local test backends
- Docker (optional)

## Run the proxy locally

1. Start two test backends in separate terminals:

   Terminal A:
   ```bash
   PORT=8081 NAME=backend-A node backend.js
   ```

   Terminal B:
   ```bash
   PORT=8082 NAME=backend-B node backend.js
   ```

   On PowerShell:
   ```powershell
   $env:PORT="8081"; $env:NAME="backend-A"; node backend.js
   ```
   In the other terminal use port `8082` and name `backend-B`.

2. Start Spring Boot:
   ```bash
   mvn spring-boot:run
   ```
   Or, if you have the Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Test:
   ```bash
   curl -i http://localhost:8080/users/123
   curl -i http://localhost:8080/users/123?active=true
   curl -i -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name":"Sania"}'
   curl -i http://localhost:8080/unknown
   ```

Expected behavior: `/users` requests alternate between backend A and B; unknown paths return 404.

## Run tests

```bash
mvn test
```

## Build and run with Docker Compose

First build the application JAR:
```bash
mvn clean package
```
Then run:
```bash
docker compose up --build
```

Test:
```bash
curl -i http://localhost:8080/users/123
```

Stop:
```bash
docker compose down
```

## Architecture

- `ProxyController`: receives incoming requests.
- `RouteResolver`: matches the longest configured path prefix.
- `UpstreamSelector`: thread-safe round-robin selection per route.
- `ProxyForwarder`: forwards requests using Java's built-in `HttpClient`.
- `ProxyExceptionHandler`: basic upstream/configuration error mapping.
- `ProxyProperties`: binds `application.yml` configuration.

## Current limitations — do not treat as production-ready

This is a learning implementation. Before production use, add and test:
- validation/allowlisting of upstream URLs and SSRF protections
- hop-by-hop header handling including headers named by `Connection`
- streaming request and response bodies and size limits
- explicit timeout, connection failure and invalid URI error mapping
- correct handling of `HEAD`, `OPTIONS`, `TRACE`, and bodies on less-common methods
- health checks and exclusion of unhealthy upstreams
- bounded retries with method/idempotency awareness
- graceful shutdown, structured logging, metrics, authentication and rate limits
- integration tests for headers, query strings, request bodies and concurrent requests

The proxy intentionally does not use Spring Cloud Gateway; routing and upstream selection are custom code.
