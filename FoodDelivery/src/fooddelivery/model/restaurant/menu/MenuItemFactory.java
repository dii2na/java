package fooddelivery.model.restaurant.menu;

import fooddelivery.utils.Validator;

import java.math.BigDecimal;
import java.util.List;

public class MenuItemFactory
{
    public static MenuItem create(
        MenuItemType type,
        String id,
        String name,
        BigDecimal price,
        String category,
        int preparationTime,
        boolean available,
        List<MenuItem> items,
        BigDecimal discountRate)
    {
        type = Validator.validateNotNull(type, "Menu item type");

        return switch (type)
        {
            case STANDARD -> new StandardItem(
                id,
                name,
                price,
                category,
                preparationTime,
                available);

            case WEIGHTED -> new WeightedItem(
                id,
                name,
                price,
                category,
                preparationTime,
                available);

            case COMBO -> new ComboItem(
                id,
                name,
                items,
                discountRate,
                category,
                preparationTime,
                available);
                
            default -> throw new IllegalArgumentException(
                "Unsupported menu item type: " + type);
        };
    }
}