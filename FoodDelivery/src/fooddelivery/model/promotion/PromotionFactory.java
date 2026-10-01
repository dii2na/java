package fooddelivery.model.promotion;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PromotionFactory
{
    private PromotionFactory()
    {
    }

    public static PercentagePromotion createPercentage(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        BigDecimal discountRate,
        BigDecimal maximumDiscount,
        String district,
        boolean firstTimeOnly)
    {
        return (new PercentagePromotion(
            code,
            expiry,
            minimumSubtotal,
            discountRate,
            maximumDiscount,
            district,
            firstTimeOnly));
    }

    public static FixedAmountPromotion createFixedAmount(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        BigDecimal discountAmount,
        String district,
        boolean firstTimeOnly)
    {
        return (new FixedAmountPromotion(
            code,
            expiry,
            minimumSubtotal,
            discountAmount,
            district,
            firstTimeOnly));
    }

    public static FreeDeliveryPromotion createFreeDelivery(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        String district,
        boolean firstTimeOnly)
    {
        return (new FreeDeliveryPromotion(
            code,
            expiry,
            minimumSubtotal,
            district,
            firstTimeOnly));
    }

    public static Promotion create(
        PromotionType type,
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        BigDecimal discountRate,
        BigDecimal discountAmount,
        BigDecimal maximumDiscount,
        String district,
        boolean firstTimeOnly)
    {
        type = Validator.validateNotNull(
            type, "Promotion type");

        switch (type)
        {
            case PERCENTAGE:
                return (createPercentage(
                    code,
                    expiry,
                    minimumSubtotal,
                    discountRate,
                    maximumDiscount,
                    district,
                    firstTimeOnly));

            case FIXED_AMOUNT:
                return (createFixedAmount(
                    code,
                    expiry,
                    minimumSubtotal,
                    discountAmount,
                    district,
                    firstTimeOnly));

            case FREE_DELIVERY:
                return (createFreeDelivery(
                    code,
                    expiry,
                    minimumSubtotal,
                    district,
                    firstTimeOnly));

            default:
                throw new IllegalArgumentException(
                    "Unsupported promotion type: " + type);
        }
    }
}
