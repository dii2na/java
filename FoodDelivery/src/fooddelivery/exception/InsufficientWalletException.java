package fooddelivery.exception;

public class InsufficientWalletException extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public InsufficientWalletException(String message)
    {
        super(message);
    }
}
