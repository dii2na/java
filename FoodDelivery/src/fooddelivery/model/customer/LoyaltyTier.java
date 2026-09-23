package fooddelivery.model.customer;

public enum LoyaltyTier 
{
    BRONZE(0, 9, 0.0),
    SILVER(10, 29, 0.10),
    GOLD(30, Integer.MAX_VALUE, 1.0);

    private final int minCompletedOrders;
    private final int maxCompletedOrders;
    private final double deliveryFeeDiscount;

    LoyaltyTier(int minCompletedOrders,
                int maxCompletedOrders,
                double deliveryFeeDiscount) 
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

    public double getDeliveryFeeDiscount() 
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