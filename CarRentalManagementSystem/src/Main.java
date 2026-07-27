import java.time.Year;
import java.util.Scanner;

public class Main
{
    private static final int MAX_CARS = 20;
    private static final int MAX_CUSTOMERS = 20;

    // Display / UI

    private static void displayWelcome()
    {
        System.out.println("""
            ======================================
                Welcome to SPEEDWAY RENTALS
            ======================================
            """);
    }

    private static void displayMenu()
    {
        System.out.println("""

            ========== SPEEDWAY RENTALS ==========
            1. Add Regular Car
            2. Add Luxury Car
            3. Add Customer
            4. Display All Cars
            5. Display Available Cars
            6. Rent Car
            7. Return Car
            8. Search Car By ID
            9. Search Car By Brand
            10. Display Customers
            11. Display Statistics
            0. Exit
            ======================================
            Choose:
            """);
    }

    private static void displayCars(Car[] cars, String title, String emptyMessage)
    {
        if (cars.length == 0)
        {
            System.out.println(emptyMessage);
            return;
        }
        System.out.println("""
            ----------------------------------------
                          %s
            ----------------------------------------
            """.formatted(title));
        for (Car car : cars)
        {
            System.out.println(car);
        }
    }

    private static void displayAllCars(RentalSystem system)
    {
        Car[] allCars;

        allCars = system.getAllCars();
        displayCars(allCars, "ALL CARS", "No cars in the fleet.");
        if (allCars.length > 0)
            System.out.println("  Total cars in the fleet: " + allCars.length);
    }

    private static void displayAvailableCars(RentalSystem system)
    {
        Car[] available;

        available = system.getAvailableCars();
        displayCars(available, "AVAILABLE CARS", "No available cars.");
        if (available.length > 0)
            System.out.println("  Total available cars: " + available.length);
    }

    private static void displayCustomers(RentalSystem system)
    {
        Customer[] customers;
        Car car;

        customers = system.getCustomers();
        if (customers.length == 0)
        {
            System.out.println("No customers registered.");
            return;
        }
        System.out.println("""
            ----------------------------------------
                      REGISTERED CUSTOMERS
            ----------------------------------------
            """);
        for (Customer customer : customers)
        {
            System.out.println(customer);
            car = system.findCustomerCar(customer.getId());
            if (car != null)
            {
                System.out.println("  Current Car:");
                System.out.println(car);
            }
            else
                System.out.println("  Current Car: None");
        }
    }

    // Program Flow

    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in)) 
        {
            RentalSystem system = new RentalSystem(MAX_CARS, MAX_CUSTOMERS);
            startSystem(scanner, system);
        }
    }

    private static void startSystem(Scanner scanner, RentalSystem system)
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
                choice = InputReader.readInt(scanner, "choice");
                switch (choice)
                {
                    case 0 -> running = !shouldExit(scanner, system);
                    case 1, 2 -> addCar(scanner, system, choice == 2);
                    case 3 -> addCustomer(scanner, system);
                    case 4 -> displayAllCars(system);
                    case 5 -> displayAvailableCars(system);
                    case 6 -> rentCar(scanner, system);
                    case 7 -> returnCar(scanner, system);
                    case 8 -> searchCarById(scanner, system);
                    case 9 -> searchCarByBrand(scanner, system);
                    case 10 -> displayCustomers(system);
                    case 11 -> System.out.println(system.getStatistics());
                    default -> System.out.println("Invalid choice.");
                }
            }
            catch (IllegalArgumentException | IllegalStateException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    private static boolean shouldExit(Scanner scanner, RentalSystem system)
    {
        String input;
        char response;

        while (true)
        {
            System.out.println("Are you sure you want to exit? (y/n)");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty())
            {
                System.out.println("Invalid input.");
                continue;
            }
            response = input.charAt(0);
            switch (response)
            {
                case 'y' ->
                {
                    System.out.println("\n" + system.getSummary());
                    System.out.println("\nGoodbye!");
                    return (true);
                }
                case 'n' -> { return (false); }
                default -> System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    // Car Operations

    private static void addCar(Scanner scanner, RentalSystem system, boolean isLuxury)
    {
        int id;
        String brand;
        String model;
        int year;
        double pricePerDay;
        double insuranceFee;

        id = InputReader.readIntPositive(scanner, "car ID");
        if (system.carExists(id))
        {
            System.out.println("Car ID already exists.");
            return;
        }
        brand = InputReader.readString(scanner, "brand");
        model = InputReader.readString(scanner, "model");
        year = InputReader.readIntInRange(scanner, "year", 1990, Year.now().getValue());
        pricePerDay = InputReader.readDoublePositive(scanner, "price per day");
        if (isLuxury)
        {
            insuranceFee = InputReader.readDoubleNonNegative(scanner, "insurance fee");
            system.addLuxuryCar(id, brand, model, year, pricePerDay, insuranceFee);
            System.out.println("Luxury car added successfully.\nCar ID: " + id);
        }
        else
        {
            system.addCar(id, brand, model, year, pricePerDay);
            System.out.println("Regular car added successfully.\nCar ID: " + id);
        }
    }

    // Customer Operations

    private static void addCustomer(Scanner scanner, RentalSystem system)
    {
        int id;
        String name;
        String phone;

        id = InputReader.readIntPositive(scanner, "customer ID");
        if (system.customerExists(id))
        {
            System.out.println("Customer ID already exists.");
            return;
        }
        name = InputReader.readString(scanner, "name");
        phone = InputReader.readPhone(scanner);
        system.addCustomer(id, name, phone);
        System.out.println("Customer added successfully.\nCustomer ID: " + id + "\nName: " + name);
    }

    // Rental Operations

    private static void rentCar(Scanner scanner, RentalSystem system)
    {
        int customerId;
        int carId;
        int days;
        String receipt;

        customerId = InputReader.readIntPositive(scanner, "customer ID");
        if (!validateCustomer(system, customerId, true))
            return;
        carId = InputReader.readIntPositive(scanner, "car ID");
        if (!validateCar(system, carId))
            return;
        days = InputReader.readIntPositive(scanner, "rental days");
        receipt = system.rentCar(customerId, carId, days);
        System.out.println(receipt);
    }

    private static void returnCar(Scanner scanner, RentalSystem system)
    {
        int customerId;
        String message;

        customerId = InputReader.readIntPositive(scanner, "customer ID");
        if (!validateCustomer(system, customerId, false))
            return;
        message = system.returnCar(customerId);
        System.out.println(message);
    }

    // Search Methods

    private static void searchCarById(Scanner scanner, RentalSystem system)
    {
        int id;
        Car car;

        id = InputReader.readIntPositive(scanner, "car ID");
        car = system.findCarById(id);
        if (car == null)
            System.out.println("Car not found.");
        else
            System.out.println(car);
    }

    private static void searchCarByBrand(Scanner scanner, RentalSystem system)
    {
        String brand;
        Car[] matches;

        brand = InputReader.readString(scanner, "car brand");
        matches = system.findCarsByBrand(brand);
        displayCars(matches, "SEARCH RESULTS", "No cars found with this brand.");
        if (matches.length > 0)
            System.out.println("  Number of matching cars: " + matches.length);
    }

    // Validation Helpers

    private static boolean validateCar(RentalSystem system, int carId)
    {
        if (!system.carExists(carId))
        {
            System.out.println("Car not found.");
            return (false);
        }
        if (!system.isCarAvailable(carId))
        {
            System.out.println("Car is already rented.");
            return (false);
        }
        return (true);
    }

    private static boolean validateCustomer(RentalSystem system, int customerId, boolean checkRenting)
    {
        if (!system.customerExists(customerId))
        {
            System.out.println("Customer not found.");
            return (false);
        }
        if (checkRenting && system.isCustomerRentingCar(customerId))
        {
            System.out.println("Customer already has a rented car.");
            return (false);
        }
        return (true);
    }
}
