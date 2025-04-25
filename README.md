# PropertyBot - AI Chatbot Backend for Property Viewings

This is a Java-based Spring Boot application that supports AI-assisted chatbot interaction for scheduling property viewing appointments. It integrates with DeepSeek AI, supports token-based authentication using JWT, and automatically sends appointment reminders.

---

## Features

- Landlord and tenant registration
- DeepSeek-powered chatbot for scheduling property viewings
- Support multi-turn AI conversation.
- JWT-based user authentication
- Appointment creation
- Automated reminders 1 hour before appointments
- H2 in-memory database

---

##  Getting Started

### 1. **Clone the Repository**
```bash
https://github.com/khomeini-air/property-chatbot
cd property-chatbot
```

### 2. **Set Your Environment Variables**
Open `run.sh` and set your environment variables below
```bash
export JWT_SECRET=[your_jwt_secret]
export DEEPSEEK_API_KEY=[your_deepseek_api_key]
```

### 3. **Run the Application**
```bash
$./run.sh
.....
2025-04-11 21:48:04.534  INFO 35010 --- [           main] c.speedhome.chatbot.ChatbotApplication   : Started ChatbotApplication in 5.29 seconds (process running for 5.54) |  
2025-04-11 21:48:04.535 DEBUG 35010 --- [           main] o.s.b.a.ApplicationAvailabilityBean      : Application availability state LivenessState changed to CORRECT |  
2025-04-11 21:48:04.536 DEBUG 35010 --- [           main] o.s.b.a.ApplicationAvailabilityBean      : Application availability state ReadinessState changed to ACCEPTING_TRAFFIC |  
```

### 4. **Endpoints**

####  User Registration:
```http
POST /api/auth/register
Request:
{
    "name": "landlords",
    "password": "123",
    "email": "landlord@x.com",
    "mobile": "+60123456789",
    "role": "LANDLORD"
}

Response:
{
    "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "message": "New user created",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYW5kbG9yZEB4LmNvbSIsImlhdCI6MTc0NTU1MjU4MSwiZXhwIjoxNzQ1NjM4OTgxfQ.VlFMKUyBF5xdslomPu-5fsaJd_URaffLhvtGVHEyhT0",
    "email": "landlord@x.com",
    "role": "LANDLORD"
}
```

####  User Login:
```http
POST /api/auth/login
Request:
{
    "password": "123",
    "email": "landlord@x.com"   
}

Response:
{
    "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "message": "Login successful",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYW5kbG9yZEB4LmNvbSIsImlhdCI6MTc0NTU1MjkxMSwiZXhwIjoxNzQ1NjM5MzExfQ.hTWjj_2LHZHBtfPNzaiU1JKZ9GHltyUQ06QlUxBX1xo",
    "email": "landlord@x.com",
    "role": "LANDLORD"
}
```

#### Add Property (Landlord Only)
```http
POST /api/properties
Authorization: Bearer [token_from_registrationOrLogin]
Request:
{
    "address":"xus street avenue"
}

Response:
{
    "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "message": "Property added",
    "id": 1,
    "address": "xus street avenue",
    "ownerId": 1
}
```

#### Chat:
```http
POST /api/chat
Authorization: Bearer [token_from_registrationOrLogin]
Headers: X-Session-ID:[your_unique_session_id]

Request:
{
  "message": "I want to schedule a viewing for 123 Main St on Friday."
}

Response:
{
     "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "message": "Property added",
    "response": "Sure, will be glad to help, but first I need more information: your email, appointment time, property id, and landlord email"
}
```


#### H2 Console
url: http://localhost:8080/h2-console

Username and password is configured on `application.properties`

## Configuration

### `application.properties`
```properties
spring.datasource.url=jdbc:h2:mem:propertybot
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true

openai.api.key=${OPENAI_API_KEY}
chatgpt.prompt.template=You are assisting in scheduling property viewings... %s
logging.api.enabled=true
```


## Application Structure

```
com.propertybot
├── api             // REST APIs
├── service         // AI service abstraction
├── entity          // JPA entities
├── repository      // Spring Data JPA
├── scheduler       // Reminder logic
├── security        // JWT & filters
├── filter          // Spring filter
├── handler          // handler logic
├── util            // utility classes 
└── config          // App config and filters

```

## Design Highlights

- **Pluggable AI Interface** (`AiService`) for easily adding new AI engines
- **Spring Scheduling** for time-based reminders
- **JWT** for stateless, secure APIs
- **ControllerAdvice** for global exception handling
- **SLF4J Logging** for traceability with masking


## Dependencies

- Spring Boot
- Spring Security
- Spring Data JPA
- DeepSeek API
- H2 Database
- SLF4J / Logback
- JUnit / Mockito
