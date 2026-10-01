package fooddelivery.console.area;

import fooddelivery.console.ConsoleContext;
import fooddelivery.service.report.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import static fooddelivery.console.ConsoleFormat.*;
import static fooddelivery.utils.ConsoleUtils.*;
import static fooddelivery.utils.InputReader.*;

public class ReportPanel
{
    private final ConsoleContext context;

    public ReportPanel(ConsoleContext context)
    {
        this.context = context;
    }

    public void run(Scanner scanner)
    {
        String[] options;

        options = new String[]
        {
            "Total revenue for a date range",
            "Top five restaurants by revenue (month)",
            "Average order value per district",
            "Highly rated restaurants",
            "Order count by status",
            "Rider delivery statistics",
            "Most frequently ordered item",
            "Peak ordering hour",
            "Customers not ordering recently"
        };
        println();
        println(sectionTitle("REPORTS"));
        printNumberedList(options);
        println();

        switch (readIntInRange(
            scanner, "Report", 1, options.length))
        {
            case 1 -> reportTotalRevenue(scanner);
            case 2 -> reportTopRestaurants(scanner);
            case 3 -> reportDistrictAverage();
            case 4 -> reportHighlyRated();
            case 5 -> reportOrderCountByStatus();
            case 6 -> reportRiderStatistics();
            case 7 -> reportMostOrderedItem();
            case 8 -> reportPeakHour();
            case 9 -> reportInactiveCustomers();
            default -> throw new IllegalArgumentException(
                "Unsupported report");
        }
    }

    // Reports

    private void reportTotalRevenue(Scanner scanner)
    {
        LocalDate from;
        LocalDate to;

        while (true)
        {
            from = readDate(scanner, "From date (yyyy-MM-dd)");
            to = readDate(scanner, "To date (yyyy-MM-dd)");

            if (to.isBefore(from))
            {
                printInvalidInput(
                    "The end date cannot be before the start date");
                continue;
            }
            break;
        }
        println(sectionTitle("Total Revenue"));
        print(fieldLine(
            "Date Range", from + " to " + to));
        print(fieldLine(
            "Total Revenue",
            money(context.reports()
                .totalRevenue(from, to))));
    }

    private void reportTopRestaurants(Scanner scanner)
    {
        YearMonth month;
        List<RevenueReportService.RestaurantRevenue> rows;

        month = YearMonth.parse(
            readString(scanner, "Month (yyyy-MM)"));
        rows = context.reports()
            .topRestaurantsByRevenue(month);
        printTable(
            "Top Restaurants - " + month,
            "No delivered orders in " + month + ".",
            new String[]
            {
                "#", "Restaurant", "District",
                "Completed", "Revenue"
            },
            numberedRows(rows, this::topRestaurantRow));
    }

    private Object[] topRestaurantRow(
        RevenueReportService.RestaurantRevenue row,
        int number)
    {
        return (new Object[]
        {
            number,
            row.restaurant().getDisplayName(),
            row.restaurant().getDistrict(),
            row.completedOrders(),
            money(row.revenue())
        });
    }

    private void reportDistrictAverage()
    {
        List<OrderReportService.DistrictAverageOrderValue> rows;

        rows = context.reports()
            .averageOrderValueByDistrict();

        printTable(
            "Average Order Value",
            "No completed orders yet.",
            new String[]
            {
                "District", "Completed", "Average Order Value"
            },
            rows(rows, this::districtRow));
    }

    private Object[] districtRow(
        OrderReportService.DistrictAverageOrderValue row)
    {
        return (new Object[]
        {
            row.district(),
            row.completedOrders(),
            money(row.averageOrderValue())
        });
    }

    private void reportHighlyRated()
    {
        List<RestaurantReportService.QualifiedRestaurant> rows;

        rows = context.reports()
            .highlyRatedRestaurants();
        printTable(
            "Highly Rated Restaurants",
            "No restaurant qualifies "
                + "(rating above 4.5 with "
                + "at least 20 completed orders).",
            new String[]
            {
                "Restaurant", "District", "Rating", "Completed"
            },
            rows(rows, this::qualifiedRow));
    }

    private Object[] qualifiedRow(
        RestaurantReportService.QualifiedRestaurant row)
    {
        return (new Object[]
        {
            row.restaurant().getDisplayName(),
            row.restaurant().getDistrict(),
            rating(row.averageRating()),
            row.completedOrders()
        });
    }

    private void reportOrderCountByStatus()
    {
        List<OrderReportService.StatusCount> rows;

        rows = context.reports()
            .orderCountByStatus();
        printTable(
            "Orders by Status",
            "No orders exist yet.",
            new String[]
            {
                "Status", "Count"
            },
            rows(rows, this::statusRow));
    }

    private Object[] statusRow(OrderReportService.StatusCount row)
    {
        return (new Object[]
        {
            row.status(),
            row.orderCount()
        });
    }

    private void reportRiderStatistics()
    {
        List<RiderReportService.RiderDeliveryStats> rows;

        rows = context.reports()
            .riderDeliveryStatistics();
        printTable(
            "Rider Delivery Statistics",
            "No riders registered.",
            new String[]
            {
                "Rider", "Vehicle", "Completed", "Avg Duration"
            },
            rows(rows, this::riderStatsRow));
    }

    private Object[] riderStatsRow(
        RiderReportService.RiderDeliveryStats row)
    {
        return (new Object[]
        {
            row.rider().getName(),
            row.rider().getVehicleType(),
            row.completedDeliveries(),
            duration(row.averageDeliveryDuration())
        });
    }

    private void reportMostOrderedItem()
    {
        Optional<OrderReportService.OrderedMenuItem> result;

        result = context.reports()
            .mostFrequentlyOrderedMenuItem();
        if (result.isEmpty())
        {
            println("No orders exist yet, "
                + "so no item is the most frequently ordered.");
            return;
        }
        println(sectionTitle("Most Frequently Ordered Item"));
        print(fieldLine(
            "Restaurant",
            result.get().restaurant().getDisplayName()));
        print(fieldLine(
            "Menu Item",
            result.get().menuItem().getName()));
        print(fieldLine(
            "Item ID",
            result.get().menuItem().getId()));
        print(fieldLine(
            "Orders Containing It",
            result.get().orderCount()));
    }

    private void reportPeakHour()
    {
        Optional<OrderReportService.PeakOrderHour> result;

        result = context.reports()
            .peakOrderingHour();
        if (result.isEmpty())
        {
            println("No orders exist yet, "
                + "so there is no peak ordering hour.");
            return;
        }
        println(sectionTitle("Peak Ordering Hour"));
        print(fieldLine(
            "Hour", result.get().hour() + ":00"));
        print(fieldLine(
            "Orders Placed",
            result.get().orderCount()));
    }

    private void reportInactiveCustomers()
    {
        List<CustomerReportService.InactiveCustomer> rows;

        rows = context.reports()
            .customersNotOrderedRecently();
        printTable(
            "Customers Not Ordering Recently",
            "Every customer ordered in the last 30 days.",
            new String[]
            {
                "Customer", "Mobile", "Last Order"
            },
            rows(rows, this::inactiveRow));
    }

    private Object[] inactiveRow(
        CustomerReportService.InactiveCustomer row)
    {
        return (new Object[]
        {
            row.customer().getName(),
            row.customer().getMobile(),
            lastOrderDate(row.lastOrderDate())
        });
    }
}
