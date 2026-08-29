package restaurantordermanager.models;

import java.util.ArrayList;

import restaurantordermanager.enums.OrderStatus;
import restaurantordermanager.utils.Validator;
import restaurantordermanager.models.MenuItem;

public class Order
{
    // Attributes

    private final int orderId;
    private final ArrayList<OrderItem> items;
    private String customerName;
    private double total;
    private OrderStatus status;

    // Constructor

    public Order(int orderId, String customerName)
    {
        this.orderId = Validator.validatePositive(orderId, "Order ID");
        items = new ArrayList<>();
        setCustomerName(customerName);
        setTotal(0);
        setStatus(OrderStatus.PENDING);
    }

    // Getters

    public int getOrderId()
    {
        return (orderId);
    }

    public String getCustomerName()
    {
        return (customerName);
    }

    public ArrayList<OrderItem> getItems()
    {
        return (items);
    }

    public double getTotal()
    {
        return (total);
    }

    public OrderStatus getStatus()
    {
        return (status);
    }

    // Setters

    private void setCustomerName(String customerName)
    {
        this.customerName = Validator.validateString(
                customerName,
                "Customer Name",
                false);
    }

    private void setTotal(double total)
    {
        this.total = Validator.validateNonNegative(total, "Total");
    }

    private void setStatus(OrderStatus status)
    {
        this.status = Validator.validateNotNull(
                status,
                "Order status cannot be null");
    }

    //
    private boolean isModifiable()
    {
        return (status != OrderStatus.COMPLETED &&
                status != OrderStatus.CANCELLED);
    }

    public double calculateTotal()
    {
        double total;

        total = 0;
        for (OrderItem orderItem : items)
            total += orderItem.calculateSubtotal();
        setTotal(total);
        return (total);
    }

    public void addItem(MenuItem item, int quantity)
    {
        OrderItem newOrderItem;

        item = Validator.validateNotNull(item, "Item cannot be null");
        quantity = Validator.validatePositive(quantity, "Quantity");
        if (!isModifiable())
        {
            throw new IllegalStateException(
            "Items cannot be added to completed or cancelled orders.");
        }
        for (OrderItem orderItem : items)
        {
            if (orderItem.getItem().equals(item))
            {
                orderItem.increaseQuantity(quantity);
                calculateTotal();
                return;
            }
        }
        newOrderItem = new OrderItem(item, quantity);
        items.add(newOrderItem);
        calculateTotal();
    }

    public void removeItem(MenuItem item)
    {
        boolean removed;

        item = Validator.validateNotNull(item, "Item cannot be null");
        if (!isModifiable())
        {
            throw new IllegalStateException(
            "Items cannot be removed from completed or cancelled orders.");
        }
        removed = items.removeIf(orderItem -> orderItem.getItem().equals(item));
        if (!removed)
            throw new IllegalArgumentException("Item is not in this order.");
        calculateTotal();
    }

    public void sendToKitchen()
    {
        if (status == OrderStatus.CANCELLED)
            throw new IllegalStateException(
                "Order is cancelled.");
        if (status == OrderStatus.COMPLETED)
            throw new IllegalStateException(
                "Order is completed.");
        if (status == OrderStatus.IN_KITCHEN)
            throw new IllegalStateException(
                "Order is already in the kitchen.");
        setStatus(OrderStatus.IN_KITCHEN);
    }

    public void complete()
    {
        if (status == OrderStatus.PENDING)
            throw new IllegalStateException(
                "Pending orders cannot be completed.");
        if (status == OrderStatus.CANCELLED)
            throw new IllegalStateException(
                "Order is cancelled.");
        if (status == OrderStatus.COMPLETED)
            throw new IllegalStateException(
                "Order is already completed.");
        setStatus(OrderStatus.COMPLETED);
    }

    public void cancel()
    {
        if (status == OrderStatus.IN_KITCHEN)
            throw new IllegalStateException(
                "Orders in the kitchen cannot be cancelled.");
        if (status == OrderStatus.COMPLETED)
            throw new IllegalStateException(
                "Completed orders cannot be cancelled.");
        if (status == OrderStatus.CANCELLED)
            throw new IllegalStateException(
                "Order is already cancelled.");
        setStatus(OrderStatus.CANCELLED);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof Order other))
            return false;
        return (orderId == other.orderId);
    }

    @Override
    public int hashCode()
    {
        return (Integer.hashCode(orderId));
    }

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle("Order #" + orderId));
        info.append(fieldLine("Customer Name", customerName));
        info.append(fieldLine("Status", status));
        info.append(fieldLine("Total", money(total)));
        info.append(separator());
        info.append("  Ordered Items").append(newLine());
        info.append(separator());
        info.append(formatRow(
                "ID",
                "Name",
                "Category",
                "Price",
                "Qty",
                "Subtotal"));
        info.append(newLine());
        if (items.isEmpty())
            info.append("  No items in this order.").append(newLine());
        else
        {
            for (OrderItem orderItem : items)
                info.append(orderItem).append(newLine());
        }
        return (info.toString());
    }
}

