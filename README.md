# Quiz Competition Application

## Project Description
Quiz application using Java Swing and MySQL.

## Features
- Timer
- Difficulty Levels
- Leaderboard
- Certificate Generation
- Progress Bar

## Software Requirements
- Java JDK
- MySQL
- VS Code

## Steps to Run

Compile:

```powershell
javac -cp ".;lib/mysql-connector-j.jar" QuizApp.java
```

Run:

```powershell
java -cp ".;lib/mysql-connector-j.jar" QuizApp
```

## Database

```sql
CREATE DATABASE quizdb;

CREATE TABLE leaderboard(
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50),
score INT,
total INT
);
```

## Author
Talla Sahasra