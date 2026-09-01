package EOM.models;

import static EOM.utils.ConsoleUtils.*;
import EOM.enums.OrderStatus;
import EOM.utils.Validator;
import java.util.ArrayList;
import java.util.List;

public class Order
{
    // Attributes

    private final int orderId;
    private final String customerName;
    private final List<CartItem> items;
    private double total;
    private OrderStatus status;

    // Constructor

    public Order(int orderId, String customerName)
    {
        this.orderId = Validator.validatePositive(orderId, "Order ID");
        this.customerName = Validator.validateString(
            customerName, "Customer name");
        this.items = new ArrayList<>();
        this.total = 0.0;
        this.status = OrderStatus.PENDING;
    }

    // Getters

    public int getOrderId()
    {
        return (orderId);
    }

    public List<CartItem> getItems()
    {
        return (List.copyOf(items));
    }

    public double getTotal()
    {
        return (total);
    }

    public OrderStatus getStatus()
    {
        return (status);
    }

    // Order Status

    public void setStatus(OrderStatus newStatus)
    {
        Validator.validateNotNull(
            newStatus, "Order status cannot be null");
        validateStatusTransition(newStatus);
        status = newStatus;
    }

    public boolean isPending()
    {
        return (status == OrderStatus.PENDING);
    }

    public boolean hasItems()
    {
        return (!items.isEmpty());
    }

    public boolean containsProduct(int productId)
    {
        return (findItemById(productId) != null);
    }

    public void cancel()
    {
        setStatus(OrderStatus.CANCELLED);
        for (CartItem item : items)
            restoreStock(item);
    }

    // Item Management

    public void addItem(Product product, int quantity)
    {
        CartItem existingItem;

        Validator.validateNotNull(product, "Product cannot be null");
        Validator.validatePositive(quantity, "Quantity");
        ensurePending();
        product.decreaseStock(quantity);
        existingItem = findItemById(product.getId());
        if (existingItem != null)
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        else
            items.add(new CartItem(product, quantity));
        calculateTotal();
    }

    public void removeItem(Product product)
    {
        Validator.validateNotNull(product, "Product cannot be null");
        removeItemById(product.getId());
    }

    public CartItem removeItemById(int productId)
    {
        CartItem item;

        ensurePending();
        item = findItemById(productId);
        if (item == null)
            throw new IllegalArgumentException(
                "Product with ID " + productId
                + " is not in this order");
        restoreStock(item);
        items.remove(item);
        calculateTotal();
        return (item);
    }

    public void calculateTotal()
    {
        total = 0.0;
        for (CartItem item : items)
            total += item.calculateSubtotal();
    }

    // Display

    public String displayOrder()
    {
        return (toString());
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
        if (items.isEmpty())
            info.append("  No items in this order.").append(newLine());
        else
        {
            info.append(formatTable(
                new String[]{"ID", "Name", "Price", "Quantity", "Subtotal"},
                buildItemRows()));
        }

        return (info.toString());
    }

    // Helpers

    private CartItem findItemById(int productId)
    {
        for (CartItem item : items)
        {
            if (item.getProduct().getId() == productId)
                return (item);
        }

        return (null);
    }

    private void restoreStock(CartItem item)
    {
        item.getProduct().increaseStock(item.getQuantity());
    }

    private void ensurePending()
    {
        if (!isPending())
            throw new IllegalStateException(
                "Items cannot be modified when order status is " + status);
    }

    private void validatePendingTransition(OrderStatus newStatus)
    {
        if (newStatus != OrderStatus.SHIPPED
                && newStatus != OrderStatus.CANCELLED)
        {
            throw new IllegalStateException(
                "Pending order can only become Shipped or Cancelled");
        }
    }

    private void validateShippedTransition(OrderStatus newStatus)
    {
        if (newStatus != OrderStatus.DELIVERED
                && newStatus != OrderStatus.CANCELLED)
        {
            throw new IllegalStateException(
                "Shipped order can only become Delivered or Cancelled");
        }
    }

    private void validateStatusTransition(OrderStatus newStatus)
    {
        if (isPending())
        {
            validatePendingTransition(newStatus);
            return;
        }
        if (status == OrderStatus.SHIPPED)
        {
            validateShippedTransition(newStatus);
            return;
        }
        throw new IllegalStateException(
            "Order status cannot be changed from "
            + status + " to " + newStatus);
    }

    private Object[][] buildItemRows()
    {
        List<Object[]> rows;

        rows = new ArrayList<>();
        for (CartItem item : items)
        {
            rows.add(new Object[] {
                item.getProduct().getId(),
                item.getProduct().getName(),
                money(item.getProduct().getPrice()),
                item.getQuantity(),
                money(item.calculateSubtotal())
            });
        }

        return (rows.toArray(new Object[0][]));
    }
}