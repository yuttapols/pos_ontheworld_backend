# Java Backend AI Rules

## STRICT FILE MODIFICATION POLICY

AI must NEVER randomly edit Java files.

Before modifying any file:

1. Read the full class
2. Understand business logic
3. Check dependencies
4. Check related services/repositories/controllers
5. Apply minimal changes only

Never rewrite entire classes unnecessarily.

---

# Java Architecture Rules

Project architecture:

* Controller → Service → Repository
* DTO pattern required
* Entity must not be exposed directly
* Business logic belongs in Service layer only

AI must preserve existing architecture.

---

# Controller Rules

Controllers must:

* handle HTTP requests only
* validate request bodies
* call services only
* return standardized responses

Controllers must NOT:

* contain business logic
* contain SQL
* access repositories directly

---

# Service Rules

Services must:

* contain business logic
* handle transactions
* coordinate repositories
* validate business rules

Services must NOT:

* return entities directly
* contain HTTP logic
* contain duplicated logic

---

# Repository Rules

Repositories must:

* only handle database access
* use JPA/Hibernate properly

Repositories must NOT:

* contain business logic
* contain unnecessary native SQL

---

# Entity Rules

Entities must:

* use UUID IDs
* use LocalDateTime
* use BigDecimal for money
* use enums for statuses

Entities must NOT:

* expose sensitive data
* contain controller logic
* contain service logic

---

# DTO Rules

Always use DTOs for:

* requests
* responses

Never expose entities directly to API responses.

DTOs must include:

* validation annotations
* clean field naming
* only necessary fields

---

# Validation Rules

Use:

* @NotNull
* @NotBlank
* @Valid
* @Size
* @Email

Never trust frontend validation.

---

# Exception Handling Rules

Use:

* GlobalExceptionHandler
* custom business exceptions

Never:

* expose stack traces
* swallow exceptions silently

---

# Security Rules

Use:

* Spring Security
* JWT Authentication
* BCrypt password hashing

Never:

* hardcode secrets
* store plain passwords
* bypass authorization

---

# Transaction Rules

Use @Transactional only where necessary.

Never:

* create unnecessary nested transactions
* perform large logic inside transactions

---

# Database Rules

Use:

* Flyway migrations
* proper indexes
* pagination

Never:

* edit old migration files
* drop tables without permission
* change schema silently

Create new migration files only.

---

# File Protection Rules

AI must NEVER modify these files unless explicitly requested:

* application.yml
* application-prod.yml
* pom.xml
* docker-compose.yml
* SecurityConfig.java
* JwtFilter.java
* JwtService.java
* migration files

---

# Safe Modification Rules

If only small changes are needed:

* modify only relevant lines

Do NOT:

* reformat unrelated code
* rename unrelated variables
* move methods unnecessarily
* regenerate entire files

---

# Refactor Rules

Do not refactor unless:

* explicitly requested
* severe issue exists

Before refactoring:

1. explain why
2. explain risks
3. preserve existing behavior

---

# Logging Rules

Use structured logging.

Never log:

* passwords
* JWT tokens
* sensitive data

---

# Performance Rules

Use:

* pagination
* lazy loading
* optimized queries

Prevent:

* N+1 queries
* unnecessary database calls

---

# Testing Rules

Generated code must:

* compile correctly
* have valid imports
* avoid broken references

Never leave:

* TODO placeholders
* pseudo code
* incomplete methods

---

# Coding Style Rules

Use:

* constructor injection
* meaningful naming
* small methods
* clean structure

Avoid:

* god classes
* giant methods
* duplicated logic

---

# AI Behavior Rules

AI must behave like:

* a careful senior Java engineer
* a maintainer of a production system

Priority:

1. Stability
2. Backward compatibility
3. Security
4. Maintainability
5. Readability

# Database Change Rules

## Mandatory Database File Update Policy

Whenever AI performs:

* CREATE TABLE
* ALTER TABLE
* DROP TABLE
* ADD COLUMN
* REMOVE COLUMN
* MODIFY COLUMN
* ADD INDEX
* REMOVE INDEX
* ADD CONSTRAINT
* MODIFY RELATIONSHIP

AI MUST also create or update database migration files inside:

C:\GIT\DATA_BASE\pos_ontheworld_db

---

# Migration File Rules

Every database structure change MUST generate:

1. SQL migration file
2. Clear migration name
3. Timestamp or version number

Example:

V12__create_product_table.sql
V13__add_barcode_to_product.sql
V14__modify_inventory_index.sql

---

# Forbidden Actions

AI must NEVER:

* modify old migration files
* overwrite previous migrations
* silently change schema
* perform destructive schema updates without explicit request

Wrong:
Editing:
V1__init.sql

Correct:
Create:
V15__update_product_schema.sql

---

# Database Synchronization Rule

Whenever Entity classes are changed:

* AI must verify database schema consistency
* AI must generate matching migration SQL
* AI must update schema files in:
  C:\GIT\DATA_BASE\pos_ontheworld_db

This rule is mandatory.

---

# SQL File Requirements

Generated SQL files must:

* be production safe
* include rollback awareness
* use proper constraints
* include indexes when needed
* avoid destructive operations

---

# Naming Convention

Migration naming format:

V{number}__{description}.sql

Examples:
V20__create_sales_table.sql
V21__add_branch_id_to_sales.sql
V22__create_inventory_index.sql

---

# Column Modification Safety

Before modifying columns:

1. Check existing data compatibility
2. Avoid breaking production data
3. Preserve backward compatibility

AI must warn before:

* changing column types
* dropping columns
* renaming columns

---

# Table Deletion Rules

AI must NEVER drop tables unless explicitly requested.

If deletion is required:

* explain risks
* identify affected modules
* identify affected foreign keys
* identify data loss impact

---

# Schema Change Checklist

Before completing database-related tasks, AI must verify:

* migration file created
* entity updated
* DTO compatibility checked
* repository compatibility checked
* indexes considered
* constraints validated
* foreign keys validated

---

# Important Rule

Database schema changes are NEVER complete unless:

* migration SQL exists
* migration SQL is stored in:
  C:\GIT\DATA_BASE\pos_ontheworld_db

---

# Error Check Rules

## Mandatory Pre-Fix Summary Policy

After completing ANY task, AI MUST:

1. Scan all modified and related files for errors
2. Check for:
   * Compilation errors (missing imports, wrong types, undefined references)
   * Logic errors (broken method signatures, mismatched interfaces and impls)
   * Missing dependencies or wiring (missing @Bean, unresolved injection)
   * Broken contracts (interface methods not implemented)
3. Summarize ALL found errors in a clear list before making any fix

---

## Error Summary Format

AI must present errors in this format before fixing:

```
[ERROR SUMMARY]
1. File: XxxClass.java — Description of error
2. File: YyyClass.java — Description of error
...

[FIX PLAN]
1. Fix #1 — what will be changed and why
2. Fix #2 — what will be changed and why
```

---

## Error Check Rules

AI must NEVER:

* Fix errors silently without summarizing first
* Skip the summary step even if only one error exists
* Apply fixes before the user sees the error list

AI MUST:

* Complete the full error scan before reporting
* Group errors by severity (compile error > logic error > warning)
* Confirm the fix plan before applying changes

---

# Logic Impact Rules

## Mandatory Ask-Before-Change Policy

Before applying ANY change that affects existing logic, AI MUST stop and ask the user first.

---

## What Counts as "Logic Impact"

AI must ask before changing if the modification:

* Changes method signature (parameters, return type, method name)
* Changes business calculation or formula
* Changes transaction boundary (@Transactional scope)
* Changes validation rules or conditions
* Changes data flow between service → repository → entity
* Removes or replaces existing behavior (not just adding new code)
* Changes how errors or exceptions are handled
* Affects more than one service or domain at the same time
* Changes security rules or access control
* Changes existing API response structure that callers depend on

---

## Ask Format

AI must ask in this format before proceeding:

```
[LOGIC IMPACT DETECTED]
File: XxxServiceImpl.java
Current behavior: (describe what it does now)
Proposed change: (describe what will change)
Impact: (list what else may be affected)

ยืนยันให้แก้ไขได้เลยไหม?
```

---

## Exceptions (No Need to Ask)

AI may proceed without asking only if:

* Adding brand new methods that do not touch existing code paths
* Adding new fields to DTOs without removing old ones
* Fixing obvious compile errors caused by the current task
* Changes are explicitly requested by the user in the same message

---

# Path Rules

## Standard Folder Mapping

When the user says **"ใส่ใน doc"** or **"เก็บไว้ใน doc"** or **"สร้าง document"**:

* Target path: `C:\GIT\DOCUMENT\pos_ontheworld_doc`

When the user says **"เขียน script db"** or **"สร้าง migration"** or **"เพิ่ม SQL"**:

* Target path: `C:\GIT\DATA_BASE\pos_ontheworld_db`

---

## Rules

AI must NEVER:

* Store documents inside the backend project folder (`pos_ontheworld_backend/docs`)
* Store migration SQL inside the backend project folder
* Create new subfolders unless explicitly requested

AI MUST:

* Always use the mapped paths above when user references "doc" or "script db"
* Confirm the target path if ambiguous before writing files
