package fooddelivery.model.rider;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;

public enum VehicleType
{
    BICYCLE(8, new BigDecimal("150.00"), 15),
    MOTORCYCLE(20, new BigDecimal("500.00"), 30),
    CAR(40, new BigDecimal("2000.00"), 25);

    private final int rangeKm;
    private final BigDecimal maxOrderValue;
    private final int speedKmh;

    VehicleType(int rangeKm,
                BigDecimal maxOrderValue,
                int speedKmh)
    {
        this.rangeKm = Validator.validatePositive(
            rangeKm, "Vehicle range");

        this.maxOrderValue = Validator.validatePositive(
            maxOrderValue, "Maximum order value");

        this.speedKmh = Validator.validatePositive(
            speedKmh, "Vehicle speed");
    }

    public int getRangeKm()
    {
        return (rangeKm);
    }

    public BigDecimal getMaxOrderValue()
    {
        return (maxOrderValue);
    }

    public int getSpeedKmh()
    {
        return (speedKmh);
    }

    public boolean canHandle(
        BigDecimal distance,
        BigDecimal orderTotal)
    {
        distance = Validator.validatePositive(
            distance, "Delivery distance");

        orderTotal = Validator.validateNonNegative(
            orderTotal, "Order total");

        if (distance.compareTo(
            new BigDecimal(rangeKm)) > 0)
        {
            return (false);
        }

        return (orderTotal.compareTo(maxOrderValue) <= 0);
    }
}
