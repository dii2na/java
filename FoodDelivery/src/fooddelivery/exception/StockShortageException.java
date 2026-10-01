package fooddelivery.exception;

public class StockShortageException extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public StockShortageException(String message)
    {
        super(message);
    }
}
