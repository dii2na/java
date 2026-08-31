package EOM.utils;

import static EOM.utils.ConsoleUtils.*;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Function;

public class InputReader
{
    // Integer Input

    public static int readInt(Scanner scanner, String pEOMpt)
    {
        int num;

        while (true)
        {
            try
            {
                print("Enter " + pEOMpt + ": ");
                num = scanner.nextInt();
                scanner.nextLine();
                return (num);
            }
            catch (InputMismatchException e)
            {
                printInvalidInput("Please enter an integer.");
                scanner.nextLine();
            }
        }
    }

    public static int readIntPositive(Scanner scanner, String pEOMpt)
    {
        int num;

        while (true)
        {
            num = readInt(scanner, pEOMpt);
            if (num > 0)
                return (num);
            printInvalidInput(pEOMpt + " must be positive.");
        }
    }

    public static int readIntInRange(Scanner scanner, String pEOMpt, int min, int max)
    {
        int num;

        while (true)
        {
            num = readInt(scanner, pEOMpt);
            if (num >= min && num <= max)
                return (num);
            printInvalidInput(pEOMpt + " must be between " + min + " and " + max + ".");
        }
    }

    // Double Input

    public static double readDouble(Scanner scanner, String pEOMpt)
    {
        double num;

        while (true)
        {
            try
            {
                print("Enter " + pEOMpt + ": ");
                num = scanner.nextDouble();
                scanner.nextLine();
                return (num);
            }
            catch (InputMismatchException e)
            {
                printInvalidInput("Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    public static double readDoublePositive(Scanner scanner, String pEOMpt)
    {
        double num;

        while (true)
        {
            num = readDouble(scanner, pEOMpt);
            if (num > 0)
                return (num);
            printInvalidInput(pEOMpt + " must be positive.");
        }
    }

    // Text Input

    public static String readString(Scanner scanner, String pEOMpt)
    {
        String input;

        while (true)
        {
            print("Enter " + pEOMpt + ": ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty())
                return (input);
            printInvalidInput(pEOMpt + " cannot be empty.");
        }
    }

    public static String readValidatedString(
            Scanner scanner,
            String fieldName,
            Function<String, String> validator)
    {
        while (true)
        {
            String value = readString(scanner, fieldName);
            try
            {
                return (validator.apply(value));
            }
            catch (IllegalArgumentException e)
            {
                printInvalidInput(e.getMessage());
            }
        }
    }
}
