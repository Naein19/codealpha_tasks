# Code Alpha Java Projects

This repository contains multiple Java projects developed as part of **Code Alpha tasks**.  
Each project is independent and demonstrates different concepts of **Object-Oriented Programming (OOP)**, **data handling**, and **Java fundamentals**.

---

## 📂 Project 1:  Stock Market Simulation

### 📌 Overview
This project is a **basic Stock Market simulation system** implemented in Java.  
It allows creating, holding, and tracking stock portfolios, simulating transactions such as buying and selling.

### 🛠️ Features
- **Stock.java** → Represents an individual stock (ticker, price, quantity, etc.).
- **StockHolding.java** → Represents an investor’s stock holding with purchase details.
- **Portfolio.java** → A collection of stock holdings, supports adding/removing stocks.
- **Transaction.java** → Simulates stock transactions (buy/sell).
- **TradingApp.java** → Main application that runs the stock market simulation.
- **portfolio.json** → Stores portfolio data in JSON format.
- Uses **Gson library (`gson-2.10.1.jar`)** for JSON serialization & deserialization.

### ▶️ How to Run
1. Compile all `.java` files:
   ```bash
   javac -cp gson-2.10.1.jar *.java
