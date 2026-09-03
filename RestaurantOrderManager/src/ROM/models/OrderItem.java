package ROM.models;

import static ROM.utils.ConsoleUtils.*;
import ROM.utils.Validator;

public class OrderItem
{
    // Attributes

    private final MenuItem item;
    private int quantity;

    // Constructor

    public OrderItem(MenuItem item, int quantity)
    {
        this.item = Validator.validateNotNull(item, "Item cannot be null");
        setQuantity(quantity);
    }

    // Getters

    public MenuItem getItem()
    {
        return (item);
    }

    public int getQuantity()
    {
        return (quantity);
    }

    // Setters

    public void setQuantity(int quantity)
    {
        this.quantity = Validator.validatePositive(quantity, "Quantity");
    }

    // Calculations

    public double calculateSubtotal()
    {
        return (item.getPrice() * quantity);
    }

    public int increaseQuantity(int quantity)
    {
        quantity = Validator.validatePositive(quantity, "Quantity");
        this.quantity += quantity;
        return (this.quantity);
    }

    // Display

    @Override
    public String toString()
    {
        return (formatTable(null, new Object[][]
        {
            { item.getName(), quantity, money(calculateSubtotal()) }
        }));
    }
}