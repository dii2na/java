package fooddelivery.console.area;

import fooddelivery.console.ConsoleContext;
import fooddelivery.exception.FoodDeliveryException;
import fooddelivery.model.customer.Address;
import fooddelivery.model.customer.Customer;
import fooddelivery.model.customer.CustomerValidator;
import fooddelivery.model.promotion.Promotion;
import fooddelivery.model.promotion.PromotionFactory;
import fooddelivery.model.promotion.PromotionType;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.rider.Rider;
import fooddelivery.model.rider.VehicleType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import static fooddelivery.console.ConsoleFormat.*;
import static fooddelivery.utils.ConsoleUtils.*;
import static fooddelivery.utils.InputReader.*;

public class AdminArea
{
    private static final String TITLE = "MASR DELIVERY - ADMIN";
    private static final int EXIT_CHOICE = 14;
    private static final DateTimeFormatter EXPIRY_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");

    private final ConsoleContext context;
    private final ReportPanel reportPanel;

    public AdminArea(ConsoleContext context)
    {
        this.context = context;
        this.reportPanel = new ReportPanel(context);
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
                switch (readIntInRange(scanner, "Choice", 1, EXIT_CHOICE))
                {
                    case 1 -> addRestaurant(scanner);
                    case 2 -> removeRestaurant(scanner);
                    case 3 -> toggleRestaurantOpen(scanner);
                    case 4 -> updateRestaurantRating(scanner);
                    case 5 -> viewRestaurants();
                    case 6 -> viewRestaurantMenu(scanner);
                    case 7 -> addCustomer(scanner);
                    case 8 -> viewCustomers();
                    case 9 -> addRider(scanner);
                    case 10 -> viewRiders();
                    case 11 -> createPromotion(scanner);
                    case 12 -> reportPanel.run(scanner);
                    case 13 -> platformStatistics();
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
            "Add Restaurant",
            "Remove Restaurant",
            "Toggle Restaurant Open / Closed",
            "Update Restaurant Rating",
            "View All Restaurants",
            "View a Restaurant Menu",
            "Register Customer",
            "View All Customers",
            "Register Rider",
            "View All Riders",
            "Create Promotion",
            "Run Reports",
            "Platform Statistics");
    }

    private void printSystemStatus()
    {
        printBanner(
            "Restaurants", context.allRestaurants().size(),
            "Customers", context.allCustomers().size(),
            "Riders", context.allRiders().size(),
            "Promotions", context.allPromotions().size(),
            "Orders", context.allOrders().size());
    }

    // Registration

    private void addRestaurant(Scanner scanner)
    {
        String id;
        String displayName;
        String district;
        Set<String> cuisines;
        boolean open;
        Restaurant restaurant;

        id = readString(scanner, "Restaurant ID");
        displayName = readString(scanner, "Display name");
        district = readString(scanner, "District");
        cuisines = readCuisines(scanner);
        open = readConfirmation(scanner, "Open now?");
        restaurant = new Restaurant(
            id,
            displayName,
            district,
            cuisines,
            open);
        context.registerRestaurant(restaurant);
        printStatusBanner(
            "Registered " + restaurant.getDisplayName());
    }

    private Set<String> readCuisines(Scanner scanner)
    {
        Set<String> cuisines;
        String cuisine;
        String more;

        cuisines = new LinkedHashSet<>();
        println("Enter at least one cuisine.");
        more = "y";
        while (more != null && more.equalsIgnoreCase("y"))
        {
            cuisine = readString(scanner, "Cuisine");
            cuisines.add(cuisine);
            more = readOptionalString(
                scanner, "Add another cuisine? (y/n)");
        }

        return (cuisines);
    }

    private void removeRestaurant(Scanner scanner)
    {
        Restaurant restaurant;
        boolean hasOrders;

        restaurant = chooseRestaurant(scanner);
        hasOrders = !context.ordersFor(restaurant).isEmpty();
        if (hasOrders)
            throw new IllegalArgumentException(
                restaurant.getDisplayName()
                    + " still has orders and cannot be removed");
        if (!readConfirmation(
            scanner,
            "Remove " + restaurant.getDisplayName() + "?"))
        {
            println("Removal abandoned.");
            return;
        }
        context.deregisterRestaurant(restaurant);
        printStatusBanner(
            "Removed " + restaurant.getDisplayName());
    }

    private void toggleRestaurantOpen(Scanner scanner)
    {
        Restaurant restaurant;
        boolean open;

        restaurant = chooseRestaurant(scanner);
        open = !restaurant.isOpen();
        if (open)
            restaurant.open();
        else
            restaurant.close();
        printStatusBanner(
            restaurant.getDisplayName() + " is now "
                + (open
                    ? "OPEN"
                    : "CLOSED"));
    }

    private void updateRestaurantRating(Scanner scanner)
    {
        Restaurant restaurant;
        double newRating;

        restaurant = chooseRestaurant(scanner);
        newRating = readDoubleInRange(
            scanner,
            "New average rating",
            0.0,
            5.0);
        restaurant.updateRating(newRating);
        printStatusBanner(
            restaurant.getDisplayName() + " rating is now "
                + rating(newRating));
    }

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

    private void viewRestaurants()
    {
        if (context.allRestaurants().isEmpty())
            throw new IllegalArgumentException(
                "No restaurants are registered yet");
        println();
        printRestaurants(context.allRestaurants());
    }

    private void viewRestaurantMenu(Scanner scanner)
    {
        Restaurant restaurant;

        restaurant = chooseRestaurant(scanner);
        println();
        printMenu(restaurant);
    }

    private void addCustomer(Scanner scanner)
    {
        String id;
        String name;
        String mobile;
        String district;
        String detail;
        BigDecimal openingBalance;
        Customer customer;

        id = readString(scanner, "Customer ID");
        name = readString(scanner, "Customer name");
        mobile = readValidatedString(
            scanner,
            "Mobile number",
            value ->
                CustomerValidator.validateEgyptianMobile(
                    value));
        district = readString(scanner, "Address district");
        detail = readString(scanner, "Address detail");
        openingBalance = readDecimalNonNegative(
            scanner, "Opening wallet balance");
        customer = new Customer(
            id,
            name,
            mobile,
            new Address(district, detail));
        customer.addToWallet(openingBalance);
        context.registerCustomer(customer);
        printStatusBanner(
            "Registered " + customer.getName()
                + " (" + customer.getLoyaltyTier() + ")");
    }

    private void addRider(Scanner scanner)
    {
        String id;
        String name;
        String district;
        VehicleType vehicleType;
        Rider rider;

        printVehicleTypeOptions();
        id = readString(scanner, "Rider ID");
        name = readString(scanner, "Rider name");
        vehicleType = chooseVehicleType(scanner);
        district = readString(scanner, "Current district");
        rider = new Rider(
            id,
            name,
            vehicleType,
            district,
            false);
        context.registerRider(rider);
        printStatusBanner(
            "Registered rider " + rider.getName());
    }

    private void viewCustomers()
    {
        if (context.allCustomers().isEmpty())
            throw new IllegalArgumentException(
                "No customers are registered yet");
        println();
        printCustomers(context.allCustomers());
    }

    private void viewRiders()
    {
        if (context.allRiders().isEmpty())
            throw new IllegalArgumentException(
                "No riders are registered yet");
        println();
        printRiders(context.allRiders());
    }

    private void printVehicleTypeOptions()
    {
        println(sectionTitle("Vehicle Types"));
        for (VehicleType type : VehicleType.values())
            println("  - " + type
                + " (range " + type.getRangeKm() + " km, "
                + "max order " + money(type.getMaxOrderValue())
                + " EGP)");
        println();
    }

    private VehicleType chooseVehicleType(Scanner scanner)
    {
        int choice;
        VehicleType[] types;

        types = VehicleType.values();
        printNumberedList(Arrays.stream(types)
            .map(String::valueOf)
            .toArray(String[]::new));
        choice = readIntInRange(
            scanner,
            "Vehicle type",
            1,
            types.length);
        return (types[choice - 1]);
    }

    private void createPromotion(Scanner scanner)
    {
        String code;
        BigDecimal minimumSubtotal;
        LocalDateTime expiry;
        String district;
        boolean firstTimeOnly;
        PromotionType type;
        BigDecimal discountRate;
        BigDecimal discountAmount;
        BigDecimal maximumDiscount;
        Promotion promotion;

        printPromotionTypeOptions();
        code = readString(scanner, "Promotion code");
        minimumSubtotal = readDecimalNonNegative(
            scanner, "Minimum subtotal");
        expiry = readExpiry(scanner);
        district = readOptionalString(
            scanner, "Restricted district");
        firstTimeOnly = readConfirmation(
            scanner, "First-time customers only?");
        type = readPromotionType(scanner);
        discountRate = null;
        discountAmount = null;
        maximumDiscount = null;
        if (type == PromotionType.PERCENTAGE)
        {
            discountRate = readRate(
                scanner, "Discount rate (0-1)");
            maximumDiscount = readDecimalPositive(
                scanner, "Maximum discount cap");
        }
        else if (type == PromotionType.FIXED_AMOUNT)
        {
            discountAmount = readDecimalPositive(
                scanner, "Discount amount");
        }
        promotion = PromotionFactory.create(
            type,
            code,
            expiry,
            minimumSubtotal,
            discountRate,
            discountAmount,
            maximumDiscount,
            district,
            firstTimeOnly);
        context.registerPromotion(promotion);
        printStatusBanner(
            "Created promotion " + promotion.getCode());
    }

    private void printPromotionTypeOptions()
    {
        println(sectionTitle("Promotion Types"));
        println("  1.  Percentage off subtotal (capped)");
        println("  2.  Fixed amount off subtotal");
        println("  3.  Free delivery");
        println();
    }

    private PromotionType readPromotionType(Scanner scanner)
    {
        return (switch (readIntInRange(
            scanner, "Type", 1, 3))
        {
            case 1 -> PromotionType.PERCENTAGE;
            case 2 -> PromotionType.FIXED_AMOUNT;
            case 3 -> PromotionType.FREE_DELIVERY;
            default -> throw new IllegalArgumentException(
                "Unsupported promotion type");
        });
    }

    private LocalDateTime readExpiry(Scanner scanner)
    {
        LocalDate date;
        String more;

        date = readDate(scanner, "Expiry date (yyyy-MM-dd)");
        more = readOptionalString(scanner, "Exact expiry time");
        if (more == null)
            return (date.atTime(23, 59));
        while (true)
        {
            try
            {
                return (LocalDateTime.parse(
                    date + " " + more.trim(), EXPIRY_FORMATTER));
            }
            catch (DateTimeParseException exception)
            {
                printInvalidInput(
                    "Please use the HH:mm or HH:mm:ss format.");
                more = readOptionalString(
                    scanner, "Exact expiry time");
                if (more == null)
                    return (date.atTime(23, 59));
            }
        }
    }

    private void platformStatistics()
    {
        println(sectionTitle("Platform Statistics"));
        printSystemStatus();
        println();
        printRestaurants(context.allRestaurants());
        println();
        printCustomers(context.allCustomers());
        println();
        printRiders(context.allRiders());
        println();
        printPromotions(context.allPromotions());
        println();
        printOrders(context.allOrders());
    }
}
