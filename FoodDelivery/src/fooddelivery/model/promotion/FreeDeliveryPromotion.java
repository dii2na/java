package fooddelivery.model.promotion;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FreeDeliveryPromotion extends Promotion
{
    public FreeDeliveryPromotion(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        String district,
        boolean firstTimeOnly)
    {
        super(code, expiry, minimumSubtotal, district, firstTimeOnly);
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal)
    {
        Validator.validateNonNegative(
            subtotal, "Subtotal");

        return (BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateDeliveryFeeDiscount(
        BigDecimal deliveryFee)
    {
        return (Validator.validateNonNegative(
            deliveryFee, "Delivery fee"));
    }

    @Override
    public PromotionType getType()
    {
        return (PromotionType.FREE_DELIVERY);
    }
}
