package fooddelivery.model.restaurant.menu;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.util.List;

public class MenuItemFactory
{
    private MenuItemFactory()
    {
    }

    public static StandardItem createStandard(
        String id,
        String name,
        BigDecimal price,
        String category,
        int preparationTime,
        boolean available,
        BigDecimal stockQuantity)
    {
        return (new StandardItem(
            id,
            name,
            price,
            category,
            preparationTime,
            available,
            stockQuantity));
    }

    public static WeightedItem createWeighted(
        String id,
        String name,
        BigDecimal pricePerKg,
        String category,
        int preparationTime,
        boolean available,
        BigDecimal stockQuantity)
    {
        return (new WeightedItem(
            id,
            name,
            pricePerKg,
            category,
            preparationTime,
            available,
            stockQuantity));
    }

    public static ComboItem createCombo(
        String id,
        String name,
        List<MenuItem> items,
        BigDecimal discountRate,
        String category,
        int preparationTime,
        boolean available,
        BigDecimal stockQuantity)
    {
        return (new ComboItem(
            id,
            name,
            items,
            discountRate,
            category,
            preparationTime,
            available,
            stockQuantity));
    }

    public static MenuItem create(
        MenuItemType type,
        String id,
        String name,
        BigDecimal price,
        String category,
        int preparationTime,
        boolean available,
        BigDecimal stockQuantity,
        List<MenuItem> items,
        BigDecimal discountRate)
    {
        type = Validator.validateNotNull(
            type, "Menu item type");

        switch (type)
        {
            case STANDARD:
                return (createStandard(
                    id,
                    name,
                    price,
                    category,
                    preparationTime,
                    available,
                    stockQuantity));

            case WEIGHTED:
                return (createWeighted(
                    id,
                    name,
                    price,
                    category,
                    preparationTime,
                    available,
                    stockQuantity));

            case COMBO:
                return (createCombo(
                    id,
                    name,
                    items,
                    discountRate,
                    category,
                    preparationTime,
                    available,
                    stockQuantity));

            default:
                throw new IllegalArgumentException(
                    "Unsupported menu item type: " + type);
        }
    }
}
