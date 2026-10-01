package fooddelivery.config;

import java.math.BigDecimal;

public class PlatformConfig
{
    private static final PlatformConfig INSTANCE =
        new PlatformConfig();

    private final BigDecimal baseDeliveryFee;
    private final BigDecimal includedDistance;
    private final BigDecimal extraKmFee;
    private final BigDecimal serviceFeeRate;

    private PlatformConfig()
    {
        baseDeliveryFee = new BigDecimal("15.00");
        includedDistance = new BigDecimal("3.00");
        extraKmFee = new BigDecimal("3.00");
        serviceFeeRate = new BigDecimal("0.10");
    }

    public static PlatformConfig getInstance()
    {
        return (INSTANCE);
    }

    public BigDecimal getBaseDeliveryFee()
    {
        return (baseDeliveryFee);
    }

    public BigDecimal getIncludedDistance()
    {
        return (includedDistance);
    }

    public BigDecimal getExtraKmFee()
    {
        return (extraKmFee);
    }

    public BigDecimal getServiceFeeRate()
    {
        return (serviceFeeRate);
    }
}
