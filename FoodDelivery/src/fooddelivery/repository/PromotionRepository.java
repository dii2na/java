package fooddelivery.repository;

import fooddelivery.model.promotion.Promotion;
import java.util.Optional;

public class PromotionRepository
    extends KeyedRepository<Promotion>
{
    public PromotionRepository()
    {
        super("Promotion", "Promotion code");
    }

    // Finding a promotion by its code

    @Override
    protected String keyOf(Promotion promotion)
    {
        return (promotion.getCode().toUpperCase());
    }

    @Override
    protected String normalizeKey(String key)
    {
        return (key.toUpperCase());
    }

    public Optional<Promotion> findByCode(String code)
    {
        return (findByKey(code));
    }
}
