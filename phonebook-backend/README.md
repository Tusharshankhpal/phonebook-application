# Phonebook Backend

Backend-only implementation of the Phonebook Application training task.

## Scope

Implemented only the required backend functionality:

- Spring Boot
- PostgreSQL
- Spring Data JPA / Hibernate
- Jakarta Bean Validation
- Controller / Service / Repository / Entity / DTO structure
- Create contact
- Get all contacts
- Get contact by ID
- Update contact
- Delete contact
- Unique phone number
- Optional unique email
- Validation
- Exception handling

Not implemented yet:

- Vue frontend
- Docker
- Search
- Pagination
- Pinia/Vuex
- CI/CD
- Optional testing enhancements

## Requirements

Install:

- JDK 21
- Maven
- PostgreSQL

## Database

Create:

```sql
CREATE DATABASE phonebook;
```

Default application settings:

```text
Host: localhost
Port: 5432
Database: phonebook
Username: postgres
Password: postgres
```

If your PostgreSQL password is different, edit:

```text
src/main/resources/application.properties
```

## Run in Eclipse

Import this folder as:

```text
File
→ Import
→ Maven
→ Existing Maven Projects
```

Select the folder containing `pom.xml`.

Then:

```text
Right click PhonebookBackendApplication.java
→ Run As
→ Java Application
```

Backend starts at:

```text
http://localhost:8080
```

## API

### Get all contacts

```http
GET http://localhost:8080/contacts
```

### Get one contact

```http
GET http://localhost:8080/contacts/1
```

### Create

```http
POST http://localhost:8080/contacts
Content-Type: application/json
```

```json
{
  "name": "John Doe",
  "phoneNumber": "+1234567890",
  "email": "john@example.com",
  "address": "Mumbai, Maharashtra"
}
```

### Update

```http
PUT http://localhost:8080/contacts/1
Content-Type: application/json
```

```json
{
  "name": "John Updated",
  "phoneNumber": "+1234567890",
  "email": "john@example.com",
  "address": "Pune, Maharashtra"
}
```

### Delete

```http
DELETE http://localhost:8080/contacts/1
```

## Database table

When the application starts, Hibernate creates/updates the `contacts` table automatically.

Expected fields:

```text
id
name
phone_number
email
address
created_at
```
