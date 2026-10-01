package fooddelivery.model.promotion;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class Promotion
{
    private final String code;
    private final LocalDateTime expiry;
    private final BigDecimal minimumSubtotal;
    private final String district;
    private final boolean firstTimeOnly;

    protected Promotion(
        String code,
        LocalDateTime expiry,
        BigDecimal minimumSubtotal,
        String district,
        boolean firstTimeOnly)
    {
        this.code = Validator.validateString(code, "Promotion code");
        this.expiry = Validator.validateNotNull(
            expiry, "Promotion expiry");
        this.minimumSubtotal = Validator.validateNonNegative(
            minimumSubtotal, "Minimum subtotal");

        if (district == null || district.isBlank())
            this.district = null;
        else
            this.district = district.trim();
        this.firstTimeOnly = firstTimeOnly;
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

    public Optional<String> getDistrict()
    {
        return (Optional.ofNullable(district));
    }

    public boolean isFirstTimeOnly()
    {
        return (firstTimeOnly);
    }

    public abstract PromotionType getType();

    public boolean isExpired(LocalDateTime currentTime)
    {
        currentTime = Validator.validateNotNull(
            currentTime, "Current time");

        return (!currentTime.isBefore(expiry));
    }

    public boolean isApplicable(
        BigDecimal subtotal,
        LocalDateTime currentTime,
        String deliveryDistrict,
        int completedOrderCount)
    {
        subtotal = Validator.validateNonNegative(
            subtotal, "Subtotal");
        currentTime = Validator.validateNotNull(
            currentTime, "Current time");
        completedOrderCount = Validator.validateNonNegative(
            completedOrderCount, "Completed order count");

        if (isExpired(currentTime))
            return (false);

        if (!isMinimumSubtotalMet(subtotal))
            return (false);

        if (!isDistrictAllowed(deliveryDistrict))
            return (false);

        return (isCustomerEligible(completedOrderCount));
    }

    public boolean isDistrictAllowed(String deliveryDistrict)
    {
        if (district == null)
            return (true);

        if (deliveryDistrict == null)
            return (false);

        return (district.equalsIgnoreCase(deliveryDistrict));
    }

    public boolean isCustomerEligible(int completedOrderCount)
    {
        completedOrderCount = Validator.validateNonNegative(
            completedOrderCount, "Completed order count");

        return (!firstTimeOnly || completedOrderCount == 0);
    }

    public boolean isMinimumSubtotalMet(BigDecimal subtotal)
    {
        subtotal = Validator.validateNonNegative(
            subtotal, "Subtotal");

        return (subtotal.compareTo(minimumSubtotal) >= 0);
    }

    public boolean matchesCode(String code)
    {
        code = Validator.validateString(code, "Promotion code");

        return (this.code.equalsIgnoreCase(code));
    }

    public abstract BigDecimal calculateDiscount(
        BigDecimal subtotal);

    public BigDecimal calculateDeliveryFeeDiscount(
        BigDecimal deliveryFee)
    {
        deliveryFee = Validator.validateNonNegative(
            deliveryFee, "Delivery fee");

        return (BigDecimal.ZERO);
    }

    // Textual representation

    @Override
    public String toString()
    {
        return ("%s{code=%s, minSubtotal=%s, expiry=%s, district=%s, firstTimeOnly=%s}"
            .formatted(
                getType(),
                code,
                minimumSubtotal,
                expiry,
                district == null ? "any" : district,
                firstTimeOnly));
    }
}
