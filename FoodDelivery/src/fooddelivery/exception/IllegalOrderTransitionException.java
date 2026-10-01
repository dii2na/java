package fooddelivery.exception;

public class IllegalOrderTransitionException extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public IllegalOrderTransitionException(String message)
    {
        super(message);
    }
}
