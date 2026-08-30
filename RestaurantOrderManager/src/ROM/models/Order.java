package ROM.models;

import static ROM.utils.ConsoleUtils.*;
import java.util.ArrayList;
import ROM.enums.OrderStatus;
import ROM.models.MenuItem;
import ROM.utils.Validator;

public class Order
{
    // Constants

    private static final String[] ITEM_HEADER = { "ID", "Name", "Category", "Price", "Qty", "Subtotal" };

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

    public OrderStatus getStatus()
    {
        return (status);
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

    // State Helpers

    private boolean isModifiable()
    {
        return (status == OrderStatus.PENDING);
    }

    private void ensureModifiable(String message)
    {
        if (!isModifiable())
            throw new IllegalStateException(message);
    }

    private void rejectIfStatus(OrderStatus unwantedStatus, String message)
    {
        if (status == unwantedStatus)
            throw new IllegalStateException(message);
    }

    // Totals

    public double calculateTotal()
    {
        double total;

        total = 0;
        for (OrderItem orderItem : items)
            total += orderItem.calculateSubtotal();
        setTotal(total);
        return (total);
    }

    // Item Operations

    private OrderItem findOrderItem(MenuItem item)
    {
        for (OrderItem orderItem : items)
        {
            if (orderItem.getItem().equals(item))
                return (orderItem);
        }
        return (null);
    }

    public void addItem(MenuItem item, int quantity)
    {
        OrderItem existing;

        item = Validator.validateNotNull(item, "Item cannot be null");
        quantity = Validator.validatePositive(quantity, "Quantity");
        ensureModifiable("Items can only be added to pending orders.");
        existing = findOrderItem(item);
        if (existing != null)
        {
            existing.increaseQuantity(quantity);
            calculateTotal();
            return;
        }
        items.add(new OrderItem(item, quantity));
        calculateTotal();
    }

    public void removeItem(MenuItem item)
    {
        OrderItem found;

        item = Validator.validateNotNull(item, "Item cannot be null");
        ensureModifiable("Items can only be removed from pending orders.");
        found = findOrderItem(item);
        if (found == null)
            throw new IllegalArgumentException("Item is not in this order.");
        items.remove(found);
        calculateTotal();
    }

    // Status Changes

    public void sendToKitchen()
    {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot send an empty order to the kitchen.");
        rejectIfStatus(OrderStatus.CANCELLED, "Order is cancelled.");
        rejectIfStatus(OrderStatus.COMPLETED, "Order is completed.");
        rejectIfStatus(OrderStatus.IN_KITCHEN, "Order is already in the kitchen.");
        setStatus(OrderStatus.IN_KITCHEN);
    }

    public void complete()
    {
        rejectIfStatus(OrderStatus.PENDING, "Pending orders cannot be completed.");
        rejectIfStatus(OrderStatus.CANCELLED, "Order is cancelled.");
        rejectIfStatus(OrderStatus.COMPLETED, "Order is already completed.");
        setStatus(OrderStatus.COMPLETED);
    }

    public void cancel()
    {
        rejectIfStatus(OrderStatus.IN_KITCHEN, "Orders in the kitchen cannot be cancelled.");
        rejectIfStatus(OrderStatus.COMPLETED, "Completed orders cannot be cancelled.");
        rejectIfStatus(OrderStatus.CANCELLED, "Order is already cancelled.");
        setStatus(OrderStatus.CANCELLED);
    }

    // Display

    public void displayOrder()
    {
        print(toString());
    }

    @Override
    public String toString()
    {
        StringBuilder info;
        Object[][] rows;
        int index;

        info = new StringBuilder();
        info.append(sectionTitle("Order #" + orderId));
        info.append(fieldLine("Customer Name", customerName));
        info.append(fieldLine("Status", status));
        info.append(fieldLine("Total", money(total)));
        info.append(separator());
        info.append("  Ordered Items").append(newLine());
        info.append(separator());
        if (items.isEmpty())
        {
            info.append("  No items in this order.").append(newLine());
        }
        else
        {
            rows = new Object[items.size()][6];
            index = 0;
            for (OrderItem orderItem : items)
            {
                rows[index][0] = orderItem.getItem().getId();
                rows[index][1] = orderItem.getItem().getName();
                rows[index][2] = orderItem.getItem().getCategory();
                rows[index][3] = money(orderItem.getItem().getPrice());
                rows[index][4] = orderItem.getQuantity();
                rows[index][5] = money(orderItem.calculateSubtotal());
                index++;
            }
            info.append(formatTable(ITEM_HEADER, rows));
        }
        return (info.toString());
    }
}