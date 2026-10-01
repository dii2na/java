package fooddelivery.model.rider;

import fooddelivery.exception.BusyRiderException;
import fooddelivery.exception.FoodDeliveryException;
import fooddelivery.model.order.Order;
import fooddelivery.utils.Validator;
import java.util.Optional;

public class Rider
{
    private final String id;
    private final String name;
    private final VehicleType vehicleType;
    private String currentDistrict;
    private boolean available;
    private int completedDeliveriesCount;
    private Order activeOrder;

    public Rider(
        String id,
        String name,
        VehicleType vehicleType,
        String currentDistrict,
        boolean available)
    {
        this.id = Validator.validateString(id, "Rider ID");
        this.name = Validator.validateString(name, "Rider name");
        this.vehicleType = Validator.validateNotNull(
            vehicleType, "Vehicle type");
        this.currentDistrict = Validator.validateString(
            currentDistrict, "Current district");
        this.available = available;
        this.completedDeliveriesCount = 0;
        this.activeOrder = null;
    }

    // Reading the rider

    public String getId()
    {
        return (id);
    }

    public String getName()
    {
        return (name);
    }

    public VehicleType getVehicleType()
    {
        return (vehicleType);
    }

    public String getCurrentDistrict()
    {
        return (currentDistrict);
    }

    public Optional<Order> getActiveOrder()
    {
        return (Optional.ofNullable(activeOrder));
    }

    public boolean isAvailable()
    {
        return (available);
    }

    public int getCompletedDeliveriesCount()
    {
        return (completedDeliveriesCount);
    }

    // Duty and deliveries

    public void updateDistrict(String district)
    {
        currentDistrict = Validator.validateString(
            district, "Current district");
    }

    public void setAvailable(boolean available)
    {
        if (available && activeOrder != null)
            throw new BusyRiderException(
                "Rider cannot go back on duty while holding an order");

        this.available = available;
    }

    public void incrementCompletedDeliveries()
    {
        completedDeliveriesCount++;
    }

    public void assignOrder(Order order)
    {
        order = Validator.validateNotNull(
            order, "Order");
        if (!available || activeOrder != null)
            throw new BusyRiderException(
                "Rider is not available for a new order");
        activeOrder = order;
        available = false;
    }

    public void completeOrder(Order order)
    {
        releaseOrder(order);
        incrementCompletedDeliveries();
    }

    public void releaseOrder(Order order)
    {
        order = Validator.validateNotNull(
            order, "Order");
        if (activeOrder == null || !activeOrder.equals(order))
            throw new FoodDeliveryException(
                "Order is not the rider's active order");
        activeOrder = null;
        available = true;
    }

    // Textual representation

    @Override
    public String toString()
    {
        return ("Rider{id=%s, name=%s, vehicle=%s, district=%s, available=%s, deliveries=%d}"
            .formatted(
                id,
                name,
                vehicleType,
                currentDistrict,
                available,
                completedDeliveriesCount));
    }

    // Identity

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return (true);

        if (!(object instanceof Rider other))
            return (false);

        return (id.equals(other.id));
    }

    @Override
    public int hashCode()
    {
        return (id.hashCode());
    }
}
