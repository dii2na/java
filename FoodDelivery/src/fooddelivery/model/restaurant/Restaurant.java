package fooddelivery.model.restaurant;

import fooddelivery.model.restaurant.menu.MenuItem;
import fooddelivery.utils.Validator;
import java.util.Optional;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Restaurant
{
    private final String id;
    private final String displayName;
    private final String district;
    private final Set<String> cuisines;
    private double averageRating;
    private boolean open;
    private final Map<String, MenuItem> menu;

    public Restaurant(
        String id,
        String displayName,
        String district,
        Set<String> cuisines,
        boolean open)
    {
        this.id = Validator.validateString(id, "Restaurant ID");
        this.displayName = Validator.validateString(displayName, "Restaurant name");
        this.district = Validator.validateString(district, "District");
        this.cuisines = new LinkedHashSet<>(
            RestaurantValidator.validateCuisines(cuisines));
        this.open = open;
        averageRating = 0.0;
        this.menu = new LinkedHashMap<>();
    }

        public String getId()
    {
        return (id);
    }

    public String getDisplayName()
    {
        return (displayName);
    }

    public String getDistrict()
    {
        return (district);
    }

    public Set<String> getCuisines()
    {
        return (Collections.unmodifiableSet(cuisines));
    }

    public Map<String, MenuItem> getMenu()
    {
        return (Collections.unmodifiableMap(menu));
    }

    public double getAverageRating()
    {
        return (averageRating);
    }

    public boolean isOpen()
    {
        return (open);
    }

    public void open()
    {
        this.open = true;
    }

    public void close()
    {
        this.open = false;
    }

    // public void setAverageRating(double averageRating)
    // {
    //     this.averageRating = RestaurantValidator.validateRating(averageRating);
    // } based on review, we should not allow setting average rating directly, it should be calculated based on reviews

    public void addCuisine(String cuisine)
    {
        cuisine = Validator.validateString(cuisine, "Cuisine");
        cuisines.add(cuisine);
    }

    public void addMenuItem(MenuItem menuItem)
    {
        menuItem = Validator.validateNotNull(
            menuItem, "Menu item");
        if (menu.containsKey(menuItem.getId()))
            throw new IllegalArgumentException(
                "Menu item ID already exists: " + menuItem.getId());
        menu.put(menuItem.getId(), menuItem);
    }

    public Optional<MenuItem> getMenuItem(String menuItemId)
    {
        menuItemId = Validator.validateString(
            menuItemId, "Menu item ID");

        return (Optional.ofNullable(menu.get(menuItemId)));
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return (true);

        if (!(object instanceof Restaurant other))
            return (false);

        return (id.equals(other.id));
    }

    @Override
    public int hashCode()
    {
        return (id.hashCode());
    }
}
