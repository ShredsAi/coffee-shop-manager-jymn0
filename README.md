# Inventory Shred

## Description
This service manages and tracks stock levels in real time, ensuring accurate inventory data consistency. It monitors thresholds to generate low-supply alerts, decrements stock when receiving orders, and integrates with external services for order updates. By maintaining data accuracy and preventing overselling, it adds value to the overall operations of the platform.

## Setup
1. Clone the repository.
2. Navigate to the project root.
3. Build using Maven: `mvn clean package`.
4. Run using Spring Boot: `mvn spring-boot:run` or `java -jar target/inventory-shred-1.0.0-SNAPSHOT.jar`.

## Usage
• Access the service endpoints to perform inventory operations.
• Point your browser or API client at `http://localhost:8080/inventory/...` endpoints.

## Requirements
• Java 11 or higher.
• Maven 3.6+.
• Docker (optional if you want to run via Docker).
• PostgreSQL version 12 or higher (if not using an in-memory DB).

## Configuration
• Configuration is located in `application.yml` under `src/main/resources`.
• Database properties, logging level, and other settings can be configured there.

## Additional Notes
• This container can be built using the provided Dockerfile. Ensure Docker is installed and run `docker build -t inventory-shred .`.
• For more advanced usage, adapt the Dockerfile or application properties as needed.
