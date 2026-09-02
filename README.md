# Midas Core

The Midas system is a high-profile initiative responsible for processing financial transactions at scale. 

Midas Core is the service responsible for receiving, validating and recording the financial transactions.

## Implementation

- Integrated Kafka into a Spring Boot microservice to consume and deserialize high-volume transaction messages using a configurable topic and embedded Kafka test framework.
- Implemented transaction validation and persistence logic with Spring Data JPA and an H2 SQL database, including entity modelling and balance updates across relational User records.
- Connected the service to an external REST Incentive API using RestTemplate, processing incentive responses and incorporating them into transactional workflows.
- Developed a REST endpoint for querying user balances, returning JSON responses through a Spring controller while maintaining clean architectural boundaries.
- Verified system behaviour using Maven test suites and debugger-driven inspection, ensuring reliability across message ingestion, database operations, and external API interactions.
