package fooddelivery.model.restaurant.menu;

import fooddelivery.exception.StockShortageException;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;

public abstract class MenuItem
{
    private final String id;
    private final String name;
    private final BigDecimal price;
    private final String category;
    private final int preparationTime;
    private boolean available;
    private BigDecimal stockQuantity;

    public MenuItem(
        String id,
        String name,
        BigDecimal price,
        String category,
        int preparationTime,
        boolean available,
        BigDecimal stockQuantity)
    {
        this.id = Validator.validateString(
            id, "Menu item ID");
        this.name = Validator.validateString(
            name, "Menu item name");
        this.price = Validator.validatePositive(
            price, "Menu item price");
        this.category = Validator.validateString(
            category, "Menu item category");
        this.preparationTime = Validator.validatePositive(
            preparationTime, "Preparation time");
        this.available = available;
        this.stockQuantity = Validator.validateNonNegative(
            stockQuantity, "Stock quantity");
    }

    // Reading the item

    public String getId()
    {
        return (id);
    }

    public String getName()
    {
        return (name);
    }

    public BigDecimal getPrice()
    {
        return (price);
    }

    /**
     * The price a customer is actually charged for one unit.
     * <p>
     * Equal to {@link #getPrice()} for every item type that stores its
     * real unit price. {@link ComboItem} overrides it because its
     * inherited price is the undiscounted sum of its parts, so
     * printing that figure would show a price the customer is
     * never billed.
     *
     * @return the unit price after any item-level discount
     */
    public BigDecimal getDisplayPrice()
    {
        return (price);
    }

    public String getCategory()
    {
        return (category);
    }

    public int getPreparationTime()
    {
        return (preparationTime);
    }

    public boolean isAvailable()
    {
        return (available);
    }

    // Availability and stock

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public BigDecimal getStockQuantity()
    {
        return (stockQuantity);
    }

    public void checkStock(BigDecimal quantity)
    {
        quantity = Validator.validatePositive(
            quantity, "Quantity");

        if (quantity.compareTo(stockQuantity) > 0)
            throw new StockShortageException(
                "Not enough stock for menu item: " + name);
    }

    public void addStock(BigDecimal quantity)
    {
        quantity = Validator.validatePositive(
            quantity, "Stock quantity");

        stockQuantity = stockQuantity.add(quantity);
    }

    public void reduceStock(BigDecimal quantity)
    {
        checkStock(quantity);
        stockQuantity = stockQuantity.subtract(quantity);
    }

    // Textual representation

    @Override
    public String toString()
    {
        return ("%s{id=%s, name=%s, category=%s, price=%s, available=%s}"
            .formatted(
                getClass().getSimpleName(),
                id,
                name,
                category,
                getDisplayPrice(),
                available));
    }

    // Identity

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return (true);

        if (!(object instanceof MenuItem other))
            return (false);

        return (id.equals(other.id));
    }

    @Override
    public int hashCode()
    {
        return (id.hashCode());
    }

    public abstract BigDecimal calculatePrice(
        BigDecimal quantity);
}
