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
        BigDecimal discountAmount)
    {
        super(code, expiry, minimumSubtotal);
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
}