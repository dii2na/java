package EOM.models;

import static EOM.utils.ConsoleUtils.*;
import EOM.utils.Validator;

public final class Product implements Comparable<Product>
{
    // Attributes

    private final int id;
    private String name;
    private double price;
    private String category;
    private int stockQuantity;

    // Constructor

    public Product(int id, String name, double price, String category, int stockQuantity)
    {
        this.id = Validator.validatePositive(id, "Product ID");
        setName(name);
        setPrice(price);
        setCategory(category);
        setStockQuantity(stockQuantity);
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

    public int getStockQuantity()
    {
        return (stockQuantity);
    }

    // Setters

    public void setName(String name)
    {
        this.name = Validator.validateString(name, "Product name");
    }

    public void setPrice(double price)
    {
        this.price = Validator.validatePositive(price, "Product price");
    }

    public void setCategory(String category)
    {
        this.category = Validator.validateString(category, "Product category");
    }

    public void setStockQuantity(int stockQuantity)
    {
        this.stockQuantity = Validator.validateNonNegative(
            stockQuantity, "Stock quantity");
    }

    // Stock Management

    public void decreaseStock(int quantity)
    {
        Validator.validatePositive(quantity, "Quantity");

        if (quantity > stockQuantity)
            throw new IllegalArgumentException(
                "Insufficient stock for product " + id + " (" + name
                + "): only " + stockQuantity + " in stock");
        stockQuantity -= quantity;
    }

    public void increaseStock(int quantity)
    {
        Validator.validatePositive(quantity, "Quantity");
        stockQuantity += quantity;
    }

    // Default Ordering (Cheapest -> Most Expensive)

    @Override
    public int compareTo(Product other)
    {
        Validator.validateNotNull(other, "Product cannot be null");
        return (Double.compare(this.price, other.price));
    }

    // Equality Based on Product ID

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return (true);
        if (!(obj instanceof Product other))
            return (false);
        return (id == other.id);
    }

    @Override
    public int hashCode()
    {
        return (Integer.hashCode(id));
    }

    // String Representation

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle("Product #" + id));
        info.append(fieldLine("Name", name));
        info.append(fieldLine("Price", money(price)));
        info.append(fieldLine("Category", category));
        info.append(fieldLine("Stock Quantity", stockQuantity));

        return (info.toString());
    }
}