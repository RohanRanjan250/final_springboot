# 🛒 Online Shopping Backend

A **robust, production-grade RESTful API** built with **Spring Boot 3** to power modern e-commerce experiences.  
This system handles the complete lifecycle of online retail—from **secure user onboarding** and **dynamic product catalogs** to **real-time inventory management** and **integrated payment processing**.

---

## 🌟 Overview

This project was engineered with a strong focus on **scalability, security, and developer experience**.  
It goes beyond basic CRUD by implementing real-world **production patterns**:

- **⚡ Performance:** Layered caching with **Caffeine**
- **🛡️ Resilience:** API rate limiting using **Bucket4j** to prevent brute-force attacks and scraping
- **🔒 Data Integrity:** Transactional workflows ensuring inventory and orders remain perfectly in sync

---

## 🛠️ Technology Stack

| Component       | Technology                     | Role                                      |
|-----------------|--------------------------------|-------------------------------------------|
| Framework       | Spring Boot 3.2.1              | Core application logic                    |
| Database        | PostgreSQL                     | Relational data persistence               |
| Security        | Spring Security + JWT           | Stateless authentication & RBAC           |
| Caching         | Caffeine                       | High-performance in-memory caching        |
| Payments        | Razorpay                       | Secure payment gateway integration        |
| Documentation   | Swagger / OpenAPI              | Interactive API testing & documentation   |
| Validation      | Jakarta Validation             | Strict input data integrity               |

---

## 🚀 Key Features

### 🔐 Secure Commerce

- **JWT Authentication:** Stateless login with role-based access (USER / ADMIN)
- **Rate Limiting:** Token-bucket algorithm via Bucket4j
- **Password Security:** BCrypt hashing for all user credentials

---

### 📦 Catalog & Inventory

- **Smart Search:** Advanced filtering, pagination, and sorting
- **Stock Guard:** Prevents adding out-of-stock items to carts
- **Automated Ratings:** Reviews automatically update average ratings

---

### 💳 Order Lifecycle

- **Cart Logic:** Persistent carts with stock validation at checkout
- **Payment Integration:** Razorpay-based digital payment flow
- **Email Notifications:** SMTP-based order confirmation emails

---

## 📁 Project Architecture

The application follows a **Layered Architecture** to ensure maintainability and separation of concerns:

```text
src/main/java/com/shopping/
├── config/      # Security, Cache, and OpenAPI configurations
├── controller/  # REST API Endpoints (Entry Layer)
├── dto/         # Data Transfer Objects (Payload Layer)
├── model/       # JPA Entities (Database Layer)
├── repository/  # Spring Data JPA (Data Access)
├── service/     # Business Logic & Third-party Integrations
└── exception/   # Global Exception Handling & custom errors
```

🔧 Getting Started
Prerequisites

Java 17 or higher

PostgreSQL 12+

Maven (dependency management)

1️⃣ Database Setup

Create a new PostgreSQL database:

CREATE DATABASE shopping_db;

2️⃣ Configuration

Update src/main/resources/application.yml with:

Database: Username & password

Mail: Gmail App Password

Razorpay: Test / Live API keys

3️⃣ Run the Application
mvn clean install
mvn spring-boot:run


The server will start at:

http://localhost:8080

📚 API Exploration

The API is fully documented using Swagger UI.

Start the application

Open:

http://localhost:8080/swagger-ui.html


Authenticate:

Register/Login via /api/auth

Copy the JWT token

Click Authorize and paste:

Bearer <your_token>

🔒 Security & Optimization Features

Global Exception Handler: Clean, consistent JSON error responses

Asynchronous Tasks: Email notifications processed in background threads

Input Validation: Controller-level validation prevents malformed data

✅ Production Ready

This backend is designed to be:
Secure
Scalable
Maintainable
Industry-aligned
Perfect for real-world e-commerce platforms and portfolio-grade backend projects 🚀
