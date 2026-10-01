package fooddelivery.service.pricing;

import fooddelivery.model.order.Order;
import java.math.BigDecimal;

public interface PricingStrategy
{
    BigDecimal calculateTotal(Order order);

    PriceBreakdown quote(Order order);
}
