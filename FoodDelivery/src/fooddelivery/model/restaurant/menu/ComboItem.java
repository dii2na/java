package fooddelivery.model.restaurant.menu;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
        boolean available,
        BigDecimal stockQuantity)
    {
        super(
            id,
            name,
            calculateItemsTotal(items),
            category,
            preparationTime,
            available,
            stockQuantity);

        items = Validator.validateNotNull(
            items, "Combo items");

        if (items.isEmpty())
            throw new IllegalArgumentException(
                "Combo must contain at least one item");

        this.items = Collections.unmodifiableList(
            new ArrayList<>(items));
        this.discountRate = Validator.validateInRange(
            discountRate,
            BigDecimal.ZERO,
            BigDecimal.ONE,
            "Discount rate");
    }

    private static BigDecimal calculateItemsTotal(
        List<MenuItem> items)
    {
        items = Validator.validateNotNull(
            items, "Combo items");

        if (items.isEmpty())
            throw new IllegalArgumentException(
                "Combo must contain at least one item");

        return (items.stream()
            .map(item -> Validator.validateNotNull(
                item, "Combo item"))
            .map(MenuItem::getPrice)
            .reduce(
                BigDecimal.ZERO,
                BigDecimal::add));
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
    public BigDecimal getDisplayPrice()
    {
        return (getPrice()
            .multiply(
                BigDecimal.ONE.subtract(discountRate)));
    }

    @Override
    public BigDecimal calculatePrice(
        BigDecimal quantity)
    {
        BigDecimal discountedPrice;

        quantity = Validator.validatePositive(
            quantity, "Quantity");

        discountedPrice = getPrice()
            .multiply(
                BigDecimal.ONE.subtract(discountRate));

        return (discountedPrice.multiply(quantity));
    }

    @Override
    public String toString()
    {
        return ("ComboItem{id=%s, name=%s, category=%s, price=%s, parts=%d, discount=%s, available=%s}"
            .formatted(
                getId(),
                getName(),
                getCategory(),
                getDisplayPrice(),
                items.size(),
                discountRate,
                isAvailable()));
    }
}
