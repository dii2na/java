package fooddelivery.service.search;

import fooddelivery.model.customer.Customer;
import fooddelivery.model.customer.SearchRecord;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class RestaurantSearchService
{
    public List<Restaurant> search(
        Collection<Restaurant> restaurants,
        Predicate<Restaurant> condition)
    {
        restaurants = Validator.validateNotNull(
            restaurants, "Restaurants");
        condition = Validator.validateNotNull(
            condition, "Search condition");

        return (restaurants.stream()
            .filter(condition)
            .sorted(
                Comparator
                    .comparing(
                        Restaurant::getAverageRating)
                    .reversed()
                    .thenComparing(
                        Restaurant::getDisplayName))
            .toList());
    }

    public Predicate<Restaurant> byDistrict(String district)
    {
        String value = Validator.validateString(
            district, "District");

        return (restaurant ->
            restaurant.getDistrict()
                .equalsIgnoreCase(value));
    }

    public Predicate<Restaurant> byCuisine(String cuisine)
    {
        String value = Validator.validateString(
            cuisine, "Cuisine");

        return (restaurant ->
            restaurant.getCuisines().stream()
                .anyMatch(cuisineName ->
                    cuisineName.equalsIgnoreCase(value)));
    }

    public Predicate<Restaurant> byMinimumRating(
    double minimumRating)
    {
        double rating = Validator.validateInRange(
            minimumRating,
            0,
            5,
            "Minimum rating");

        return (restaurant ->
            restaurant.getAverageRating() >= rating);
    }

    public Predicate<Restaurant> byPriceCeiling(
    BigDecimal maximumPrice)
    {
        BigDecimal ceiling = Validator.validatePositive(
            maximumPrice, "Maximum price");

        return (restaurant ->
            restaurant.getMenu().values().stream()
                .anyMatch(item ->
                    item.getPrice()
                        .compareTo(ceiling) <= 0));
    }

    public List<String> findDistinctCuisines(
        Collection<Restaurant> restaurants)
    {
        restaurants = Validator.validateNotNull(
            restaurants, "Restaurants");

        return (restaurants.stream()
            .flatMap(restaurant ->
                restaurant.getCuisines().stream())
            .distinct()
            .sorted()
            .toList());
    }

    public List<Restaurant> searchForCustomer(
        Customer customer,
        Collection<Restaurant> restaurants,
        Predicate<Restaurant> condition,
        String description)
    {
        List<Restaurant> results;

        customer = Validator.validateNotNull(
            customer, "Customer");
        description = Validator.validateString(
            description, "Search description");
        results = search(restaurants, condition);
        customer.addSearchRecord(
            new SearchRecord(
                description,
                LocalDateTime.now()));

        return (results);
    }
}
