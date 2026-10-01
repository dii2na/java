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
        boolean available,
        BigDecimal stockQuantity)
    {
        super(
            id,
            name,
            pricePerKg,
            category,
            preparationTime,
            available,
            stockQuantity);
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal quantity)
    {
        quantity = Validator.validatePositive(
            quantity, "Weight");

        return (getPrice().multiply(quantity));
    }

    @Override
    public String toString()
    {
        return ("WeightedItem{id=%s, name=%s, category=%s, pricePerKg=%s, available=%s}"
            .formatted(
                getId(),
                getName(),
                getCategory(),
                getDisplayPrice(),
                isAvailable()));
    }
}
