package fooddelivery.model.restaurant.menu;

import fooddelivery.utils.Validator;

import java.math.BigDecimal;

public class StandardItem extends MenuItem
{
    public StandardItem(
        String id,
        String name,
        BigDecimal price,
        String category,
        int preparationTime,
        boolean available)
    {
        super(
            id,
            name,
            price,
            category,
            preparationTime,
            available);
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal quantity)
    {
        quantity = Validator.validatePositive(
            quantity, "Quantity");

        return (getPrice().multiply(quantity));
    }
}