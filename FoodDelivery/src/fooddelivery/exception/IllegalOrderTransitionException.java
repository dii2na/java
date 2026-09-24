package fooddelivery.exception;

public class IllegalOrderTransitionException extends FoodDeliveryException
{
    public IllegalOrderTransitionException(String message)
    {
        super(message);
    }
}