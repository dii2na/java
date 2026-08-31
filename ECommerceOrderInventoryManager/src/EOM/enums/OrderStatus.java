public enum OrderStatus
{
    PENDING("Pending"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String label;

    OrderStatus(String label)
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