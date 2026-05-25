# Quiz Competition Application

## Project Description
Quiz Competition Application is a Java Swing based desktop application developed using Java and MySQL. The application allows users to answer quiz questions with timer functionality, select difficulty levels, calculate scores, display leaderboard results and generate certificates.

## Features
- Timer
- Difficulty Levels
- Leaderboard
- Certificate Generation
- Progress Bar
- Score Calculation
- Java Swing GUI

## Software Requirements
- Java JDK
- MySQL
- VS Code
- MySQL Connector JAR

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

## Screenshots

Add screenshots of:

- Name Input Screen
- Difficulty Selection Screen
- Quiz Screen
- Result Screen
- Leaderboard (Optional)

## Folder Structure

```text
QuizApp
│
├── QuizApp.java
├── README.md
├── screenshots
├── Certificate.txt
└── lib
```



