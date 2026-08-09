package utils;

public final class ConsoleUtils
{
    public static final String SEPARATOR = "----------------------------------------------------";
    public static final String DOUBLE_SEPARATOR = "====================================================";

    private static final int LABEL_WIDTH = 22;

    private ConsoleUtils() {}

    // Output

    public static void print(Object text)
    {
        System.out.print(text);
    }

    public static void println()
    {
        System.out.println();
    }

    public static void println(Object text)
    {
        System.out.println(text);
    }

    public static String newLine()
    {
        return (System.lineSeparator());
    }

    public static void printInvalidInput(String message)
    {
        println("Invalid input. " + message);
    }

    public static void printList(Object[] items)
    {
        for (int i = 0; i < items.length; i++)
        {
            print(items[i]);
            if (i < items.length - 1)
                println();
        }
    }

    // Structural Output

    public static void printSeparator()
    {
        print(separator());
    }

    public static String separator()
    {
        return ("  " + SEPARATOR + newLine());
    }

    // Formatting

    public static String fieldLine(String label, Object value)
    {
        return (("  %-" + LABEL_WIDTH + "s %s%n").formatted(label + ":", value));
    }

    public static String money(double amount)
    {
        return ("%.2f".formatted(amount));
    }

    public static String percent(double fraction)
    {
        return ("%.2f%%".formatted(fraction * 100));
    }

    public static String yesNo(boolean value)
    {
        return (value ? "Yes" : "No");
    }

    public static String sectionTitle(String title)
    {
        return ("%s%n  %s%n%s%n".formatted(DOUBLE_SEPARATOR, title, DOUBLE_SEPARATOR));
    }

    public static String formatRow(Object... values)
    {
        StringBuilder row;

        row = new StringBuilder();
        for (Object value : values)
            row.append("%-15s".formatted(value));
        return (row.toString());
    }
}
