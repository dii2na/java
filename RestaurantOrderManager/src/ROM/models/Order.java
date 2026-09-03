package ROM.models;

import static ROM.utils.ConsoleUtils.*;
import java.util.ArrayList;
import java.util.Optional;
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

        total = items.stream()
                .mapToDouble(OrderItem::calculateSubtotal)
                .sum();
        setTotal(total);
        return (total);
    }

    // Item Operations

    private Optional<OrderItem> findOrderItem(MenuItem item)
    {
        return items.stream()
                .filter(orderItem -> orderItem.getItem().equals(item))
                .findFirst();
    }

    public void addItem(MenuItem item, int quantity)
    {
        Optional<OrderItem> existing;

        item = Validator.validateNotNull(item, "Item cannot be null");
        quantity = Validator.validatePositive(quantity, "Quantity");
        ensureModifiable("Items can only be added to pending orders.");
        existing = findOrderItem(item);
        if (existing.isPresent())
        {
            existing.get().increaseQuantity(quantity);
            calculateTotal();
            return;
        }
        items.add(new OrderItem(item, quantity));
        calculateTotal();
    }

    public void removeItem(MenuItem item)
    {
        item = Validator.validateNotNull(item, "Item cannot be null");
        ensureModifiable("Items can only be removed from pending orders.");
        OrderItem found = findOrderItem(item)
                .orElseThrow(() -> new IllegalArgumentException("Item is not in this order."));
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

    private Object[] toRow(OrderItem orderItem)
    {
        return new Object[]
        {
            orderItem.getItem().getId(),
            orderItem.getItem().getName(),
            orderItem.getItem().getCategory(),
            money(orderItem.getItem().getPrice()),
            orderItem.getQuantity(),
            money(orderItem.calculateSubtotal())
        };
    }

    @Override
    public String toString()
    {
        StringBuilder info;
        Object[][] rows;

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
            rows = items.stream()
                    .map(this::toRow)
                    .toArray(Object[][]::new);
            info.append(formatTable(ITEM_HEADER, rows));
        }
        return (info.toString());
    }
}