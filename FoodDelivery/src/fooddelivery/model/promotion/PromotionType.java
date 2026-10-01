package fooddelivery.model.promotion;

public enum PromotionType
{
    PERCENTAGE("PERCENTAGE"),
    FIXED_AMOUNT("FIXED_AMOUNT"),
    FREE_DELIVERY("FREE_DELIVERY");

    private final String label;

    PromotionType(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return (label);
    }

    @Override
    public String toString()
    {
        return (label);
    }
}
