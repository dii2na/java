package fooddelivery.exception;

public class ExpiredPromotionException extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public ExpiredPromotionException(String message)
    {
        super(message);
    }
}
