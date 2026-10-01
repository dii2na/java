package fooddelivery.model.order;

import fooddelivery.exception.IllegalOrderTransitionException;
import fooddelivery.model.customer.Address;
import fooddelivery.model.customer.Customer;
import fooddelivery.model.promotion.Promotion;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.rider.Rider;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Order
{
    private final String id;
    private final Customer customer;
    private final Restaurant restaurant;
    private final Address deliveryAddress;
    private final BigDecimal deliveryDistance;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime placedAt;
    private LocalDateTime outForDeliveryAt;
    private LocalDateTime deliveredAt;
    private final Promotion promotion;
    private Rider rider;
    private BigDecimal total;

    private Order(Builder builder)
    {
        this.id = builder.id;
        this.customer = builder.customer;
        this.restaurant = builder.restaurant;
        this.deliveryAddress = builder.deliveryAddress;
        this.deliveryDistance = builder.deliveryDistance;
        this.items = new ArrayList<>(builder.items);
        this.promotion = builder.promotion;
        this.status = OrderStatus.PLACED;
        this.placedAt = LocalDateTime.now();
        this.outForDeliveryAt = null;
        this.deliveredAt = null;
        this.rider = null;
        this.total = BigDecimal.ZERO.setScale(2);
    }

    // Building an order

    public static class Builder
    {
        private String id;
        private Customer customer;
        private Restaurant restaurant;
        private Address deliveryAddress;
        private BigDecimal deliveryDistance;
        private final List<OrderItem> items = new ArrayList<>();
        private Promotion promotion;

        private void validateRequiredFields()
        {
            Validator.validateString(
                id, "Order ID");
            Validator.validateNotNull(
                customer, "Customer");
            Validator.validateNotNull(
                restaurant, "Restaurant");
            Validator.validateNotNull(
                deliveryAddress, "Delivery address");
            Validator.validatePositive(
                deliveryDistance, "Delivery distance");

            if (items.isEmpty())
                throw new IllegalArgumentException(
                    "Order must contain at least one item");
        }

        public Builder id(String id)
        {
            this.id = id;
            return (this);
        }

        public Builder customer(Customer customer)
        {
            this.customer = customer;
            return (this);
        }

        public Builder restaurant(Restaurant restaurant)
        {
            this.restaurant = restaurant;
            return (this);
        }

        public Builder deliveryAddress(Address deliveryAddress)
        {
            this.deliveryAddress = deliveryAddress;
            return (this);
        }

        public Builder deliveryDistance(
            BigDecimal deliveryDistance)
        {
            this.deliveryDistance = deliveryDistance;
            return (this);
        }

        public Builder addItem(OrderItem item)
        {
            item = Validator.validateNotNull(
                item, "Order item");

            this.items.add(item);
            return (this);
        }

        public Builder items(List<OrderItem> items)
        {
            items = Validator.validateNotNull(
                items, "Order items");

            this.items.clear();
            for (OrderItem item : items)
                addItem(item);
            return (this);
        }

        public Builder promotion(Promotion promotion)
        {
            this.promotion = promotion;
            return (this);
        }

        public Order build()
        {
            validateRequiredFields();

            return (new Order(this));
        }
    }

    public static Builder builder()
    {
        return (new Builder());
    }

    // Reading the order

    public String getId()
    {
        return (id);
    }

    public Customer getCustomer()
    {
        return (customer);
    }

    public Restaurant getRestaurant()
    {
        return (restaurant);
    }

    public Address getDeliveryAddress()
    {
        return (deliveryAddress);
    }

    public BigDecimal getDeliveryDistance()
    {
        return (deliveryDistance);
    }

    public List<OrderItem> getItems()
    {
        return (Collections.unmodifiableList(items));
    }

    public OrderStatus getStatus()
    {
        return (status);
    }

    public LocalDateTime getPlacedAt()
    {
        return (placedAt);
    }

    public Optional<LocalDateTime> getOutForDeliveryAt()
    {
        return (Optional.ofNullable(outForDeliveryAt));
    }

    public Optional<LocalDateTime> getDeliveredAt()
    {
        return (Optional.ofNullable(deliveredAt));
    }

    public Promotion getPromotion()
    {
        return (promotion);
    }

    public Optional<Rider> getRider()
    {
        return (Optional.ofNullable(rider));
    }

    // Changing the order

    private boolean canCancel()
    {
        return (status != OrderStatus.OUT_FOR_DELIVERY
            && status != OrderStatus.DELIVERED
            && status != OrderStatus.CANCELLED);
    }

    public void cancel()
    {
        if (!canCancel())
            throw new IllegalOrderTransitionException(
                "Order cannot be cancelled at this stage");

        if (rider != null)
        {
            rider.releaseOrder(this);
            rider = null;
        }
        status = OrderStatus.CANCELLED;
    }

    public void changeStatus(OrderStatus newStatus)
    {
        Optional<OrderStatus> nextStatus;

        newStatus = Validator.validateNotNull(
            newStatus, "Order status");

        if (newStatus == OrderStatus.CANCELLED)
        {
            cancel();
            return;
        }

        nextStatus = status.next();

        if (!nextStatus.isPresent()
            || nextStatus.get() != newStatus)
        {
            throw new IllegalOrderTransitionException(
                "Invalid status transition from "
                    + status + " to " + newStatus);
        }
        if (newStatus == OrderStatus.OUT_FOR_DELIVERY)
            outForDeliveryAt = LocalDateTime.now();

        if (newStatus == OrderStatus.DELIVERED)
            deliveredAt = LocalDateTime.now();

        status = newStatus;
    }

    public void assignRider(Rider rider)
    {
        rider = Validator.validateNotNull(
            rider, "Rider");

        if (status != OrderStatus.READY)
            throw new IllegalOrderTransitionException(
                "Only a READY order can be assigned to a rider");

        rider.assignOrder(this);
        this.rider = rider;
    }

    // Totals

    public static BigDecimal calculateSubtotal(
        List<OrderItem> orderItems)
    {
        orderItems = Validator.validateNotNull(
            orderItems, "Order items");

        return (orderItems.stream()
            .map(OrderItem::calculateTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public BigDecimal calculateSubtotal()
    {
        return (calculateSubtotal(items));
    }

    public void setTotal(BigDecimal total)
    {
        this.total = Validator.validateNonNegative(
            total, "Order total");
    }

    public BigDecimal getTotal()
    {
        return (total);
    }

    // Textual representation

    @Override
    public String toString()
    {
        return ("Order{id=%s, status=%s, customer=%s, restaurant=%s, items=%d, total=%s}"
            .formatted(
                id,
                status,
                customer.getName(),
                restaurant.getDisplayName(),
                items.size(),
                total));
    }

    // Identity

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return (true);

        if (!(object instanceof Order other))
            return (false);

        return (id.equals(other.id));
    }

    @Override
    public int hashCode()
    {
        return (id.hashCode());
    }
}
