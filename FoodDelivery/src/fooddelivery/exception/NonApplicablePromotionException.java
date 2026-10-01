package fooddelivery.exception;

public class NonApplicablePromotionException
    extends FoodDeliveryException
{
    private static final long serialVersionUID = 1L;

    public NonApplicablePromotionException(String message)
    {
        super(message);
    }
}
