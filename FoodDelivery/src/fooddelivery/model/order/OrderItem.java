package fooddelivery.model.order;

import fooddelivery.model.restaurant.menu.MenuItem;
import fooddelivery.utils.Validator;

import java.math.BigDecimal;

public class OrderItem
{
    private final MenuItem menuItem;
    private final BigDecimal quantity;

    public OrderItem(MenuItem menuItem, BigDecimal quantity)
    {
        this.menuItem = Validator.validateNotNull(
            menuItem, "Menu item");
        this.quantity = Validator.validatePositive(
            quantity, "Quantity");
    }

    public MenuItem getMenuItem()
    {
        return (menuItem);
    }

    public BigDecimal getQuantity()
    {
        return (quantity);
    }

    public BigDecimal calculateTotal()
    {
        return (menuItem.calculatePrice(quantity));
    }
}