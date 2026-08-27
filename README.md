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
