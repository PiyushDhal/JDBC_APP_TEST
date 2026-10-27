# College Student Registration System (JDBC)

A Java application using JDBC to register and manage college students in a database.

## Features
- **Insert a new student record** (Student ID, Name, Department, Year of Study, Email Address)
- **Prevent duplicate student IDs** (Pre-check query + SQL Primary Key constraint)
- **Display all registered students** in a formatted table
- **Search student by Student ID**
- **Update and Delete student records**

## Database
- Compatible with **MySQL** and **SQLite** (local database: `college_students.db`).
- SQL schema script available in [`schema.sql`](schema.sql).

## Prerequisites & JDBC Drivers
Ensure the MySQL connector or SQLite JDBC jar is in the `lib/` directory:
- [MySQL Connector/J](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar)
- [SQLite JDBC](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.49.1.0/sqlite-jdbc-3.49.1.0.jar)

## How to Compile & Run

### Compile:
```powershell
javac -cp "lib/*" StudentRegistrationApp.java
```

### Run:
```powershell
java -cp ".;lib/*" StudentRegistrationApp
```
