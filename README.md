# Wallet Service
This is the backend service for a wallet management system, which enables clients to perform balance operations and retrieve wallet information via REST API.
The application is designed to handle high concurrency workloads (up to 1000 RPS on a single wallet) while ensuring data consistency and reliability.

## Features
### Wallet Operations

Users can perform balance operations on wallets:
* DEPOSIT — increase wallet balance
* WITHDRAW — decrease wallet balance
All operations are processed atomically with proper handling of concurrent requests.

### Balance Retrieval
Users can retrieve the current balance of a wallet using its UUID.

### Concurrency Safety
The system is designed to handle high concurrent load on the same wallet using database transaction mechanisms and proper locking strategies.

### Validation & Error Handling
The application validates requests and returns structured error responses for cases such as:
* invalid JSON
* wallet not found
* insufficient funds
* validation errors

### Database Migrations
Database schema is managed using Liquibase migrations, ensuring versioned and repeatable database changes.

### Containerized Deployment
The application and database run inside Docker containers and are orchestrated with Docker Compose.

## Technologies
* Java 17
* Spring Boot 3
* Spring Data JPA
* Hibernate
* Liquibase
* PostgreSQL
* Docker
* Maven
* Lombok
* Testcontainers
* JUnit 5

## Database
The application uses PostgreSQL as the primary database.
Database schema is automatically created and versioned via Liquibase migrations at application startup.

Main entity:
### Wallet
Fields:
* id (UUID)
* balance (numeric)
