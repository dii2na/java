package EOM.models;

import static EOM.utils.ConsoleUtils.*;
import EOM.utils.Validator;

public final class CartItem
{
    // Attributes

    private final Product product;
    private int quantity;

    // Constructor

    public CartItem(Product product, int quantity)
    {
        this.product = Validator.validateNotNull(product, "Product cannot be null");
        setQuantity(quantity);
    }

    // Getters

    public Product getProduct()
    {
        return (product);
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
        return (product.getPrice() * quantity);
    }

    // String Representation

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle("Cart Item"));
        info.append(fieldLine("Product", product.getName()));
        info.append(fieldLine("Product ID", product.getId()));
        info.append(fieldLine("Price", money(product.getPrice())));
        info.append(fieldLine("Quantity", quantity));
        info.append(fieldLine("Subtotal", money(calculateSubtotal())));

        return (info.toString());
    }
}