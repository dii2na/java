import java.util.InputMismatchException;
import java.util.Scanner;

public class InputReader
{
    // Private Helpers

    private static void showInvalid(String message)
    {
        System.out.println("Invalid input. " + message);
    }

    // Integer Input

    public static int readInt(Scanner scanner, String prompt)
    {
        int num;

        while (true)
        {
            try
            {
                System.out.print("Enter " + prompt + ": ");
                num = scanner.nextInt();
                scanner.nextLine();
                return (num);
            }
            catch (InputMismatchException e)
            {
                showInvalid("Please enter an integer.");
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
            showInvalid(prompt + " must be positive.");
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
            showInvalid(prompt + " must be between " + min + " and " + max + ".");
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
                System.out.print("Enter " + prompt + ": ");
                num = scanner.nextDouble();
                scanner.nextLine();
                return (num);
            }
            catch (InputMismatchException e)
            {
                showInvalid("Please enter a number.");
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
            showInvalid(prompt + " must be positive.");
        }
    }

    public static double readDoubleNonNegative(Scanner scanner, String prompt)
    {
        double num;

        while (true)
        {
            num = readDouble(scanner, prompt);
            if (num >= 0)
                return (num);
            showInvalid(prompt + " cannot be negative.");
        }
    }

    // String Input

    public static String readString(Scanner scanner, String prompt)
    {
        String input;

        while (true)
        {
            System.out.print("Enter " + prompt + ": ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty())
                return (input);
            showInvalid(prompt + " cannot be empty.");
        }
    }

    // Phone Input

    public static String readPhone(Scanner scanner)
    {
        String phone;

        while (true)
        {
            phone = readString(scanner, "phone");
            try
            {
                phone = Validator.validatePhone(phone);
                return (phone);
            }
            catch (IllegalArgumentException e)
            {
                showInvalid(e.getMessage());
            }
        }
    }
}
