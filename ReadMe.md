Here is an updated README reflecting the use of Java 21.

---

# Gestao de Pedidos

A backend application for order management, built as a technical challenge.

## Technologies Used

- **Java 21**
- **Spring Boot**
- **Hibernate/JPA** (with **MSSQL**)
- **Maven** (dependency management)
- **OAuth2** (JWT Bearer tokens for authentication)
- **RestTemplate/WebClient** (external API consumption)
- **Docker** (containerization)
- **Spring Mail** (email error reporting)

## Features

- CRUD for orders (with client info, status, value, creation date)
- External client validation via REST API
- Data model normalization (Order, User, OrderStatusHistory, ErrorLog)
- Error logging and daily email reports
- Secure REST API with OAuth2
- Filtering orders by status or date

## Getting Started

### Prerequisites

- Docker & Docker Compose installed
- Java 21+ (for local development)
- MSSQL Server (if not using Docker)

### Running with Docker

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Mari018/gestao-pedidos.git
   cd gestao-pedidos
   ```

2. **Start the application and database:**
   ```sh
   docker compose up
   ```
   This will build and run both the Spring Boot app and MSSQL database.

3. **Access the API:**
    - The backend will be available at `http://localhost:8080`
    - API documentation (if enabled): `/swagger-ui.html`

### Configuration

- All settings are in `src/main/resources/application.properties`
- Update email and database credentials as needed.

## Example Usage

- **Create Order:**  
  `POST /order` with JSON body (see Postman/curl examples in docs)
- **List Orders by State:**  
  `GET /order/state?state=PENDING`
- **List Orders by Date:**  
  `GET /order/date?date=2024-06-01`

## Improvements

- Add caching for frequent queries
- Expand test coverage

---

For more details, see the source code and comments.