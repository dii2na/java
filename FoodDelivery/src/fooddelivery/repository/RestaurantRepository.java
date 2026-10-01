package fooddelivery.repository;

import fooddelivery.model.restaurant.Restaurant;
import java.util.Optional;

public class RestaurantRepository
    extends KeyedRepository<Restaurant>
{
    public RestaurantRepository()
    {
        super("Restaurant", "Restaurant ID");
    }

    // Finding a restaurant by its key

    @Override
    protected String keyOf(Restaurant restaurant)
    {
        return (restaurant.getId());
    }

    public Optional<Restaurant> findById(String id)
    {
        return (findByKey(id));
    }
}
