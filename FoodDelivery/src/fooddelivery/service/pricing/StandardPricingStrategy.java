package fooddelivery.service;

import fooddelivery.model.order.Order;
import fooddelivery.utils.Validator;
import fooddelivery.model.customer.LoyaltyTier;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StandardPricingStrategy
    implements PricingStrategy
{
    @Override
    public BigDecimal calculateTotal(Order order)
    {
        BigDecimal subtotal;
        BigDecimal deliveryFee;
        BigDecimal serviceFee;
        BigDecimal promotionDiscount;
        BigDecimal total;
        LoyaltyTier loyaltyTier;

        order = Validator.validateNotNull(
            order, "Order");
        subtotal = order.calculateSubtotal();
        deliveryFee = calculateDeliveryFee(order);
        loyaltyTier = order.getCustomer().getLoyaltyTier();
        deliveryFee = applyLoyaltyDiscount(
            deliveryFee, loyaltyTier);
        serviceFee = calculateServiceFee(subtotal);
        promotionDiscount = calculatePromotionDiscount(
            order, subtotal);

        total = subtotal
            .add(deliveryFee)
            .add(serviceFee)
            .subtract(promotionDiscount);

        return (total.max(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateDeliveryFee(Order order)
    {
        BigDecimal baseFee;
        BigDecimal feePerExtraKilometer;
        BigDecimal includedDistance;
        BigDecimal extraDistance;

        baseFee = new BigDecimal("15");
        feePerExtraKilometer = new BigDecimal("3");
        includedDistance = new BigDecimal("3");

        extraDistance = order.getDeliveryDistance()
            .subtract(includedDistance);

        if (extraDistance.compareTo(BigDecimal.ZERO) <= 0)
            return (baseFee);

        return (baseFee.add(
            extraDistance.multiply(feePerExtraKilometer)));
    }

    private BigDecimal applyLoyaltyDiscount(
        BigDecimal deliveryFee,
        LoyaltyTier loyaltyTier)
    {
        BigDecimal discountRate;

        discountRate = loyaltyTier.getDeliveryFeeDiscount();

        return (deliveryFee.multiply(
            BigDecimal.ONE.subtract(discountRate)));
    }

    private BigDecimal calculateServiceFee(
        BigDecimal subtotal)
    {
        BigDecimal serviceFeeRate;

        serviceFeeRate = new BigDecimal("0.10");

        return (subtotal.multiply(serviceFeeRate)
            .setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calculatePromotionDiscount(
        Order order,
        BigDecimal subtotal)
    {
        if (order.getPromotion() == null)
            return (BigDecimal.ZERO);

        return (order.getPromotion()
            .calculateDiscount(subtotal));
    }
}