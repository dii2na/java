package fooddelivery.model.restaurant.menu;

import fooddelivery.utils.Validator;

import java.math.BigDecimal;
import java.util.List;

public class ComboItem extends MenuItem
{
    private final List<MenuItem> items;
    private final BigDecimal discountRate;

    public ComboItem(
        String id,
        String name,
        List<MenuItem> items,
        BigDecimal discountRate,
        String category,
        int preparationTime,
        boolean available)
    {
        super(
            id,
            name,
            calculateItemsTotal(items),
            category,
            preparationTime,
            available);

        this.items = List.copyOf(
            Validator.validateNotNull(items, "Combo items"));
        this.discountRate = Validator.validateInRange(
            discountRate,
            BigDecimal.ZERO,
            BigDecimal.ONE,
            "Discount rate");
    }

    private static BigDecimal calculateItemsTotal(List<MenuItem> items)
    {
        BigDecimal total;

        items = Validator.validateNotNull(items, "Combo items");
        if (items.isEmpty())
            throw new IllegalArgumentException(
                "Combo must contain at least one item");
        total = BigDecimal.ZERO;
        for (MenuItem item : items)
        {
            Validator.validateNotNull(item, "Combo item");
            total = total.add(item.getPrice());
        }

        return (total);
    }

    public List<MenuItem> getItems()
    {
        return (items);
    }

    public BigDecimal getDiscountRate()
    {
        return (discountRate);
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal quantity)
    {
        BigDecimal discountedPrice;

        quantity = Validator.validatePositive(
            quantity, "Quantity");

        discountedPrice = getPrice()
            .multiply(BigDecimal.ONE.subtract(discountRate));

        return (discountedPrice.multiply(quantity));
    }
}