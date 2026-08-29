package restaurantordermanager.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import restaurantordermanager.models.MenuItem;
import restaurantordermanager.models.Order;
import restaurantordermanager.utils.Validator;

public class Restaurant
{
    // Attributes

    private final ArrayList<MenuItem> menu;
    private final LinkedList<Order> kitchenQueue;
    private final HashMap<Integer, Order> orders;
    private final LinkedHashMap<Integer, Order> completedOrders;

    // Constructor

    public Restaurant()
    {
        menu = new ArrayList<>();
        kitchenQueue = new LinkedList<>();
        orders = new HashMap<>();
        completedOrders = new LinkedHashMap<>();
    }

    // Getters

    public ArrayList<MenuItem> getMenu()
    {
        return (menu);
    }

    public LinkedList<Order> getKitchenQueue()
    {
        return (kitchenQueue);
    }

    public HashMap<Integer, Order> getOrders()
    {
        return (orders);
    }

    public LinkedHashMap<Integer, Order> getCompletedOrders()
    {
        return (completedOrders);
    }

    public MenuItem findMenuItemById(int id)
    {
        for (MenuItem item : menu)
        {
            if (item.getId() == id)
                return (item);
        }
        return (null);
    }

    private void checkMenuItem(MenuItem item, boolean shouldExist)
    {
        boolean exists;

        item = Validator.validateNotNull(item, "Menu item cannot be null");
        exists = menu.contains(item);
        if (shouldExist && !exists)
            throw new IllegalArgumentException("Menu item not found.");
        if (!shouldExist && exists)
            throw new IllegalArgumentException("Menu item already exists.");
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

    public Order findOrderById(int id)
    {
        return (orders.get(id));
    }

    private void checkOrder(Order order, boolean shouldExist)
    {
        boolean exists;

        order = Validator.validateNotNull(order, "Order cannot be null");
        exists = orders.containsKey(order.getOrderId());
        if (shouldExist && !exists)
            throw new IllegalArgumentException("Order not found.");
        if (!shouldExist && exists)
            throw new IllegalArgumentException("Order already exists.");
    }

    public void createOrder(Order order)
    {
        checkOrder(order, false);
        orders.put(order.getOrderId(), order);
    }

    public void addItemToOrder(Order order, MenuItem item, int quantity)
    {
        checkOrder(order, true);
        checkMenuItem(item, true);
        order.addItem(item, quantity);
    }

    public void removeItemFromOrder(Order order, MenuItem item)
    {
        checkOrder(order, true);
        checkMenuItem(item, true);
        order.removeItem(item);
    }

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

    public void cancelOrder(Order order)
    {
        checkOrder(order, true);
        order.cancel();
    }

}