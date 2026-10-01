package fooddelivery.service.observer;

import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.utils.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderStatusSubject
{
    private final List<OrderObserver> observers;

    public OrderStatusSubject()
    {
        observers = new ArrayList<>();
    }

    public void addObserver(OrderObserver observer)
    {
        observer = Validator.validateNotNull(
            observer, "Order observer");

        if (!observers.contains(observer))
            observers.add(observer);
    }

    public void removeObserver(OrderObserver observer)
    {
        observer = Validator.validateNotNull(
            observer, "Order observer");

        observers.remove(observer);
    }

    public List<OrderObserver> getObservers()
    {
        return (Collections.unmodifiableList(observers));
    }

    public void notifyObservers(
        Order order,
        OrderStatus oldStatus)
    {
        Order currentOrder;
        OrderStatus previousStatus;
        List<OrderObserver> currentObservers;

        currentOrder = Validator.validateNotNull(
            order, "Order");
        previousStatus = Validator.validateNotNull(
            oldStatus, "Old order status");
        currentObservers = new ArrayList<>(observers);

        currentObservers.forEach(
            observer -> observer.update(
                currentOrder, previousStatus));
    }
}
