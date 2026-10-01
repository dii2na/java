# Masr Delivery

A Java console food-delivery platform: restaurants publish menus, customers order and pay from a wallet, riders dispatch, and an admin area governs the whole thing. Pure JDK — no frameworks, no dependencies.

Each section states the requirement, then the design that satisfies it. Part E was not supplied with the brief.

| | |
|---|---|
| Stack | Java 16+ (`record`, pattern-matching `instanceof`, `Stream.toList()`), standard library only |
| Build | `Makefile` over `javac` / `java`, entry `fooddelivery.console.Main` |
| Shape | 70 files, 18 packages, layered `console → service → repository → model` |
| Storage | In-memory, so every run starts empty |

---

## 1. Business Domain (Part A)

| Entity | Implementation |
|---|---|
| Restaurant | id, display name, district, cuisines (`LinkedHashSet`), rating 0.0–5.0, open/closed |
| Menu item | `MenuItem` → `StandardItem` / `ComboItem` / `WeightedItem`; id, name, price, category, prep minutes, available, stock |
| Customer | id, name, mobile `01[0125]\d{8}`, addresses, wallet, search history (`ArrayDeque`, last 5) |
| Rider | id, name, `VehicleType`, district, available, completed count, at most one active order |
| Order | customer + restaurant + address + lines, placement time; private constructor via `Builder` |

`MenuItem.calculatePrice` is abstract, so a new item kind is a new subclass and order pricing never changes. `Customer.getLoyaltyTier()` derives the tier from completed orders — Bronze 0–9 (no benefit), Silver 10–29 (10% off delivery), Gold 30+ (fee waived).

**Lifecycle** `PLACED → ACCEPTED → PREPARING → READY → ASSIGNED → OUT_FOR_DELIVERY → DELIVERED`, with `CANCELLED` reachable any time before `OUT_FOR_DELIVERY`. `OrderStatus.next()` yields only the forward successor and `Order.changeStatus` rejects anything else, so `PLACED` cannot jump to `DELIVERED`, `DELIVERED` cannot be cancelled, and `CANCELLED` is final. A second order for the same rider raises `BusyRiderException`.

## 2. Pricing (Part B)

`StandardPricingStrategy.quote`, in order:

**(1)** subtotal — standard `price × count`, combo `bundle × count`, weighted `per kg × kg`;

**(2)** delivery fee — `15.00` plus `3.00`/km beyond the first `3.00` km, then the loyalty benefit;

**(3)** service fee — 10% of subtotal, `HALF_UP` to 2 dp;

**(4)** promotion — at most one code, discount capped at the subtotal;

**(5)** total — floored at zero, 2 dp.

Money is `BigDecimal` with an explicit rounding mode, and quantities are `BigDecimal` too. Promotion types: percentage off with a maximum cap, fixed amount off, free delivery. Conditions: expiry, minimum subtotal, one district, first-time customers.

**Worked example — the compiled code produces `258.00` EGP:**

| Component | Value | Derivation |
|---|---|---|
| Subtotal | `240.00` | 4 × `60.00` |
| Delivery fee | `42.00` | `15.00` + (`12.00` − `3.00`) × `3.00` |
| Loyalty discount | `0.00` | Bronze |
| Service fee | `24.00` | `240.00` × `0.10` |
| Promotion `NILE20` | `48.00` | 20% of `240.00`, under the `50.00` cap |
| **Total** | **`258.00`** | `240.00 + 42.00 + 24.00 − 48.00` |

## 3. Data Organisation (Part C)

| Requirement | Structure |
|---|---|
| Lookup by ID without scanning | `LinkedHashMap` in `KeyedRepository<T>` |
| Menu in insertion order | `LinkedHashMap` in `Restaurant` |
| Distinct cuisines, no duplicates | `LinkedHashSet`, case-insensitive |
| Rating desc, ties by name | `Comparator` — a total order |
| READY: longest waiting, Gold first | `PriorityQueue` by gold rank → `placedAt` → id, rank frozen on insert |
| Last five searches, newest first | `ArrayDeque`, capped at 5 |
| Same entity never twice | `equals` / `hashCode` on the identifier |
| Exposed collections immutable | `Collections.unmodifiable…` views and defensive copies |

## 4. Reporting, Analytics, Search (Part D)

Ten reports, all stream-based — the report and search layers contain no `for` and no `while`. `ReportService` is a facade over five services that depend only on repositories, and `OrderQueries` centralises the shared filters. Reports 7 and 9 return `Optional`, so an empty platform reports absence rather than a value.

| # | Report | Entry point |
|---|---|---|
| 1 | Total revenue for a date range | `totalRevenue(LocalDate, LocalDate)` |
| 2 | Top five restaurants by revenue, for a month | `topRestaurantsByRevenue(YearMonth)` |
| 3 | Average order value per district | `averageOrderValueByDistrict()` |
| 4 | Rating above 4.5 with ≥ 20 completed orders | `highlyRatedRestaurants()` |
| 5 | Order count grouped by status | `orderCountByStatus()` |
| 6 | Per rider: deliveries and average duration, descending | `riderDeliveryStatistics()` |
| 7 | Most frequently ordered item | `mostFrequentlyOrderedMenuItem()` |
| 8 | Customer order history, newest first, with total spent | `customerOrderHistory(Customer)` |
| 9 | Peak ordering hour | `peakOrderingHour()` |
| 10 | Customers not ordered in the last 30 days | `customersNotOrderedRecently()` |

**Search.** `RestaurantSearchService.search` takes a `Predicate<Restaurant>` and knows nothing of districts, cuisines, ratings, or prices — so a new criterion never edits the search code. Criteria validate their own argument when created: `byDistrict`, `byCuisine`, `byMinimumRating`, `byPriceCeiling`, combining with `and`. `CustomerArea` exposes seven browse options: unfiltered, the four criteria, two combined pairs. Free-text search matches name or cuisine and results sort by rating desc, then name. Dates use `java.time` throughout, never legacy `Date`.

## 5. Failures (Part F)

```text
RuntimeException
├── FoodDeliveryException              base for platform faults
│   ├── BusyRiderException             ClosedRestaurantException
│   ├── ExpiredPromotionException      IllegalOrderTransitionException
│   ├── InsufficientWalletException    NonApplicablePromotionException
│   └── StockShortageException         UnavailableItemException
└── ConsoleInputClosedException        end of input, not a domain fault
```

All eight platform exceptions are unchecked and share one parent, so a caller can catch the category or a single subtype without swallowing unrelated JVM errors. Argument-level problems instead use `IllegalArgumentException` via `utils.Validator`, keeping "the platform refused this" distinct from "bad argument".

## 6. Design Decisions (Part G)

| # | Problem | Approach | Pattern |
|---|---|---|---|
| 1 | Pricing varies by promotion type | `Promotion.calculateDiscount` is abstract and the strategy holds one reference, so pricing carries no type test; `PromotionFactory` picks the subtype | Polymorphism via abstract method |
| 2 | Multi-step order construction | Private constructor plus `Order.builder()`; six fields validated, promotion optional, partial construction impossible | Builder |
| 3 | Build the right item kind from a type field | `MenuItemFactory.create(MenuItemType, …)` returns the subtype in one place | Factory |
| 4 | One status change triggers several reactions | `OrderStatusSubject` notifies `OrderObserver`s from `OrderService`; `ConsoleOrderObserver` prints | Observer |
| 5 | Config loaded once, reachable anywhere | `PlatformConfig` private constructor plus `getInstance()` | Singleton |
| 6 | Dispatch differs per vehicle | `VehicleType.canHandle` encodes range and order size; the strategy takes the slowest capable rider | Strategy over an enum with behaviour |

## 7. Console (Part H)

| Area | Menu | Operations |
|---|---|---|
| Customer | 1 | Browse (7 filter options), free-text search, view menu, place order, track, cancel, order history, wallet and loyalty with top-up |
| Restaurant | 2 | Accept or reject a pending order, mark preparing then ready, toggle availability, add or remove menu items, adjust daily stock, today's orders and revenue |
| Rider | 3 | On or off duty, accept the next ready delivery, view the assigned order, mark picked up and delivered, personal statistics |
| Admin & Reports | 4 | Manage restaurants, view restaurants and any one menu, register customers and riders, view both, create promotions, run reports, platform statistics |
| Exit | 0 | Confirm and return; closing stdin also exits cleanly |

Platform statistics is an admin view, not one of the ten Part D reports. Each area catches `FoodDeliveryException`, `IllegalArgumentException`, and `IllegalStateException`, then redraws its own menu — a domain fault never reaches the main menu.

| Validation rule | Enforced by |
|---|---|
| Unique identifiers per type | `KeyedRepository.save`, `OrderService.validateOrderId`, `Restaurant.addMenuItem` |
| Mobile `01[0125]\d{8}`; prices, quantities, weights > 0; ratings 0.0–5.0 inclusive | `CustomerValidator`, `Validator.validatePositive`, `validateInRange`, `Restaurant.updateRating` |
| Closed restaurant, unavailable item, stock shortage; wallet never negative | `ClosedRestaurantException`, `UnavailableItemException`, `StockShortageException`, `Customer.pay` |
| Promotion codes case-insensitive, unexpired, one per order; minimum-subtotal refused with a reason | `Promotion.matchesCode`, `PromotionRepository`, `Promotion.isMinimumSubtotalMet` |
| Only an available rider may be assigned | `Rider.assignOrder` |
| At least one line item; only legal transitions; address must belong to the customer | `Order.Builder.validateRequiredFields`, `Order.changeStatus`, `OrderService` |
| Invalid input re-prompts | `InputReader` |

## 8. How to Use

`make run`. Storage is in-memory, so every start is empty — register a restaurant, a customer, and a rider before an order can exist. Enter `0` to exit, or close stdin (Ctrl-D); both leave without a stack trace.

### First run, in order

1. **Admin (4) → 1 Add Restaurant.** `Restaurant ID`, `Display name`, `District`, then at least one `Cuisine` (Enter on *Add another cuisine?* to stop), then `Open now?`.
2. **Restaurant (2) → 6 Add Menu Item.** `Restaurant ID`, `Type` (`1` standard / `2` weighted per kg / `3` combo), `Item ID`, `Item name`, `Category`, `Preparation time`, `Stock quantity`, `Available?`, then `Price` / `Price per kg` — or for a combo the `Menu Item ID` of each part plus a `Discount rate (0-1)`. A restaurant with no menu items cannot take orders.
3. **Admin (4) → 7 Register Customer.** `Customer ID`, name, `Mobile number` (11 digits, `010`/`011`/`012`/`015`), address district and detail, opening wallet balance.
4. **Admin (4) → 9 Register Rider.** `Rider ID`, name, `Vehicle type` (`1` BICYCLE / `2` MOTORCYCLE / `3` CAR), current district. Riders start **off duty** — Rider (3) → 1 to go on duty before accepting a delivery.
5. **Admin (4) → 11 Create Promotion** (optional). `Promotion code`, `Minimum subtotal`, `Expiry date` (`yyyy-MM-dd`), optional `Exact expiry time` (`HH:mm` or `HH:mm:ss`, Enter means 23:59), optional `Restricted district`, `First-time customers only?`, then `Type` (`1` percentage + rate + cap / `2` fixed amount / `3` free delivery) and that type's fields.

### Admin menu map

| # | Operation | | # | Operation |
|---|---|---|---|---|
| 1 | Add Restaurant | | 8 | View All Customers |
| 2 | Remove Restaurant | | 9 | Register Rider |
| 3 | Toggle Restaurant Open / Closed | | 10 | View All Riders |
| 4 | Update Restaurant Rating | | 11 | Create Promotion |
| 5 | View All Restaurants | | 12 | Run Reports |
| 6 | View a Restaurant Menu | | 13 | Platform Statistics |
| 7 | Register Customer | | 14 | Back to Main Menu |

The three view options print a full table and take no input; option 6 asks only for a `Restaurant ID`. Each reports "No restaurants/customers/riders are registered yet" instead of printing an empty table.

### Order lifecycle

Every step is one menu choice, in a different area:

| Step | Status after | Where |
|---|---|---|
| Place Order | `PLACED` | Customer 4 |
| Accept a Pending Order | `ACCEPTED` | Restaurant 1 |
| Mark an Order Preparing | `PREPARING` | Restaurant 3 |
| Mark an Order Ready | `READY` | Restaurant 4 |
| Accept Next Ready Delivery | `ASSIGNED` | Rider 2 |
| Mark Order Picked Up | `OUT_FOR_DELIVERY` | Rider 4 |
| Mark Order Delivered | `DELIVERED` | Rider 5 |
| Reject a Pending Order / Cancel Order | `CANCELLED` | Restaurant 2 / Customer 6 |

Placing an order asks, in order: `Order ID` (must be unused), `Customer ID`, `Restaurant ID`, one or more `Menu Item ID` + `Quantity (count or kg)`, a saved `Address` (or a new one), `Delivery distance in km`, and an optional `Promotion code`. The wallet pays immediately and stock is decremented, so the wallet must cover the total; cancelling refunds the wallet and restores stock.

The `OrderStatusSubject` fires on every change, so each one prints a line like `Order O4 status changed from READY to ASSIGNED`.

### Elsewhere in the menus

- **Browse (Customer 1)** filters by district, cuisine, minimum rating, maximum price, or two combinations; **Search (Customer 2)** matches name or cuisine and records the last 5 searches per customer.
- **Wallet and Loyalty (Customer 8)** shows tier and delivery-fee benefit, then offers a top-up.
- **Restaurant 5 / 7 / 8 / 9** — toggle item availability, remove a menu item, adjust daily stock, today's orders and revenue.
- **Reports (Admin 12)** offers the nine report-driven choices; most need orders to exist first. **Platform Statistics (Admin 13)** is the always-available overview of every entity.
- An unknown ID, a closed restaurant, an unavailable item, insufficient stock, or an inapplicable promotion prints one error line and returns to the same menu. You are never trapped in a prompt loop.

## 9. Architecture

Layered and acyclic: `console → service → repository → model`, with `config`, `exception`, and `utils` as leaves. `service` is a container directory only — nothing declares `package fooddelivery.service`.

| Layer | Contents |
|---|---|
| `console` | `Main`, `ConsoleContext`, `ConsoleFormat`; `area`: `CustomerArea`, `RestaurantArea`, `RiderArea`, `AdminArea`, `ReportPanel` |
| `service` | `order` (`OrderService`, `OrderQueries`), `pricing`, `search`, `report` (facade + 5), `dispatch`, `observer` |
| `model` | `customer`, `order`, `restaurant` + `restaurant.menu`, `rider`, `promotion` |
| `repository` | `KeyedRepository<T>` + 5 concrete repositories |
| `config` / `exception` / `utils` | `PlatformConfig`; `FoodDeliveryException` + 8; `Validator`, `InputReader`, `ConsoleUtils` |

## 10. Run

```bash
make          # compile src/ into out/
make run      # compile, then start the application
make re       # discard out/ and recompile from scratch
make clean    # remove out/
make fclean   # clean, then drop IntelliJ metadata
```

Without `make`: `javac -d out $(find src -name '*.java')` then `java -cp out fooddelivery.console.Main`. Requires JDK 16+. The app reads stdin, so it can be driven from a file with `< input.txt`.

## 11. Verification

| Check | Result |
|---|---|
| `javac -Xlint:all` | 0 errors, 0 warnings |
| `--release 16` / `17` / `21` | all compile cleanly |
| Worked example | `258.00` EGP from the compiled code |
| Lifecycle and cancellation | order to delivery; refund and stock restored |
| All ten reports | exercised against live in-memory data |
| Dispatch limits | vehicle range and order-size caps enforced |
| Every menu choice in all four areas | driven by scripted stdin sessions, happy path and error paths |
| Wallet top-up | balance `20.00` → added `100.00` → `120.00` |

**Known gaps:** no automated unit test suite ships — verification was by compilation and by driving the compiled application and its services directly, including scripted stdin sessions covering every menu choice. Storage is in-memory only, which the brief did not require.