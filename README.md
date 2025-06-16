# CentDB

A lightweight simulation of a database system implemented in Java.

CentDB is a basic, educational project that demonstrates how core database functionalities can be modeled from scratch — including table creation, data insertion, and query execution — without relying on any external database engine.

## 🚀 Features

- Create and manage multiple tables
- Insert and store records in memory
- Perform simple query simulations
- Object-oriented Java design for clarity and modularity
- CLI interface for user interaction

## 🧠 Why I Built This

This project was developed as part of my journey to understand the inner workings of database engines. By building a simplified model, I was able to deepen my understanding of:

- Data storage and retrieval
- Table schema handling
- Query interpretation and command parsing
- Java's capabilities for structuring backend logic

## 🛠 Tech Stack

- **Language:** Java
- **Environment:** CLI
- **Tools:** Standard Java Libraries (no external DB dependencies)

## 🗂 Project Structure

CentDB/
│
├── src/ # Source code
│ ├── database/ # Core logic for database operations
│ ├── models/ # Table and record data structures
│ └── Main.java # Entry point of the application
│
├── test/ # Unit tests (if applicable)
└── README.md # Project documentation

## 🧪 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/Ali-Shan-Khawaja/CentDB.git
   cd CentDB

2. Compile the source code
   ```bash
   javac src/Main.java

3. Run the application:
   ```bash
   java src/Main