package fooddelivery.model.order;

import java.util.Optional;

public enum OrderStatus
{
    PLACED,
    ACCEPTED,
    PREPARING,
    READY,
    ASSIGNED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    public Optional<OrderStatus> next()
    {
        switch (this)
        {
            case PLACED:
                return (Optional.of(ACCEPTED));
            case ACCEPTED:
                return (Optional.of(PREPARING));
            case PREPARING:
                return (Optional.of(READY));
            case READY:
                return (Optional.of(ASSIGNED));
            case ASSIGNED:
                return (Optional.of(OUT_FOR_DELIVERY));
            case OUT_FOR_DELIVERY:
                return (Optional.of(DELIVERED));
            default:
                return (Optional.empty());
        }
    }
}