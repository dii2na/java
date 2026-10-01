package fooddelivery.exception;

public class FoodDeliveryException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public FoodDeliveryException(String message)
    {
        super(message);
    }
}
