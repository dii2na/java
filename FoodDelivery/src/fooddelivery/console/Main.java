package fooddelivery.console;

import fooddelivery.console.area.AdminArea;
import fooddelivery.console.area.CustomerArea;
import fooddelivery.console.area.RestaurantArea;
import fooddelivery.console.area.RiderArea;
import fooddelivery.exception.ConsoleInputClosedException;
import fooddelivery.exception.FoodDeliveryException;
import java.util.Scanner;
import static fooddelivery.console.ConsoleFormat.*;
import static fooddelivery.utils.ConsoleUtils.*;
import static fooddelivery.utils.InputReader.*;

public class Main
{
    private static final String TITLE = "MASR DELIVERY";
    private static final int EXIT_CHOICE = 0;

    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            ConsoleContext context = new ConsoleContext();
            startSystem(scanner, context);
        }
    }

    private static void startSystem(
        Scanner scanner,
        ConsoleContext context)
    {
        boolean running;

        running = true;
        printWelcome();
        while (running)
        {
            printMenuOptions();
            printSystemStatus(context);
            try
            {
                switch (readChoice(scanner))
                {
                    case 1 ->
                        new CustomerArea(context).start(scanner);
                    case 2 ->
                        new RestaurantArea(context).start(scanner);
                    case 3 ->
                        new RiderArea(context).start(scanner);
                    case 4 ->
                        new AdminArea(context).start(scanner);
                    case EXIT_CHOICE ->
                        running = !shouldExit(scanner);
                }
            }
            catch (ConsoleInputClosedException e)
            {
                println();
                printGoodbye();
                return;
            }
            catch (FoodDeliveryException |
                   IllegalArgumentException |
                   IllegalStateException e)
            {
                printError(e);
            }
        }
    }

    private static int readChoice(Scanner scanner)
    {
        return (readIntInRange(
            scanner,
            "Choose an option",
            0,
            4));
    }

    private static void printWelcome()
    {
        println(sectionTitle(TITLE + " - Main Menu"));
        println("  A food delivery platform console.");
        println("  Pick an area to work in.");
    }

    private static void printMenuOptions()
    {
        printMenu(
            TITLE + " - Main Menu",
            EXIT_CHOICE,
            "Exit",
            "Customer",
            "Restaurant",
            "Rider",
            "Admin & Reports");
    }

    private static void printSystemStatus(ConsoleContext context)
    {
        printBanner(
            "Restaurants", context.allRestaurants().size(),
            "Customers", context.allCustomers().size(),
            "Riders", context.allRiders().size(),
            "Orders", context.allOrders().size());
    }

    private static boolean shouldExit(Scanner scanner)
    {
        if (!readConfirmation(
            scanner,
            "Are you sure you want to exit?"))
        {
            return (false);
        }

        printGoodbye();
        return (true);
    }
}
