package baytalhekma.utils;

public final class ConsoleUtils
{
    // Constants

    public static final String SEPARATOR = "----------------------------------------------------";
    public static final String DOUBLE_SEPARATOR = "====================================================";

    private static final int LABEL_WIDTH = 25;
    private static final int[] COLUMN_WIDTHS = { 3, 8, 24, 9, 12, 7, 5, 27 };

    // Constructors

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
        for (Object item : items)
            println(item);
    }

    // Section Headings

    public static String separator()
    {
        return ("  " + SEPARATOR + newLine());
    }

    public static String sectionTitle(String title)
    {
        return ("%s%n  %s%n%s%n".formatted(DOUBLE_SEPARATOR, title, DOUBLE_SEPARATOR));
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

    public static String percentage(double value)
    {
        return ("%.2f%%".formatted(value));
    }

    private static String truncate(String text, int width)
    {
        if (width <= 1)
            return (text.substring(0, Math.min(text.length(), width)));
        return (text.substring(0, width - 1) + "…");
    }

    private static String pad(Object value, int width)
    {
        String text;

        text = String.valueOf(value);
        if (text.length() > width)
            return (truncate(text, width));
        return (text + " ".repeat(width - text.length()));
    }

    public static String formatRow(Object... values)
    {
        StringBuilder row;
        int i;

        row = new StringBuilder();
        for (i = 0; i < values.length; i++)
        {
            row.append("| ").append(pad(values[i], COLUMN_WIDTHS[i])).append(" ");
            if (i == values.length - 1)
                row.append("|");
        }
        return (row.toString());
    }
}
