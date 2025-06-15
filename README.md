## 📚 Migration from Monolith to Microservices – Online Bookstore

This project consists of migrating a monolithic Java Spring Boot application of an online bookstore to a microservices-based architecture. The system was segmented following the principles of Domain-Driven Design (DDD), with each microservice having its own database. 

We implemented an API Gateway with **API Composition**, synchronous communication via **REST**, **CQRS** (Command Query Responsibility Segregation), and Saga coordination using **Eventuate Tram** (*Outbox + CDC*) to ensure consistency and atomicity in distributed operations.

The project is fully containerized with **Docker**, tested locally, and deployed on **Oracle Cloud**.
