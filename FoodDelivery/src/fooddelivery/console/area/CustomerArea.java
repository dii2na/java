package fooddelivery.console.area;

import fooddelivery.console.ConsoleContext;
import fooddelivery.exception.FoodDeliveryException;
import fooddelivery.exception.UnavailableItemException;
import fooddelivery.model.customer.Address;
import fooddelivery.model.customer.Customer;
import fooddelivery.model.customer.SearchRecord;
import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderItem;
import fooddelivery.model.promotion.Promotion;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.restaurant.menu.MenuItem;
import fooddelivery.service.report.CustomerReportService.CustomerOrderHistory;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import static fooddelivery.console.ConsoleFormat.*;
import static fooddelivery.utils.ConsoleUtils.*;
import static fooddelivery.utils.InputReader.*;

public class CustomerArea
{
    private static final String TITLE = "MASR DELIVERY - CUSTOMER";
    private static final int EXIT_CHOICE = 9;

    private final ConsoleContext context;

    public CustomerArea(ConsoleContext context)
    {
        this.context = context;
    }

    public void start(Scanner scanner)
    {
        boolean running;

        running = true;
        println(sectionTitle(TITLE));
        while (running)
        {
            printMenuOptions();
            printSystemStatus();
            try
            {
                switch (readIntInRange(
                    scanner, "Choice", 1, EXIT_CHOICE))
                {
                    case 1 -> browseRestaurants(scanner);
                    case 2 -> searchRestaurants(scanner);
                    case 3 -> viewMenu(scanner);
                    case 4 -> placeOrder(scanner);
                    case 5 -> trackOrder(scanner);
                    case 6 -> cancelOrder(scanner);
                    case 7 -> orderHistory(scanner);
                    case 8 -> walletAndTier(scanner);
                    case EXIT_CHOICE -> running = false;
                }
            }
            catch (FoodDeliveryException |
                   IllegalArgumentException |
                   IllegalStateException e)
            {
                printError(e);
            }
        }
    }

    private void printMenuOptions()
    {
        printMenu(
            TITLE,
            EXIT_CHOICE,
            "Back to Main Menu",
            "Browse Restaurants",
            "Search Restaurants",
            "View Restaurant Menu",
            "Place Order",
            "Track Order",
            "Cancel Order",
            "Order History",
            "Wallet and Loyalty");
    }

    private void printSystemStatus()
    {
        printBanner(
            "Registered Customers", context.allCustomers().size(),
            "Restaurants", context.allRestaurants().size());
    }

    // Browse and Search

    private void browseRestaurants(Scanner scanner)
    {
        int choice;
        List<Restaurant> matches;

        printFilterOptions();
        choice = readIntInRange(scanner, "Filter", 1, 7);
        matches = chooseRestaurantFilter(scanner, choice);
        printRestaurants(matches);
    }

    private void printFilterOptions()
    {
        println(sectionTitle("Filter Options"));
        println("  1.  All open restaurants (no filter)");
        println("  2.  By district");
        println("  3.  By cuisine");
        println("  4.  By minimum rating");
        println("  5.  By maximum menu price");
        println("  6.  District AND minimum rating");
        println("  7.  Cuisine AND maximum price");
        println();
    }

    private List<Restaurant> chooseRestaurantFilter(
        Scanner scanner,
        int choice)
    {
        return (switch (choice)
        {
            case 1 -> browseOpenRestaurants();
            case 2 -> browseByDistrict(scanner);
            case 3 -> browseByCuisine(scanner);
            case 4 -> browseByMinimumRating(scanner);
            case 5 -> browseByMaximumPrice(scanner);
            case 6 -> browseByDistrictAndRating(scanner);
            case 7 -> browseByCuisineAndPrice(scanner);
            default -> throw new IllegalArgumentException(
                "Unsupported filter");
        });
    }

    private List<Restaurant> browseOpenRestaurants()
    {
        return (context.browseRestaurants(Restaurant::isOpen));
    }

    private List<Restaurant> browseByDistrict(Scanner scanner)
    {
        String district;

        district = readString(scanner, "District");

        return (context.browseRestaurants(
            context.search().byDistrict(district)));
    }

    private List<Restaurant> browseByCuisine(Scanner scanner)
    {
        String cuisine;

        cuisine = readString(scanner, "Cuisine");

        return (context.browseRestaurants(
            context.search().byCuisine(cuisine)));
    }

    private List<Restaurant> browseByMinimumRating(Scanner scanner)
    {
        double minimumRating;

        minimumRating = readMinimumRating(scanner);

        return (context.browseRestaurants(
            context.search().byMinimumRating(minimumRating)));
    }

    private List<Restaurant> browseByMaximumPrice(Scanner scanner)
    {
        BigDecimal maximumPrice;

        maximumPrice = readMaximumPrice(scanner);

        return (context.browseRestaurants(
            context.search().byPriceCeiling(maximumPrice)));
    }

    private List<Restaurant> browseByDistrictAndRating(
        Scanner scanner)
    {
        String district;
        double minimumRating;

        district = readString(scanner, "District");
        minimumRating = readMinimumRating(scanner);

        return (context.browseRestaurants(
            context.search()
                .byDistrict(district)
                .and(context.search()
                    .byMinimumRating(minimumRating))));
    }

    private List<Restaurant> browseByCuisineAndPrice(Scanner scanner)
    {
        String cuisine;
        BigDecimal maximumPrice;

        cuisine = readString(scanner, "Cuisine");
        maximumPrice = readMaximumPrice(scanner);

        return (context.browseRestaurants(
            context.search()
                .byCuisine(cuisine)
                .and(context.search()
                    .byPriceCeiling(maximumPrice))));
    }

    private double readMinimumRating(Scanner scanner)
    {
        return (readDoubleInRange(
            scanner,
            "Minimum rating",
            0.0,
            5.0));
    }

    private BigDecimal readMaximumPrice(Scanner scanner)
    {
        return (readDecimalPositive(scanner, "Maximum price"));
    }

    private void searchRestaurants(Scanner scanner)
    {
        Customer customer;
        String text;
        List<Restaurant> matches;

        customer = chooseCustomer(scanner);
        printAvailableCuisines();
        text = readString(scanner, "Search text");
        matches = context.searchFreeText(customer, text);
        if (matches.isEmpty())
            println("Nothing found for \"" + text + "\".");
        else
            printRestaurants(matches);
        printRecentSearches(customer);
    }

    private void printAvailableCuisines()
    {
        println(sectionTitle("Available Cuisines"));
        println(commaSeparated(context.distinctCuisines()));
    }

    private void printRecentSearches(Customer customer)
    {
        List<SearchRecord> history;

        history = customer.getSearchHistory();
        if (history.isEmpty())
            return;

        println();
        println(sectionTitle("Recent Searches"));
        printNumberedList(history.stream()
            .map(record -> record.getDescription()
                + "  (" + when(record.getSearchedAt()) + ")")
            .toArray(String[]::new));
    }

    private void viewMenu(Scanner scanner)
    {
        Restaurant restaurant;

        restaurant = chooseRestaurant(scanner);
        printMenu(restaurant);
    }

    // Ordering

    private void placeOrder(Scanner scanner)
    {
        Order order;

        if (context.allRestaurants().isEmpty())
            throw new IllegalArgumentException(
                "No restaurants are registered yet");
        if (context.allCustomers().isEmpty())
            throw new IllegalArgumentException(
                "No customers are registered yet");

        order = buildOrder(scanner);
        printOrderSummary(order);
    }

    private Order buildOrder(Scanner scanner)
    {
        String orderId;
        Customer customer;
        Restaurant restaurant;
        Address address;
        List<OrderItem> items;
        Promotion promotion;
        BigDecimal distance;

        orderId = readUnusedOrderId(scanner);
        customer = chooseCustomer(scanner);
        restaurant = chooseRestaurant(scanner);
        items = buildOrderItems(scanner, restaurant);
        address = chooseAddress(scanner, customer);
        distance = readDistance(scanner, restaurant, address);
        promotion = choosePromotion(scanner);

        return (context.placeOrder(
            orderId,
            customer,
            restaurant,
            address,
            distance,
            items,
            promotion));
    }

    private String readUnusedOrderId(Scanner scanner)
    {
        return (readValidatedString(
            scanner,
            "Order ID",
            this::validateOrderId));
    }

    private String validateOrderId(String value)
    {
        if (context.orderIdExists(value))
        {
            throw new IllegalArgumentException(
                "Order ID already exists: " + value);
        }

        return (Validator.validateString(value, "Order ID"));
    }

    private List<OrderItem> buildOrderItems(
        Scanner scanner,
        Restaurant restaurant)
    {
        List<OrderItem> items;
        String more;

        items = new ArrayList<>();

        if (restaurant.getMenu().isEmpty())
            throw new IllegalArgumentException(
                "This restaurant has no menu items");

        println(sectionTitle(
            "Menu - " + restaurant.getDisplayName()));
        printMenu(restaurant);
        more = "y";
        while (more != null && more.equalsIgnoreCase("y"))
        {
            items.add(readOrderItem(scanner, restaurant));
            more = readOptionalString(
                scanner, "Add another item? (y/n)");
        }

        return (items);
    }

    private OrderItem readOrderItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        MenuItem item;
        BigDecimal quantity;

        item = chooseMenuItem(scanner, restaurant);
        quantity = readDecimalPositive(
            scanner,
            "Quantity (count or kg)");
        item.checkStock(quantity);

        return (new OrderItem(item, quantity));
    }

    private MenuItem chooseMenuItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        String itemId;
        Optional<MenuItem> found;

        if (restaurant.getMenu().values().stream()
            .noneMatch(MenuItem::isAvailable))
        {
            throw new IllegalArgumentException(
                "This restaurant has no available menu items");
        }
        while (true)
        {
            itemId = readString(scanner, "Menu Item ID");
            found = restaurant.getMenuItem(itemId);
            if (found.isEmpty())
            {
                println("Menu item \"" + itemId
                    + "\" not found at this restaurant.");
                continue;
            }
            if (!found.get().isAvailable())
                throw new UnavailableItemException(
                    "Menu item \"" + itemId
                        + "\" is currently unavailable");

            return (found.get());
        }
    }

    private Address chooseAddress(
        Scanner scanner,
        Customer customer)
    {
        List<Address> addresses;
        Address address;
        int choice;
        int newAddressChoice;

        addresses = new ArrayList<>(
            customer.getAddresses());
        newAddressChoice = printAddressOptions(addresses);

        choice = readIntInRange(
            scanner, "Address", 1, newAddressChoice);

        if (choice < newAddressChoice)
            return (addresses.get(choice - 1));

        address = readNewAddress(scanner);
        customer.addAddress(address);

        return (address);
    }

    private int printAddressOptions(List<Address> addresses)
    {
        String[] entries;
        int index;

        entries = new String[addresses.size() + 1];
        index = 0;
        for (Address candidate : addresses)
            entries[index++] = candidate.getDistrict()
                + " - " + candidate.getDetail();
        entries[index] = "Add a new address";

        println(sectionTitle("Saved Addresses"));
        printNumberedList(entries);
        println();

        return (entries.length);
    }

    private Address readNewAddress(Scanner scanner)
    {
        String district;
        String detail;

        district = readString(scanner, "District");
        detail = readString(scanner, "Address detail");

        return (new Address(district, detail));
    }

    private BigDecimal readDistance(
        Scanner scanner,
        Restaurant restaurant,
        Address address)
    {
        BigDecimal distance;

        println("Restaurant district: "
            + restaurant.getDistrict());
        println("Delivery district:   "
            + address.getDistrict());

        distance = readDecimalPositive(
            scanner,
            "Delivery distance in km");

        return (distance);
    }

    private Promotion choosePromotion(Scanner scanner)
    {
        String code;
        Optional<Promotion> found;

        printActivePromotions();
        code = readOptionalString(scanner, "Promotion code");
        if (code == null)
            return (null);

        found = context.findPromotion(code);
        if (found.isEmpty())
            throw new IllegalArgumentException(
                "Promotion code not found: " + code);

        return (found.get());
    }

    private void printActivePromotions()
    {
        println("Active promotions:");
        for (Promotion promotion : context.allPromotions())
            println("  - " + promotion.getCode());
    }

    private void printOrderSummary(Order order)
    {
        println();
        println(sectionTitle("Order Placed"));
        printOrderDetails(order);
        printOrderItems(order);
        printPriceBreakdown(order, context.quote(order));
        println(fieldLine(
            "Wallet Balance",
            money(order.getCustomer().getWallet())));
    }

    // Tracking and Cancellation

    private void trackOrder(Scanner scanner)
    {
        Order order;
        Duration elapsed;

        order = chooseOwnOrder(scanner);

        elapsed = Duration.between(
            order.getPlacedAt(),
            LocalDateTime.now());
        printOrderDetails(order);
        print(fieldLine(
            "Elapsed Since Placement",
            duration(elapsed)));
        printStatusBanner(
            "Current status: " + order.getStatus());
    }

    private void cancelOrder(Scanner scanner)
    {
        Order order;

        order = chooseOwnOrder(scanner);

        if (!readConfirmation(
            scanner,
            "Cancel order " + order.getId() + "?"))
        {
            println("Cancellation abandoned.");
            return;
        }
        context.cancelOrder(order.getId());

        printStatusBanner(
            "Order " + order.getId() + " cancelled");
        print(fieldLine(
            "Refunded To Wallet",
            money(order.getTotal())));
        print(fieldLine(
            "New Wallet Balance",
            money(order.getCustomer().getWallet())));
    }

    private void orderHistory(Scanner scanner)
    {
        Customer customer;
        CustomerOrderHistory history;
        List<Order> orders;

        customer = chooseCustomer(scanner);
        history = context.reports()
            .customerOrderHistory(customer);
        orders = history.orders();

        if (orders.isEmpty())
        {
            println("This customer has no orders yet.");
            return;
        }

        printCustomerSection("Order History", customer);
        print(fieldLine(
            "Lifetime Total Spent", money(history.totalSpent())));
        printOrders(orders);
    }

    private void walletAndTier(Scanner scanner)
    {
        Customer customer;

        customer = chooseCustomer(scanner);
        printCustomerSection("Wallet and Loyalty", customer);
        print(fieldLine("Mobile", customer.getMobile()));
        print(fieldLine(
            "Wallet Balance",
            money(customer.getWallet())));
        print(fieldLine(
            "Loyalty Tier",
            customer.getLoyaltyTier()));
        print(fieldLine(
            "Completed Orders",
            customer.getCompletedOrderCount()));
        print(fieldLine(
            "Delivery Fee Benefit",
            customer.getLoyaltyTier()
                .getDeliveryFeeDiscount()
                .multiply(BigDecimal.valueOf(100))
                + "% off"));

        topUpWallet(scanner, customer);
    }

    private void topUpWallet(
        Scanner scanner,
        Customer customer)
    {
        BigDecimal amount;
        BigDecimal previousBalance;

        if (!readConfirmation(
            scanner,
            "Add money to this wallet?"))
        {
            return;
        }
        amount = readDecimalPositive(scanner, "Amount to add");
        previousBalance = customer.getWallet();
        customer.addToWallet(amount);

        printStatusBanner(
            "Added " + money(amount) + " to "
                + customer.getName() + "'s wallet");
        print(fieldLine(
            "Previous Balance",
            money(previousBalance)));
        print(fieldLine(
            "New Wallet Balance",
            money(customer.getWallet())));
    }

    private void printCustomerSection(
        String title,
        Customer customer)
    {
        println(sectionTitle(title));
        print(fieldLine("Customer", customer.getName()));
    }

    // Lookups

    private Customer chooseCustomer(Scanner scanner)
    {
        if (context.allCustomers().isEmpty())
            throw new IllegalArgumentException(
                "No customers are registered yet");
        println(sectionTitle("Customers"));
        printCustomers(context.allCustomers());
        return (context.findCustomer(
            readString(scanner, "Customer ID")));
    }

    private Restaurant chooseRestaurant(Scanner scanner)
    {
        if (context.allRestaurants().isEmpty())
            throw new IllegalArgumentException(
                "No restaurants are registered yet");
        println(sectionTitle("Restaurants"));
        printRestaurants(
            context.browseRestaurants(
                Restaurant::isOpen));

        return (context.findRestaurant(
            readString(scanner, "Restaurant ID")));
    }

    private Order chooseOwnOrder(Scanner scanner)
    {
        Customer customer;
        List<Order> orders;

        customer = chooseCustomer(scanner);
        orders = ownOrdersNewestFirst(customer);

        if (orders.isEmpty())
            throw new IllegalArgumentException(
                "This customer has no orders");

        println(sectionTitle("Orders"));
        printOrders(orders);

        return (readOwnOrderId(scanner, orders));
    }

    private List<Order> ownOrdersNewestFirst(Customer customer)
    {
        return (context.ordersFor(customer).stream()
            .sorted((first, second) ->
                second.getPlacedAt()
                    .compareTo(first.getPlacedAt()))
            .toList());
    }

    private Order readOwnOrderId(
        Scanner scanner,
        List<Order> orders)
    {
        String candidateId;
        Optional<Order> found;

        while (true)
        {
            candidateId = readString(scanner, "Order ID");
            found = context.findOrderIn(orders, candidateId);
            if (found.isPresent())
                return (found.get());
            println("Order \"" + candidateId
                + "\" does not belong to this customer.");
        }
    }
}
