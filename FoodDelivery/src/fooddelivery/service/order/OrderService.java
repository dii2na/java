package fooddelivery.service.order;

import fooddelivery.exception.*;
import fooddelivery.model.customer.Address;
import fooddelivery.model.customer.Customer;
import fooddelivery.model.order.*;
import fooddelivery.model.promotion.Promotion;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.restaurant.menu.MenuItem;
import fooddelivery.model.rider.Rider;
import fooddelivery.repository.*;
import fooddelivery.service.dispatch.*;
import fooddelivery.service.observer.OrderStatusSubject;
import fooddelivery.service.pricing.PricingStrategy;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService
{
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final PricingStrategy pricingStrategy;
    private final RiderRepository riderRepository;
    private final RiderDispatchStrategy riderDispatchStrategy;
    private final ReadyOrderQueue readyOrderQueue;
    private final OrderStatusSubject orderStatusSubject;

    public OrderService(
        CustomerRepository customerRepository,
        RestaurantRepository restaurantRepository,
        OrderRepository orderRepository,
        RiderRepository riderRepository,
        RiderDispatchStrategy riderDispatchStrategy,
        PricingStrategy pricingStrategy,
        ReadyOrderQueue readyOrderQueue,
        OrderStatusSubject orderStatusSubject)
    {
        this.customerRepository = Validator.validateNotNull(
            customerRepository, "Customer repository");
        this.restaurantRepository = Validator.validateNotNull(
            restaurantRepository, "Restaurant repository");
        this.orderRepository = Validator.validateNotNull(
            orderRepository, "Order repository");
        this.pricingStrategy = Validator.validateNotNull(
            pricingStrategy, "Pricing strategy");
        this.riderRepository = Validator.validateNotNull(
            riderRepository, "Rider repository");
        this.riderDispatchStrategy = Validator.validateNotNull(
            riderDispatchStrategy, "Rider dispatch strategy");
        this.readyOrderQueue = Validator.validateNotNull(
            readyOrderQueue, "Ready order queue");
        this.orderStatusSubject = Validator.validateNotNull(
            orderStatusSubject, "Order status subject");
    }

    // Internal lookups

    private Customer findCustomer(Customer customer)
    {
        customer = Validator.validateNotNull(
            customer, "Customer");

        return (customerRepository.findById(customer.getId())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Customer does not exist")));
    }

    private Restaurant findRestaurant(Restaurant restaurant)
    {
        restaurant = Validator.validateNotNull(
            restaurant, "Restaurant");

        return (restaurantRepository.findById(restaurant.getId())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Restaurant does not exist")));
    }

    private Order findOrder(String orderId)
    {
        orderId = Validator.validateString(
            orderId, "Order ID");

        return (orderRepository.findById(orderId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Order not found")));
    }

    // Checks made before an order is accepted

    private void validateOrderId(String id)
    {
        id = Validator.validateString(id, "Order ID");

        if (orderRepository.findById(id).isPresent())
            throw new IllegalArgumentException(
                "Order ID already exists");
    }

    private void validateOrderRequest(
        Customer customer,
        Restaurant restaurant,
        Address deliveryAddress,
        List<OrderItem> items)
    {
        Validator.validateNotNull(
            deliveryAddress, "Delivery address");
        Validator.validateNotNull(
            items, "Order items");

        if (items.isEmpty())
            throw new IllegalArgumentException(
                "Order must contain at least one item");

        if (!restaurant.isOpen())
            throw new ClosedRestaurantException(
                "Restaurant is currently closed");

        if (!customer.getAddresses().contains(deliveryAddress))
            throw new IllegalArgumentException(
                "Delivery address does not belong to customer");
    }

    private List<OrderItem> resolveOrderItems(
        Restaurant restaurant,
        List<OrderItem> items)
    {
        List<OrderItem> resolvedItems;
        MenuItem menuItem;

        items = Validator.validateNotNull(
            items, "Order items");

        resolvedItems = new ArrayList<>();

        for (OrderItem orderItem : items)
        {
            Validator.validateNotNull(
                orderItem, "Order item");

            menuItem = restaurant.getMenuItem(
                    orderItem.getMenuItem().getId())
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Menu item does not belong to restaurant"));

            resolvedItems.add(new OrderItem(
                menuItem,
                orderItem.getQuantity()));
        }

        return (resolvedItems);
    }

    private void validateOrderItems(List<OrderItem> items)
    {
        MenuItem menuItem;

        for (OrderItem orderItem : items)
        {
            menuItem = orderItem.getMenuItem();

            if (!menuItem.isAvailable())
                throw new UnavailableItemException(
                    "Menu item is currently unavailable: "
                        + menuItem.getName());

            menuItem.checkStock(orderItem.getQuantity());
        }
    }

    private void validatePromotion(
        Customer customer,
        Address deliveryAddress,
        Promotion promotion,
        List<OrderItem> items)
    {
        LocalDateTime currentTime;
        BigDecimal subtotal;

        if (promotion == null)
            return;

        currentTime = LocalDateTime.now();
        subtotal = Order.calculateSubtotal(items);

        if (promotion.isExpired(currentTime))
            throw new ExpiredPromotionException(
                "Promotion has expired");

        if (promotion.isApplicable(
            subtotal,
            currentTime,
            deliveryAddress.getDistrict(),
            customer.getCompletedOrderCount()))
        {
            return;
        }

        throw (nonApplicablePromotion(
            promotion,
            subtotal,
            customer,
            deliveryAddress));
    }

    private NonApplicablePromotionException nonApplicablePromotion(
        Promotion promotion,
        BigDecimal subtotal,
        Customer customer,
        Address deliveryAddress)
    {
        if (!promotion.isMinimumSubtotalMet(subtotal))
            return (new NonApplicablePromotionException(
                "Promotion requires a minimum subtotal of "
                    + promotion.getMinimumSubtotal()));

        if (!promotion.isDistrictAllowed(
            deliveryAddress.getDistrict()))
        {
            return (new NonApplicablePromotionException(
                "Promotion is not available in "
                    + deliveryAddress.getDistrict()));
        }

        return (new NonApplicablePromotionException(
            "Promotion is only available to first-time customers, "
                + "and this customer has "
                + customer.getCompletedOrderCount()
                + " completed orders"));
    }

    // Creating an order

    public Order createOrder(
        String id,
        Customer customer,
        Restaurant restaurant,
        Address deliveryAddress,
        BigDecimal deliveryDistance,
        List<OrderItem> items,
        Promotion promotion)
    {
        Order order;
        BigDecimal total;
        Customer storedCustomer;
        Restaurant storedRestaurant;
        List<OrderItem> resolvedItems;

        validateOrderId(id);
        storedCustomer = findCustomer(customer);
        storedRestaurant = findRestaurant(restaurant);
        validateOrderRequest(
            storedCustomer,
            storedRestaurant,
            deliveryAddress,
            items);
        resolvedItems = resolveOrderItems(
            storedRestaurant,
            items);
        validateOrderItems(resolvedItems);
        validatePromotion(
            storedCustomer,
            deliveryAddress,
            promotion,
            resolvedItems);
        order = Order.builder()
            .id(id)
            .customer(storedCustomer)
            .restaurant(storedRestaurant)
            .deliveryAddress(deliveryAddress)
            .deliveryDistance(deliveryDistance)
            .items(resolvedItems)
            .promotion(promotion)
            .build();
        total = pricingStrategy.calculateTotal(order);
        order.setTotal(total);
        storedCustomer.pay(total);
        reduceStock(resolvedItems);
        orderRepository.save(order);

        return (order);
    }

    // Stock

    private void reduceStock(List<OrderItem> items)
    {
        for (OrderItem orderItem : items)
        {
            orderItem.getMenuItem()
                .reduceStock(orderItem.getQuantity());
        }
    }

    private void restoreStock(List<OrderItem> items)
    {
        for (OrderItem orderItem : items)
        {
            orderItem.getMenuItem()
                .addStock(orderItem.getQuantity());
        }
    }

    // Cancelling and dispatching

    public void cancelOrder(String orderId)
    {
        OrderStatus oldStatus;
        Order order;

        order = findOrder(orderId);
        oldStatus = order.getStatus();
        order.cancel();
        if (oldStatus == OrderStatus.READY)
            readyOrderQueue.remove(order);
        restoreStock(order.getItems());
        order.getCustomer().addToWallet(
            order.getTotal());
        orderStatusSubject.notifyObservers(
            order,
            oldStatus);
    }

    public Order assignRider()
    {
        Order order;
        Rider rider;

        order = readyOrderQueue.peek();

        if (order == null)
            throw new FoodDeliveryException(
                "No READY orders waiting for a rider");
        rider = riderDispatchStrategy
            .findRider(
                order,
                riderRepository.findAll())
            .orElseThrow(() ->
                new BusyRiderException(
                    "No available rider found"));
        order.assignRider(rider);
        changeOrderStatus(
            order,
            OrderStatus.ASSIGNED);
        readyOrderQueue.poll();

        return (order);
    }

    // Moving an order to its next status

    private Order changeOrderStatus(
        String orderId,
        OrderStatus newStatus)
    {
        Order order;

        order = findOrder(orderId);
        changeOrderStatus(order, newStatus);

        return (order);
    }

    private void changeOrderStatus(
        Order order,
        OrderStatus newStatus)
    {
        OrderStatus oldStatus;

        order = Validator.validateNotNull(
            order, "Order");
        newStatus = Validator.validateNotNull(
            newStatus, "Order status");
        oldStatus = order.getStatus();
        order.changeStatus(newStatus);
        orderStatusSubject.notifyObservers(
            order,
            oldStatus);
    }

    public void acceptOrder(String orderId)
    {
        changeOrderStatus(
            orderId,
            OrderStatus.ACCEPTED);
    }

    public void startPreparing(String orderId)
    {
        changeOrderStatus(
            orderId,
            OrderStatus.PREPARING);
    }

    public void markOrderReady(String orderId)
    {
        Order order;

        order = changeOrderStatus(
            orderId,
            OrderStatus.READY);
        readyOrderQueue.add(order);
    }

    public void startDelivery(String orderId)
    {
        Order order;

        order = findOrder(orderId);

        if (!order.getRider().isPresent())
            throw new FoodDeliveryException(
                "Order cannot start delivery without a rider");
        changeOrderStatus(
            order,
            OrderStatus.OUT_FOR_DELIVERY);
    }

    public void completeDelivery(String orderId)
    {
        Order order;
        Rider rider;
        OrderStatus oldStatus;

        order = findOrder(orderId);
        rider = order.getRider()
            .orElseThrow(() ->
                new FoodDeliveryException(
                    "Order does not have a rider"));
        oldStatus = order.getStatus();
        order.changeStatus(OrderStatus.DELIVERED);
        rider.completeOrder(order);
        order.getCustomer()
            .incrementCompletedOrderCount();
        orderStatusSubject.notifyObservers(
            order,
            oldStatus);
    }

    // Queries

    public List<Order> ordersFor(Restaurant restaurant)
    {
        return (OrderQueries.ordersOfRestaurant(
            orderRepository.findAll(),
            restaurant));
    }

    public List<Order> ordersFor(
        Restaurant restaurant,
        OrderStatus status)
    {
        return (OrderQueries.ordersOfRestaurantAtStatus(
            orderRepository.findAll(),
            restaurant,
            status));
    }

    public List<Order> ordersFor(Customer customer)
    {
        return (OrderQueries.ordersOfCustomer(
            orderRepository.findAll(),
            customer));
    }

    public List<Order> ordersFor(Rider rider)
    {
        return (OrderQueries.ordersOfRider(
            orderRepository.findAll(),
            rider));
    }

    public boolean orderIdExists(String orderId)
    {
        return (orderRepository.exists(orderId));
    }
}
