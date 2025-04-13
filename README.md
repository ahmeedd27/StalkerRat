# 🕵️‍♂️ stalkerRat

**stalkerRat** is a unique anonymous messaging platform built using Java and Spring Boot. It allows users to send and receive anonymous messages with a seamless experience. Even guests (unauthenticated users) can send messages to registered users without the need to log in.

---

## 📌 Features

- ✅ Register and authenticate users using JWT tokens
- ⏳ Token expiration support – login required for a new token after expiry
- ✉️ Send anonymous messages to other users
- 👻 Message senders remain anonymous to the receiver
- 📥 Paginated message retrieval for authenticated users
- ⭐ Mark messages as favorites
- ❌ Delete received messages (only accessible to the receiver)
- 🔍 Search users by name
- 🏆 View top 3 most messaged (ranked) users
- ⚙️ Retrieve and update personal settings
- 🧑 View authenticated user profile
- 🖼️ Upload profile image
- 📤 Send messages as a guest without authentication

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **Framework:** Spring Boot
- **Security:** Spring Security + JWT
- **Validation:** Jakarta Bean Validation
- **Build Tool:** Maven
- **API Documentation:** Swagger / OpenAPI
- **Database:** (e.g. MySQL / PostgreSQL)
- **File Uploads:** Spring Multipart (Cloudinary optional)
- **Others:** Lombok, Javax Validation, AuthenticationContext

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- MySQL (or any supported relational database)
- Cloudinary account (optional, for image upload)
- SMTP config (optional, for email features)

### Running the Project

```bash
# Clone the repository
git clone https://github.com/your-username/stalkerRat.git

# Navigate to the project folder
cd stalkerRat

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

---

## 🔐 Authentication Flow

- Users receive a **JWT token** upon successful registration or login.
- The token is valid for a certain period.
- Users must re-authenticate to receive a new token after expiration.
- All secured routes require the token in the header:

```
Authorization: Bearer <your_token_here>
```

---

## 📡 API Endpoints Overview

### 🔑 Authentication

| Method | Endpoint     | Description                 |
|--------|--------------|-----------------------------|
| POST   | `/register`  | Register a new user         |
| POST   | `/login`     | Authenticate and get token  |

---

### 👤 Profile & Settings

| Method | Endpoint        | Description                  |
|--------|------------------|------------------------------|
| GET    | `/profile`       | Get current user profile     |
| GET    | `/settings`      | Get user settings            |
| PUT    | `/settings`      | Update user settings         |
| POST   | `/upload`        | Upload profile image         |

---

### 📩 Messaging

| Method | Endpoint                    | Description                                     |
|--------|-----------------------------|-------------------------------------------------|
| GET    | `/messages`                 | Retrieve paginated user messages               |
| POST   | `/user/{receiver-id}`       | Send message to user anonymously               |
| DELETE | `/messages/{message-id}`    | Delete received message (receiver only)        |
| PATCH  | `/favorite/{message-id}`    | Mark message as favorite (receiver only)       |

---

### 🔎 Search & Rankings

| Method | Endpoint             | Description                    |
|--------|----------------------|--------------------------------|
| GET    | `/search/{name}`     | Search users by name           |
| GET    | `/search`            | View top 3 most messaged users |

---

## 📬 Public Access

> Even unauthenticated users can send messages using:

```http
POST /user/{receiver-id}
Content-Type: application/json

{
  "content": "You are doing great!"
}
```

---

## 📦 Project Structure (high-level)

```
src/
├── controllers/          # REST Controllers (Authentication, Main)
├── service/              # Business logic
├── helpers/              # DTOs and request/response wrappers
├── config/               # Security configuration
└── ...
```

---

## 📈 Possible Improvements

- Message reporting/blocking
- Real-time WebSocket messaging
- Admin dashboard for moderation
- Email notifications
- Rate limiting for unauthenticated senders

---

## 👨‍💻 Author

Developed by **Ahmed Khamis**

> “Sometimes the message matters more than the messenger.”

---
