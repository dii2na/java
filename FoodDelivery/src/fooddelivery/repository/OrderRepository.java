package fooddelivery.repository;

import fooddelivery.model.order.Order;
import fooddelivery.utils.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class OrderRepository
{
    private final Map<String, Order> orders;

    public OrderRepository()
    {
        orders = new LinkedHashMap<>();
    }

    public void save(Order order)
    {
        order = Validator.validateNotNull(
            order, "Order");

        if (exists(order.getId()))
            throw new IllegalArgumentException(
                "Order ID already exists: "
                + order.getId());

        orders.put(order.getId(), order);
    }

    public Optional<Order> findById(String id)
    {
        id = Validator.validateString(id, "Order ID");

        return (Optional.ofNullable(orders.get(id)));
    }

    public Collection<Order> findAll()
    {
        return (Collections.unmodifiableCollection(
            orders.values()));
    }

    public boolean exists(String id)
    {
        id = Validator.validateString(id, "Order ID");

        return (orders.containsKey(id));
    }
}