package EOM;

import static EOM.utils.ConsoleUtils.*;
import static EOM.utils.InputReader.*;
import EOM.models.*;
import EOM.services.Store;
import java.util.Scanner;

public class Main
{
    // Constants

    private static final int EXIT_CHOICE = 23;
    private static final int MENU_OPTION_COUNT = 23;

    // Entry Point

    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            Store store = new Store();
            startSystem(scanner, store);
        }
    }

    // Program Flow

    private static void startSystem(
        Scanner scanner, Store store)
    {
        boolean running;
        int choice;

        running = true;
        displayWelcome();
        while (running)
        {
            printMenuOptions();
            try
            {
                choice = readIntInRange(
                    scanner, "Choice", 1, MENU_OPTION_COUNT);
                switch (choice)
                {
                    case 1 -> addProduct(scanner, store);
                    case 2 -> removeProduct(scanner, store);
                    case 3 -> displayAllProducts(store);
                    case 4 -> searchProductById(scanner, store);
                    case 5 -> showAllCategories(store);
                    case 6 -> displayProductsOrderedByPrice(store);
                    case 7 -> createOrder(scanner, store);
                    case 8 -> addItemToOrder(scanner, store);
                    case 9 -> removeItemFromOrder(scanner, store);
                    case 10 -> displayOrder(scanner, store);
                    case 11 -> addOrderToShipping(scanner, store);
                    case 12 -> shipNextOrder(store);
                    case 13 -> cancelOrder(scanner, store);
                    case 14 -> searchOrderById(scanner, store);
                    case 15 -> addReview(scanner, store);
                    case 16 -> displayReviewsForProduct(scanner, store);
                    case 17 -> removeOutOfStockProducts(store);
                    case 18 -> displayOrdersOrderedByTotal(store);
                    case 19 -> displayAllOrders(store);
                    case 20 -> displayShippingQueue(store);
                    case 21 -> displayDeliveredOrders(store);
                    case 22 -> displayAllReviews(store);
                    case EXIT_CHOICE -> running = !shouldExit(scanner);
                }
            }
            catch (IllegalArgumentException | IllegalStateException e)
            {
                println("Error: "
                    + (e.getMessage() != null
                    ? e.getMessage()
                    : e.toString()));
            }
        }
    }

    // Menu Display

    private static void displayWelcome()
    {
        println(sectionTitle("Welcome to E-COMMERCE ORDER MANAGER"));
    }

    private static void printMenuOptions()
    {
        String[] options;

        options = new String[]
        {
            "Add Product",
            "Remove Product",
            "Display All Products",
            "Search Product by ID",
            "Show All Categories",
            "Display Products Ordered by Price",
            "Create Order",
            "Add Item to Order",
            "Remove Item from Order",
            "Display Order",
            "Add Order to the Shipping List",
            "Ship Next Order",
            "Cancel Order",
            "Search Order by ID",
            "Add Review to a Product",
            "Show All Reviews for a Product",
            "Remove Out-of-Stock Products",
            "Display Orders Ordered by Total",
            "Display All Orders",
            "Display Shipping Queue",
            "Display Delivered Orders",
            "Display All Reviews",
            "Exit"
        };
        print(formatMenu("E-COMMERCE ORDER MANAGER", options));
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

    // Product Operations

    private static void addProduct(
        Scanner scanner, Store store)
    {
        int id;
        String name;
        double price;
        String category;
        int stockQuantity;
        Product product;

        id = readIntPositive(scanner, "Product ID");
        name = readString(scanner, "Product Name");
        price = readDoublePositive(scanner, "Price");
        category = readString(scanner, "Category");
        stockQuantity = readIntNonNegative(scanner, "Stock Quantity");
        product = new Product(
            id, name, price, category, stockQuantity);
        store.addProduct(product);
        println("Product added successfully.");
    }

    private static void removeProduct(
        Scanner scanner, Store store)
    {
        int productId;

        productId = readIntPositive(scanner, "Product ID");
        store.removeProduct(productId);
        println("Product removed successfully.");
    }

    private static void searchProductById(
        Scanner scanner, Store store)
    {
        int productId;
        Product product;

        productId = readIntPositive(scanner, "Product ID");
        product = store.getProductById(productId);
        println(product);
    }

    private static void displayAllProducts(Store store)
    {
        println(store.displayAllProducts());
    }

    private static void showAllCategories(Store store)
    {
        println(store.showAllCategories());
    }

    private static void displayProductsOrderedByPrice(Store store)
    {
        println(store.displayProductsOrderedByPrice());
    }

    private static void removeOutOfStockProducts(Store store)
    {
        if (store.removeOutOfStockProducts())
            println("Out-of-stock products removed successfully.");
        else
            println("No out-of-stock products to remove.");
    }

    // Lookup Helpers

    private static boolean checkExists(boolean exists, String message)
    {
        if (!exists)
        {
            println("Error: " + message);
            return (false);
        }

        return (true);
    }

    // Order Operations

    private static void createOrder(
        Scanner scanner, Store store)
    {
        int orderId;
        String customerName;
        Order order;

        orderId = readIntPositive(scanner, "Order ID");
        if (!checkExists(!store.hasOrder(orderId),
                "Order with ID " + orderId + " already exists"))
            return;
        customerName = readString(scanner, "Customer Name");
        order = new Order(orderId, customerName);
        store.addOrder(order);
        println("Order created successfully.");
    }

    private static void addItemToOrder(
        Scanner scanner, Store store)
    {
        int orderId;
        int productId;
        int quantity;

        orderId = readIntPositive(scanner, "Order ID");
        if (!checkExists(store.hasOrder(orderId),
                "Order with ID " + orderId + " does not exist"))
            return;
        productId = readIntPositive(scanner, "Product ID");
        if (!checkExists(store.hasProduct(productId),
                "Product with ID " + productId + " does not exist"))
            return;
        quantity = readIntPositive(scanner, "Quantity");
        store.addItemToOrder(orderId, productId, quantity);
        println("Item added to order successfully.");
    }

    private static void removeItemFromOrder(
        Scanner scanner, Store store)
    {
        int orderId;
        int productId;

        orderId = readIntPositive(scanner, "Order ID");
        if (!checkExists(store.hasOrder(orderId),
                "Order with ID " + orderId + " does not exist"))
            return;
        productId = readIntPositive(scanner, "Product ID");
        store.removeItemFromOrder(orderId, productId);
        println("Item removed from order successfully.");
    }

    private static void displayOrder(
        Scanner scanner, Store store)
    {
        searchOrderById(scanner, store);
    }

    private static void searchOrderById(
        Scanner scanner, Store store)
    {
        int orderId;
        Order order;

        orderId = readIntPositive(scanner, "Order ID");
        order = store.getOrderById(orderId);
        println(order);
    }

    private static void displayOrdersOrderedByTotal(Store store)
    {
        println(store.displayOrdersOrderedByTotal());
    }

    private static void displayAllOrders(Store store)
    {
        println(store.displayAllOrders());
    }

    // Shipping Operations

    private static void addOrderToShipping(
        Scanner scanner, Store store)
    {
        int orderId;

        orderId = readIntPositive(scanner, "Order ID");
        store.addOrderToShipping(orderId);
        println("Order added to shipping successfully.");
    }

    private static void shipNextOrder(Store store)
    {
        store.shipNextOrder();
        println("Next order shipped successfully.");
    }

    private static void cancelOrder(
        Scanner scanner, Store store)
    {
        int orderId;

        orderId = readIntPositive(scanner, "Order ID");
        store.cancelOrder(orderId);
        println("Order cancelled successfully.");
    }

    private static void displayShippingQueue(Store store)
    {
        println(store.displayShippingQueue());
    }

    private static void displayDeliveredOrders(Store store)
    {
        println(store.displayDeliveredOrders());
    }

    // Review Operations

    private static void addReview(
        Scanner scanner, Store store)
    {
        int productId;
        String customerName;
        String comment;

        productId = readIntPositive(scanner, "Product ID");
        if (!checkExists(store.hasProduct(productId),
                "Product with ID " + productId + " does not exist"))
            return;
        customerName = readString(scanner, "Customer Name");
        comment = readString(scanner, "Comment");
        store.addReview(productId, customerName, comment);
        println("Review added successfully.");
    }

    private static void displayReviewsForProduct(
        Scanner scanner, Store store)
    {
        int productId;

        productId = readIntPositive(scanner, "Product ID");
        println(store.displayProductReviews(productId));
    }

    private static void displayAllReviews(Store store)
    {
        println(store.displayAllReviews());
    }
}