package fooddelivery.model.restaurant.menu;

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

    public MenuItem(
        String id,
        String name,
        BigDecimal price,
        String category,
        int preparationTime,
        boolean available)
    {
        this.id = Validator.validateString(id, "Menu item ID");
        this.name = Validator.validateString(name, "Menu item name");
        this.price = Validator.validatePositive(price, "Menu item price");
        this.category = Validator.validateString(category, "Menu item category");
        this.preparationTime = Validator.validatePositive(
            preparationTime, "Preparation time");
        this.available = available;
    }

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

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

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

    public abstract BigDecimal calculatePrice(BigDecimal quantity);
}