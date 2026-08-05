# AL MANARA BANK

Java Console Application | OOP Project

A console-based Java banking system for a small bank branch. Employees can register customers, open and manage accounts, process transactions, and run periodic maintenance through an interactive menu.

## Table of Contents

- [Overview](#overview)
- [Highlights](#highlights)
- [Features](#features)
- [Account Types and Business Rules](#account-types-and-business-rules)
- [OOP Concepts Used](#oop-concepts-used)
- [Project Structure](#project-structure)
- [Technologies](#technologies)
- [How to Run](#how-to-run)
- [Example Application Flow](#example-application-flow)
- [Design Decisions](#design-decisions)
- [Testing Scenarios](#testing-scenarios)
- [Author](#author)

## Overview

Al Manara Bank is a console-based Java banking system that simulates a small bank branch. It was developed as an educational project to demonstrate object-oriented programming, clean design, and practical software organization.

The system manages customers, multiple account types, and money movement through an interactive menu. Its main goal is to keep all data consistent by enforcing account-specific rules, such as no negative savings balances, no overdrafts beyond the limit, and no withdrawals before a fixed deposit matures. Transfers are rolled back automatically if any step fails.

The design is centered on an abstract `Account` base class, account-specific subclasses, interfaces for shared capabilities, and a central `Bank` service that coordinates operations. Data is stored in memory with fixed-size arrays, so the project runs with no external dependencies.

## Highlights

- Clean OOP design using abstraction, inheritance, polymorphism, and interfaces.
- Account-specific business rules without duplicated logic.
- Safe transaction handling with automatic rollback.
- Input validation and clear separation of responsibilities.

## Features

- Customer registration with automatic customer ID generation
- Account opening with automatic account number generation
- Support for Savings, Current, and Fixed Deposit accounts
- Deposit and withdrawal operations
- Transfers between accounts
- Account search by number and by type
- Display of customer accounts, all branch accounts, and all customers
- Close, freeze, and activate account operations
- Monthly processing (fees, interest, maturity updates)
- Input validation and error handling

## Account Types and Business Rules

All accounts enforce a minimum opening balance and a minimum transaction amount, configured per account type.

**Savings**
- Balance can never go negative.
- Tracks monthly withdrawals; the counter resets on each monthly update.
- Earns annual interest based on the account rate plus the customer tier bonus.

**Current**
- Supports overdraft, but the balance cannot go below the overdraft limit of 1,000.
- Does not earn interest.

**Fixed Deposit**
- Opened with a fixed maturity period.
- Blocks withdrawals and closure until maturity.
- Counts down the remaining months on each monthly update.
- Earns the highest base interest rate.

## OOP Concepts Used

| Concept | How it is used |
| --- | --- |
| Encapsulation | Fields are private and balances change only through validated methods. |
| Inheritance | All account types extend the abstract `Account` base class. |
| Polymorphism | The bank operates on `Account` references; each subclass overrides rules and display behavior. |
| Abstraction | `Account` defines the contract (`canDecreaseBalance`, `applyWithdrawalRules`) and hides implementation details. |
| Interfaces | `MonthlyUpdatable` and `InterestBearing` represent shared capabilities used by multiple classes. |
| Enums | `AccountType`, `AccountStatus`, and `CustomerTier` encode domain values with behavior such as rates, fees, and limits. |
| Composition | A `Customer` owns an array of `Account` objects, and each account references its owner. |

## Project Structure

```text
AlManaraBank/
└── src/
    ├── bank/
    │   ├── Main.java                      Entry point and interactive menu
    │   ├── accounts/
    │   │   ├── Account.java               Abstract base account
    │   │   ├── SavingsAccount.java        Savings business rules
    │   │   ├── CurrentAccount.java        Overdraft behavior
    │   │   ├── FixedDepositAccount.java   Maturity rules
    │   │   ├── InterestBearing.java       Interest computation interface
    │   │   ├── AccountType.java           Account types and per-type configuration
    │   │   └── AccountStatus.java         ACTIVE / FROZEN / CLOSED
    │   ├── customers/
    │   │   ├── Customer.java              Customer entity and account ownership
    │   │   └── CustomerTier.java          Tiers, fees, and interest bonuses
    │   ├── interfaces/
    │   │   └── MonthlyUpdatable.java      Monthly-processing contract
    │   └── services/
    │       └── Bank.java                  Branch logic and coordination
    └── utils/
        ├── InputReader.java               Safe, looping console input
        ├── Validator.java                 Field and domain validation
        └── ConsoleUtils.java              Shared printing and formatting helpers
```

The `bank` package holds the application logic (accounts, customers, interfaces, and the `Bank` service), while `utils` provides reusable input, validation, and console helpers.

## Technologies

- Java (17+)
- Object-Oriented Programming
- Console application
- Arrays for in-memory data storage
- Makefile for build automation

## How to Run

Prerequisite: Java 17 or later and Make.

1. Clone the repository:

```
git clone <repository-url>
cd AlManaraBank
```

2. Build the project:

```
make
```

3. Run the application:

```
make run
```

Utility targets:

```
make clean  # remove compiled output
make re     # clean and rebuild
```

## Example Application Flow

```
Register Customer → Open Account → Deposit / Withdraw → Transfer → Search / Manage Accounts
```

Each step is available through the main menu. Register a customer, open an account for them, deposit or withdraw money, transfer between accounts, and search or manage accounts as needed. Customer IDs and account numbers are generated automatically.

## Design Decisions

- `Account` as an abstract base class: it owns balance, status, and the shared deposit and withdraw flow used by all account types.
- Subclasses handle account-specific rules: each type overrides the template method (`applyWithdrawalRules`) to enforce its own constraints.
- Interfaces for shared capabilities: `MonthlyUpdatable` and `InterestBearing` let the bank treat classes uniformly without coupling to concrete types.
- `Bank` service as orchestrator: lookups, transfers, and monthly processing live in one service, while `Main` stays focused on the user interface.
- Safe transfer rollback: the withdrawal and deposit steps are guarded so a failure anywhere rolls the transfer back and keeps balances consistent.
- In-memory array storage: fixed capacities (100 customers, 500 accounts, 50 accounts per customer) keep the system simple and dependency-free.
- Validation separation: `InputReader` and `Validator` handle all input and field validation outside the business logic, so account and customer classes stay focused on domain rules.
- Manual monthly processing: the application simulates periodic banking operations through a menu-triggered action instead of a real-time scheduler. This operation applies customer tier fees, updates interest, resets savings withdrawal counters, and progresses fixed deposit maturity. This approach keeps the console application simple and dependency-free.

## Testing Scenarios

- Duplicate national ID rejection.
- Invalid input handling.
- Savings withdrawal restrictions.
- Current account overdraft limits.
- Fixed deposit maturity rules.
- Successful and failed transfers with rollback.
- Account closure validation (zero balance required).
- Account status restrictions (frozen accounts reject transactions).
- Monthly processing behavior (fees, interest, withdrawal counter reset, and maturity updates).

## Author

- **Name:** Diana Khalil
- **GitHub:** [dikhalil](https://github.com/dikhalil)
- **Email:** [diananaif02@gmail.com](mailto:diananaif02@gmail.com)
