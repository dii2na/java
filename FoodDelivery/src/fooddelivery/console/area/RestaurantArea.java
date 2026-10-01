package fooddelivery.console.area;

import fooddelivery.console.ConsoleContext;
import fooddelivery.exception.FoodDeliveryException;
import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.restaurant.menu.*;
import fooddelivery.service.report.RevenueReportService.RestaurantRevenueBreakdown;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import static fooddelivery.console.ConsoleFormat.*;
import static fooddelivery.utils.ConsoleUtils.*;
import static fooddelivery.utils.InputReader.*;

public class RestaurantArea
{
    private static final String TITLE = "MASR DELIVERY - RESTAURANT";
    private static final int EXIT_CHOICE = 10;

    private final ConsoleContext context;

    public RestaurantArea(ConsoleContext context)
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
                    case 1 -> advanceOrder(
                        scanner, OrderStatus.PLACED);
                    case 2 -> cancelPendingOrder(scanner);
                    case 3 -> advanceOrder(
                        scanner, OrderStatus.ACCEPTED);
                    case 4 -> advanceOrder(
                        scanner, OrderStatus.PREPARING);
                    case 5 -> toggleItemAvailability(scanner);
                    case 6 -> addMenuItem(scanner);
                    case 7 -> removeMenuItem(scanner);
                    case 8 -> adjustDailyStock(scanner);
                    case 9 -> dailySummary(scanner);
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
            "Accept a Pending Order",
            "Reject a Pending Order",
            "Mark an Order Preparing",
            "Mark an Order Ready",
            "Toggle Item Availability",
            "Add Menu Item",
            "Remove Menu Item",
            "Adjust Daily Stock",
            "Today's Orders and Revenue");
    }

    private void printSystemStatus()
    {
        printBanner(
            "Restaurants", context.allRestaurants().size(),
            "Orders", context.allOrders().size(),
            "Ready Queue",
            context.readyQueue().size() + " waiting");
    }

    // Order handling

    private Restaurant chooseRestaurant(Scanner scanner)
    {
        if (context.allRestaurants().isEmpty())
            throw new IllegalArgumentException(
                "No restaurants are registered yet");
        println(sectionTitle("Restaurants"));
        printRestaurants(context.allRestaurants());

        return (context.findRestaurant(
            readString(scanner, "Restaurant ID")));
    }

    private void advanceOrder(
        Scanner scanner,
        OrderStatus currentStatus)
    {
        Restaurant restaurant;
        Order order;
        OrderStatus nextStatus;

        restaurant = chooseRestaurant(scanner);
        order = chooseOrderAtStatus(
            scanner,
            restaurant,
            currentStatus);
        nextStatus = currentStatus.next()
            .orElseThrow(() ->
                new IllegalStateException(
                    "Order cannot advance from " + currentStatus));
        context.advanceOrder(order.getId(), nextStatus);
        printStatusBanner(
            "Order " + order.getId() + " is now " + nextStatus);
    }

    private void cancelPendingOrder(Scanner scanner)
    {
        Restaurant restaurant;
        Order order;

        restaurant = chooseRestaurant(scanner);
        order = chooseOrderAtStatus(
            scanner,
            restaurant,
            OrderStatus.PLACED);
        if (!readConfirmation(
            scanner,
            "Reject order " + order.getId() + "?"))
        {
            println("Rejection abandoned.");
            return;
        }
        context.cancelOrder(order.getId());

        printStatusBanner(
            "Order " + order.getId() + " rejected");
    }

    private Order chooseOrderAtStatus(
        Scanner scanner,
        Restaurant restaurant,
        OrderStatus status)
    {
        List<Order> matching;
        Optional<Order> found;
        String candidateId;

        matching = context.ordersFor(restaurant, status);

        if (matching.isEmpty())
            throw new IllegalArgumentException(
                "This restaurant has no order at status "
                    + status);

        println(sectionTitle("Orders at " + status));
        printOrders(matching);

        while (true)
        {
            candidateId = readString(scanner, "Order ID");
            found = context.findOrderIn(
                matching, candidateId);
            if (found.isPresent())
                return (found.get());
            println("Order \"" + candidateId
                + "\" is not at status " + status + ".");
        }
    }

    // Menu management

    private void toggleItemAvailability(Scanner scanner)
    {
        Restaurant restaurant;
        MenuItem item;

        restaurant = chooseRestaurant(scanner);
        item = chooseMenuItem(scanner, restaurant);

        item.setAvailable(!item.isAvailable());

        printStatusBanner(
            item.getName() + " is now "
                + (item.isAvailable()
                    ? "AVAILABLE"
                    : "UNAVAILABLE"));
    }

    private void adjustDailyStock(Scanner scanner)
    {
        Restaurant restaurant;
        MenuItem item;
        BigDecimal newStock;
        BigDecimal delta;

        restaurant = chooseRestaurant(scanner);
        item = chooseMenuItem(scanner, restaurant);
        newStock = readDecimalNonNegative(
            scanner, "New stock quantity");
        delta = newStock.subtract(item.getStockQuantity());

        if (delta.signum() > 0)
            item.addStock(delta);
        else if (delta.signum() < 0)
            item.reduceStock(delta.abs());

        printStatusBanner(
            item.getName() + " stock is now "
                + money(item.getStockQuantity()));
    }

    private void addMenuItem(Scanner scanner)
    {
        Restaurant restaurant;
        MenuItemType type;
        MenuItem item;

        restaurant = chooseRestaurant(scanner);
        type = readMenuItemType(scanner);
        item = buildMenuItem(scanner, restaurant, type);
        restaurant.addMenuItem(item);
        printStatusBanner(
            "Added " + item.getName() + " to "
                + restaurant.getDisplayName());
    }

    private MenuItemType readMenuItemType(Scanner scanner)
    {
        printMenuItemTypeOptions();

        return (switch (readIntInRange(scanner, "Type", 1, 3))
        {
            case 1 -> MenuItemType.STANDARD;
            case 2 -> MenuItemType.WEIGHTED;
            case 3 -> MenuItemType.COMBO;
            default -> throw new IllegalArgumentException(
                "Unsupported item type");
        });
    }

    private void printMenuItemTypeOptions()
    {
        println(sectionTitle("Item Types"));
        println("  1.  Standard item (fixed price)");
        println("  2.  Weighted item (price per kg)");
        println("  3.  Combo (bundle at a discount)");
        println();
    }

    private MenuItem buildMenuItem(
        Scanner scanner,
        Restaurant restaurant,
        MenuItemType type)
    {
        String id;
        String name;
        String category;
        int prepTime;
        BigDecimal stock;
        boolean available;
        BigDecimal price;
        List<MenuItem> parts;
        BigDecimal discount;

        id = readString(scanner, "Item ID");
        name = readString(scanner, "Item name");
        category = readString(scanner, "Category");
        prepTime = readIntPositive(scanner, "Preparation time");
        stock = readDecimalPositive(scanner, "Stock quantity");
        available = readConfirmation(scanner, "Available?");
        price = null;
        parts = null;
        discount = null;

        if (type == MenuItemType.COMBO)
        {
            parts = readComboParts(scanner, restaurant);
            discount = readRate(scanner, "Discount rate (0-1)");
        }
        else
        {
            price = readItemPrice(scanner, type);
        }

        return (MenuItemFactory.create(
            type,
            id,
            name,
            price,
            category,
            prepTime,
            available,
            stock,
            parts,
            discount));
    }

    private BigDecimal readItemPrice(
        Scanner scanner,
        MenuItemType type)
    {
        return (readDecimalPositive(
            scanner,
            type == MenuItemType.WEIGHTED
                ? "Price per kg"
                : "Price"));
    }

    private List<MenuItem> readComboParts(
        Scanner scanner,
        Restaurant restaurant)
    {
        List<MenuItem> parts;
        String more;

        println(sectionTitle(
            "Combo parts - " + restaurant.getDisplayName()));
        printMenu(restaurant);
        parts = new ArrayList<>();
        while (true)
        {
            parts.add(chooseMenuItem(scanner, restaurant));
            more = readOptionalString(
                scanner, "Add another part? (y/n)");
            if (more == null || !more.equalsIgnoreCase("y"))
                break;
        }

        return (parts);
    }

    private void removeMenuItem(Scanner scanner)
    {
        Restaurant restaurant;
        MenuItem item;

        restaurant = chooseRestaurant(scanner);
        item = chooseMenuItem(scanner, restaurant);

        if (!readConfirmation(
            scanner,
            "Remove " + item.getName() + "?"))
        {
            println("Removal abandoned.");
            return;
        }
        restaurant.removeMenuItem(item.getId());
        printStatusBanner(
            "Removed " + item.getName());
    }

    private MenuItem chooseMenuItem(
        Scanner scanner,
        Restaurant restaurant)
    {
        String itemId;
        Optional<MenuItem> found;

        if (restaurant.getMenu().isEmpty())
            throw new IllegalArgumentException(
                "This restaurant has no menu items");
        printMenu(restaurant);
        itemId = readString(scanner, "Menu Item ID");
        found = restaurant.getMenuItem(itemId);
        if (found.isEmpty())
            throw new IllegalArgumentException(
                "Menu item \"" + itemId + "\" not found.");
        return (found.get());
    }

    // Daily summary

    private void dailySummary(Scanner scanner)
    {
        Restaurant restaurant;
        List<Order> today;
        RestaurantRevenueBreakdown summary;
        LocalDate date;

        restaurant = chooseRestaurant(scanner);
        date = LocalDate.now();
        today = context.reports().ordersFor(restaurant, date, date);

        if (today.isEmpty())
        {
            println("No orders placed today.");
            return;
        }

        summary = context.reports().revenueFor(
            restaurant, date, date);

        println(sectionTitle(
            "Today - " + restaurant.getDisplayName()));
        print(fieldLine("Orders Today", today.size()));
        print(fieldLine(
            "Delivered Orders", summary.completedOrders()));
        print(fieldLine(
            "Item Subtotal", money(summary.itemsSubtotal())));
        print(fieldLine(
            "Fees and Adjustments",
            money(summary.feesAndAdjustments())));
        print(fieldLine(
            "Delivered Revenue", money(summary.revenue())));
        println();
        printOrders(today);
    }
}
