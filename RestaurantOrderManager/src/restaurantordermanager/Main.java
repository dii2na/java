package restaurantordermanager;

import static restaurantordermanager.utils.ConsoleUtils.*;
import restaurantordermanager.utils.InputReader;
import restaurantordermanager.utils.Validator;
import restaurantordermanager.services.Restaurant;
import restaurantordermanager.models.MenuItem;
import restaurantordermanager.models.Order;
import restaurantordermanager.enums.OrderStatus;
import java.util.Scanner;

public class Main
{
    // Constants

    private static final int EXIT_CHOICE = 14;
    private static final int MENU_OPTION_COUNT = 14;


    // Display / UI

    private static void displayWelcome()
    {
        println(sectionTitle("Welcome to RESTAURANT ORDER MANAGER"));
    }

   private static void displayMenu()
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
                "Display Completed Orders"
        };
        println();
        println(sectionTitle("RESTAURANT ORDER MANAGER"));
        for (index = 0; index < options.length; index++)
            println("  %2d.  %s".formatted(index + 1, options[index]));
        println("  %2d.  %s".formatted(EXIT_CHOICE, "Exit"));
        println();
    }

    // Input Helpers

    private static int readMenuItemId(Scanner scanner)
    {
        return (InputReader.readIntPositive(scanner, "Menu Item ID"));
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


    private static int readOrderId(Scanner scanner)
    {
        return (InputReader.readIntPositive(scanner, "Order ID"));
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

        id = readMenuItemId(scanner);
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
        int id;
        MenuItem item;

        id = readMenuItemId(scanner);
        item = restaurant.findMenuItemById(id);
        if (item == null)
        {
            println("Menu item not found.");
            return;
        }
        restaurant.removeMenuItem(item);
        println("Menu item removed successfully.");
    }

    private static void displayMenu(Restaurant restaurant)
    {
        if (restaurant.getMenu().isEmpty())
        {
            println("No menu items found.");
            return;
        }
        println(sectionTitle("Restaurant Menu"));
        println(formatRow("ID", "Name", "Category", "Price"));
        for (MenuItem item : restaurant.getMenu())
            println(item);
    }

    private static void searchMenuItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        int id;
        MenuItem item;

        id = readMenuItemId(scanner);
        item = restaurant.findMenuItemById(id);
        if (item == null)
        {
            println("Menu item not found.");
            return;
        }
        println(sectionTitle("Search Result"));
        println(formatRow("ID", "Name", "Category", "Price"));
        println(item);
    }

    // Order Operations

    private static void createOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        String customerName;
        Order order;

        orderId = readOrderId(scanner);
        customerName = readName(scanner, "Customer Name");
        order = new Order(orderId, customerName);
        restaurant.createOrder(order);
        println("Order created successfully.");
        println(order);
    }

    private static void addItemToOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        int menuItemId;
        int quantity;
        Order order;
        MenuItem item;

        orderId = readOrderId(scanner);
        menuItemId = readMenuItemId(scanner);
        quantity = readQuantity(scanner);
        order = restaurant.findOrderById(orderId);
        if (order == null)
        {
            println("Order not found.");
            return;
        }
        item = restaurant.findMenuItemById(menuItemId);
        if (item == null)
        {
            println("Menu item not found.");
            return;
        }
        restaurant.addItemToOrder(order, item, quantity);
        println("Item added to order successfully.");
    }

    private static void removeItemFromOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        int menuItemId;
        Order order;
        MenuItem item;

        orderId = readOrderId(scanner);
        menuItemId = readMenuItemId(scanner);
        order = restaurant.findOrderById(orderId);
        if (order == null)
        {
            println("Order not found.");
            return;
        }
        item = restaurant.findMenuItemById(menuItemId);
        if (item == null)
        {
            println("Menu item not found.");
            return;
        }
        restaurant.removeItemFromOrder(order, item);
        println("Item removed from order successfully.");
    }

    private static void displayOrder(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        Order order;

        orderId = readOrderId(scanner);
        order = restaurant.findOrderById(orderId);
        if (order == null)
        {
            println("Order not found.");
            return;
        }
        println(order);
    }

    // Kitchen Operations

    private static void addOrderToKitchen(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        Order order;

        orderId = readOrderId(scanner);
        order = restaurant.findOrderById(orderId);
        if (order == null)
        {
            println("Order not found.");
            return;
        }
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
        int orderId;
        Order order;

        orderId = readOrderId(scanner);
        order = restaurant.findOrderById(orderId);
        if (order == null)
        {
            println("Order not found.");
            return;
        }
        println(sectionTitle("Search Result"));
        println(order);
    }

    private static void checkOrderStatus(
        Scanner scanner,
        Restaurant restaurant)
    {
        int orderId;
        Order order;

        orderId = readOrderId(scanner);
        order = restaurant.findOrderById(orderId);
        if (order == null)
        {
            println("Order not found.");
            return;
        }
        println(fieldLine("Order ID", order.getOrderId()));
        println(fieldLine("Status", order.getStatus()));
    }

    private static void displayCompletedOrders(
        Restaurant restaurant)
    {
        if (restaurant.getCompletedOrders().isEmpty())
        {
            println("No completed orders found.");
            return;
        }
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
            displayMenu();

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
                    case 2 ->removeMenuItem(scanner, restaurant);
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
                    case EXIT_CHOICE -> running = !shouldExit(scanner);
                    default -> println("Invalid choice.");
                }
            }
            catch (IllegalArgumentException | IllegalStateException e)
            {
                println("Error: " + 
                    (e.getMessage() != null? e.getMessage(): e.toString()));
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
