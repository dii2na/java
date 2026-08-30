package ROM.utils;

import static ROM.utils.ConsoleUtils.*;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Function;

public class InputReader
{
    // Integer Input

    public static int readInt(Scanner scanner, String prompt)
    {
        int num;

        while (true)
        {
            try
            {
                print("Enter " + prompt + ": ");
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

    public static int readIntPositive(Scanner scanner, String prompt)
    {
        int num;

        while (true)
        {
            num = readInt(scanner, prompt);
            if (num > 0)
                return (num);
            printInvalidInput(prompt + " must be positive.");
        }
    }

    public static int readIntInRange(Scanner scanner, String prompt, int min, int max)
    {
        int num;

        while (true)
        {
            num = readInt(scanner, prompt);
            if (num >= min && num <= max)
                return (num);
            printInvalidInput(prompt + " must be between " + min + " and " + max + ".");
        }
    }

    // Double Input

    public static double readDouble(Scanner scanner, String prompt)
    {
        double num;

        while (true)
        {
            try
            {
                print("Enter " + prompt + ": ");
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

    public static double readDoublePositive(Scanner scanner, String prompt)
    {
        double num;

        while (true)
        {
            num = readDouble(scanner, prompt);
            if (num > 0)
                return (num);
            printInvalidInput(prompt + " must be positive.");
        }
    }

    // Text Input

    public static String readString(Scanner scanner, String prompt)
    {
        String input;

        while (true)
        {
            print("Enter " + prompt + ": ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty())
                return (input);
            printInvalidInput(prompt + " cannot be empty.");
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
