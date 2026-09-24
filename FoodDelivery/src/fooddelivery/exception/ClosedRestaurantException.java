package fooddelivery.exception;

public class ClosedRestaurantException extends FoodDeliveryException
{
    public ClosedRestaurantException(String message)
    {
        super(message);
    }
}