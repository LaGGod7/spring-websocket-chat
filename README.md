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
🔐 Authentication Flow

The application uses JWT-based authentication for securing HTTP and WebSocket communication.

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

Passwords are never stored as plain text. They are encoded using BCrypt before being persisted.

💬 Private Messaging Flow
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

Private messages are stored in PostgreSQL and can later be retrieved through the conversation history endpoint.

🌐 REST API
Register
POST /auth/register

Creates a new user account.

Example request:

{
  "username": "GD",
  "password": "password"
}

Example response:

User registered successfully
Login
POST /auth/login

Authenticates a user and returns a JWT.

Example request:

{
  "username": "GD",
  "password": "password"
}

Example response:

<JWT_TOKEN>

The returned token is used to authenticate protected requests and WebSocket connections.

Get Conversation History
GET /messages/{recipient}

Returns the authenticated user's private conversation with the specified recipient.

Example:

GET /messages/Rahul
Authorization: Bearer <JWT_TOKEN>

Example response:

[
  {
    "sender": "GD",
    "recipient": "Rahul",
    "content": "Hello Rahul",
    "messageType": "CHAT",
    "timestamp": "2026-08-27T13:30:00"
  }
]
🔌 WebSocket Configuration
WebSocket Endpoint
/ws

The application uses SockJS to establish the WebSocket connection.

Application Destination Prefix
/app
Public Topic
/topic/public

Used for:

Public chat messages
JOIN notifications
LEAVE notifications
Online Users
/topic/users

Used to broadcast the currently connected users.

Private Messages
/user/queue/message

Used for one-to-one private messages.

📡 STOMP Mappings
Send Public Message
/app/chat.sendMessage
Add User
/app/chat.addUser
Send Private Message
/app/chat.privateMessage
🗄️ Database Model

The application uses PostgreSQL with JPA/Hibernate.

User
User
 ├── id
 ├── username
 └── password
Conversation
Conversation
 ├── id
 └── members

A conversation contains the users participating in a private conversation.

Message
Message
 ├── id
 ├── sender
 ├── conversation
 ├── content
 ├── messageType
 └── timestamp

Public messages do not belong to a private conversation, while private messages are associated with a conversation.

📁 Project Structure
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
⚙️ Getting Started
Prerequisites

Make sure you have the following installed:

Java 17+
Maven
PostgreSQL
