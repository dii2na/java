package fooddelivery.service.report;

import fooddelivery.model.customer.Customer;
import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.repository.CustomerRepository;
import fooddelivery.repository.OrderRepository;
import fooddelivery.service.order.OrderQueries;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerReportService
{
    private static final int INACTIVE_DAYS = 30;

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public CustomerReportService(
        OrderRepository orderRepository,
        CustomerRepository customerRepository)
    {
        this.orderRepository = Validator.validateNotNull(
            orderRepository, "Order repository");
        this.customerRepository = Validator.validateNotNull(
            customerRepository, "Customer repository");
    }

    public record CustomerOrderHistory(
        Customer customer,
        List<Order> orders,
        BigDecimal totalSpent)
    {
    }

    public record InactiveCustomer(
        Customer customer,
        Optional<LocalDate> lastOrderDate)
    {
    }

    // Order history

    public CustomerOrderHistory customerOrderHistory(Customer customer)
    {
        Customer subject;
        List<Order> orders;
        List<Order> history;
        BigDecimal totalSpent;

        subject = Validator.validateNotNull(customer, "Customer");
        orders = ordersOf(subject, orderRepository.findAll());
        history = orders.stream()
            .sorted(byNewestFirst())
            .collect(Collectors.toList());
        totalSpent = OrderQueries.sumTotals(
            orders.stream()
                .filter(order ->
                    order.getStatus() != OrderStatus.CANCELLED)
                .toList());

        return (new CustomerOrderHistory(
            subject,
            Collections.unmodifiableList(history),
            totalSpent));
    }

    private static Comparator<Order> byNewestFirst()
    {
        return (Comparator
            .comparing(Order::getPlacedAt)
            .reversed());
    }

    // Inactive customers

    public List<InactiveCustomer> customersNotOrderedRecently()
    {
        Collection<Order> orders;
        LocalDate cutoffDate;
        List<InactiveCustomer> report;

        orders = orderRepository.findAll();
        cutoffDate = LocalDate.now().minusDays(INACTIVE_DAYS);
        report = customerRepository.findAll().stream()
            .filter(customer ->
                !hasOrderedSince(customer, orders, cutoffDate))
            .map(customer -> new InactiveCustomer(
                customer,
                lastOrderDate(customer, orders)))
            .sorted(Comparator.comparing(row ->
                row.customer().getName()))
            .collect(Collectors.toList());

        return (Collections.unmodifiableList(report));
    }

    private static boolean hasOrderedSince(
        Customer customer,
        Collection<Order> orders,
        LocalDate cutoffDate)
    {
        return (ordersOf(customer, orders).stream()
            .anyMatch(order ->
                !order.getPlacedAt().toLocalDate()
                    .isBefore(cutoffDate)));
    }

    private static Optional<LocalDate> lastOrderDate(
        Customer customer,
        Collection<Order> orders)
    {
        return (ordersOf(customer, orders).stream()
            .map(order ->
                order.getPlacedAt().toLocalDate())
            .max(LocalDate::compareTo));
    }

    private static List<Order> ordersOf(
        Customer customer,
        Collection<Order> orders)
    {
        return (orders.stream()
            .filter(order ->
                order.getCustomer().equals(customer))
            .toList());
    }
}
