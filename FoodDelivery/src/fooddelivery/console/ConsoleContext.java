package fooddelivery.console;

import fooddelivery.model.customer.Address;
import fooddelivery.model.customer.Customer;
import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderItem;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.model.promotion.Promotion;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.model.rider.Rider;
import fooddelivery.repository.CustomerRepository;
import fooddelivery.repository.OrderRepository;
import fooddelivery.repository.PromotionRepository;
import fooddelivery.repository.RestaurantRepository;
import fooddelivery.repository.RiderRepository;
import fooddelivery.service.dispatch.ReadyOrderQueue;
import fooddelivery.service.dispatch.StandardRiderDispatchStrategy;
import fooddelivery.service.observer.ConsoleOrderObserver;
import fooddelivery.service.observer.OrderStatusSubject;
import fooddelivery.service.order.OrderService;
import fooddelivery.service.pricing.PriceBreakdown;
import fooddelivery.service.pricing.PricingStrategy;
import fooddelivery.service.pricing.StandardPricingStrategy;
import fooddelivery.service.report.ReportService;
import fooddelivery.service.search.RestaurantSearchService;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ConsoleContext
{
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final PromotionRepository promotionRepository;
    private final OrderService orderService;
    private final RestaurantSearchService searchService;
    private final ReportService reportService;
    private final ReadyOrderQueue readyOrderQueue;
    private final PricingStrategy pricingStrategy;

    public ConsoleContext()
    {
        OrderStatusSubject orderStatusSubject;

        customerRepository = new CustomerRepository();
        restaurantRepository = new RestaurantRepository();
        orderRepository = new OrderRepository();
        riderRepository = new RiderRepository();
        promotionRepository = new PromotionRepository();
        readyOrderQueue = new ReadyOrderQueue();
        searchService = new RestaurantSearchService();
        reportService = new ReportService(
            orderRepository,
            riderRepository,
            customerRepository);
        pricingStrategy = new StandardPricingStrategy();
        orderStatusSubject = new OrderStatusSubject();
        orderStatusSubject.addObserver(
            new ConsoleOrderObserver());
        orderService = new OrderService(
            customerRepository,
            restaurantRepository,
            orderRepository,
            riderRepository,
            new StandardRiderDispatchStrategy(),
            pricingStrategy,
            readyOrderQueue,
            orderStatusSubject);
    }

    // Lookups

    public Collection<Restaurant> allRestaurants()
    {
        return (restaurantRepository.findAll());
    }

    public Collection<Customer> allCustomers()
    {
        return (customerRepository.findAll());
    }

    public Collection<Rider> allRiders()
    {
        return (riderRepository.findAll());
    }

    public Collection<Order> allOrders()
    {
        return (orderRepository.findAll());
    }

    public Collection<Promotion> allPromotions()
    {
        return (promotionRepository.findAll());
    }

    public PriceBreakdown quote(Order order)
    {
        return (pricingStrategy.quote(order));
    }

    public RestaurantSearchService search()
    {
        return (searchService);
    }

    public ReportService reports()
    {
        return (reportService);
    }

    public ReadyOrderQueue readyQueue()
    {
        return (readyOrderQueue);
    }

    // Registration

    public void registerCustomer(Customer customer)
    {
        customerRepository.save(
            Validator.validateNotNull(
                customer, "Customer"));
    }

    public void registerRestaurant(Restaurant restaurant)
    {
        restaurantRepository.save(
            Validator.validateNotNull(
                restaurant, "Restaurant"));
    }

    public void deregisterRestaurant(Restaurant restaurant)
    {
        restaurantRepository.remove(
            Validator.validateNotNull(
                restaurant, "Restaurant").getId());
    }

    public void registerRider(Rider rider)
    {
        riderRepository.save(
            Validator.validateNotNull(
                rider, "Rider"));
    }

    public void registerPromotion(Promotion promotion)
    {
        promotionRepository.save(
            Validator.validateNotNull(
                promotion, "Promotion"));
    }

    public Customer findCustomer(String id)
    {
        return (requireFound(
            customerRepository.findById(id), "Customer", id));
    }

    public Restaurant findRestaurant(String id)
    {
        return (requireFound(
            restaurantRepository.findById(id), "Restaurant", id));
    }

    public Rider findRider(String id)
    {
        return (requireFound(
            riderRepository.findById(id), "Rider", id));
    }

    public Order findOrder(String id)
    {
        return (requireFound(
            orderRepository.findById(id), "Order", id));
    }

    public Optional<Promotion> findPromotion(String code)
    {
        return (promotionRepository.findByCode(code));
    }

    private static <T> T requireFound(
        Optional<T> found,
        String label,
        String key)
    {
        return (found.orElseThrow(() ->
            new IllegalArgumentException(
                label + " not found: " + key)));
    }

    // Operations

    public Order placeOrder(
        String orderId,
        Customer customer,
        Restaurant restaurant,
        Address address,
        BigDecimal distance,
        List<OrderItem> items,
        Promotion promotion)
    {
        return (orderService.createOrder(
            orderId,
            customer,
            restaurant,
            address,
            distance,
            items,
            promotion));
    }

    public void cancelOrder(String orderId)
    {
        orderService.cancelOrder(orderId);
    }

    public void advanceOrder(String orderId, OrderStatus next)
    {
        switch (next)
        {
            case ACCEPTED ->
                orderService.acceptOrder(orderId);
            case PREPARING ->
                orderService.startPreparing(orderId);
            case READY ->
                orderService.markOrderReady(orderId);
            case OUT_FOR_DELIVERY ->
                orderService.startDelivery(orderId);
            case DELIVERED ->
                orderService.completeDelivery(orderId);
            default ->
                throw new IllegalArgumentException(
                    "Unsupported automatic transition: " + next);
        }
    }

    public Order assignNextRider()
    {
        return (orderService.assignRider());
    }

    // Order queries

    public List<Order> ordersFor(Restaurant restaurant)
    {
        return (orderService.ordersFor(restaurant));
    }

    public List<Order> ordersFor(
        Restaurant restaurant,
        OrderStatus status)
    {
        return (orderService.ordersFor(restaurant, status));
    }

    public List<Order> ordersFor(Customer customer)
    {
        return (orderService.ordersFor(customer));
    }

    public List<Order> ordersFor(Rider rider)
    {
        return (orderService.ordersFor(rider));
    }

    public boolean orderIdExists(String orderId)
    {
        return (orderService.orderIdExists(orderId));
    }

    public Optional<Order> findOrderIn(
        Collection<Order> orders,
        String orderId)
    {
        return (orders.stream()
            .filter(order ->
                order.getId().equals(orderId))
            .findFirst());
    }

    public List<Restaurant> browseRestaurants(
        Predicate<Restaurant> condition)
    {
        return (searchService.search(
            allRestaurants(),
            condition));
    }

    public List<String> distinctCuisines()
    {
        return (searchService.findDistinctCuisines(
            allRestaurants()));
    }

    public List<Restaurant> searchFreeText(
        Customer customer,
        String text)
    {
        List<Restaurant> matches;

        matches = searchService.search(
            allRestaurants(),
            restaurant ->
                containsIgnoreCase(
                    restaurant.getDisplayName(),
                    text)
                || restaurant.getCuisines().stream()
                    .anyMatch(cuisine ->
                        containsIgnoreCase(
                            cuisine,
                            text)));

        recordSearch(customer, text);
        return (matches);
    }

    private void recordSearch(
        Customer customer,
        String description)
    {
        searchService.searchForCustomer(
            customer,
            allRestaurants(),
            restaurant -> true,
            description);
    }

    private static boolean containsIgnoreCase(
        String text,
        String search)
    {
        return (text.toLowerCase()
            .contains(search.toLowerCase()));
    }

    public RiderDeliverySummary riderSummary(Rider rider)
    {
        return (reportService.riderDeliveryStatistics()
            .stream()
            .filter(stats ->
                stats.rider().equals(rider))
            .findFirst()
            .map(stats ->
                new RiderDeliverySummary(
                    stats.completedDeliveries(),
                    stats.averageDeliveryDuration()))
            .orElseGet(() ->
                new RiderDeliverySummary(
                    0,
                    Optional.empty())));
    }

    public record RiderDeliverySummary(
        int completedDeliveries,
        Optional<Duration> averageDuration)
    {
    }
}
