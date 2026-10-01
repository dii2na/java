package fooddelivery.utils;

import fooddelivery.exception.ConsoleInputClosedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;
import static fooddelivery.utils.ConsoleUtils.*;

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
            catch (NoSuchElementException e)
            {
                throw (new ConsoleInputClosedException());
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

    public static int readIntInRange(
        Scanner scanner,
        String prompt,
        int min,
        int max)
    {
        int num;

        while (true)
        {
            num = readInt(scanner, prompt);
            if (num >= min && num <= max)
                return (num);
            printInvalidInput(
                prompt + " must be between " + min + " and " + max + ".");
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
            catch (NoSuchElementException e)
            {
                throw (new ConsoleInputClosedException());
            }
        }
    }

    // Decimal Input

    public static BigDecimal readDecimal(
        Scanner scanner,
        String prompt)
    {
        BigDecimal num;

        while (true)
        {
            try
            {
                print("Enter " + prompt + ": ");
                num = new BigDecimal(
                    scanner.nextLine().trim());
                return (num);
            }
            catch (NoSuchElementException e)
            {
                throw (new ConsoleInputClosedException());
            }
            catch (NumberFormatException e)
            {
                printInvalidInput("Please enter a valid number.");
            }
        }
    }

    public static BigDecimal readDecimalPositive(
        Scanner scanner,
        String prompt)
    {
        BigDecimal num;

        while (true)
        {
            num = readDecimal(scanner, prompt);
            if (num.compareTo(BigDecimal.ZERO) > 0)
                return (num);
            printInvalidInput(prompt + " must be greater than zero.");
        }
    }

    public static BigDecimal readDecimalNonNegative(
        Scanner scanner,
        String prompt)
    {
        BigDecimal num;

        while (true)
        {
            num = readDecimal(scanner, prompt);
            if (num.compareTo(BigDecimal.ZERO) >= 0)
                return (num);
            printInvalidInput(prompt + " must not be negative.");
        }
    }

    public static BigDecimal readRate(
        Scanner scanner,
        String prompt)
    {
        BigDecimal value;

        while (true)
        {
            value = readDecimalPositive(scanner, prompt);
            if (value.compareTo(BigDecimal.ONE) <= 0)
                return (value);
            printInvalidInput(prompt + " must not exceed 1.");
        }
    }

    public static double readDoubleInRange(
        Scanner scanner,
        String prompt,
        double min,
        double max)
    {
        double value;

        while (true)
        {
            value = readDouble(scanner, prompt);
            if (value >= min && value <= max)
                return (value);
            printInvalidInput(
                prompt + " must be between " + min + " and " + max + ".");
        }
    }

    // Date Input

    public static LocalDate readDate(Scanner scanner, String prompt)
    {
        while (true)
        {
            try
            {
                return (LocalDate.parse(
                    readString(scanner, prompt)));
            }
            catch (DateTimeParseException e)
            {
                printInvalidInput(
                    "Please use the yyyy-MM-dd format.");
            }
        }
    }

    // Text Input

    public static String readString(Scanner scanner, String prompt)
    {
        String input;

        while (true)
        {
            print("Enter " + prompt + ": ");
            input = readLine(scanner).trim();
            if (!input.isEmpty())
                return (input);
            printInvalidInput(prompt + " cannot be empty.");
        }
    }

    private static String readLine(Scanner scanner)
    {
        if (!scanner.hasNextLine())
            throw (new ConsoleInputClosedException());

        return (scanner.nextLine());
    }

    public static String readValidatedString(
        Scanner scanner,
        String fieldName,
        Function<String, String> validator)
    {
        String value;

        while (true)
        {
            value = readString(scanner, fieldName);
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

    public static String readOptionalString(
        Scanner scanner,
        String prompt)
    {
        String input;

        print("Enter " + prompt + " (press Enter to skip): ");
        input = readLine(scanner).trim();

        return (input.isEmpty() ? null : input);
    }

    // Choice Input

    public static boolean readConfirmation(
        Scanner scanner,
        String question)
    {
        String input;

        while (true)
        {
            println(question + " (y/n)");
            input = readLine(scanner).trim().toLowerCase();
            if (input.isEmpty())
            {
                printInvalidInput("Please enter 'y' or 'n'.");
                continue;
            }
            switch (input.charAt(0))
            {
                case 'y' ->
                {
                    return (true);
                }
                case 'n' ->
                {
                    return (false);
                }
                default -> printInvalidInput(
                    "Please enter 'y' or 'n'.");
            }
        }
    }
}
