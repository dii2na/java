package fooddelivery.service.report;

import fooddelivery.model.order.Order;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.repository.OrderRepository;
import fooddelivery.service.order.OrderQueries;
import fooddelivery.utils.Validator;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantReportService
{
    private static final double HIGH_RATING = 4.5;
    private static final int MIN_COMPLETED_ORDERS = 20;

    private final OrderRepository orderRepository;

    public RestaurantReportService(OrderRepository orderRepository)
    {
        this.orderRepository = Validator.validateNotNull(
            orderRepository, "Order repository");
    }

    public record QualifiedRestaurant(
        Restaurant restaurant,
        int completedOrders,
        double averageRating)
    {
    }

    // Highly rated restaurants

    public List<QualifiedRestaurant> highlyRatedRestaurants()
    {
        Map<Restaurant, List<Order>> deliveredByRestaurant;
        List<QualifiedRestaurant> report;

        deliveredByRestaurant = OrderQueries
            .deliveredOrders(orderRepository.findAll()).stream()
            .collect(Collectors.groupingBy(Order::getRestaurant));
        report = deliveredByRestaurant.entrySet().stream()
            .filter(RestaurantReportService::qualifies)
            .map(entry -> new QualifiedRestaurant(
                entry.getKey(),
                entry.getValue().size(),
                entry.getKey().getAverageRating()))
            .sorted(Comparator.comparing(row ->
                row.restaurant().getDisplayName()))
            .toList();

        return (report);
    }

    private static boolean qualifies(
        Map.Entry<Restaurant, List<Order>> entry)
    {
        return (entry.getValue().size() >= MIN_COMPLETED_ORDERS
            && entry.getKey().getAverageRating() > HIGH_RATING);
    }
}
