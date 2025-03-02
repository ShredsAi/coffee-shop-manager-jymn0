# Payment Service

## Project Description
This project is a Payment Service Shred designed to handle the core functionalities of payment lifecycle management, including creating payments, processing them through external gateways, logging transactions, and notifying other services.

## Setup Instructions
1. Clone this repository.
2. Navigate to the root directory.
3. Run 'mvn clean install' to build the project.
4. Use Docker or a standalone Java environment (Java 11) to run the jar.

## Usage Guide
1. An endpoint for creating payments is available at POST /payments.
2. An endpoint for retrieving payment status is available at GET /payments/{paymentId}.
3. Additional endpoints or features can be discovered by reading the code or using an API exploration tool.

## Deployment Guide
1. Build the Docker image using the provided Dockerfile.
2. Deploy the container in your preferred environment (e.g., Kubernetes or Docker Compose).
3. Ensure database connectivity to PostgreSQL and that environment variables are properly configured.

## Security Notes
- Sensitive data (e.g., credit card information) must be encrypted and handled according to PCI-DSS rules.
- Limit exposure of sensitive endpoints and logs containing payment details.
