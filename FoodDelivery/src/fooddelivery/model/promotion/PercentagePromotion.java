package fooddelivery.model.promotion;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PercentagePromotion extends Promotion
{
    private final BigDecimal discountRate;
    private final BigDecimal maximumDiscount;

    public PercentagePromotion(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        BigDecimal discountRate,
        BigDecimal maximumDiscount)
    {
        super(code, expiry, minimumSubtotal);
        this.discountRate = Validator.validateInRange(
            discountRate,
            BigDecimal.ZERO,
            BigDecimal.ONE,
            "Discount rate");
        this.maximumDiscount = Validator.validatePositive(
            maximumDiscount, "Maximum discount");
    }

    public BigDecimal getDiscountRate()
    {
        return (discountRate);
    }

    public BigDecimal getMaximumDiscount()
    {
        return (maximumDiscount);
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal)
    {
        BigDecimal discount;

        subtotal = Validator.validateNonNegative(
            subtotal, "Subtotal");
        discount = subtotal.multiply(discountRate);

        return (discount.min(maximumDiscount));
    }
}