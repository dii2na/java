package fooddelivery.repository;

import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.utils.Validator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class RestaurantRepository
{
    private final Map<String, Restaurant> restaurants;

    public RestaurantRepository()
    {
        restaurants = new LinkedHashMap<>();
    }

    public void save(Restaurant restaurant)
    {
        restaurant = Validator.validateNotNull(
            restaurant, "Restaurant");
        if (exists(restaurant.getId()))
            throw new IllegalArgumentException(
                "Restaurant ID already exists: "
                + restaurant.getId());
        restaurants.put(restaurant.getId(), restaurant);
    }

    public Optional<Restaurant> findById(String id)
    {
        id = Validator.validateString(id, "Restaurant ID");

        return (Optional.ofNullable(restaurants.get(id)));
    }

    public Collection<Restaurant> findAll()
    {
        return (Collections.unmodifiableCollection(
            restaurants.values()));
    }

    public boolean exists(String id)
    {
        id = Validator.validateString(id, "Restaurant ID");

        return (restaurants.containsKey(id));
    }
}