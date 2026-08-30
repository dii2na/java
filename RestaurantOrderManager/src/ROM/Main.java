package ROM;

import static ROM.utils.ConsoleUtils.*;

import java.util.Collection;
import java.util.Scanner;

import ROM.models.MenuItem;
import ROM.models.Order;
import ROM.services.Restaurant;
import ROM.utils.InputReader;
import ROM.utils.Validator;

public class Main
{
    // Constants

    private static final int EXIT_CHOICE = 15;
    private static final int MENU_OPTION_COUNT = 15;
    private static final String[] MENU_HEADER = { "ID", "Name", "Category", "Price" };

    // Display / UI

    private static void displayWelcome()
    {
        println(sectionTitle("Welcome to RESTAURANT ORDER MANAGER"));
    }

    private static void printMenuOptions()
    {
        String[] options;
        int index;

        options = new String[]
        {
                "Add Menu Item",
                "Remove Menu Item",
                "Display Menu",
                "Search Menu Item",
                "Create Order",
                "Add Item to Order",
                "Remove Item from Order",
                "Display Order",
                "Add Order to Kitchen Queue",
                "Process Next Order",
                "Search Order",
                "Check Order Status",
                "Display Completed Orders",
                "Cancel Order"
        };
        println();
        println(sectionTitle("RESTAURANT ORDER MANAGER"));
        for (index = 0; index < options.length; index++)
            println("  %2d.  %s".formatted(index + 1, options[index]));
        println("  %2d.  %s".formatted(EXIT_CHOICE, "Exit"));
        println();
    }

    private static String ordersSummary(Restaurant restaurant)
    {
        StringBuilder orderIds;
        int orderCount;

        orderIds = new StringBuilder();
        orderCount = 0;
        for (Order order : restaurant.getOrders().values())
        {
            if (orderCount > 0)
                orderIds.append(", ");
            orderIds.append(order.getOrderId()).append(" (").append(order.getStatus()).append(")");
            orderCount++;
        }
        return (orderCount == 0 ? "0" : orderCount + " | " + orderIds);
    }

    private static void displaySystemStatus(Restaurant restaurant)
    {
        StringBuilder status;

        status = new StringBuilder();
        status.append(separator());
        status.append(fieldLine("Menu Items", restaurant.getMenu().size()));
        status.append(fieldLine("Orders", ordersSummary(restaurant)));
        status.append(fieldLine("Kitchen Queue", restaurant.getKitchenQueue().size() + " waiting"));
        status.append(fieldLine("Completed Orders", restaurant.getCompletedOrders().size()));
        status.append(separator());
        print(status);
    }

    private static boolean isEmptyCollection(
        Collection<?> collection,
        String emptyMessage)
    {
        if (collection.isEmpty())
        {
            println(emptyMessage);
            return (true);
        }
        return (false);
    }

    // Input Helpers

    private static int readId(Scanner scanner, String prompt)
    {
        return (InputReader.readIntPositive(scanner, prompt));
    }

    private static String readName(Scanner scanner, String field)
    {
        return (InputReader.readValidatedString(
                scanner,
                field,
                value -> Validator.validateString(value, field, false)));
    }

    private static double readPrice(Scanner scanner)
    {
        return (InputReader.readDoublePositive(scanner, "Price"));
    }

    private static int readQuantity(Scanner scanner)
    {
        return (InputReader.readIntPositive(scanner, "Quantity"));
    }

    private static boolean shouldExit(Scanner scanner)
    {
        String input;

        while (true)
        {
            println("Are you sure you want to exit? (y/n)");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty())
            {
                println("Invalid input.");
                continue;
            }
            switch (input.charAt(0))
            {
                case 'y' ->
                {
                    println("Goodbye!");
                    return (true);
                }
                case 'n' ->
                {
                    return (false);
                }
                default -> println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    // Lookup Helpers

    private static String menuItemIds(Restaurant restaurant)
    {
        StringBuilder ids;

        ids = new StringBuilder();
        for (MenuItem item : restaurant.getMenu())
        {
            if (ids.length() > 0)
                ids.append(", ");
            ids.append(item.getId());
        }
        return (ids.toString());
    }

    private static String orderIds(Restaurant restaurant)
    {
        StringBuilder ids;

        ids = new StringBuilder();
        for (Order order : restaurant.getOrders().values())
        {
            if (ids.length() > 0)
                ids.append(", ");
            ids.append(order.getOrderId());
        }
        return (ids.toString());
    }

    private static MenuItem findMenuItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        int id;
        MenuItem item;

        println("Available menu items: " + menuItemIds(restaurant));
        while (true)
        {
            id = readId(scanner, "Menu Item ID");
            item = restaurant.findMenuItemById(id);
            if (item != null)
                return (item);
            println("Menu item not found.");
        }
    }

    private static Order findOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        Order order;

        println("Available orders: " + orderIds(restaurant));
        while (true)
        {
            orderId = readId(scanner, "Order ID");
            order = restaurant.findOrderById(orderId);
            if (order != null)
                return (order);
            println("Order not found.");
        }
    }

    // Menu Item Operations

    private static void addMenuItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        int id;
        String name;
        double price;
        String category;
        MenuItem item;

        id = readId(scanner, "Menu Item ID");
        name = readName(scanner, "Name");
        price = readPrice(scanner);
        category = readName(scanner, "Category");
        item = new MenuItem(id, name, price, category);
        restaurant.addMenuItem(item);
        println("Menu item added successfully.");
    }

    private static void removeMenuItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        MenuItem item;

        if (isEmptyCollection(restaurant.getMenu(), "No menu items found."))
            return;
        item = findMenuItem(scanner, restaurant);
        restaurant.removeMenuItem(item);
        println("Menu item removed successfully.");
    }

    private static void displayMenu(Restaurant restaurant)
    {
        Object[][] rows;
        int index;

        if (isEmptyCollection(restaurant.getMenu(), "No menu items found."))
            return;
        rows = new Object[restaurant.getMenu().size()][4];
        index = 0;
        for (MenuItem item : restaurant.getMenu())
        {
            rows[index][0] = item.getId();
            rows[index][1] = item.getName();
            rows[index][2] = item.getCategory();
            rows[index][3] = money(item.getPrice());
            index++;
        }
        println(sectionTitle("Restaurant Menu"));
        print(formatTable(MENU_HEADER, rows));
    }

    private static void searchMenuItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        MenuItem item;

        if (isEmptyCollection(restaurant.getMenu(), "No menu items found."))
            return;
        item = findMenuItem(scanner, restaurant);
        println(sectionTitle("Search Result"));
        print(formatTable(MENU_HEADER, new Object[][]
        {
                { item.getId(), item.getName(), item.getCategory(), money(item.getPrice()) }
        }));
    }

    // Order Operations

    private static void createOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        String customerName;
        Order order;

        orderId = readId(scanner, "Order ID");
        customerName = readName(scanner, "Customer Name");
        order = new Order(orderId, customerName);
        restaurant.createOrder(order);
        println("Order created successfully.");
        order.displayOrder();
    }

    private static void addItemToOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        Order order;
        MenuItem item;
        int quantity;

        if (isEmptyCollection(restaurant.getOrders().values(), "No orders found."))
            return;
        if (isEmptyCollection(restaurant.getMenu(), "No menu items found."))
            return;
        order = findOrder(scanner, restaurant);
        item = findMenuItem(scanner, restaurant);
        quantity = readQuantity(scanner);
        restaurant.addItemToOrder(order, item, quantity);
        println("Item added to order successfully.");
    }

    private static void removeItemFromOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        Order order;
        MenuItem item;

        if (isEmptyCollection(restaurant.getOrders().values(), "No orders found."))
            return;
        if (isEmptyCollection(restaurant.getMenu(), "No menu items found."))
            return;
        order = findOrder(scanner, restaurant);
        item = findMenuItem(scanner, restaurant);
        restaurant.removeItemFromOrder(order, item);
        println("Item removed from order successfully.");
    }

    private static void displayOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        Order order;

        if (isEmptyCollection(restaurant.getOrders().values(), "No orders found."))
            return;
        order = findOrder(scanner, restaurant);
        order.displayOrder();
    }

    private static void cancelOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        Order order;

        if (isEmptyCollection(restaurant.getOrders().values(), "No orders found."))
            return;
        order = findOrder(scanner, restaurant);
        restaurant.cancelOrder(order);
        println("Order cancelled successfully.");
    }

    // Kitchen Operations

    private static void addOrderToKitchen(
        Scanner scanner,
        Restaurant restaurant)
    {
        Order order;

        if (isEmptyCollection(restaurant.getOrders().values(), "No orders found."))
            return;
        order = findOrder(scanner, restaurant);
        restaurant.sendOrderToKitchen(order);
        println("Order added to kitchen queue successfully.");
    }

    private static void processNextOrder(Restaurant restaurant)
    {
        restaurant.processNextOrder();
        println("Next order processed successfully.");
    }

    // Search Operations

    private static void searchOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        Order order;

        if (isEmptyCollection(restaurant.getOrders().values(), "No orders found."))
            return;
        order = findOrder(scanner, restaurant);
        println(sectionTitle("Search Result"));
        order.displayOrder();
    }

    private static void checkOrderStatus(
        Scanner scanner,
        Restaurant restaurant)
    {
        Order order;
        StringBuilder info;

        if (isEmptyCollection(restaurant.getOrders().values(), "No orders found."))
            return;
        order = findOrder(scanner, restaurant);
        info = new StringBuilder();
        info.append(fieldLine("Order ID", order.getOrderId()));
        info.append(fieldLine("Status", order.getStatus()));
        print(info);
    }

    // Completed Orders

    private static void displayCompletedOrders(
        Restaurant restaurant)
    {
        if (isEmptyCollection(
                restaurant.getCompletedOrders().values(),
                "No completed orders found."))
            return;
        println(sectionTitle("Completed Orders"));
        for (Order order : restaurant.getCompletedOrders().values())
            println(order);
    }

    // Program Flow

    private static void startSystem(
        Scanner scanner,
        Restaurant restaurant)
    {
        boolean running;
        int choice;

        running = true;
        displayWelcome();

        while (running)
        {
            printMenuOptions();
            displaySystemStatus(restaurant);

            try
            {
                choice = InputReader.readIntInRange(
                        scanner,
                        "Choice",
                        1,
                        MENU_OPTION_COUNT);

                switch (choice)
                {
                    case 1 -> addMenuItem(scanner, restaurant);
                    case 2 -> removeMenuItem(scanner, restaurant);
                    case 3 -> displayMenu(restaurant);
                    case 4 -> searchMenuItem(scanner, restaurant);
                    case 5 -> createOrder(scanner, restaurant);
                    case 6 -> addItemToOrder(scanner, restaurant);
                    case 7 -> removeItemFromOrder(scanner, restaurant);
                    case 8 -> displayOrder(scanner, restaurant);
                    case 9 -> addOrderToKitchen(scanner, restaurant);
                    case 10 -> processNextOrder(restaurant);
                    case 11 -> searchOrder(scanner, restaurant);
                    case 12 -> checkOrderStatus(scanner, restaurant);
                    case 13 -> displayCompletedOrders(restaurant);
                    case 14 -> cancelOrder(scanner, restaurant);
                    case EXIT_CHOICE -> running = !shouldExit(scanner);
                    default -> println("Invalid choice.");
                }
            }
            catch (IllegalArgumentException | IllegalStateException e)
            {
                println("Error: " +
                    (e.getMessage() != null ? e.getMessage() : e.toString()));
            }
        }
    }

    // Entry Point

    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            Restaurant restaurant = new Restaurant();
            startSystem(scanner, restaurant);
        }
    }
}