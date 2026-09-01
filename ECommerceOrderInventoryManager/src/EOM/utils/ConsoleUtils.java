package EOM.utils;

public final class ConsoleUtils
{
    // Constants

    public static final String SEPARATOR = "----------------------------------------------------";
    public static final String DOUBLE_SEPARATOR = "====================================================";

    private static final int LABEL_WIDTH = 25;
    private static final int MAX_COLUMN_WIDTH = 24;

    // Constructors

    private ConsoleUtils() {}

    // Output

    public static void print(Object text)
    {
        System.out.print(text);
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

    public static String formatTable(String[] headers, Object[][] rows)
    {
        int columns;
        int[] widths;
        int i;
        int j;

        columns = (headers == null ? 0 : headers.length);
        for (i = 0; i < rows.length; i++)
            columns = Math.max(columns, rows[i].length);
        if (columns == 0)
            return ("");
        widths = new int[columns];
        if (headers != null)
        {
            for (j = 0; j < headers.length; j++)
                widths[j] = String.valueOf(headers[j]).length();
        }
        for (i = 0; i < rows.length; i++)
        {
            for (j = 0; j < rows[i].length; j++)
                widths[j] = Math.max(widths[j], String.valueOf(rows[i][j]).length());
        }
        for (j = 0; j < columns; j++)
            widths[j] = Math.min(widths[j], MAX_COLUMN_WIDTH);
        return (renderTable(headers, rows, widths));
    }

    private static String renderTable(String[] headers, Object[][] rows, int[] widths)
    {
        StringBuilder table;
        int i;

        table = new StringBuilder();
        if (headers != null)
            appendRow(table, headers, widths);
        for (i = 0; i < rows.length; i++)
            appendRow(table, rows[i], widths);
        return (table.toString());
    }

    private static void appendRow(StringBuilder table, Object[] values, int[] widths)
    {
        int j;

        for (j = 0; j < widths.length; j++)
        {
            table.append("| ");
            table.append(pad(j < values.length ? values[j] : "", widths[j]));
            table.append(" ");
        }
        table.append("|").append(System.lineSeparator());
    }

    public static String formatMenu(String title, String[] options)
    {
        StringBuilder menu;

        menu = new StringBuilder();
        menu.append("╔══════════════════════════════════════════╗")
            .append(newLine());
        menu.append("║       ")
            .append(title)
            .append("           ║")
            .append(newLine());
        menu.append("╠══════════════════════════════════════════╣")
            .append(newLine());
        for (int index = 0; index < options.length; index++)
        {
            menu.append("║  %2d.  %-34s ║"
                .formatted(index + 1, options[index]))
                .append(newLine());
        }
        menu.append("╚══════════════════════════════════════════╝")
            .append(newLine());

        return (menu.toString());
    }

}
