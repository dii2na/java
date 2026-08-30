# Restaurant Order Manager

A console-based Java application that manages a restaurant's menu and order
workflow, built as a Java Collections assignment. The program demonstrates
`ArrayList`, `LinkedList`, `HashMap`, and `LinkedHashMap` through a complete
menu-driven order-management system.

## Features

The main menu provides the following options:

| #  | Option                  | #  | Option                   |
|----|-------------------------|----|--------------------------|
|  1 | Add Menu Item           |  9 | Add Order to Kitchen Queue |
|  2 | Remove Menu Item        | 10 | Process Next Order       |
|  3 | Display Menu            | 11 | Search Order             |
|  4 | Search Menu Item        | 12 | Check Order Status       |
|  5 | Create Order            | 13 | Display Completed Orders |
|  6 | Add Item to Order       | 14 | Cancel Order             |
|  7 | Remove Item from Order  | 15 | Exit                     |
|  8 | Display Order           |    |                          |

Every prompt is validated and re-asks until a valid value is entered, so the
program never crashes on bad input.

## Order Lifecycle

```
Create Order  ->  PENDING  ->  Send to Kitchen  ->  IN_KITCHEN  ->  Process  ->  COMPLETED
                  PENDING  ->  Cancel           ->  CANCELLED
```

- An order must contain at least one item before it can be sent to the kitchen.
- Only the next order in the FIFO kitchen queue can be processed.
- Completed and cancelled orders are permanent records and can no longer be
  modified in any way.

## Java Collections Used

| Collection              | Responsibility                                              |
|-------------------------|-------------------------------------------------------------|
| `ArrayList<MenuItem>`   | Stores and manages the restaurant menu                      |
| `LinkedList<Order>`     | FIFO kitchen queue of orders to be processed                |
| `HashMap<Integer, Order>` | Permanent record of every order ever created             |
| `LinkedHashMap<Integer, Order>` | Completed orders, preserved in completion order   |

## Requirements

- Java 17 or later (developed with Java 26)
- `make` (optional; for manual build see below)

## Build and Run

Using `make`:

```bash
make                # compile into ./bin
make run            # run the program
make clean          # remove compiled classes
make re             # clean and rebuild
```

Manually:

```bash
javac -d bin $(find src -name '*.java')
java  -cp bin ROM.Main
```

## Example Session

```text
  1.  Add Menu Item
  2.  Remove Menu Item
  3.  Display Menu
  4.  Search Menu Item
  5.  Create Order
  6.  Add Item to Order
  7.  Remove Item from Order
  8.  Display Order
  9.  Add Order to Kitchen Queue
 10.  Process Next Order
 11.  Search Order
 12.  Check Order Status
 13.  Display Completed Orders
 14.  Cancel Order
 15.  Exit

Enter Choice: 1
Enter Menu Item ID: 1
Enter Name: Burger
Enter Price: 150
Enter Category: Main Course
Menu item added successfully.
```

## Project Structure

```
src/ROM/
├── Main.java                 # Program flow, menu, and input handling
├── enums/
│   └── OrderStatus.java      # PENDING, IN_KITCHEN, COMPLETED, CANCELLED
├── models/
│   ├── MenuItem.java         # Menu item with id, name, price, category
│   ├── Order.java            # Order with items, total, and status
│   └── OrderItem.java        # Item within an order (item + quantity)
├── services/
│   └── Restaurant.java       # Owns all four collections and operations
└── utils/
    ├── ConsoleUtils.java     # Output and formatting helpers
    ├── InputReader.java      # Re-prompting console input readers
    └── Validator.java        # Reusable input validation
```

## Business Rules and Validation

- IDs must be positive; duplicate menu item and order IDs are rejected.
- Names and categories must be non-blank.
- Prices and quantities must be positive.
- An order cannot be sent to the kitchen twice or while cancelled/completed.
- Items can only be added or removed while the order is `PENDING`; once the
  order is in the kitchen or finished, its items are locked.
- Cancelling is only possible while the order is still `PENDING`.
- The kitchen queue processes one order at a time in FIFO order.