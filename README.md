# 🔐 Secure Notes Application

A full-stack secure notes application built with Spring Boot that provides end-to-end encryption for personal note storage. Features include JWT-based authentication, AES-256 encryption for data at rest, and a complete REST API for note management.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

## 🌟 Features

### Security
- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **AES-256 Encryption**: All note content encrypted at rest in the database
- **BCrypt Password Hashing**: Secure password storage with adaptive hashing
- **Role-Based Access Control**: User roles and permissions management
- **Data Isolation**: Users can only access their own notes

### Functionality
- **User Registration & Login**: Secure account creation and authentication
- **CRUD Operations**: Create, read, update, and delete encrypted notes
- **Automatic Encryption/Decryption**: Transparent encryption handling
- **RESTful API**: Clean, well-documented API endpoints

## 🏗️ Architecture

### Security Layers
```
┌────────────────────────────────────────────────────────┐
│ Layer 1: JWT Authentication   │ Token-based auth      │
│ Layer 2: User Authorization    │ Ownership verification│
│ Layer 3: AES-256 Encryption    │ Data at rest security │
└────────────────────────────────────────────────────────┘
```

### Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.5.4
- Spring Security 6
- Spring Data JPA
- Hibernate
- JWT (JSON Web Tokens)
- AES-256 Encryption

**Database:**
- PostgreSQL

**Build Tool:**
- Gradle

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Gradle 8.x
- PostgreSQL 

### Installation

1. **Clone the repository**
```bash
   git clone https://github.com/yourusername/secure-notes.git
   cd secure-notes
```

2. **Configure application properties**
   
   Create or update `src/main/resources/application.properties`:
```properties
   # JWT Configuration
   jwt.secret=mySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm
   jwt.expiration=86400000
   
   # AES Encryption Configuration
   aes.secret=sixteenByteKey!!
   
   # Database 
   spring.datasource.url=jdbc:postgresql://localhost:5432/secure_notes_db
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

```

3. **Build the project**
```bash
   ./gradlew build
```

4. **Run the application**
```bash
   ./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Quick Test
```bash
# Health check
curl http://localhost:8080/api/auth/test

# Expected response: "AuthController is working."
```

## 📡 API Endpoints

### Authentication

#### Sign Up
```bash
POST /api/auth/signup
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securePassword123"
}

# Response: { "token": "eyJhbGc..." }
```

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securePassword123"
}

# Response: { "token": "eyJhbGc..." }
```

### Notes (Requires Authentication)

All note endpoints require the JWT token in the Authorization header:
```bash
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Create Note
```bash
POST /api/notes
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "title": "Shopping List",
  "content": "Buy milk, eggs, and bread"
}
```

#### Get All Notes
```bash
GET /api/notes
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Get Single Note
```bash
GET /api/notes/{id}
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Update Note
```bash
PUT /api/notes/{id}
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "title": "Updated Title",
  "content": "Updated content"
}
```

#### Delete Note
```bash
DELETE /api/notes/{id}
Authorization: Bearer YOUR_JWT_TOKEN
```

## 🔒 Security Features Explained

### 1. Password Security
- Passwords are hashed using **BCrypt** with a cost factor of 10
- Original passwords are never stored in the database
- Each password gets a unique salt automatically

### 2. JWT Authentication
- Stateless authentication - no server-side sessions
- Tokens contain user information and roles
- Tokens expire after 24 hours (configurable)
- HMAC-SHA256 signature prevents tampering

### 3. AES-256 Encryption
- All note content is encrypted before database storage
- Industry-standard encryption algorithm
- Data remains encrypted at rest
- Automatic decryption when retrieving notes

### 4. Authorization
- Users can only access their own notes
- Database queries include user ownership checks
- Prevents horizontal privilege escalation

## 📁 Project Structure
```
secure-notes/
├── src/
│   ├── main/
│   │   ├── java/com/example/secure_notes/
│   │   │   ├── controller/          # REST API endpoints
│   │   │   │   ├── AuthController.java
│   │   │   │   └── NoteController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── JwtResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── SignupRequest.java
│   │   │   │   └── CreateNoteRequest.java
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   └── Note.java
│   │   │   ├── repository/          # Data Access Layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── NoteRepository.java
│   │   │   ├── security/            # Security Configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── NoteService.java
│   │   │   │   └── UserService.java
│   │   │   ├── util/                # Utilities
│   │   │   │   └── AesEncryptionUtil.java
│   │   │   └── SecureNotesApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                        # Unit Tests
│       └── java/com/example/secure_notes/
│           ├── UserTest.java
│           ├── NoteTest.java
│           ├── AuthServiceTest.java
│           └── JwtUtilTest.java
├── build.gradle
└── README.md
```

## 🧪 Testing

Run all tests:
```bash
./gradlew test
```

Run specific test class:
```bash
./gradlew test --tests AuthServiceTest
```

View test reports:
```bash
open build/reports/tests/test/index.html
```

### Test Coverage
- Entity tests for User and Note
- Service layer tests with Mockito
- JWT utility tests
- Encryption utility tests

## 🔧 Configuration

### Environment Variables (Production)
For production deployment, use environment variables instead of hardcoding secrets:
```bash
export JWT_SECRET=your-secure-jwt-secret-here
export AES_SECRET=your-secure-aes-secret-here
export DB_URL=jdbc:postgresql://localhost:5432/securenotesdb
export DB_USERNAME=your-db-username
export DB_PASSWORD=your-db-password
```

Update `application.properties`:
```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
aes.secret=${AES_SECRET}

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

## 🎯 Use Cases

- **Personal Note Taking**: Secure storage for sensitive information
- **Password Manager**: Store encrypted passwords and credentials
- **Secure Journaling**: Private encrypted journal entries
- **Document Storage**: Encrypted text document management
- **Team Notes**: Multi-user secure note sharing (with enhancements)

## 🚧 Future Enhancements

- [ ] Note sharing between users
- [ ] Rich text editor support
- [ ] File attachments with encryption
- [ ] Note tagging and categories
- [ ] Search functionality (title-based)
- [ ] Email verification
- [ ] Password reset functionality
- [ ] Multi-factor authentication (MFA)
- [ ] Rate limiting for API endpoints
- [ ] Note version history

## 📝 API Documentation

For detailed API documentation with request/response examples, see:
- [Authentication API Documentation](docs/AUTH_API.md)
- [Notes API Documentation](docs/NOTES_API.md)

Or use tools like Postman or Swagger UI for interactive API exploration.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
