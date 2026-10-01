package fooddelivery.service.dispatch;

import fooddelivery.model.order.Order;
import fooddelivery.model.rider.Rider;
import fooddelivery.utils.Validator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class StandardRiderDispatchStrategy
    implements RiderDispatchStrategy
{
    private static final Comparator<Rider> DELIVERY_PRIORITY =
        Comparator
            .comparingInt(
                (Rider rider) ->
                    rider.getVehicleType().getSpeedKmh())
            .thenComparing(Rider::getId);

    @Override
    public Optional<Rider> findRider(
        Order order,
        Collection<Rider> riders)
    {
        Order pendingOrder = Validator.validateNotNull(
            order, "Order");
        Collection<Rider> availableRiders = Validator.validateNotNull(
            riders, "Riders");

        return (availableRiders.stream()
            .filter(Rider::isAvailable)
            .filter(rider -> rider.getVehicleType().canHandle(
                pendingOrder.getDeliveryDistance(),
                pendingOrder.getTotal()))
            .min(DELIVERY_PRIORITY));
    }
}
