package fooddelivery.repository;

import fooddelivery.model.order.Order;
import java.util.Optional;

public class OrderRepository
    extends KeyedRepository<Order>
{
    public OrderRepository()
    {
        super("Order", "Order ID");
    }

    // Finding a order by its key

    @Override
    protected String keyOf(Order order)
    {
        return (order.getId());
    }

    public Optional<Order> findById(String id)
    {
        return (findByKey(id));
    }
}
