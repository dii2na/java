package fooddelivery.model.restaurant.menu;

import fooddelivery.utils.Validator;

import java.math.BigDecimal;

public class WeightedItem extends MenuItem
{
    public WeightedItem(
        String id,
        String name,
        BigDecimal pricePerKg,
        String category,
        int preparationTime,
        boolean available)
    {
        super(
            id,
            name,
            pricePerKg,
            category,
            preparationTime,
            available);
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal quantity)
    {
        quantity = Validator.validatePositive(
            quantity, "Weight");

        return (getPrice().multiply(quantity));
    }
}