package baytalhekma.utils;

import static baytalhekma.utils.ConsoleUtils.*;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.IntPredicate;

public class InputReader
{
    // Integer Input

    private static int readIntUntil(
            Scanner scanner,
            String prompt,
            IntPredicate condition,
            String errorMessage)
    {
        int num;

        while (true)
        {
            num = readInt(scanner, prompt);
            if (condition.test(num))
                return (num);
            printInvalidInput(errorMessage);
        }
    }

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
        return (readIntUntil(
                scanner,
                prompt,
                value -> value > 0,
                prompt + " must be positive."));
    }

    public static int readIntInRange(Scanner scanner, String prompt, int min, int max)
    {
        return (readIntUntil(
                scanner,
                prompt,
                value -> value >= min && value <= max,
                prompt + " must be between " + min + " and " + max + "."));
    }

    public static int readIntNonNegative(Scanner scanner, String prompt)
    {
        return (readIntUntil(
                scanner,
                prompt,
                value -> value >= 0,
                prompt + " cannot be negative."));
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

    // Enum Input

    public static <T extends Enum<T>> T readEnum(Scanner scanner, String prompt, T[] values)
    {
        int choice;

        println(prompt);
        for (int i = 0; i < values.length; i++)
        {
            println("  %2d.  %s".formatted(i + 1, values[i]));
        }
        choice = readIntInRange(scanner, "Choice", 1, values.length);
        return (values[choice - 1]);
    }
}
