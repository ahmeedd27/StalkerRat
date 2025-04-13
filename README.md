# 📚 BookNetwork

![BookNetwork Logo](https://via.placeholder.com/150) <!-- Replace with actual logo if available -->

**BookNetwork** is a digital library platform built with **Spring Boot**, designed to simplify the management and sharing of books. Users can register, manage books, borrow and return them, and leave feedback — all in a secure, user-friendly environment.

---

## 📑 Table of Contents

- [🚀 Overview](#-overview)
- [✨ Features](#-features)
- [🛠 Technologies Used](#-technologies-used)
- [⚙️ Prerequisites](#-prerequisites)
- [🧰 Setup Guide](#-setup-guide)
- [📌 Usage Instructions](#-usage-instructions)
- [📡 API Endpoints](#-api-endpoints)
- [🤝 Contributing](#-contributing)
- [📝 License](#-license)
- [📬 Contact](#-contact)

---

## 🚀 Overview

BookNetwork offers a centralized solution for managing digital libraries. After registration and email activation, users can add books, browse titles, borrow and return books, and share feedback. The platform uses JWT for authentication and includes features like pagination, role-based access, and book archiving.

**Problem Solved**: It provides a structured and scalable system for tracking book ownership, borrowing status, and user interactions — ideal for communities or institutions.

---

## ✨ Features

### 🔐 User Authentication
- Register with email verification
- Login with JWT authentication
- Email-based account activation

### 📖 Book Management
- Add books with metadata (title, author, ISBN, synopsis)
- Upload book cover images
- Archive or mark books as shareable
- Browse books by ID, owner, or availability

### 🔁 Borrowing System
- Borrow and return books
- Owners can approve returns
- Track borrowing and return history

### 💬 Feedback System
- Submit feedback for books
- View feedback with pagination

### 🔎 Efficient Browsing
- Paginated and sortable book lists
- Filter books by owner or borrow status

### 🛡 Security
- Role-based access control using Spring Security
- Secure endpoints with JWT authentication

---

## 🛠 Technologies Used

- **Java** 17
- **Spring Boot** 3.x
- **Spring Security**
- **JWT** for authentication
- **PostgreSQL** for database
- **Maven** for build management
- **Swagger/OpenAPI** for API documentation
- **Lombok**, **Spring Data JPA**, **Jakarta Mail**, and more

---

## ⚙️ Prerequisites

Make sure you have the following installed:

- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Git
- SMTP Server (e.g. Gmail SMTP)

---

## 🧰 Setup Guide

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/yourusername/BookNetwork.git
cd BookNetwork
```

### 2️⃣ Configure the Database
Create a PostgreSQL database named `booknetwork`.

Update your `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/booknetwork
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### 3️⃣ Configure Email Settings
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your.email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 4️⃣ Build the Project
```bash
mvn clean install
```

### 5️⃣ Run the Application
```bash
mvn spring-boot:run
```

### 6️⃣ Access the App
- Backend API: [http://localhost:8080](http://localhost:8080)
- Swagger Docs: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📌 Usage Instructions

### 👤 Register & Authenticate
1. **Register**: `POST /auth/register`
2. **Activate Account**: Check your email for the token → `GET /auth/activate-account?token=...`
3. **Login**: `POST /auth/authenticate` → Receive JWT
4. **Use JWT**: Add it in the `Authorization` header for protected endpoints

### 📚 Manage Books
- Add a book: `POST /books`
- View all: `GET /books`
- View by owner: `GET /books/owner`
- Borrow book: `POST /books/borrow/{book-id}`
- Return book: `PATCH /books/borrow/return/{book-id}`

### 💬 Feedback
- Submit: `POST /feedbacks`
- View for book: `GET /feedbacks/book/{book-id}`

---

## 📡 API Endpoints

### 🔐 Authentication

| Method | Endpoint                      | Description                   |
|--------|-------------------------------|-------------------------------|
| POST   | `/auth/register`              | Register a new user           |
| POST   | `/auth/authenticate`          | Login & receive JWT token     |
| GET    | `/auth/activate-account`      | Activate account with token   |

### 📚 Books

| Method | Endpoint                                     | Description                            |
|--------|----------------------------------------------|----------------------------------------|
| POST   | `/books`                                     | Add a new book                         |
| GET    | `/books/{book-id}`                           | Get book by ID                         |
| GET    | `/books`                                     | List all books (paginated)             |
| GET    | `/books/owner`                               | Get books by owner                     |
| GET    | `/books/borrowed`                            | List borrowed books                    |
| GET    | `/books/returned`                            | List returned books                    |
| PATCH  | `/books/shareable/{book-id}`                 | Toggle shareable status                |
| PATCH  | `/books/archived/{book-id}`                  | Toggle archived status                 |
| POST   | `/books/borrow/{book-id}`                    | Borrow a book                          |
| PATCH  | `/books/borrow/return/{book-id}`             | Return a borrowed book                 |
| PATCH  | `/books/borrow/return/approve/{book-id}`     | Approve return of a book               |
| POST   | `/books/cover/{book-id}`                     | Upload book cover image                |

### 💬 Feedback

| Method | Endpoint                        | Description                          |
|--------|----------------------------------|--------------------------------------|
| POST   | `/feedbacks`                    | Submit feedback for a book           |
| GET    | `/feedbacks/book/{book-id}`     | Get feedback for a book (paginated)  |

> ⚠️ Most endpoints require JWT authentication. Use:  
> `Authorization: Bearer <your-token>`

---

## 🤝 Contributing

We welcome contributions! Follow these steps:

1. **Fork** the repo
2. **Create** a new branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. **Commit** your changes:
   ```bash
   git commit -m "Add your feature"
   ```
4. **Push** to GitHub:
   ```bash
   git push origin feature/your-feature
   ```
5. **Open a Pull Request** 🎉

Please follow the project's coding conventions and write tests when applicable.

---

## 📝 License

This project is licensed under the **MIT License**.  
See the [LICENSE](LICENSE) file for more details.

---

## 📬 Contact

- **Author**: Ahmed [Your Last Name]  
- **GitHub**: [yourusername](https://github.com/yourusername)  
- **Email**: your.email@example.com  

Feel free to reach out for questions, ideas, or collaboration!