package fooddelivery.service.pricing;

import fooddelivery.config.PlatformConfig;
import fooddelivery.model.customer.LoyaltyTier;
import fooddelivery.model.order.Order;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class StandardPricingStrategy
    implements PricingStrategy
{
    private final PlatformConfig config;

    public StandardPricingStrategy()
    {
        this.config = PlatformConfig.getInstance();
    }

    @Override
    public BigDecimal calculateTotal(Order order)
    {
        return (quote(order).getTotal());
    }

    @Override
    public PriceBreakdown quote(Order order)
    {
        BigDecimal subtotal;
        BigDecimal baseDeliveryFee;
        BigDecimal extraDistanceFee;
        BigDecimal rawDeliveryFee;
        BigDecimal loyaltyDeliveryDiscount;
        BigDecimal promotionDeliveryDiscount;
        BigDecimal serviceFee;
        BigDecimal promotionDiscount;
        BigDecimal total;
        LoyaltyTier loyaltyTier;

        order = Validator.validateNotNull(
            order, "Order");
        subtotal = order.calculateSubtotal();
        baseDeliveryFee = config.getBaseDeliveryFee();
        extraDistanceFee = calculateExtraDistanceFee(order);
        rawDeliveryFee = baseDeliveryFee
            .add(extraDistanceFee);
        loyaltyTier = order.getCustomer().getLoyaltyTier();
        loyaltyDeliveryDiscount = rawDeliveryFee
            .multiply(loyaltyTier.getDeliveryFeeDiscount());
        promotionDeliveryDiscount = calculatePromotionDeliveryDiscount(
            order, rawDeliveryFee.subtract(loyaltyDeliveryDiscount));
        serviceFee = calculateServiceFee(subtotal);
        promotionDiscount = calculatePromotionDiscount(
            order, subtotal);
        total = subtotal
            .add(rawDeliveryFee)
            .add(serviceFee)
            .subtract(loyaltyDeliveryDiscount)
            .subtract(promotionDeliveryDiscount)
            .subtract(promotionDiscount);

        return (new PriceBreakdown(
            subtotal,
            baseDeliveryFee,
            extraDistanceFee,
            loyaltyDeliveryDiscount,
            promotionDeliveryDiscount,
            serviceFee,
            promotionDiscount,
            total.max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP)));
    }

    private BigDecimal calculateExtraDistanceFee(Order order)
    {
        BigDecimal extraDistance;

        extraDistance = order.getDeliveryDistance()
            .subtract(config.getIncludedDistance());

        if (extraDistance.compareTo(BigDecimal.ZERO) <= 0)
            return (BigDecimal.ZERO);

        return (extraDistance.multiply(
            config.getExtraKmFee()));
    }

    private BigDecimal calculatePromotionDeliveryDiscount(
        Order order,
        BigDecimal deliveryFee)
    {
        if (order.getPromotion() == null)
            return (BigDecimal.ZERO);

        return (order.getPromotion()
            .calculateDeliveryFeeDiscount(deliveryFee)
            .min(deliveryFee)
            .max(BigDecimal.ZERO));
    }

    private BigDecimal calculateServiceFee(
        BigDecimal subtotal)
    {
        return (subtotal
            .multiply(config.getServiceFeeRate())
            .setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calculatePromotionDiscount(
        Order order,
        BigDecimal subtotal)
    {
        if (order.getPromotion() == null)
            return (BigDecimal.ZERO);

        return (order.getPromotion()
            .calculateDiscount(subtotal)
            .min(subtotal)
            .setScale(2, RoundingMode.HALF_UP));
    }
}
