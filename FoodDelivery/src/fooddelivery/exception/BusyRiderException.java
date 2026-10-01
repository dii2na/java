package fooddelivery.exception;

public class BusyRiderException extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public BusyRiderException(String message)
    {
        super(message);
    }
}
