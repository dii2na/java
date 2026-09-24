package fooddelivery.repository;

import fooddelivery.model.promotion.Promotion;
import fooddelivery.utils.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class PromotionRepository
{
    private final Map<String, Promotion> promotions;

    public PromotionRepository()
    {
        promotions = new LinkedHashMap<>();
    }

    public void save(Promotion promotion)
    {
        String code;

        promotion = Validator.validateNotNull(
            promotion, "Promotion");
        code = promotion.getCode().toUpperCase();
        if (exists(code))
            throw new IllegalArgumentException(
                "Promotion code already exists: " + code);
        promotions.put(code, promotion);
    }

    public Optional<Promotion> findByCode(String code)
    {
        code = Validator.validateString(
            code, "Promotion code");

        return (Optional.ofNullable(
            promotions.get(code.toUpperCase())));
    }

    public Collection<Promotion> findAll()
    {
        return (Collections.unmodifiableCollection(
            promotions.values()));
    }

    public boolean exists(String code)
    {
        code = Validator.validateString(
            code, "Promotion code");

        return (promotions.containsKey(
            code.toUpperCase()));
    }
}