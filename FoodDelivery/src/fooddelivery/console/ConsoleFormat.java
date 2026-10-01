package fooddelivery.console;

import fooddelivery.console.ConsoleContext.RiderDeliverySummary;
import fooddelivery.model.customer.Customer;
import fooddelivery.model.order.Order;
import fooddelivery.model.promotion.Promotion;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.restaurant.menu.MenuItem;
import fooddelivery.model.rider.Rider;
import fooddelivery.service.pricing.PriceBreakdown;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import static fooddelivery.utils.ConsoleUtils.*;

public final class ConsoleFormat
{
    // Table Headers

    public static final String[] RESTAURANT_HEADER =
    {
        "#", "ID", "Name", "District", "Cuisines", "Rating", "Status"
    };

    public static final String[] MENU_HEADER =
    {
        "ID", "Name", "Category", "Price", "Prep", "Stock", "Status"
    };

    public static final String[] CUSTOMER_HEADER =
    {
        "ID", "Name", "Mobile", "Tier", "Completed", "Wallet"
    };

    public static final String[] RIDER_HEADER =
    {
        "ID", "Name", "Vehicle", "District", "Deliveries", "Status"
    };

    public static final String[] ORDER_HEADER =
    {
        "ID", "Restaurant", "Customer", "Status",
        "Placed", "Total"
    };

    public static final String[] PROMOTION_HEADER =
    {
        "Code", "Type", "Min Subtotal", "Expires",
        "District", "First Time"
    };

    // Constructors

    private ConsoleFormat()
    {
    }

    // Domain Value Formatting

    public static String cuisines(Restaurant restaurant)
    {
        return (commaSeparated(restaurant.getCuisines()));
    }

    public static String availability(MenuItem item)
    {
        if (!item.isAvailable())
            return ("UNAVAILABLE");
        if (item.getStockQuantity()
            .compareTo(BigDecimal.ZERO) <= 0)
        {
            return ("OUT OF STOCK");
        }
        return ("AVAILABLE");
    }

    // Domain Tables

    public static void printRestaurants(Collection<Restaurant> restaurants)
    {
        printTable(
            "Restaurants",
            "No restaurants found.",
            RESTAURANT_HEADER,
            restaurantsRows(restaurants));
    }

    private static Object[][] restaurantsRows(
        Collection<Restaurant> restaurants)
    {
        return (numberedRows(restaurants, ConsoleFormat::restaurantRow));
    }

    private static Object[] restaurantRow(
        Restaurant restaurant,
        int number)
    {
        return (new Object[]
        {
            number,
            restaurant.getId(),
            restaurant.getDisplayName(),
            restaurant.getDistrict(),
            cuisines(restaurant),
            rating(restaurant.getAverageRating()),
            restaurant.isOpen() ? "OPEN" : "CLOSED"
        });
    }

    public static void printMenu(Restaurant restaurant)
    {
        printTable(
            "Menu - " + restaurant.getDisplayName(),
            "This restaurant has no menu items.",
            MENU_HEADER,
            menuRows(restaurant.getMenu().values()));
    }

    public static void printMenu(
        Restaurant restaurant,
        Collection<MenuItem> items)
    {
        printTable(
            "Menu - " + restaurant.getDisplayName(),
            "No menu items found.",
            MENU_HEADER,
            menuRows(items));
    }

    private static Object[][] menuRows(Collection<MenuItem> items)
    {
        return (rows(items, item -> new Object[]
        {
            item.getId(),
            item.getName(),
            item.getCategory(),
            money(item.getDisplayPrice()),
            item.getPreparationTime() + " min",
            money(item.getStockQuantity()),
            availability(item)
        }));
    }

    public static void printCustomers(Collection<Customer> customers)
    {
        printTable(
            "Customers",
            "No customers found.",
            CUSTOMER_HEADER,
            customersRows(customers));
    }

    private static Object[][] customersRows(
        Collection<Customer> customers)
    {
        return (rows(customers, customer -> new Object[]
        {
            customer.getId(),
            customer.getName(),
            customer.getMobile(),
            customer.getLoyaltyTier(),
            customer.getCompletedOrderCount(),
            money(customer.getWallet())
        }));
    }

    public static void printRiders(Collection<Rider> riders)
    {
        printTable(
            "Riders",
            "No riders found.",
            RIDER_HEADER,
            ridersRows(riders));
    }

    private static Object[][] ridersRows(Collection<Rider> riders)
    {
        return (rows(riders, rider -> new Object[]
        {
            rider.getId(),
            rider.getName(),
            rider.getVehicleType(),
            rider.getCurrentDistrict(),
            rider.getCompletedDeliveriesCount(),
            dutyStatus(rider)
        }));
    }

    private static String dutyStatus(Rider rider)
    {
        if (rider.getActiveOrder().isPresent())
            return ("ON ORDER");
        if (rider.isAvailable())
            return ("ON DUTY");
        return ("OFF DUTY");
    }

    public static void printPromotions(Collection<Promotion> promotions)
    {
        printTable(
            "Promotions",
            "No promotions found.",
            PROMOTION_HEADER,
            promotionsRows(promotions));
    }

    private static Object[][] promotionsRows(
        Collection<Promotion> promotions)
    {
        return (rows(promotions, ConsoleFormat::promotionRow));
    }

    private static Object[] promotionRow(Promotion promotion)
    {
        return (new Object[]
        {
            promotion.getCode(),
            promotion.getType(),
            money(promotion.getMinimumSubtotal()),
            when(promotion.getExpiry()),
            promotion.getDistrict()
                .orElse("any"),
            promotion.isFirstTimeOnly() ? "yes" : "no"
        });
    }

    public static void printOrders(Collection<Order> orders)
    {
        printTable(
            "Orders",
            "No orders found.",
            ORDER_HEADER,
            ordersRows(orders));
    }

    private static Object[][] ordersRows(Collection<Order> orders)
    {
        return (rows(orders, order -> new Object[]
        {
            order.getId(),
            order.getRestaurant().getDisplayName(),
            order.getCustomer().getName(),
            order.getStatus(),
            when(order.getPlacedAt()),
            money(order.getTotal())
        }));
    }

    public static void printOrderDetails(Order order)
    {
        StringBuilder details;

        println();
        details = new StringBuilder();
        details.append(fieldLine("Order ID", order.getId()));
        details.append(fieldLine(
            "Status", order.getStatus()));
        details.append(fieldLine(
            "Customer", order.getCustomer().getName()
                + " (" + order.getCustomer().getLoyaltyTier() + ")"));
        details.append(fieldLine(
            "Restaurant",
            order.getRestaurant().getDisplayName()));
        details.append(fieldLine(
            "Delivery Address",
            order.getDeliveryAddress().getDistrict()
                + " - " + order.getDeliveryAddress().getDetail()));
        details.append(fieldLine(
            "Distance", money(order.getDeliveryDistance()) + " km"));
        details.append(fieldLine(
            "Placed At", when(order.getPlacedAt())));
        order.getRider().ifPresentOrElse(
            rider -> details.append(fieldLine(
                "Rider",
                rider.getName() + " (" + rider.getId() + ")")),
            () -> details.append(fieldLine("Rider", "not assigned")));
        details.append(fieldLine(
            "Promotion",
            order.getPromotion() == null
                ? "none"
                : order.getPromotion().getCode()));
        print(details);
    }

    public static void printOrderItems(Order order)
    {
        print(formatTable(
            new String[]
            {
                "Menu Item ID", "Name", "Quantity", "Amount"
            },
            orderItemsRows(order)));
    }

    private static Object[][] orderItemsRows(Order order)
    {
        return (rows(order.getItems(), orderItem -> new Object[]
        {
            orderItem.getMenuItem().getId(),
            orderItem.getMenuItem().getName(),
            money(orderItem.getQuantity()),
            money(orderItem.calculateTotal())
        }));
    }

    // Domain Sections

    public static void printPriceBreakdown(
        Order order,
        PriceBreakdown quote)
    {
        StringBuilder breakdown;

        breakdown = new StringBuilder();
        breakdown.append(fieldLine(
            "Items Subtotal",
            money(quote.getItemsSubtotal())));
        breakdown.append(fieldLine(
            "Base Delivery Fee",
            money(quote.getBaseDeliveryFee())));
        breakdown.append(fieldLine(
            "Extra Distance Fee",
            money(quote.getExtraDistanceFee())));
        appendOptionalDiscount(
            breakdown,
            "Loyalty Delivery Discount",
            quote.getLoyaltyDeliveryDiscount());
        appendOptionalDiscount(
            breakdown,
            "Promotion Delivery Discount",
            quote.getPromotionDeliveryDiscount());
        breakdown.append(fieldLine(
            "Service Fee", money(quote.getServiceFee())));
        appendOptionalDiscount(
            breakdown,
            "Promotion Discount",
            quote.getPromotionOrderDiscount());
        breakdown.append(fieldLine(
            "Total", money(quote.getTotal())));
        print(breakdown);
    }

    private static void appendOptionalDiscount(
        StringBuilder breakdown,
        String label,
        BigDecimal amount)
    {
        if (amount.compareTo(BigDecimal.ZERO) > 0)
            breakdown.append(fieldLine(
                label,
                "-" + money(amount)));
    }

    public static void printRiderSummary(
        Rider rider,
        RiderDeliverySummary summary)
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(fieldLine("Rider", rider.getName()));
        info.append(fieldLine("ID", rider.getId()));
        info.append(fieldLine("Vehicle", rider.getVehicleType()));
        info.append(fieldLine(
            "District", rider.getCurrentDistrict()));
        info.append(fieldLine("Duty Status", dutyStatus(rider)));
        info.append(fieldLine(
            "Completed Deliveries",
            summary.completedDeliveries()));
        info.append(fieldLine(
            "Average Duration",
            duration(summary.averageDuration())));
        print(info);
    }
}
