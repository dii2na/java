package ROM.models;

import static ROM.utils.ConsoleUtils.*;
import ROM.utils.Validator;

public class MenuItem
{
    // Attributes

    private final int id;
    private String name;
    private double price;
    private String category;

    // Constructors

    public MenuItem(int id, String name, double price, String category)
    {
        this.id = Validator.validatePositive(id, "ID");
        setName(name);
        setPrice(price);
        setCategory(category);
    }

    // Getters

    public int getId()
    {
        return (id);
    }

    public String getName()
    {
        return (name);
    }

    public double getPrice()
    {
        return (price);
    }

    public String getCategory()
    {
        return (category);
    }

    // Setters

    public void setName(String name)
    {
        this.name = Validator.validateString(name, "Name", false);
    }

    public void setPrice(double price)
    {
        this.price = Validator.validatePositive(price, "Price");
    }

    public void setCategory(String category)
    {
        this.category = Validator.validateString(category, "Category", false);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return (true);
        if (!(obj instanceof MenuItem other))
            return (false);
        return (id == other.id);
    }

    // Display

    @Override
    public String toString()
    {
        return ("ID: " + id +
                " | Name: " + name +
                " | Category: " + category +
                " | Price: " + money(price));
    }
}
