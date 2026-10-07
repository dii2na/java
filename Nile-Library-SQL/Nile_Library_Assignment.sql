-- Step 1: Create the four tables

CREATE TABLE categories (
    category_id NUMBER,
    category_name VARCHAR2(100) NOT NULL
);

CREATE TABLE members (
    member_id NUMBER,
    full_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100),
    phone VARCHAR2(20),
    city VARCHAR2(50),
    joined_on DATE DEFAULT SYSDATE,
    points NUMBER DEFAULT 0
);

CREATE TABLE books (
    book_id NUMBER,
    title VARCHAR2(200) NOT NULL,
    author VARCHAR2(100),
    category_id NUMBER,
    price NUMBER(8,2),
    copies_available NUMBER,
    published_year NUMBER
);

CREATE TABLE loans (
    loan_id NUMBER,
    member_id NUMBER,
    book_id NUMBER,
    loaned_at TIMESTAMP DEFAULT SYSTIMESTAMP,
    status VARCHAR2(20) DEFAULT 'ACTIVE'
);

-- Step 2: Insert the provided data

INSERT INTO categories VALUES (1, 'Technology');
INSERT INTO categories VALUES (2, 'Fiction');
INSERT INTO categories VALUES (3, 'History');

INSERT INTO members VALUES (1, 'Ahmed Hassan', 'ahmed.hassan@mail.com', '01012345678', 'Cairo', ADD_MONTHS(SYSDATE, -14), 250);
INSERT INTO members VALUES (2, 'Mona Ali', 'mona.ali@mail.com', '01123456789', 'Cairo', ADD_MONTHS(SYSDATE, -9), 120);
INSERT INTO members VALUES (3, 'Youssef Samir', 'youssef@gmail.com', '01234567890', 'Giza', ADD_MONTHS(SYSDATE, -3), 0);
INSERT INTO members VALUES (4, 'Amira Tarek', 'amira@mail.com', '01098765432', 'Alexandria', ADD_MONTHS(SYSDATE, -20), 80);
INSERT INTO members VALUES (5, 'Karim Nabil', 'karim@gmail.com', NULL, 'Cairo', ADD_MONTHS(SYSDATE, -7), 150);
INSERT INTO members VALUES (6, 'Sara Mahmoud', 'sara@mail.com', '01155551234', 'Giza', ADD_MONTHS(SYSDATE, -2), 400);
INSERT INTO members VALUES (7, 'Omar Fathy', 'omar@yahoo.com', '01277778888', 'Cairo', ADD_MONTHS(SYSDATE, -12), 95);
INSERT INTO members VALUES (8, 'Laila Adel', 'laila@mail.com', '01066669999', 'Mansoura', ADD_MONTHS(SYSDATE, -1), 30);

INSERT INTO books VALUES (1, 'Algorithms Made Simple', 'Omar Salem', 1, 450.00, 5, 2020);
INSERT INTO books VALUES (2, 'Ancient Egypt', 'Hana Mostafa', 3, 320.00, 3, 2018);
INSERT INTO books VALUES (3, 'Pocket SQL Guide', 'Nadia Fouad', 1, 85.00, 12, 2022);
INSERT INTO books VALUES (4, 'Spring Boot in Action', 'Tarek Hegazy', 1, 500.00, 4, 2023);
INSERT INTO books VALUES (5, 'Complete Oracle Reference', 'Mina Gerges', 1, 1250.00, 2, 2021);
INSERT INTO books VALUES (6, 'The Desert Night', 'Laila Hamdy', 2, 150.00, 0, 2015);
INSERT INTO books VALUES (7, 'Egyptian History', 'Hana Mostafa', 3, 280.00, 6, 2019);
INSERT INTO books VALUES (8, 'City of Lights', NULL, 2, 199.99, 7, 2017);
INSERT INTO books VALUES (9, 'Clean Code Basics', 'Youssef Anwar', 1, 600.00, 8, 2020);
INSERT INTO books VALUES (10, 'The Last Pharaoh', 'Amr Zaki', 3, 100.00, 1, 2016);

INSERT INTO loans (loan_id, member_id, book_id) VALUES (1, 1, 1);
INSERT INTO loans (loan_id, member_id, book_id) VALUES (2, 2, 3);
INSERT INTO loans (loan_id, member_id, book_id) VALUES (3, 3, 2);
INSERT INTO loans (loan_id, member_id, book_id) VALUES (4, 1, 4);
INSERT INTO loans (loan_id, member_id, book_id) VALUES (5, 6, 5);
INSERT INTO loans (loan_id, member_id, book_id) VALUES (6, 7, 9);

-- Step 3: Commit the data and alter the tables

COMMIT;

ALTER TABLE books
ADD isbn VARCHAR2(20);

ALTER TABLE loans
ADD returned_at TIMESTAMP;

-- Step 4: Return one loan and increase Fiction book prices

UPDATE loans
SET status = 'RETURNED',
    returned_at = SYSTIMESTAMP
WHERE loan_id = 1;

UPDATE books
SET price = price * 1.10
WHERE category_id = (
    SELECT category_id
    FROM categories
    WHERE category_name = 'Fiction'
);

COMMIT;

-- Step 5: Delete all books, inspect, then rollback

DELETE FROM books;

SELECT * FROM books;

ROLLBACK;

-- TRUNCATE is a DDL statement in Oracle and performs an implicit COMMIT,
-- so its effect cannot be undone using ROLLBACK.

-- Step 6: Delete one member who never borrowed a book and rename LOANS

SELECT *
FROM members
WHERE member_id NOT IN (
    SELECT member_id
    FROM loans
);

DELETE FROM members
WHERE member_id = 4;


ALTER TABLE loans
RENAME TO book_loans;

COMMIT;

-- Step 7: Display all member data

SELECT *
FROM members;

-- Step 8: Display book title, author, and price with aliases

SELECT title AS "Book Title",
       author AS "Author",
       price AS "Price"
FROM books;

-- Step 9: Display distinct member cities

SELECT DISTINCT city
FROM members;

-- Step 10: Display book prices including 14% VAT

SELECT title,
       ROUND(price * 1.14, 2) AS price_vat
FROM books;

-- Step 11: Display members who live in Cairo

SELECT *
FROM members
WHERE city = 'Cairo';

SELECT *
FROM members
WHERE city = 'cairo';

-- String comparisons are case-sensitive, so 'cairo' does not match 'Cairo'.

-- Step 12: Display books priced above 300 with available copies

SELECT *
FROM books
WHERE price > 300
  AND copies_available > 0;
  
-- Step 13: Display books priced between 100 and 500

SELECT *
FROM books
WHERE price BETWEEN 100 AND 500;

-- Step 14: Display members from Cairo, Giza, or Alexandria

SELECT *
FROM members
WHERE city = 'Cairo'
   OR city = 'Giza'
   OR city = 'Alexandria';

SELECT *
FROM members
WHERE city IN ('Cairo', 'Giza', 'Alexandria');

-- IN is cleaner and easier to read when checking multiple values for the same column.

-- Step 15: Display books whose title starts with A and ends with y

SELECT *
FROM books
WHERE title LIKE 'A%y';

-- Step 16: Display members with no phone number

SELECT *
FROM members
WHERE phone IS NULL;

-- phone = NULL does not work because NULL represents a missing/unknown value.
-- IS NULL must be used to check for NULL values.

-- Step 17: Display books ordered by price descending and title ascending

SELECT *
FROM books
ORDER BY price DESC,
         title ASC;
         
-- Step 18: Display a contact sheet for Cairo and Giza members

SELECT full_name AS "Member Name",
       phone AS "Phone",
       city AS "City",
       points AS "Points"
FROM members
WHERE city IN ('Cairo', 'Giza')
  AND phone IS NOT NULL
ORDER BY points DESC;

-- Step 19: Work with member text data

SELECT full_name || ' — ' || city AS "Greeting"
FROM members;

SELECT UPPER(full_name) AS "Upper Name",
       LENGTH(full_name) AS "Name Length"
FROM members;

SELECT SUBSTR(email, 1, INSTR(email, '@') - 1) AS "Email Username"
FROM members;

-- Step 20: Display members who joined more than 6 months ago with fewer than 200 points

SELECT *
FROM members
WHERE joined_on < ADD_MONTHS(SYSDATE, -6)
  AND points < 200;
  
-- Step 21: Predict and compare query results

-- Query 1 prediction: 5 rows
SELECT *
FROM members
WHERE city = 'Cairo' OR city = 'Giza' AND points >= 100;

-- Query 2 prediction: 4 rows
SELECT *
FROM members
WHERE (city = 'Cairo' OR city = 'Giza')
  AND points >= 100;

-- Query 3 prediction: 0 rows
SELECT *
FROM books
WHERE price BETWEEN 500 AND 100;

-- Query 4 prediction: Error - ORA-00904 because SELECT aliases cannot be used in WHERE
SELECT title,
       price * 1.14 AS price_vat
FROM books
WHERE price_vat > 500;