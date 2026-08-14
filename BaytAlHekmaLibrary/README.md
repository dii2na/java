# Bayt Al Hekma Library

A console-based library management system written in Java. It manages a catalogue of books, magazines, and DVDs, along with members, loans, renewals, and overdue fines.

## Features

- Register members and manage a catalogue of books, magazines, and DVDs
- Borrow, return, renew, and search items
- View items by status and browse the full catalogue
- Automatic fine calculation with per-type rules and an administrative charge on overdue returns
- Track member balances, payments, and borrowing limits
- Library report with loan rate, outstanding balance, and projected fines

## Requirements

- Java 17 or later

## Build and Run

Using make:

```bash
make        # compile
make run    # run the program
make clean  # remove compiled output
```

Or directly:

```bash
javac -d out $(find src -name '*.java')
java -cp out baytalhekma.Main
```

## Loan and Fine Rules

| Item Type | Loan Period | Fine | Renewals |
|-----------|-------------|------|----------|
| Book | 14 days | 5.00 EGP per day | Up to 2 |
| Magazine | 7 days | 3.00 EGP per day (max 30.00) | Up to 1 |
| DVD | 3 days | 15.00 EGP per day | None |

- A fixed 2.00 EGP administrative charge is added when an item is returned overdue.
- Members may hold up to 3 items and cannot borrow when their balance exceeds 100.00 EGP.
- Membership IDs are exactly 4 alphanumeric characters.

## Project Structure

```
src/baytalhekma/
├── Main.java                  Entry point and menu
├── enums/                     ItemCategory, ItemStatus
├── interfaces/                Renewable
├── models/
│   ├── items/                 LibraryItem, Book, Magazine, DVD
│   ├── members/               Member
│   └── results/               ReturnBreakdown
├── services/                  Library
└── utils/                     ConsoleUtils, InputReader, Validator
```

The item hierarchy uses inheritance and polymorphism: `LibraryItem` is abstract, each subclass defines its own loan period, fine rule, and category, and the `Renewable` interface controls renewal behaviour.
