# ⚡ Realtime Chat Application

A full-stack realtime chat application built with **Spring Boot, WebSocket/STOMP, PostgreSQL, Spring Security, and JWT authentication**.

The application provides secure user authentication, public and private realtime messaging, persistent conversation history, online-user tracking, and a responsive web interface.

---

## 🚀 Features

### 🔐 Authentication & Security

- User registration and login
- BCrypt password hashing
- JWT-based authentication
- Custom JWT authentication filter
- Protected REST endpoints
- JWT authentication for WebSocket connections
- Authenticated WebSocket `Principal`
- Request validation using Jakarta Bean Validation
- Global exception handling

### 💬 Realtime Messaging

- Public chat
- Private one-to-one messaging
- STOMP over WebSocket
- SockJS support
- Realtime message delivery
- Online user tracking
- JOIN notifications
- LEAVE notifications
- Messages delivered to both sender and recipient

### 💾 Message Persistence

- PostgreSQL database
- Persistent users
- Persistent conversations
- Persistent private messages
- Conversation history retrieval
- Server-generated message timestamps

### 🖥️ Frontend

- Login interface
- User registration
- Public chat interface
- Private chat interface
- Online users sidebar
- Unread message indicators
- Conversation history
- Message timestamps
- Responsive layout

### 🧪 Testing

- Unit testing with JUnit 5
- Mockito-based unit tests
- JWT service tests
- Conversation service tests
- Authentication controller tests
- Private messaging controller tests
- **12 unit tests covering core application logic**

---

## 🛠️ Tech Stack

### Backend

- **Java**
- **Spring Boot**
- **Spring Security**
- **Spring Web**
- **Spring WebSocket**
- **STOMP**
- **JPA / Hibernate**
- **PostgreSQL**
- **JWT**
- **Lombok**
- **Jakarta Bean Validation**

### Frontend

- **HTML**
- **CSS**
- **JavaScript**
- **SockJS**
- **STOMP.js**

### Testing

- **JUnit 5**
- **Mockito**

---

## 🏗️ Architecture

The application follows a layered backend architecture.

```text
                    ┌─────────────────┐
                    │     Frontend    │
                    │ HTML/CSS/JS     │
                    └────────┬────────┘
                             │
                    HTTP / WebSocket
                             │
                             ▼
                    ┌─────────────────┐
                    │   Controllers   │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │    Services     │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Repositories   │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    └─────────────────┘
```
## 🔐 Authentication Flow

The application uses JWT-based authentication for securing HTTP and WebSocket communication.

```text
                    Login
                      │
                      ▼
              POST /auth/login
                      │
                      ▼
             Validate credentials
                      │
                      ▼
                 Generate JWT
                      │
                      ▼
                  Frontend
                      │
                      ▼
             WebSocket CONNECT
                      │
               Bearer JWT
                      │
                      ▼
            WebSocket Authentication
                      │
                      ▼
          Authenticated Principal
```

Passwords are never stored as plain text. They are encoded using BCrypt before being persisted.

## 💬 Private Messaging Flow

```text
Sender
   │
   │ WebSocket / STOMP
   ▼
Controller
   │
   ├── Find sender
   │
   ├── Find recipient
   │
   ├── Get/Create conversation
   │
   ├── Save message
   │
   └── Create ChatMessage DTO
              │
        ┌─────┴─────┐
        ▼           ▼
    Recipient     Sender
```

Private messages are stored in PostgreSQL and can later be retrieved through the conversation history endpoint.
## 🌐 REST API

### Register

```http
POST /auth/register
```

Creates a new user account.

**Example request:**

```json
{
  "username": "GD",
  "password": "password"
}
```

**Example response:**

```text
User registered successfully
```

---

### Login

```http
POST /auth/login
```

Authenticates a user and returns a JWT.

**Example request:**

```json
{
  "username": "GD",
  "password": "password"
}
```

**Example response:**

```text
<JWT_TOKEN>
```

The returned token is used to authenticate protected requests and WebSocket connections.

---

### Get Conversation History

```http
GET /messages/{recipient}
```

Returns the authenticated user's private conversation with the specified recipient.

**Example:**

```http
GET /messages/Rahul
Authorization: Bearer <JWT_TOKEN>
```

**Example response:**

```json
[
  {
    "sender": "GD",
    "recipient": "Rahul",
    "content": "Hello Rahul",
    "messageType": "CHAT",
    "timestamp": "2026-08-27T13:30:00"
  }
]
```

---

## 🔌 WebSocket Configuration

### WebSocket Endpoint

```text
/ws
```

The application uses SockJS to establish the WebSocket connection.

### Application Destination Prefix

```text
/app
```

### Public Topic

```text
/topic/public
```

Used for:

- Public chat messages
- JOIN notifications
- LEAVE notifications

### Online Users

```text
/topic/users
```

Used to broadcast the currently connected users.

### Private Messages

```text
/user/queue/message
```

Used for one-to-one private messages.

---

## 📡 STOMP Mappings

### Send Public Message

```text
/app/chat.sendMessage
```

### Add User

```text
/app/chat.addUser
```

### Send Private Message

```text
/app/chat.privateMessage
```

---

## 🗄️ Database Model

The application uses PostgreSQL with JPA/Hibernate.

### User

```text
User
 ├── id
 ├── username
 └── password
```

### Conversation

```text
Conversation
 ├── id
 └── members
```

A conversation contains the users participating in a private conversation.

### Message

```text
Message
 ├── id
 ├── sender
 ├── conversation
 ├── content
 ├── messageType
 └── timestamp
```

Public messages do not belong to a private conversation, while private messages are associated with a conversation.

---

## 📁 Project Structure

```text
src
├── main
│   ├── java
│   │   └── org.gd.ws2
│   │       │
│   │       ├── config
│   │       │   ├── SecurityConfig
│   │       │   ├── WebSocketConfig
│   │       │   └── WebSocketAuthInterceptor
│   │       │
│   │       ├── controllers
│   │       │   ├── AuthController
│   │       │   ├── Controller
│   │       │   └── WebSocketUserService
│   │       │
│   │       ├── Entity
│   │       │   └── dto
│   │       │
│   │       ├── exception
│   │       │
│   │       ├── filter
│   │       │
│   │       ├── repository
│   │       │
│   │       └── Service
│   │
│   └── resources
│       ├── static
│       │   ├── index.html
│       │   ├── main.js
│       │   └── style.css
│       │
│       └── application.properties
│
└── test
    └── java
        └── org.gd.ws2
            ├── Service
            │   ├── JwtServiceTest
            │   └── ConversationServiceTest
            │
            └── controllers
                ├── AuthControllerTest
                └── ControllerTest
```

---

## ⚙️ Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 17+
- Maven
- PostgreSQL

### 1. Clone the Repository

```bash
git clone <your-repository-url>
```

Navigate into the project:

```bash
cd <project-directory>
```

### 2. Configure PostgreSQL

Create a PostgreSQL database for the application.

Then configure the database connection in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chatdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

> **Important:** Do not commit real database credentials or JWT secrets to the repository.

### 3. Run the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8082
```

Open the application in your browser and create an account.
## 🧪 Running Tests

Run the complete test suite using:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

The project contains **12 unit tests** covering the core application logic:

- JWT generation and validation
- JWT username extraction
- Conversation creation and retrieval
- Conversation members
- User registration
- Duplicate username handling
- Successful login
- Incorrect password handling
- Private message persistence and delivery

---

## 🔒 Security

The application implements:

- BCrypt password hashing
- JWT-based authentication
- Custom JWT authentication filter
- Protected REST endpoints
- WebSocket authentication
- Authenticated WebSocket principals
- Jakarta Bean Validation
- Global exception handling
- DTO-based WebSocket communication

Sensitive values such as database credentials and JWT secrets should be supplied through environment-specific configuration and should not be committed to source control.

---

## 🎯 Project Goals

This project was built to gain practical experience with:

- Building REST APIs using Spring Boot
- Implementing JWT authentication
- Securing applications with Spring Security
- Implementing realtime communication using WebSocket/STOMP
- Working with PostgreSQL and JPA/Hibernate
- Designing DTO-based communication
- Managing persistent conversations and messages
- Writing unit tests with JUnit 5 and Mockito
- Building a frontend that communicates with a Java backend

---

## 👨‍💻 Author

**GD**

Built using the Spring ecosystem with realtime WebSocket communication, JWT security, PostgreSQL persistence, and automated unit testing.
