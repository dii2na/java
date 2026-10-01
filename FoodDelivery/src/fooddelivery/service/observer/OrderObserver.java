package fooddelivery.service.observer;

import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;

public interface OrderObserver
{
    void update(Order order, OrderStatus oldStatus);
}
