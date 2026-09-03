package ROM.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import ROM.models.MenuItem;
import ROM.models.Order;
import ROM.utils.Validator;

public class Restaurant
{
    // Attributes

    private final List<MenuItem> menu;
    private final Queue<Order> kitchenQueue;
    private final Map<Integer, Order> orders;
    // LinkedHashMap preserves the completion (insertion) order.
    private final Map<Integer, Order> completedOrders;

    // Constructor

    public Restaurant()
    {
        menu = new ArrayList<>();
        kitchenQueue = new LinkedList<>();
        orders = new HashMap<>();
        completedOrders = new LinkedHashMap<>();
    }

    // Getters

    public List<MenuItem> getMenu()
    {
        return (menu);
    }

    public Map<Integer, Order> getCompletedOrders()
    {
        return (completedOrders);
    }

    public Map<Integer, Order> getOrders()
    {
        return (orders);
    }

    public Queue<Order> getKitchenQueue()
    {
        return (kitchenQueue);
    }

    // Validation Helpers

    private void checkMenuItem(MenuItem item, boolean shouldExist)
    {
        item = Validator.validateNotNull(item, "Menu item cannot be null");
        if (shouldExist && !menu.contains(item))
            throw new IllegalArgumentException("Menu item not found.");
        if (!shouldExist && menu.contains(item))
            throw new IllegalArgumentException("Menu item already exists.");
    }

    private void checkOrder(Order order, boolean shouldExist)
    {
        order = Validator.validateNotNull(order, "Order cannot be null");
        if (shouldExist && !orders.containsKey(order.getOrderId()))
            throw new IllegalArgumentException("Order not found.");
        if (!shouldExist && orders.containsKey(order.getOrderId()))
            throw new IllegalArgumentException("Order already exists.");
    }

    private void checkOrderAndItem(Order order, MenuItem item)
    {
        checkOrder(order, true);
        checkMenuItem(item, true);
    }

    // Menu Item Operations

    public Optional<MenuItem> findMenuItemById(int id)
    {
        return (menu.stream()
                .filter(item -> item.getId() == id)
                .findFirst());
    }

    public void addMenuItem(MenuItem item)
    {
        checkMenuItem(item, false);
        menu.add(item);
    }

    public void removeMenuItem(MenuItem item)
    {
        checkMenuItem(item, true);
        menu.remove(item);
    }

    // Order Operations

    public Optional<Order> findOrderById(int id)
    {
        return (Optional.ofNullable(orders.get(id)));
    }

    public void createOrder(Order order)
    {
        checkOrder(order, false);
        orders.put(order.getOrderId(), order);
    }

    public void addItemToOrder(Order order, MenuItem item, int quantity)
    {
        checkOrderAndItem(order, item);
        order.addItem(item, quantity);
    }

    public void removeItemFromOrder(Order order, MenuItem item)
    {
        checkOrderAndItem(order, item);
        order.removeItem(item);
    }

    public void cancelOrder(Order order)
    {
        checkOrder(order, true);
        order.cancel();
    }

    // Kitchen Operations

    public void sendOrderToKitchen(Order order)
    {
        checkOrder(order, true);
        order.sendToKitchen();
        kitchenQueue.add(order);
    }

    public void processNextOrder()
    {
        Order order;

        order = kitchenQueue.poll();
        if (order == null)
            throw new IllegalStateException(
                    "Kitchen queue is empty.");
        order.complete();
        completedOrders.put(order.getOrderId(), order);
    }
}