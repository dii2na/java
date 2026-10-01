package fooddelivery.service.report;

import fooddelivery.model.order.Order;
import fooddelivery.model.rider.Rider;
import fooddelivery.repository.OrderRepository;
import fooddelivery.repository.RiderRepository;
import fooddelivery.service.order.OrderQueries;
import fooddelivery.utils.Validator;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RiderReportService
{
    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;

    public RiderReportService(
        OrderRepository orderRepository,
        RiderRepository riderRepository)
    {
        this.orderRepository = Validator.validateNotNull(
            orderRepository, "Order repository");
        this.riderRepository = Validator.validateNotNull(
            riderRepository, "Rider repository");
    }

    public record RiderDeliveryStats(
        Rider rider,
        int completedDeliveries,
        Optional<Duration> averageDeliveryDuration)
    {
    }

    // Delivery statistics

    public List<RiderDeliveryStats> riderDeliveryStatistics()
    {
        List<Order> delivered;
        List<RiderDeliveryStats> statistics;

        delivered = OrderQueries.deliveredOrders(
            orderRepository.findAll());
        statistics = riderRepository.findAll().stream()
            .map(rider -> new RiderDeliveryStats(
                rider,
                rider.getCompletedDeliveriesCount(),
                averageDeliveryDuration(rider, delivered)))
            .sorted(byDeliveriesDescending())
            .toList();

        return (statistics);
    }

    private Optional<Duration> averageDeliveryDuration(
        Rider rider,
        Collection<Order> delivered)
    {
        List<Duration> durations;
        long totalNanos;

        durations = delivered.stream()
            .filter(order -> order.getRider()
                .map(assigned -> assigned.equals(rider))
                .orElse(false))
            .map(RiderReportService::deliveryDuration)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
        if (durations.isEmpty())
            return (Optional.empty());
        totalNanos = durations.stream()
            .mapToLong(Duration::toNanos)
            .sum();

        return (Optional.of(Duration.ofNanos(
            totalNanos / durations.size())));
    }

    private static Optional<Duration> deliveryDuration(Order order)
    {
        Optional<LocalDateTime> outForDeliveryAt;
        Optional<LocalDateTime> deliveredAt;

        outForDeliveryAt = order.getOutForDeliveryAt();
        deliveredAt = order.getDeliveredAt();
        if (outForDeliveryAt.isEmpty()
            || deliveredAt.isEmpty())
        {
            return (Optional.empty());
        }

        return (Optional.of(Duration.between(
            outForDeliveryAt.get(),
            deliveredAt.get())));
    }

    private static Comparator<RiderDeliveryStats> byDeliveriesDescending()
    {
        return (Comparator
            .comparingInt(RiderDeliveryStats::completedDeliveries)
            .reversed()
            .thenComparing(stats -> stats.rider().getName()));
    }
}
