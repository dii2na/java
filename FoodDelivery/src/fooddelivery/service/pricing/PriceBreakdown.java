package fooddelivery.service.pricing;

import java.math.BigDecimal;

public class PriceBreakdown
{
    private final BigDecimal itemsSubtotal;
    private final BigDecimal baseDeliveryFee;
    private final BigDecimal extraDistanceFee;
    private final BigDecimal loyaltyDeliveryDiscount;
    private final BigDecimal promotionDeliveryDiscount;
    private final BigDecimal serviceFee;
    private final BigDecimal promotionOrderDiscount;
    private final BigDecimal total;

    public PriceBreakdown(
        BigDecimal itemsSubtotal,
        BigDecimal baseDeliveryFee,
        BigDecimal extraDistanceFee,
        BigDecimal loyaltyDeliveryDiscount,
        BigDecimal promotionDeliveryDiscount,
        BigDecimal serviceFee,
        BigDecimal promotionOrderDiscount,
        BigDecimal total)
    {
        this.itemsSubtotal = itemsSubtotal;
        this.baseDeliveryFee = baseDeliveryFee;
        this.extraDistanceFee = extraDistanceFee;
        this.loyaltyDeliveryDiscount = loyaltyDeliveryDiscount;
        this.promotionDeliveryDiscount = promotionDeliveryDiscount;
        this.serviceFee = serviceFee;
        this.promotionOrderDiscount = promotionOrderDiscount;
        this.total = total;
    }

    public BigDecimal getItemsSubtotal()
    {
        return (itemsSubtotal);
    }

    public BigDecimal getBaseDeliveryFee()
    {
        return (baseDeliveryFee);
    }

    public BigDecimal getExtraDistanceFee()
    {
        return (extraDistanceFee);
    }

    public BigDecimal getLoyaltyDeliveryDiscount()
    {
        return (loyaltyDeliveryDiscount);
    }

    public BigDecimal getPromotionDeliveryDiscount()
    {
        return (promotionDeliveryDiscount);
    }

    public BigDecimal getServiceFee()
    {
        return (serviceFee);
    }

    public BigDecimal getPromotionOrderDiscount()
    {
        return (promotionOrderDiscount);
    }

    public BigDecimal getTotal()
    {
        return (total);
    }

    @Override
    public String toString()
    {
        return ("PriceBreakdown{items=%s, delivery=%s, serviceFee=%s, discounts=%s, total=%s}"
            .formatted(
                itemsSubtotal,
                baseDeliveryFee.add(extraDistanceFee),
                serviceFee,
                loyaltyDeliveryDiscount
                    .add(promotionDeliveryDiscount)
                    .add(promotionOrderDiscount),
                total));
    }
}
