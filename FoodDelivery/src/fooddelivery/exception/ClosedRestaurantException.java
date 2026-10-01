package fooddelivery.exception;

public class ClosedRestaurantException extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public ClosedRestaurantException(String message)
    {
        super(message);
    }
}
