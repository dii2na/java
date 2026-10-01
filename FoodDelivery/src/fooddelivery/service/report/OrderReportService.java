package fooddelivery.service.report;

import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.restaurant.menu.MenuItem;
import fooddelivery.repository.OrderRepository;
import fooddelivery.service.order.OrderQueries;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderReportService
{
    private final OrderRepository orderRepository;

    public OrderReportService(OrderRepository orderRepository)
    {
        this.orderRepository = Validator.validateNotNull(
            orderRepository, "Order repository");
    }

    public record StatusCount(
        OrderStatus status,
        int orderCount)
    {
    }

    public record DistrictAverageOrderValue(
        String district,
        int completedOrders,
        BigDecimal averageOrderValue)
    {
    }

    public record OrderedMenuItem(
        Restaurant restaurant,
        MenuItem menuItem,
        int orderCount)
    {
    }

    public record PeakOrderHour(
        int hour,
        int orderCount)
    {
    }

    private record MenuItemKey(
        String restaurantId,
        String menuItemId)
    {
    }

    private record OrderedLine(
        Restaurant restaurant,
        MenuItem menuItem)
    {
    }

    // Order volume

    public List<Order> ordersFor(
        Restaurant restaurant,
        LocalDate fromDate,
        LocalDate toDate)
    {
        List<Order> orders;

        OrderQueries.validateRange(fromDate, toDate);
        orders = OrderQueries.ordersPlacedBetween(
            OrderQueries.ordersOfRestaurant(
                orderRepository.findAll(),
                Validator.validateNotNull(
                    restaurant, "Restaurant")),
            fromDate,
            toDate);

        return (Collections.unmodifiableList(orders));
    }

    public List<StatusCount> orderCountByStatus()
    {
        Map<OrderStatus, Long> counts;
        List<StatusCount> report;

        counts = orderRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Order::getStatus,
                Collectors.counting()));
        report = counts.entrySet().stream()
            .map(entry -> new StatusCount(
                entry.getKey(),
                entry.getValue().intValue()))
            .sorted(Comparator.comparing(StatusCount::status))
            .toList();

        return (report);
    }

    public Optional<PeakOrderHour> peakOrderingHour()
    {
        Map<Integer, Long> countsByHour;
        List<PeakOrderHour> hours;

        countsByHour = orderRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                order -> order.getPlacedAt().getHour(),
                Collectors.counting()));
        hours = countsByHour.entrySet().stream()
            .map(entry -> new PeakOrderHour(
                entry.getKey(),
                entry.getValue().intValue()))
            .sorted(byHourCountDescending())
            .toList();
        if (hours.isEmpty())
            return (Optional.empty());

        return (Optional.of(hours.get(0)));
    }

    private static Comparator<PeakOrderHour> byHourCountDescending()
    {
        return (Comparator
            .comparingInt(PeakOrderHour::orderCount)
            .reversed()
            .thenComparingInt(PeakOrderHour::hour));
    }

    // Order value

    public List<DistrictAverageOrderValue> averageOrderValueByDistrict()
    {
        Map<String, List<Order>> deliveredByDistrict;
        List<DistrictAverageOrderValue> report;

        deliveredByDistrict = OrderQueries
            .deliveredOrders(orderRepository.findAll()).stream()
            .collect(Collectors.groupingBy(
                order -> order.getDeliveryAddress().getDistrict()));
        report = deliveredByDistrict.entrySet().stream()
            .map(entry -> new DistrictAverageOrderValue(
                entry.getKey(),
                entry.getValue().size(),
                OrderQueries.averageTotal(entry.getValue())))
            .sorted(Comparator.comparing(
                DistrictAverageOrderValue::district))
            .toList();

        return (report);
    }

    // Menu items

    public Optional<OrderedMenuItem> mostFrequentlyOrderedMenuItem()
    {
        Map<MenuItemKey, List<OrderedLine>> linesByItem;
        List<OrderedMenuItem> candidates;

        linesByItem = orderRepository.findAll().stream()
            .filter(order ->
                order.getStatus() != OrderStatus.CANCELLED)
            .flatMap(order -> order.getItems().stream()
                .map(orderItem -> new OrderedLine(
                    order.getRestaurant(),
                    orderItem.getMenuItem())))
            .collect(Collectors.groupingBy(
                line -> new MenuItemKey(
                    line.restaurant().getId(),
                    line.menuItem().getId())));
        candidates = linesByItem.entrySet().stream()
            .map(OrderReportService::toOrderedMenuItem)
            .sorted(byOrderCountDescending())
            .toList();
        if (candidates.isEmpty())
            return (Optional.empty());

        return (Optional.of(candidates.get(0)));
    }

    private static OrderedMenuItem toOrderedMenuItem(
        Map.Entry<MenuItemKey, List<OrderedLine>> entry)
    {
        OrderedLine line;

        line = entry.getValue().get(0);

        return (new OrderedMenuItem(
            line.restaurant(),
            line.menuItem(),
            entry.getValue().size()));
    }

    private static Comparator<OrderedMenuItem> byOrderCountDescending()
    {
        return (Comparator
            .comparingInt(OrderedMenuItem::orderCount)
            .reversed()
            .thenComparing(candidate ->
                candidate.restaurant().getDisplayName())
            .thenComparing(candidate ->
                candidate.menuItem().getName())
            .thenComparing(candidate ->
                candidate.restaurant().getId())
            .thenComparing(candidate ->
                candidate.menuItem().getId()));
    }
}
