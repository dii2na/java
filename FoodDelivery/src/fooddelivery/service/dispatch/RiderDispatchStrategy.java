package fooddelivery.service.dispatch;

import fooddelivery.model.order.Order;
import fooddelivery.model.rider.Rider;
import java.util.Collection;
import java.util.Optional;

public interface RiderDispatchStrategy
{
    Optional<Rider> findRider(
        Order order,
        Collection<Rider> riders);
}
