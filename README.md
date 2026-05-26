# POS OnTheWorld Backend

Java Spring Boot backend for a POS system supporting multiple branches, inventory, sales, customers, and reporting.

## Features
- JWT authentication and role-based access control (ADMIN, CASHIER, MANAGER)
- Product management with SKU, barcode, category, pricing, and images
- Inventory management with stock movement history and branch stock separation
- Sales orders, payment processing, receipt number generation
- Customer management with loyalty points
- Daily and monthly reporting
- Swagger/OpenAPI documentation
- Docker and PostgreSQL support

## Run Locally
1. Create the PostgreSQL database `pos_ontheworld`.
2. Apply SQL migration located at `C:\GIT\DATA_BASE\pos_ontheworld_db\V1__init_pos_schema.sql`.
3. Start the database and application with Docker Compose:
   ```bash
   docker-compose up --build
   ```
4. Access API docs at `http://localhost:8080/swagger-ui.html`.

## Default credentials
- Username: `admin`
- Password: `admin123`

## Important files
- `pom.xml` - Maven build configuration
- `src/main/java` - Java source code
- `src/main/resources/application.yml` - environment settings
- `docker-compose.yml` - service definitions for app and PostgreSQL
- `C:\GIT\DATA_BASE\pos_ontheworld_db\V1__init_pos_schema.sql` - database migration script
- `README.md` - project overview
