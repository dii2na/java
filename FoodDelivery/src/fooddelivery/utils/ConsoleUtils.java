package fooddelivery.utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ConsoleUtils
{
    // Constants

    public static final String SEPARATOR =
        "----------------------------------------------------";

    public static final String DOUBLE_SEPARATOR =
        "====================================================";

    public static final DateTimeFormatter DATE_TIME =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final int LABEL_WIDTH = 25;
    private static final int MAX_COLUMN_WIDTH = 24;

    // Constructors

    private ConsoleUtils()
    {
    }

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

    public static void printInvalidInput(String message)
    {
        println("Invalid input. " + message);
    }

    // Section Headings

    public static String separator()
    {
        return ("  " + SEPARATOR + System.lineSeparator());
    }

    public static String sectionTitle(String title)
    {
        return ("%s%n  %s%n%s%n"
            .formatted(DOUBLE_SEPARATOR, title, DOUBLE_SEPARATOR));
    }

    // Formatting

    public static String fieldLine(String label, Object value)
    {
        return (("  %-" + LABEL_WIDTH + "s %s%n")
            .formatted(label + ":", value));
    }

    // Value Formatting

    public static String money(BigDecimal amount)
    {
        return (twoDecimals(amount));
    }

    public static String rating(double value)
    {
        return (twoDecimals(value));
    }

    private static String twoDecimals(Object value)
    {
        return ("%.2f".formatted(value));
    }

    public static String when(LocalDateTime time)
    {
        return (time.format(DATE_TIME));
    }

    public static String duration(Duration value)
    {
        long seconds;
        long minutes;

        seconds = value.getSeconds();
        if (seconds < 60)
            return (seconds + " sec");

        minutes = value.toMinutes();
        return ((minutes / 60) + "h " + (minutes % 60) + "m");
    }

    public static String duration(Optional<Duration> value)
    {
        return (value.isPresent()
            ? duration(value.get())
            : "-");
    }

    public static String lastOrderDate(Optional<LocalDate> value)
    {
        return (value.isPresent()
            ? value.get().toString()
            : "never");
    }

    public static String commaSeparated(Collection<?> values)
    {
        return (values.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(", ")));
    }

    // Tables

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
                widths[j] = Math.max(
                    widths[j], String.valueOf(rows[i][j]).length());
        }
        for (j = 0; j < columns; j++)
            widths[j] = Math.min(widths[j], MAX_COLUMN_WIDTH);
        return (renderTable(headers, rows, widths));
    }

    private static String renderTable(
        String[] headers,
        Object[][] rows,
        int[] widths)
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

    private static void appendRow(
        StringBuilder table,
        Object[] values,
        int[] widths)
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

    // Structured Output

    public static void printNumberedList(String... entries)
    {
        int index;

        for (index = 0; index < entries.length; index++)
            println("  %2d.  %s"
                .formatted(index + 1, entries[index]));
    }

    public static void printMenu(
        String title,
        int exitChoice,
        String exitLabel,
        String... options)
    {
        println();
        println(sectionTitle(title));
        printNumberedList(options);
        println("  %2d.  %s"
            .formatted(exitChoice, exitLabel));
        println();
    }

    public static void printBanner(Object... labelThenValue)
    {
        StringBuilder banner;
        int index;

        banner = new StringBuilder();
        banner.append(separator());
        for (index = 0; index + 1 < labelThenValue.length; index += 2)
            banner.append(fieldLine(
                String.valueOf(labelThenValue[index]),
                labelThenValue[index + 1]));
        banner.append(separator());
        print(banner);
    }

    public static void printTable(
        String title,
        String emptyMessage,
        String[] headers,
        Object[][] rows)
    {
        if (rows.length == 0)
        {
            println(emptyMessage);
            return;
        }
        println(sectionTitle(title));
        print(formatTable(headers, rows));
    }

    public static <T> Object[][] rows(
        Collection<T> items,
        Function<T, Object[]> cells)
    {
        return (items.stream()
            .map(cells)
            .toArray(Object[][]::new));
    }

    public static <T> Object[][] numberedRows(
        Collection<T> items,
        BiFunction<T, Integer, Object[]> cells)
    {
        AtomicInteger number;

        number = new AtomicInteger(1);

        return (rows(items, item -> cells.apply(
            item, number.getAndIncrement())));
    }

    public static void printStatusBanner(String title)
    {
        println(separator());
        println("  " + title);
        println(separator());
    }

    public static void printError(Exception e)
    {
        println("Error: " + (e.getMessage() == null
            ? e.toString()
            : e.getMessage()));
    }

    public static void printGoodbye()
    {
        println(sectionTitle("Goodbye"));
    }
}
