package fooddelivery.service.observer;

import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import static fooddelivery.utils.ConsoleUtils.println;

public class ConsoleOrderObserver implements OrderObserver
{
    @Override
    public void update(
        Order order,
        OrderStatus oldStatus)
    {
        println(
            "Order " + order.getId()
            + " status changed from "
            + oldStatus
            + " to "
            + order.getStatus());
    }
}
