# Nile Library Management System

An educational Oracle SQL assignment that builds a small library management database from scratch. The project follows **21 ordered steps** that cover table creation, data manipulation, filtering, sorting, string and date functions, subqueries, and several classic Oracle SQL pitfalls.

Everything lives in a single script, `Nile_Library_Assignment.sql`, written for **Oracle Database 26ai** and executed with **Oracle SQL Developer**.

---

## Contents

- [Setup / Prerequisites](#setup--prerequisites)
- [Development Environment](#development-environment)
- [Database Connection Setup](#database-connection-setup)
- [Database Schema](#database-schema)
- [SQL Concepts Reference](#sql-concepts-reference)
- [Important Differences](#important-differences)
- [Assignment Walkthrough](#assignment-walkthrough)
- [Key Lessons](#key-lessons)
- [How to Run](#how-to-run)
- [Project Structure](#project-structure)

---

## Setup / Prerequisites

To run the SQL file, you need:

- [Oracle Database 26ai Free](https://www.oracle.com/database/free/)
- [Oracle SQL Developer](https://www.oracle.com/database/sqldeveloper/)
- An Oracle database connection
- The SQL script included in this repository (`Nile_Library_Assignment.sql`)

---

## Development Environment

I personally developed and tested this assignment using:

- Debian GNU/Linux 13 as the SQL client
- Oracle Database 26ai Free running on Oracle Linux 9.8
- Oracle Linux running inside a VMware virtual machine
- Oracle SQL Developer installed on Debian

> This setup is specific to my development environment. It is not required to run the project. You can use any supported environment that provides Oracle Database 26ai Free and Oracle SQL Developer.

### Alternative Setup

Windows users can install Oracle Database and Oracle SQL Developer directly on Windows, so Debian, Oracle Linux, and VMware are not needed. The SQL script itself is not tied to a specific operating system or virtual machine setup — it only requires a connection to an Oracle database.

---

## Database Connection Setup

This is the connection configuration used **in my own development environment** (SQL Developer on Debian connecting to the Oracle database on the Oracle Linux VM):

| Setting         | Value                          |
| --------------- | ------------------------------ |
| Connection Type | Basic                          |
| Hostname        | Oracle Linux VM IP address     |
| Port            | `1521`                         |
| Service Name    | `FREEPDB1`                     |
| Role            | Default                        |
| Username        | Oracle database user           |

In my setup, the Oracle Linux virtual machine was configured to allow TCP connections on port `1521`, so SQL Developer on Debian could connect to the Oracle database. The connection was tested successfully before running the SQL script. No actual IP address, password, or other credentials are stored in this repository.

---

## Database Schema

The assignment creates **four tables**:

| Table                         | What it represents                                                                                     |
| ----------------------------- | ------------------------------------------------------------------------------------------------------ |
| `CATEGORIES`                  | Book categories (Technology, Fiction, History). A small lookup table used to classify books.            |
| `MEMBERS`                     | Registered library members: name, email, phone, city, join date, and loyalty points.                    |
| `BOOKS`                       | The book catalogue: title, author, category, price, available copies, and publication year.             |
| `LOANS`                       | Which member borrowed which book, when it was loaned, and the loan status. Renamed to `BOOK_LOANS` in Step 6. |

### Conceptual relationships

The tables are related through matching column values:

```text
CATEGORIES  1 ---*  BOOKS          (books.category_id  points to a category)
MEMBERS     1 ---*  BOOK_LOANS     (book_loans.member_id points to a member)
BOOKS       1 ---*  BOOK_LOANS     (book_loans.book_id   points to a book)
```

- One category is intended to describe many books; each book is associated with one category through `category_id`.
- One member can take many loans; each loan belongs to one member.
- One book can appear in many loans; each loan refers to one book.

> **Scope note:** these relationships are **logical only**. The assignment did not create `PRIMARY KEY` or `FOREIGN KEY` constraints — the link is expressed through matching column values. Constraints, indexes, views, triggers, sequences, and joins were not part of this assignment.

---

## SQL Concepts Reference

### Data types and column rules

| Concept      | Beginner explanation                                                        | Example in the assignment                        |
| ------------ | --------------------------------------------------------------------------- | ------------------------------------------------ |
| `VARCHAR2`   | Variable-length text. The size is the maximum number of characters.          | `email VARCHAR2(100)`                            |
| `NUMBER`     | Numeric column with no fixed scale — used for IDs, counts, years.            | `member_id NUMBER`                               |
| `NUMBER(8,2)`| Numeric with a precision of 8 digits and 2 decimal places (max `999999.99`). | `price NUMBER(8,2)`                              |
| `DATE`       | Stores a date **and** a time. Used for when something happened by day.       | `joined_on DATE DEFAULT SYSDATE`                 |
| `TIMESTAMP`  | Like `DATE`, but with fractional-second precision.                          | `loaned_at TIMESTAMP DEFAULT SYSTIMESTAMP`       |
| `DEFAULT`    | The value used automatically when an `INSERT` omits the column.              | `points NUMBER DEFAULT 0`                        |
| `NOT NULL`   | The column must always hold a value; empty inserts are rejected.             | `title VARCHAR2(200) NOT NULL`                    |

```sql
CREATE TABLE members (
    member_id NUMBER,
    full_name VARCHAR2(100) NOT NULL,
    email     VARCHAR2(100),
    city      VARCHAR2(50),
    joined_on DATE DEFAULT SYSDATE,
    points    NUMBER DEFAULT 0
);
```

### Creating and changing tables (DDL)

| Command                            | Purpose                                                              |
| ---------------------------------- | -------------------------------------------------------------------- |
| `CREATE TABLE`                     | Defines a new table with its columns and their rules.                |
| `ALTER TABLE ... ADD`              | Adds a new column to an existing table.                              |
| `ALTER TABLE ... RENAME TO`        | Renames a table.                                                       |
| `TRUNCATE`                         | Removes **all** rows from a table and cannot be undone with `ROLLBACK`. |

```sql
ALTER TABLE books ADD isbn VARCHAR2(20);
ALTER TABLE loans ADD returned_at TIMESTAMP;

ALTER TABLE loans RENAME TO book_loans;
```

### Inserting and modifying data (DML)

| Command    | Purpose                                                                  |
| ---------- | ------------------------------------------------------------------------ |
| `INSERT`   | Adds one row of data. `INSERT INTO table VALUES (...)` fills every column. |
| `UPDATE`   | Modifies existing rows; `WHERE` decides **which** rows change.           |
| `DELETE`   | Removes rows; without `WHERE` it removes every row.                      |
| `COMMIT`   | Makes all pending changes permanent.                                     |
| `ROLLBACK` | Undoes all pending changes since the last `COMMIT`.                      |

```sql
INSERT INTO categories VALUES (2, 'Fiction');

UPDATE books
SET price = price * 1.10
WHERE category_id = (
    SELECT category_id
    FROM categories
    WHERE category_name = 'Fiction'
);

DELETE FROM books WHERE book_id = 10;
ROLLBACK;
```

### Reading data (SELECT)

| Concept                | Beginner explanation                                                        | Example                          |
| ---------------------- | ---------------------------------------------------------------------------- | -------------------------------- |
| `SELECT *`             | Returns **all** columns. Handy for inspection, not ideal for reports.        | `SELECT * FROM members;`         |
| Column aliases (`AS`)  | Renames a column in the output only. Quoted aliases may contain spaces.      | `title AS "Book Title"`          |
| `DISTINCT`             | Removes duplicate values so each value appears once.                         | `SELECT DISTINCT city FROM members;` |
| Calculated column      | An expression computed on the fly; the underlying data is not changed.       | `price * 1.14`                   |
| `ROUND(value, n)`      | Rounds a number to `n` decimal places.                                       | `ROUND(price * 1.14, 2)`         |

```sql
SELECT title AS "Book Title",
       author AS "Author",
       price  AS "Price"
FROM books;

SELECT DISTINCT city
FROM members;

SELECT title,
       ROUND(price * 1.14, 2) AS price_vat
FROM books;
```

### Filtering rows

| Concept             | Beginner explanation                                                         | Example                                     |
| ------------------- | ----------------------------------------------------------------------------- | ------------------------------------------- |
| `WHERE`             | Keeps only the rows that satisfy a condition.                                 | `WHERE price > 300`                         |
| `AND` / `OR`        | Combines conditions: **both** must be true, or **at least one** must be true. | `WHERE price > 300 AND copies_available > 0` |
| `BETWEEN`           | Range test that **includes both boundary values**.                            | `WHERE price BETWEEN 100 AND 500`           |
| `IN`                | Shorthand for a list of allowed values on the same column.                    | `WHERE city IN ('Cairo', 'Giza')`           |
| `LIKE`              | Pattern matching for text.                                                    | `WHERE title LIKE 'A%y'`                    |
| `%` wildcard        | Matches any number of characters (including none).                            | `'A%'` = starts with **A**                  |
| `NULL`              | Means *missing or unknown* — not zero, not an empty space.                    | `phone` column with no value                |
| `IS NULL` / `IS NOT NULL` | The only correct way to test for `NULL`.                                | `WHERE phone IS NULL`                       |

```sql
SELECT * FROM books
WHERE price > 300 AND copies_available > 0;

SELECT * FROM books
WHERE price BETWEEN 100 AND 500;

SELECT * FROM members
WHERE city IN ('Cairo', 'Giza', 'Alexandria');

SELECT * FROM books
WHERE title LIKE 'A%y';

SELECT * FROM members
WHERE phone IS NULL;
```

### Sorting

| Concept              | Beginner explanation                                                   |
| -------------------- | ---------------------------------------------------------------------- |
| `ORDER BY`           | Sorts the result rows. Without it, row order is not guaranteed.        |
| `ASC` / `DESC`       | Ascending (smallest first) or descending (largest first). `ASC` is the default. |

```sql
SELECT * FROM books
ORDER BY price DESC,   -- primary sort: most expensive first
         title ASC;    -- ties broken alphabetically
```

### String and date functions

| Concept           | Beginner explanation                                                        | Example                                    |
| ----------------- | --------------------------------------------------------------------------- | ------------------------------------------ |
| `\|\|`            | Concatenates (joins) text values into one string.                           | `full_name \|\| ' — ' \|\| city`           |
| `UPPER`           | Converts text to uppercase.                                                 | `UPPER(full_name)`                         |
| `LENGTH`          | Counts the characters in a string.                                          | `LENGTH(full_name)`                        |
| `SUBSTR`          | Extracts part of a string: `SUBSTR(text, start, length)`.                    | `SUBSTR(email, 1, 5)`                      |
| `INSTR`           | Returns the position of a substring inside a string, or `0` if not found.   | `INSTR(email, '@')`                        |
| `ADD_MONTHS`      | Adds (or subtracts) months from a date.                                     | `ADD_MONTHS(SYSDATE, -6)`                  |
| `SYSTIMESTAMP`    | The current date and time with fractional-second precision (server clock).  | `loaned_at TIMESTAMP DEFAULT SYSTIMESTAMP` |

```sql
SELECT full_name || ' — ' || city AS "Greeting"
FROM members;

SELECT UPPER(full_name)  AS "Upper Name",
       LENGTH(full_name) AS "Name Length"
FROM members;

-- Everything before the "@" in the email address
SELECT SUBSTR(email, 1, INSTR(email, '@') - 1) AS "Email Username"
FROM members;

-- Members who joined more than six months ago
SELECT * FROM members
WHERE joined_on < ADD_MONTHS(SYSDATE, -6);
```

### Subqueries

A **subquery** is a query nested inside another statement. It is evaluated first, and its result is used by the outer statement.

```sql
-- Raise the price of books whose category is 'Fiction'
UPDATE books
SET price = price * 1.10
WHERE category_id = (
    SELECT category_id FROM categories WHERE category_name = 'Fiction'
);

-- Members who never borrowed a book
SELECT *
FROM members
WHERE member_id NOT IN (
    SELECT member_id
    FROM book_loans
);
```

---

## Important Differences

### `DELETE` vs `TRUNCATE`

|                    | `DELETE`                                       | `TRUNCATE`                                          |
| ------------------ | ---------------------------------------------- | --------------------------------------------------- |
| Type               | DML                                            | DDL                                                 |
| Row filtering      | Can remove selected rows with `WHERE`          | Always removes **all** rows                         |
| Undo               | Can be undone with `ROLLBACK`                  | **Cannot** be undone with `ROLLBACK`                |
| Speed              | Slower (row by row, logs each change)          | Very fast (deallocates the data)                    |

In the assignment, `DELETE FROM books` was inspected and then undone with `ROLLBACK`. A `TRUNCATE` would have failed to roll back, because in Oracle `TRUNCATE` is a DDL statement and DDL performs an **implicit `COMMIT`** — it commits everything before it runs and cannot be rolled back afterwards.

### `COMMIT` vs `ROLLBACK`

|              | `COMMIT`                                                     | `ROLLBACK`                                                |
| ------------ | ------------------------------------------------------------ | --------------------------------------------------------- |
| Effect       | Makes all changes since the last commit **permanent**.       | Discards all changes since the last commit.               |
| When to use  | After you have verified the data is correct.                 | After a mistake, or to undo a practice/test operation.     |
| Analogy      | Saving a file.                                               | Discarding unsaved changes and reopening the file.        |

Uncommitted changes are visible within your own session, but `COMMIT` makes them permanent and available to other sessions.

### `NULL` vs an empty string

- `NULL` means **the value is missing or unknown**.
- An empty string (`''`) in Oracle is treated as `NULL` — Oracle does not distinguish between "empty text" and "no value".

### `= NULL` vs `IS NULL`

`=` compares values, and comparing anything with an unknown value produces `UNKNOWN`, never `TRUE` — so the row is never returned.

```sql
SELECT * FROM members WHERE phone = NULL;    -- returns 0 rows (wrong approach)
SELECT * FROM members WHERE phone IS NULL;   -- returns the rows with no phone
```

The rule: always use `IS NULL` / `IS NOT NULL` for `NULL` checks.

### `WHERE` with `AND` / `OR` and operator precedence

In Oracle, **`AND` binds more tightly than `OR`**. The database groups `AND` conditions first, then applies `OR` — regardless of how you read the sentence.

```sql
-- Parsed as:  city = 'Cairo'  OR  (city = 'Giza' AND points >= 100)
WHERE city = 'Cairo' OR city = 'Giza' AND points >= 100;

-- Explicit grouping: (Cairo OR Giza) AND points >= 100
WHERE (city = 'Cairo' OR city = 'Giza') AND points >= 100;
```

These two queries return **different row counts** (5 vs 4 in Step 21). Parentheses make the intent explicit and prevent logic bugs.

### `BETWEEN` includes its boundaries

`price BETWEEN 100 AND 500` is exactly equivalent to `price >= 100 AND price <= 500` — both `100` and `500` are **included**.

The bounds also matter in order: `BETWEEN 500 AND 100` asks for values that are `>= 500` **and** `<= 100` at the same time, which is impossible, so it returns **0 rows**.

### Why a SELECT alias such as `price_vat` cannot be used in `WHERE`

Oracle evaluates a query in this order: **`FROM` → `WHERE` → `GROUP BY` → `HAVING` → `SELECT` → `ORDER BY`**.

The `WHERE` clause runs **before** the `SELECT` list is computed, so an alias created in `SELECT` does not exist yet when `WHERE` is evaluated. Using it raises:

```text
ORA-00904: invalid identifier
```

```sql
-- Fails with ORA-00904
SELECT title, price * 1.14 AS price_vat
FROM books
WHERE price_vat > 500;

-- Correct: repeat the expression, or wrap the query in a subquery
SELECT * FROM (
    SELECT title, price * 1.14 AS price_vat FROM books
)
WHERE price_vat > 500;
```

Aliases **can** be used in `ORDER BY`, because sorting happens after the `SELECT` list is evaluated.

---

## Assignment Walkthrough

| Step  | What was practiced                                                                                                                                                 |
| ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1     | Created the four tables (`CATEGORIES`, `MEMBERS`, `BOOKS`, `LOANS`) using `CREATE TABLE`, data types, `NOT NULL`, and `DEFAULT`.                                     |
| 2     | Inserted the library's sample data with `INSERT INTO ... VALUES`, using `ADD_MONTHS(SYSDATE, ...)` and `NULL` values.                                                 |
| 3     | Saved the data with `COMMIT`, then used `ALTER TABLE ... ADD` to add `isbn` to `BOOKS` and `returned_at` to `LOANS`.                                                  |
| 4     | Marked one loan as `RETURNED` with `UPDATE`, and raised all Fiction book prices by 10% using a **subquery** in `WHERE`; then committed the changes.                    |
| 5     | Ran `DELETE FROM books`, inspected the empty table with `SELECT *`, then `ROLLBACK` to restore it — and learned why `TRUNCATE` could not be undone the same way.       |
| 6     | Found members who never borrowed a book with a `NOT IN` subquery, deleted one of them, renamed `LOANS` to `BOOK_LOANS` with `ALTER TABLE ... RENAME`, and committed.   |
| 7     | Displayed all member data with `SELECT *` — the simplest way to inspect a table.                                                                                    |
| 8     | Selected specific columns and renamed them for readability with `AS` aliases (`"Book Title"`, `"Author"`, `"Price"`).                                                 |
| 9     | Used `DISTINCT` to list each member city only once, removing duplicates.                                                                                            |
| 10    | Built a calculated column (`price * 1.14`) for prices with 14% VAT and rounded the result with `ROUND(..., 2)`.                                                      |
| 11    | Filtered rows with `WHERE city = 'Cairo'` and compared it with `city = 'cairo'` to see that Oracle string comparison is **case-sensitive**.                           |
| 12    | Combined two conditions with `AND`: books priced above 300 **and** with at least one copy available.                                                                 |
| 13    | Used `BETWEEN 100 AND 500` to select a price range and confirmed that both boundary values are included.                                                             |
| 14    | Selected members from Cairo, Giza, or Alexandria first with chained `OR`, then with `IN` — and saw that `IN` is cleaner for checking one column against many values.   |
| 15    | Used `LIKE 'A%y'` with the `%` wildcard to find titles that start with **A** and end with **y**.                                                                     |
| 16    | Found members with no phone using `IS NULL`, and learned why `phone = NULL` returns no rows.                                                                         |
| 17    | Sorted results with `ORDER BY price DESC, title ASC` — a descending primary sort with an ascending tie-breaker.                                                      |
| 18    | Combined everything into a contact-style report: aliases + `IN` + `IS NOT NULL` + `ORDER BY points DESC`.                                                            |
| 19    | Practiced string functions: `||` concatenation, `UPPER`, `LENGTH`, and extracting the email username with `SUBSTR` and `INSTR`.                                       |
| 20    | Filtered on dates and numbers together: joined more than six months ago (`ADD_MONTHS(SYSDATE, -6)`) **and** fewer than 200 points.                                    |
| 21    | Predicted and compared results for `AND`/`OR` precedence, parentheses, reversed `BETWEEN` bounds, and using a `SELECT` alias inside `WHERE`.                          |

### Step 21 — expected results

| Query | Concept tested                                  | Expected result                                                                                          |
| ----- | ----------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| 1     | `AND` binds tighter than `OR`                   | **5 rows** — all Cairo members, plus Giza members with `points >= 100`                                    |
| 2     | Parentheses force `(Cairo OR Giza)` first       | **4 rows** — only Cairo/Giza members who also have `points >= 100`                                        |
| 3     | Reversed `BETWEEN 500 AND 100`                  | **0 rows** — `price >= 500 AND price <= 100` can never be true                                            |
| 4     | Using alias `price_vat` in `WHERE`              | **Oracle error `ORA-00904`** — the alias does not exist yet when `WHERE` is evaluated                     |

---

## Key Lessons

- **Design the schema carefully.** Data types, `NOT NULL`, and `DEFAULT` rules help control the data stored in the tables.
- **Be careful with `UPDATE` and `DELETE`.** Without a `WHERE` clause, they can affect every row.
- **Understand transactions.** `COMMIT` makes changes permanent, while `ROLLBACK` can undo uncommitted DML changes.
- **Handle `NULL` correctly.** Use `IS NULL` / `IS NOT NULL`, and remember that Oracle treats an empty string as `NULL`.
- **Be explicit with conditions.** Parentheses clarify `AND`/`OR` logic, `IN` simplifies multiple comparisons, and aliases improve query output.

---

## How to Run

1. Start your Oracle Database instance.
2. Open Oracle SQL Developer.
3. Create or open an Oracle database connection.
4. Open `Nile_Library_Assignment.sql`.
5. Execute the script in order from Step 1 to Step 21.

> In my development environment, Oracle Database ran on an Oracle Linux virtual machine in VMware and SQL Developer ran on Debian GNU/Linux. This is only one possible setup.

The steps should be executed in order because some later steps depend on changes made in earlier steps — especially Step 6, which renames `LOANS` to `BOOK_LOANS`.

> **Tip:** run the file as a **script** (`F5` / `Run Script`), or execute statements step by step by placing the cursor inside a statement and pressing `Ctrl + Enter`.

---

## Project Structure

```text
Nile-Library-SQL/
├── README.md
└── Nile_Library_Assignment.sql
```
