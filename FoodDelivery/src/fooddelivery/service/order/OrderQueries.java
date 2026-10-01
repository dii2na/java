package fooddelivery.service.order;

import fooddelivery.model.customer.Customer;
import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.rider.Rider;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class OrderQueries
{
    private OrderQueries()
    {
    }

    // Ranges

    public static void validateRange(
        LocalDate from,
        LocalDate to)
    {
        from = Validator.validateNotNull(from, "From date");
        to = Validator.validateNotNull(to, "To date");
        if (from.isAfter(to))
            throw new IllegalArgumentException(
                "From date must not be after the to date");
    }

    // Orders by owner

    public static List<Order> ordersOfRestaurant(
        Collection<Order> orders,
        Restaurant restaurant)
    {
        return (orders.stream()
            .filter(order ->
                order.getRestaurant().equals(restaurant))
            .toList());
    }

    public static List<Order> ordersOfRestaurantAtStatus(
        Collection<Order> orders,
        Restaurant restaurant,
        OrderStatus status)
    {
        return (ordersAtStatus(
            ordersOfRestaurant(orders, restaurant), status));
    }

    public static List<Order> ordersOfCustomer(
        Collection<Order> orders,
        Customer customer)
    {
        return (orders.stream()
            .filter(order ->
                order.getCustomer().equals(customer))
            .toList());
    }

    public static List<Order> ordersOfRider(
        Collection<Order> orders,
        Rider rider)
    {
        return (orders.stream()
            .filter(order ->
                isAssignedTo(order, rider))
            .toList());
    }

    // Orders by status and date

    public static List<Order> ordersAtStatus(
        Collection<Order> orders,
        OrderStatus status)
    {
        return (orders.stream()
            .filter(order ->
                order.getStatus() == status)
            .toList());
    }

    public static List<Order> ordersPlacedBetween(
        Collection<Order> orders,
        LocalDate from,
        LocalDate to)
    {
        return (orders.stream()
            .filter(order ->
                isPlacedWithin(order, from, to))
            .toList());
    }

    public static List<Order> deliveredOrders(
        Collection<Order> orders)
    {
        return (ordersAtStatus(orders, OrderStatus.DELIVERED));
    }

    public static List<Order> deliveredOrdersPlacedBetween(
        Collection<Order> orders,
        LocalDate from,
        LocalDate to)
    {
        return (ordersPlacedBetween(
            deliveredOrders(orders), from, to));
    }

    private static boolean isPlacedWithin(
        Order order,
        LocalDate from,
        LocalDate to)
    {
        LocalDate placedOn;

        placedOn = order.getPlacedAt().toLocalDate();

        return (!placedOn.isBefore(from)
            && !placedOn.isAfter(to));
    }

    private static boolean isAssignedTo(
        Order order,
        Rider rider)
    {
        Optional<Rider> assigned;

        assigned = order.getRider();

        return (assigned.isPresent()
            && assigned.get().getId().equals(rider.getId()));
    }

    // Totals

    public static BigDecimal sumTotals(
        Collection<Order> orders)
    {
        return (orders.stream()
            .map(Order::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public static BigDecimal sumSubtotals(
        Collection<Order> orders)
    {
        return (orders.stream()
            .map(Order::calculateSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public static BigDecimal averageTotal(
        Collection<Order> orders)
    {
        return (sumTotals(orders).divide(
            BigDecimal.valueOf(orders.size()),
            2,
            RoundingMode.HALF_UP));
    }
}
