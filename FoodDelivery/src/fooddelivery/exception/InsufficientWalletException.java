package fooddelivery.exception;

public class InsufficientWalletException extends FoodDeliveryException
{
    public InsufficientWalletException(String message)
    {
        super(message);
    }
}