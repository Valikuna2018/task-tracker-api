# Task Tracker API

## Description

Task Tracker API is a RESTful Spring Boot application that allows users to manage projects and tasks in a collaborative environment. The application implements Role-Based Access Control (RBAC) using JWT authentication.

The project was developed to demonstrate clean architecture, Spring Boot, authentication and authorization, DTO mapping, validation, exception handling, and unit testing.

---

## Technologies Used

* Java 17
* Spring Boot 3
* Spring Security
* JWT Authentication
* Spring Data JPA (Hibernate)
* PostgreSQL
* MapStruct
* Lombok
* Swagger / OpenAPI
* JUnit 5
* Mockito

---

## How to Run the Application

### Prerequisites

* Java 17
* PostgreSQL
* Maven

### Database Configuration

Configure your PostgreSQL database credentials in:

`src/main/resources/application.properties`

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Start the Application

Clone the repository:

```bash
git clone <repository-url>
```

Navigate to the project folder:

```bash
cd task-tracker-api
```

Run the application:

```bash
mvn spring-boot:run
```

Or run the main application class directly from your IDE.

The application starts on:

```text
http://localhost:8081
```

---

## Authentication

The application uses JWT-based authentication.

### Register

Users can register using:

```text
POST /api/auth/register
```

### Login

Authenticate using:

```text
POST /api/auth/login
```

Successful authentication returns a JWT token.

Include the token in protected requests:

```text
Authorization: Bearer <jwt-token>
```

Swagger authorization is also supported.

---

## Roles and Permissions

### ADMIN

* Full access to all resources.
* Can manage all projects.
* Can manage all tasks.
* Can view all tasks.

### MANAGER

* Can create projects.
* Can view only their own projects.
* Can update and delete only their own projects.
* Can create and assign tasks within their own projects.
* Can manage tasks related to their own projects.
* Can view tasks belonging to their own projects.

### USER

* Can view only tasks assigned to them.
* Can update only tasks assigned to them.
* Can update task status only for tasks assigned to them.

---

## API Endpoint Summary

### Authentication

| Method | Endpoint             | Description         |
| ------ | -------------------- | ------------------- |
| POST   | `/api/auth/register` | Register a new user |
| POST   | `/api/auth/login`    | Authenticate user   |

### Projects

| Method | Endpoint             | Description       |
| ------ | -------------------- | ----------------- |
| POST   | `/api/projects`      | Create project    |
| GET    | `/api/projects`      | Get projects      |
| GET    | `/api/projects/{id}` | Get project by ID |
| PUT    | `/api/projects/{id}` | Update project    |
| DELETE | `/api/projects/{id}` | Delete project    |

### Tasks

| Method | Endpoint                         | Description                           |
| ------ | -------------------------------- | ------------------------------------- |
| POST   | `/api/tasks`                     | Create task                           |
| GET    | `/api/tasks`                     | Get tasks with pagination and filters |
| GET    | `/api/tasks/{id}`                | Get task by ID                        |
| PUT    | `/api/tasks/{id}`                | Update task                           |
| DELETE | `/api/tasks/{id}`                | Delete task                           |
| PATCH  | `/api/tasks/{id}/status`         | Update task status                    |
| GET    | `/api/tasks/project/{projectId}` | Get tasks by project                  |
| GET    | `/api/tasks/assigned/{userId}`   | Get tasks by assigned user            |

---

## Pagination and Filtering

Task listing supports pagination:

Example:

```text
GET /api/tasks?page=0&size=10
```

Filtering by status:

```text
GET /api/tasks?status=TODO
```

Filtering by priority:

```text
GET /api/tasks?priority=HIGH
```

Combined filtering:

```text
GET /api/tasks?status=IN_PROGRESS&priority=MEDIUM
```

---

## API Documentation

Swagger UI is available at:

```text
http://localhost:8081/swagger-ui/index.html
```

Swagger can be used to test all endpoints directly from the browser.

---

## Unit Testing

Unit tests were implemented using JUnit 5 and Mockito.

Test coverage includes:

* Authentication logic
* Project service business rules
* Task service business rules
* Access control validation

---
