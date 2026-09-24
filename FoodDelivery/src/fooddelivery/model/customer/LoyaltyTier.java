package fooddelivery.model.customer;

import java.math.BigDecimal;

public enum LoyaltyTier 
{
    BRONZE(0, 9, BigDecimal.ZERO),
    SILVER(10, 29, new BigDecimal("0.10")),
    GOLD(30, Integer.MAX_VALUE, BigDecimal.ONE);

    private final int minCompletedOrders;
    private final int maxCompletedOrders;
    private final BigDecimal deliveryFeeDiscount;

    LoyaltyTier(int minCompletedOrders,
                int maxCompletedOrders,
                BigDecimal deliveryFeeDiscount) 
    {
        this.minCompletedOrders = minCompletedOrders;
        this.maxCompletedOrders = maxCompletedOrders;
        this.deliveryFeeDiscount = deliveryFeeDiscount;
    }

    public int getMinCompletedOrders()
    {
        return (minCompletedOrders);
    }

    public int getMaxCompletedOrders()
    {
        return (maxCompletedOrders);
    }

    public BigDecimal getDeliveryFeeDiscount() 
    {
        return (deliveryFeeDiscount);
    }

    public static LoyaltyTier fromCompletedOrderCount(int count)
    {
        for (LoyaltyTier tier : values())
        {
            if (count >= tier.getMinCompletedOrders()
                && count <= tier.getMaxCompletedOrders())
            {
                return (tier);
            }
        }

        throw new IllegalStateException("No loyalty tier found");
    }
}