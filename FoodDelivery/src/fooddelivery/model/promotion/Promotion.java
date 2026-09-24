package fooddelivery.model.promotion;

import fooddelivery.utils.Validator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Promotion
{
    private final String code;
    private final LocalDateTime expiry;
    private final BigDecimal minimumSubtotal;

    protected Promotion(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal)
    {
        this.code = Validator.validateString(code, "Promotion code");
        this.expiry = Validator.validateNotNull(
            expiry, "Promotion expiry");
        this.minimumSubtotal = Validator.validateNonNegative(
            minimumSubtotal, "Minimum subtotal");
    }

    public String getCode()
    {
        return (code);
    }

    public LocalDateTime getExpiry()
    {
        return (expiry);
    }

    public BigDecimal getMinimumSubtotal()
    {
        return (minimumSubtotal);
    }

    public boolean isExpired(LocalDateTime currentTime)
    {
        currentTime = Validator.validateNotNull(
            currentTime, "Current time");

        return (currentTime.isAfter(expiry));
    }

    public boolean isApplicable(
        BigDecimal subtotal,
        LocalDateTime currentTime)
    {
        subtotal = Validator.validateNonNegative(
            subtotal, "Subtotal");

        currentTime = Validator.validateNotNull(
            currentTime, "Current time");

        return (!isExpired(currentTime)
            && subtotal.compareTo(minimumSubtotal) >= 0);
    }

    public boolean matchesCode(String code)
    {
        code = Validator.validateString(code, "Promotion code");

        return (this.code.equalsIgnoreCase(code));
    }

    public abstract BigDecimal calculateDiscount(
        BigDecimal subtotal);
}