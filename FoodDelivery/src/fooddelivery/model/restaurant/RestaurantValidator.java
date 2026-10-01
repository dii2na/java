package fooddelivery.model.restaurant;

import fooddelivery.utils.Validator;
import java.util.LinkedHashSet;
import java.util.Set;

public class RestaurantValidator
{
    public static Set<String> validateCuisines(
        Set<String> cuisines)
    {
        LinkedHashSet<String> validatedCuisines;

        cuisines = Validator.validateNotNull(
            cuisines, "Cuisines");

        if (cuisines.isEmpty())
            throw new IllegalArgumentException(
                "Restaurant must have at least one cuisine");

        validatedCuisines = new LinkedHashSet<>();

        for (String cuisine : cuisines)
        {
            String value = Validator.validateString(
                cuisine, "Cuisine");

            if (validatedCuisines.stream()
                .anyMatch(existing ->
                    existing.equalsIgnoreCase(value)))
            {
                continue;
            }
            validatedCuisines.add(value);
        }

        return (validatedCuisines);
    }

    public static double validateRating(double rating)
    {
        return (Validator.validateInRange(
            rating,
            0,
            5,
            "Average rating"));
    }
}
