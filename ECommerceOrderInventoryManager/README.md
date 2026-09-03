# E-Commerce Order & Inventory Manager

A small console application in Java for a shop's day-to-day work: products with stock levels, customer orders, order statuses, a FIFO shipping queue, and product reviews. Everything runs from a text menu, with no libraries or database.

## What It Does

The app walks through the whole order flow. You add products, create orders and put items in them, move orders through Pending → Shipped → Delivered, cancel them, and write reviews. Stock is reserved when an item goes into an order and given back when the item is removed or the order is cancelled.

A big part of this assignment is picking the right collection for each kind of data, so the section below explains those choices.

## Collections Used and Why

The `Store` class keeps seven collections — plus the list of items inside each order — each for one clear reason:

| Data | Collection | Reason |
| --- | --- | --- |
| Product listing | `List<Product>` (`ArrayList`) | Products are printed as one list in the order they were added, and the whole list is iterated often (displaying products, the out-of-stock sweep). `ArrayList` keeps insertion order and is simple to iterate. |
| Products by ID | `Map<Integer, Product>` (`HashMap`) | Products are constantly looked up by ID — to add one to an order, or to reject a duplicate ID. A `HashMap` finds a product in one step instead of scanning the whole list. |
| Orders | `Map<Integer, Order>` (`HashMap`) | Every order is addressed by its ID for its whole lifetime (display, shipping, cancellation) and orders are never deleted. Orders are only reached by ID, never by position, so an ID → order map is a straightforward fit. |
| Categories | `Set<String>` (`HashSet`) | A category must appear exactly once even when many products share it. A set enforces that for free, and the order of categories doesn't matter in this app. |
| Shipping queue | `Queue<Order>` (`LinkedList`) | Shipping is strict FIFO: the order added first is the one shipped first. The `Queue` interface is exactly that contract, and `LinkedList` provides the implementation. |
| Delivered orders | `LinkedHashMap<Integer, Order>` | Delivered orders matter in two ways: the order they were delivered in, and their IDs. `LinkedHashMap` keeps insertion order and still allows key-based lookup. |
| Reviews | `List<Review>` (`ArrayList`) | All reviews live in one flat list and are shown per product in the order they were written. `ArrayList` keeps that order and makes it easy to filter by product. |
| Items in an order | `List<CartItem>` (`ArrayList`) | Items stay in the order the customer added them, and display and total walk the list in that order. Adding a product you already have just raises the existing `CartItem`'s quantity, so it never appears twice. |

## Products and Orders: How They're Sorted

- `Product` implements `Comparable<Product>` with its natural order being price, cheapest first. Sorting code then never has to explain how two products compare.
- Orders are sorted by a different rule — their total — so instead of forcing `Order` into a second natural order, `Comparator.comparingDouble(Order::getTotal)` is used inline.
- Both sorts run on a **copy**, so the stored collections themselves are never reordered.

## Project Structure

```
ECommerceOrderInventoryManager/
├── src/EOM/
│   ├── Main.java            # menu loop + input handling
│   ├── models/              # Product, CartItem, Order, Review
│   ├── enums/               # OrderStatus
│   ├── services/            # Store (all collections + store logic)
│   └── utils/               # Validator, InputReader, ConsoleUtils
├── Makefile                 # compile / run / clean
└── README.md
```

## Order Lifecycle

```
Create Order        → PENDING
Add to Shipping     → SHIPPED
Ship Next Order     → DELIVERED
Cancel Order        → CANCELLED
```

An order starts as `PENDING`. From there it can go to `SHIPPED` (put on the shipping list) or straight to `CANCELLED`; a `SHIPPED` order ends up `DELIVERED` or `CANCELLED`. `DELIVERED` and `CANCELLED` are final — the order can't be modified, shipped, or cancelled again. Orders are never removed from the permanent `orders` map, and the shipping queue only holds `SHIPPED` orders that are still waiting.

## Stock and Out-of-Stock Handling

- A product lives in two views at once: the `products` list (the order it's displayed in) and the `productsById` map (fast lookups). Anything that deletes a product goes through `deleteProductEverywhere(...)`, which updates both views and also drops the category if no other product uses it — so the two views never disagree.
- Manual removal is different from the zero-stock sweep. `Remove Product` refuses to delete a product that still sits in a `PENDING` order — the customer's active order needs it. If the product only appears in `SHIPPED`, `DELIVERED`, or `CANCELLED` orders, deletion is allowed; those orders are locked, so their historical `CartItem` keeps its reference even after the product leaves the catalog.
- Adding an item to an order lowers the stock. When the last unit is taken, the product is removed from the catalog on the spot. The `CartItem` in the order still points to the same `Product` object, which is what lets the stock be given back to it later.
- `Remove Out-of-Stock Products` goes through the list once and safely removes every zero-stock product while iterating, so the catalog only keeps products that actually have stock.
- Removing an item from a pending order, or cancelling one, restores the reserved stock on that same `Product` object. If the product had been removed from the catalog, it is added back (with its category) at that point — but only if it isn't already there, so it never ends up in the store twice.

## Cancelling an Order

- Pending orders can be cancelled (`PENDING → CANCELLED`).
- Shipped orders can be cancelled too, and they are taken off the shipping queue.
- Delivered orders can't be cancelled.
- A cancelled order stays in the permanent `orders` map and is never added to the delivered list.
- Cancelling restores the stock of every item exactly once — in `Order.cancel()` — and products that are no longer in the catalog are put back.

## Order Total

`Order.addItem()` and `Order.removeItem()` / `removeItemById()` call `calculateTotal()` before returning, so the total is always up to date when it's displayed. The order displays (`Display Order`, `Display All Orders`, the shipping queue, and the delivered list) print that total beneath the item table.

## OrderStatus Enum

Order status is an enum: `PENDING`, `SHIPPED`, `DELIVERED`, `CANCELLED`. An enum means an invalid status can't even be typed, and each status controls which transitions are legal at runtime. `toString()` returns a friendlier label (Pending, Shipped, …).

## Java Features Used

- **Streams** — filtering, mapping, sorting, collecting, `anyMatch`, `findFirst`, `mapToDouble`, `toList`, `toArray`
- **Lambda Expressions** — comparators, `forEach`, predicates, stream operations
- **Method References** — `CartItem::calculateSubtotal`, `Product::getPrice`, `Order::getTotal`, `this::restoreStock`, `info::append`
- **Functional Interfaces** — `Predicate<T>` in `Validator` for generic validation
- **Optional** — `findItemById` returns `Optional<CartItem>`, used with `isPresent`/`get`, `orElseThrow`
- **`Comparable<Product>`** — natural ordering by price, cheapest first
- **`Comparator.comparingDouble`** — inline comparators for order sorting

## Building and Running

Requires Java 16 or newer (tested on JDK 26) with `javac`/`java` on the PATH.

The Makefile is just a small helper for the compile/run/clean commands:

```bash
make           # compile everything
make compile   # compile all sources into build/
make run       # run the application (compiles first if needed)
make re        # wipe build/ and recompile from scratch
make clean     # remove the build/ folder
```

Or manually:

```bash
mkdir -p build
javac -d build $(find src -name '*.java')
java -cp build EOM.Main
```

## Using the App

The program is menu-driven — type the number of the action you want:

```
 1. Add Product                        13. Cancel Order
 2. Remove Product                     14. Search Order by ID
 3. Display All Products               15. Add Review to a Product
 4. Search Product by ID               16. Show All Reviews for a Product
 5. Show All Categories                17. Remove Out-of-Stock Products
 6. Display Products Ordered by Price  18. Display Orders Ordered by Total
 7. Create Order                       19. Display All Orders
 8. Add Item to Order                  20. Display Shipping Queue
 9. Remove Item from Order             21. Display Delivered Orders
10. Display Order                      22. Display All Reviews
11. Add Order to the Shipping List     23. Exit
12. Ship Next Order
```

Invalid input never crashes the program — it prints an `Error: ...` message and asks again.

## Business Rules

- Product IDs and order IDs are unique; duplicates are rejected.
- Items can only be added to or removed from a `PENDING` order.
- Adding the same product twice to an order just raises its quantity.
- An empty order can't be added to shipping, and an empty order at the front of the queue can't be shipped.
- An order can never be shipped or delivered twice.
- PENDING and SHIPPED orders can be cancelled; DELIVERED orders can't.
- Cancelling a SHIPPED order removes it from the shipping queue.
- Cancelling an order restores the stock reserved by its items.
- Orders are never removed from the permanent order history.
- A product that belongs to a `PENDING` order can't be manually removed from the catalog; products only in `SHIPPED`, `DELIVERED`, or `CANCELLED` orders can be (the historical order keeps its items).
- Out-of-stock products are put back in the catalog once their reserved stock is returned.
- Reviews can only be written for a product that's still in the catalog.
- Each category is stored once, no matter how many products share it.

## Example Walkthrough

```
 1. Add Product          → Product 1: Mouse, $10, Electronics, stock 5
 7. Create Order         → Order 501 (Sara)
 8. Add Item to Order    → Order 501 + Product 1 × 2   → stock 3, total $20
11. Add to Shipping      → Order 501 → SHIPPED
12. Ship Next Order      → Order 501 → DELIVERED
19. Display All Orders   → shows Order 501 as Delivered
```

From there you could review the product, or cancel another pending order and watch its stock come back.