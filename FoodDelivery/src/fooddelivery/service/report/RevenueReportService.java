package fooddelivery.service.report;

import fooddelivery.model.order.Order;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.repository.OrderRepository;
import fooddelivery.service.order.OrderQueries;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RevenueReportService
{
    private static final int TOP_RESTAURANTS = 5;

    private final OrderRepository orderRepository;

    public RevenueReportService(OrderRepository orderRepository)
    {
        this.orderRepository = Validator.validateNotNull(
            orderRepository, "Order repository");
    }

    public record RestaurantRevenue(
        Restaurant restaurant,
        int completedOrders,
        BigDecimal revenue)
    {
    }

    public record RestaurantRevenueBreakdown(
        int completedOrders,
        BigDecimal itemsSubtotal,
        BigDecimal revenue,
        BigDecimal feesAndAdjustments)
    {
    }

    // Total revenue

    public BigDecimal totalRevenue(
        LocalDate fromDate,
        LocalDate toDate)
    {
        OrderQueries.validateRange(fromDate, toDate);

        return (OrderQueries.sumTotals(
            OrderQueries.deliveredOrdersPlacedBetween(
                orderRepository.findAll(),
                fromDate,
                toDate)));
    }

    // Revenue by restaurant

    public RestaurantRevenueBreakdown revenueFor(
        Restaurant restaurant,
        LocalDate fromDate,
        LocalDate toDate)
    {
        List<Order> delivered;
        BigDecimal itemsSubtotal;
        BigDecimal revenue;

        OrderQueries.validateRange(fromDate, toDate);
        delivered = OrderQueries.deliveredOrdersPlacedBetween(
            OrderQueries.ordersOfRestaurant(
                orderRepository.findAll(),
                Validator.validateNotNull(
                    restaurant, "Restaurant")),
            fromDate,
            toDate);
        itemsSubtotal = OrderQueries.sumSubtotals(delivered);
        revenue = OrderQueries.sumTotals(delivered);

        return (new RestaurantRevenueBreakdown(
            delivered.size(),
            itemsSubtotal,
            revenue,
            revenue.subtract(itemsSubtotal)));
    }

    public List<RestaurantRevenue> topRestaurantsByRevenue(
        YearMonth month)
    {
        Map<Restaurant, List<Order>> deliveredByRestaurant;
        List<RestaurantRevenue> report;

        month = Validator.validateNotNull(month, "Month");
        deliveredByRestaurant = OrderQueries
            .deliveredOrdersPlacedBetween(
                orderRepository.findAll(),
                month.atDay(1),
                month.atEndOfMonth()).stream()
            .collect(Collectors.groupingBy(Order::getRestaurant));
        report = deliveredByRestaurant.entrySet().stream()
            .map(RevenueReportService::toRestaurantRevenue)
            .sorted(byRevenueDescending())
            .limit(TOP_RESTAURANTS)
            .collect(Collectors.toList());

        return (Collections.unmodifiableList(report));
    }

    private static RestaurantRevenue toRestaurantRevenue(
        Map.Entry<Restaurant, List<Order>> entry)
    {
        return (new RestaurantRevenue(
            entry.getKey(),
            entry.getValue().size(),
            OrderQueries.sumTotals(entry.getValue())));
    }

    private static Comparator<RestaurantRevenue> byRevenueDescending()
    {
        return (Comparator
            .comparing(RestaurantRevenue::revenue)
            .reversed()
            .thenComparing(row ->
                row.restaurant().getDisplayName()));
    }
}
