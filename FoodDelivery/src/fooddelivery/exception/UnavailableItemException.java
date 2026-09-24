package fooddelivery.exception;

public class UnavailableItemException extends FoodDeliveryException
{
    public UnavailableItemException(String message)
    {
        super(message);
    }
}