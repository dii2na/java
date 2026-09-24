package fooddelivery.exception;

public class StockShortageException extends FoodDeliveryException
{
    public StockShortageException(String message)
    {
        super(message);
    }
}