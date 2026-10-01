package fooddelivery.exception;

public class UnavailableItemException extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public UnavailableItemException(String message)
    {
        super(message);
    }
}
