# 🛒 Resilient E-commerce Microservices

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java&logoColor=white)](https://www.oracle.com/java/)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)  
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.x-green?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)  
[![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)  
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue?logo=docker&logoColor=white)](https://www.docker.com/)  

A microservices-based e-commerce platform built with **Spring Boot, Spring Cloud, Eureka, Feign, Resilience4j, API Gateway, Config Server, and MySQL**.  
This project demonstrates scalable, fault-tolerant, and secure service-to-service communication in a distributed system.

---

## 🚀 Features

- Centralized Config Management with **Spring Cloud Config Server**
- **API Gateway** for unified routing, authentication, and request forwarding
- **Service Discovery** with Eureka
- Resilient Communication with **Feign Clients + Resilience4j**
- **Circuit Breaker** (Resilience4j)
- **Retry with Exponential Backoff**
- **Fallbacks** for graceful degradation
- Authentication & Authorization Service *(in progress)*
- **Microservices Architecture**:
  - **Shop Service** – Handles cart, orders, and payments
  - **Inventory Service** – product stock management
  - **Wallet Service** – Manages user wallets & transactions
  - **Auth Service** – User authentication/authorization *(JWT planned)*
- Database: **MySQL**
- REST APIs with **Swagger/OpenAPI**
- **Dockerized** for containerized deployment

---

## 🛠 Tech Stack
- **Backend Framework:** Spring Boot, Spring Cloud
- **Resilience & Fault Tolerance:** Resilience4j
- **Service Discovery & Load Balancing:** Eureka
- **Database:** MySQL
- **API Gateway:** Spring Cloud Gateway
- **Containerization:** Docker, Docker Compose
- **Documentation:** Swagger / OpenAPI


---

## 🏗️ Architecture
                    +---------------------+
                    |  Config Server      |
                    +---------------------+
                             │
                             ▼
                    +---------------------+
                    |    Eureka Server    |
                    +---------------------+
                             ▲
                             │ Service Discovery + Load Balancing
                   +---------------------+
                   |    API Gateway      |
                   +---------------------+
                 /          |          \
                /           |           \
               ▼            ▼            ▼
    +----------------+  +----------------+  +----------------+
    |  Shop Service  |  | Inventory Svc  |  | Wallet Service |
    +----------------+  +----------------+  +----------------+
             │                 │                   │
             │                 │                   │
             │                 │                   │
             │                 │                   │
             │                 │                   │
             │                 ▼                   │
             ▼                                    ▼
    Payments, Orders, Cart              Wallets, Transactions


---

## ⚙️ Prerequisites
- Java Development Kit (JDK) 17+
- Apache Maven 3.6+
- Git
- (Optional) Docker Desktop

---

## 🔐 Resilience Highlights

- **Wallet Client** → Circuit Breaker + Retry on withdrawal  
- **Inventory Client** → Circuit Breaker + Retry on stock check  
- **Fallbacks** → Graceful degradation *(cancel order / skip withdrawal)*  

---

## ▶️ Running Locally

1. **Clone the Repository**
    git clone https://github.com/Mazen-Abdelfattah/E-commerce_Microservices_Project.git
    cd E-commerce_Microservices_Project

2. **Edit the configuration**
    Edit the ports in application.properties

3. **(Optional) Run with Docker**
    Download Docker desktop https://www.docker.com/products/docker-desktop/
    docker-compose up --build

    - If Docker is not used, ensure MySQL is installed and running locally with the correct schema before starting services.

4. **Start Config Server**
   cd config-server
   mvn spring-boot:run

5. **Start Eureka Server**
    cd eureka-server
    mvn spring-boot:run

6. **Start API Gateway**
    cd api-gateway
    mvn spring-boot:run

7. **Run Microservices (Shop, Inventory, Wallet, Auth)**
    cd <service-name>
    mvn spring-boot:run

---

## 📊 API Documentation (Swagger/OpenAPI)

[Wallet Service](http://localhost:8081/swagger-ui/index.html#/)

[Inventory Service](http://localhost:8082/swagger-ui/index.html#/)

[Shop Service](http://localhost:8083/swagger-ui/index.html#/)

---

## 🔌 Default Ports

| Service          | Default Port | Purpose                               |
|------------------|--------------|---------------------------------------|
| Naming Server    | 8761         | Service Registry (Eureka Dashboard)   |
| Config Server    | 8888         | Configuration Management              |
| API Gateway      | 8080         | Main Application Entry Point          |
| Wallet Service   | 8081         | Payment Processing                    |
| Inventory Service| 8082         | Inventory Management                  |
| Shop Service     | 8083         | Shopping Operations                   |

---

## 📄 License

This project is licensed under the **MIT License** – feel free to use, modify, and distribute.