# Fitness Tracker Microservices

This project is a practice implementation of a microservice architecture using Spring Boot. It includes the following services:

- **API Gateway**: Routes requests to the appropriate services.
- **Discovery Server**: Service registry using Spring Cloud Netflix Eureka.
- **Auth Server**: Handles authentication and token issuance.

## Prerequisites
- Java 17+
- Gradle
- IntelliJ / VSCode / Any Java IDE

## Getting Started

Start the services in this order:
1. **discovery-server**
2. **auth-server**
3. **api-gateway**

## Folder Structure

```plaintext
├── api-gateway
│   └── src
├── auth-server
│   └── src
└── discovery-server
    └── src
