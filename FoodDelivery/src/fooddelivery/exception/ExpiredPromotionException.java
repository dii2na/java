package fooddelivery.exception;

public class ExpiredPromotionException extends FoodDeliveryException
{
    public ExpiredPromotionException(String message)
    {
        super(message);
    }
}