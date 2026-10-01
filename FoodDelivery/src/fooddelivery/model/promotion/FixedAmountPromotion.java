package fooddelivery.model.promotion;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FixedAmountPromotion extends Promotion
{
    private final BigDecimal discountAmount;

    public FixedAmountPromotion(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        BigDecimal discountAmount,
        String district,
        boolean firstTimeOnly)
    {
        super(code, expiry, minimumSubtotal, district, firstTimeOnly);
        this.discountAmount = Validator.validatePositive(
            discountAmount, "Discount amount");
    }

    public BigDecimal getDiscountAmount()
    {
        return (discountAmount);
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal)
    {
        subtotal = Validator.validateNonNegative(
            subtotal, "Subtotal");

        return (discountAmount.min(subtotal));
    }

    @Override
    public PromotionType getType()
    {
        return (PromotionType.FIXED_AMOUNT);
    }

    @Override
    public String toString()
    {
        return ("%s{code=%s, discount=%s, minSubtotal=%s, expiry=%s, district=%s, firstTimeOnly=%s}"
            .formatted(
                getType(),
                getCode(),
                discountAmount,
                getMinimumSubtotal(),
                getExpiry(),
                getDistrict().orElse("any"),
                isFirstTimeOnly()));
    }
}
