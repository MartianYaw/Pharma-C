## Getting Started with VS Code Java

Welcome to the VS Code Java world! This guide will help you get started with writing Java code in Visual Studio Code.

## Folder Structure

Your workspace contains two default folders:

- src: for maintaining source code
- lib: for managing dependencies

Compiled output files will be generated in the bin folder by default. To customize the folder structure, update the related settings in `.vscode/settings.json`.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


## Steps to Run the Application For VSCode 
1. Copy the classpath in the launch file in `vscode` and delete the launch file. Create a new launch file in the
    run/debug terminal and path the copied classpath.
2. Update Database Credentials:
    - Open `src/DatabaseConnection.java` and change the database credentials to your local database credentials.
3. Create Database and Tables:
    - Execute the following queries in your MySQL Workbench or terminal:
-
      -  CREATE DATABASE pharmacy_db;
-
      -  USE pharmacy_db;
-
      -  CREATE TABLE Drugs (
      -  drugCode VARCHAR(20) PRIMARY KEY,
      -  name VARCHAR(100) NOT NULL,
      -  category VARCHAR(50),
      -  price DECIMAL(10, 2) NOT NULL,
      -  stockQuantity INT NOT NULL
      -  );
-
      -  CREATE TABLE Suppliers (
      -  supplierId INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
      -  name VARCHAR(100) NOT NULL,
      -  location VARCHAR(100),
      -  contactInfo VARCHAR(100)
      -  );
-
      -  CREATE TABLE Purchases (
      -  purchaseId INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
      -  drugCode VARCHAR(20),
      -  quantity INT NOT NULL,
      -  totalAmount DECIMAL(10, 2) NOT NULL,
      -  dateTime TIMESTAMP NOT NULL,
      -  buyer VARCHAR(100),
      -  FOREIGN KEY (drugCode) REFERENCES Drugs(drugCode)
      -  );
-
      -  CREATE TABLE DrugSuppliers (
      - drugCode VARCHAR(20),
      - supplierId INT,
      - PRIMARY KEY (drugCode, supplierId),
      - FOREIGN KEY (drugCode) REFERENCES Drugs(drugCode),
      - FOREIGN KEY (supplierId) REFERENCES Suppliers(supplierId)
        );

##  Run the Application:
    - Run the app to start using it!
