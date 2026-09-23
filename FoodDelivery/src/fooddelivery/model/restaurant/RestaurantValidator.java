package fooddelivery.model.restaurant;

import fooddelivery.utils.Validator;

import java.util.LinkedHashSet;
import java.util.Set;

public class RestaurantValidator
{
    public static Set<String> validateCuisines(Set<String> cuisines)
    {
        cuisines = Validator.validateNotNull(cuisines, "Cuisines");
        if (cuisines.isEmpty())
            throw new IllegalArgumentException(
                "Restaurant must have at least one cuisine");
        for (String cuisine : cuisines)
            Validator.validateString(cuisine, "Cuisine");

        return (new LinkedHashSet<>(cuisines));
    }

    public static double validateRating(double rating)
    {
        return (Validator.validateInRange(
            rating, 0, 5, "Average rating"));
    }
}