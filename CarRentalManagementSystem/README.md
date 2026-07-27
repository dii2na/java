# Car Rental Management System

A console-based car rental management system built in Java for **SpeedWay Rentals**. The application allows rental office employees to manage a vehicle fleet, register customers, process rentals and returns, and view office statistics , all through an interactive text menu.

## About

This project was developed as a small Java OOP assignment during the Route training program.  
It focuses on applying object-oriented programming concepts, including inheritance, encapsulation, validation, and class design.

## Features

- Add and manage regular and luxury vehicles in the fleet
- Register customers with phone number validation
- Rent cars to customers with automatic cost calculation (14% tax included)
- Return rented cars and update availability in real time
- Search cars by ID or by brand name
- Display all cars, available cars, or all customers with their current rental
- View fleet statistics: total income, rented cars, most expensive car, average daily price
- Full input validation with clear error messages at every step
- Safe exit with a summary of total cars, customers, and income

## Object-Oriented Design

| Class | Responsibility |
|-------|---------------|
| `Car` | Base class for vehicles. Stores id, brand, model, year, price per day, and availability. Handles rental cost calculation including the 14% tax rate. |
| `LuxuryCar` | Extends `Car`. Adds an insurance fee and enforces a minimum rental period of 3 days. |
| `Customer` | Represents a customer. Tracks id, name, phone, rented car, rental days, and total amount paid. |
| `RentalSystem` | Core business logic. Manages arrays of cars and customers, handles rental/return operations, queries, and statistics. |
| `InputReader` | Handles all user input with type checking, range validation, and re-prompting on invalid entries. |
| `Validator` | Static utility methods for validating positive numbers, non-negative values, ranges, and non-empty strings. |
| `Main` | Entry point. Controls the program loop, displays the menu, reads user choices, and delegates to the appropriate handler. |

## Technologies

- Java 26
- No external libraries - standard library only

## Project Structure

```
RentalManagementSystem/
├── src/
│   ├── Main.java
│   ├── Car.java
│   ├── LuxuryCar.java
│   ├── Customer.java
│   ├── RentalSystem.java
│   ├── InputReader.java
│   └── Validator.java
├── Makefile
└── README.md
```
Compiled `.class` files are generated inside the `bin/` directory.

## How to Compile and Run

### Using Make

```bash
# Compile all source files
make 

# Run the program
make run

# Clean compiled files
make clean

# Clean and recompile
make re
```

### Manual

```bash
# Compile
mkdir -p bin
javac -d bin src/*.java

# Run
java -cp bin Main
```

## Menu Options

```
======================================
        SPEEDWAY RENTALS
======================================
1.  Add Regular Car
2.  Add Luxury Car
3.  Add Customer
4.  Display All Cars
5.  Display Available Cars
6.  Rent Car
7.  Return Car
8.  Search Car By ID
9.  Search Car By Brand
10. Display Customers
11. Display Statistics
0.  Exit
======================================
```

| Option | Description |
|--------|-------------|
| 1 | Add a regular car (ID, brand, model, year, price/day) |
| 2 | Add a luxury car (same as above + insurance fee) |
| 3 | Register a new customer (ID, name, phone) |
| 4 | List all cars in the fleet |
| 5 | List only currently available cars |
| 6 | Rent a car to a customer for a number of days |
| 7 | Return a rented car |
| 8 | Search for a car by its ID |
| 9 | Search for cars by brand name |
| 10 | Display all customers with their current rental |
| 11 | View fleet statistics |
| 0 | Exit with a summary |

## Validation Rules

| # | Rule |
|---|------|
| 1 | Car IDs must be unique |
| 2 | Customer IDs must be unique |
| 3 | Fleet size cannot exceed 20 cars |
| 4 | Customer list cannot exceed 20 customers |
| 5 | Price per day must be greater than zero |
| 6 | Insurance fees cannot be negative |
| 7 | Manufacturing year must be between 1990 and the current year |
| 8 | A car that is already rented cannot be rented again |
| 9 | A customer may hold only one car at a time |
| 10 | Number of rental days must be greater than zero |
| 11 | Luxury cars require a minimum rental of 3 days |
| 12 | A customer with no rented car cannot return one |
| 13 | Invalid menu choices display an error and redisplay the menu |

## Author

## Author

**dikhalil(Diana Khalil)** - [diananaif02@gmail.com](mailto:diananaif02@gmail.com)