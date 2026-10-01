package fooddelivery.service.report;

import fooddelivery.model.customer.Customer;
import fooddelivery.model.order.Order;
import fooddelivery.model.restaurant.Restaurant;
import fooddelivery.repository.*;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class ReportService
{
    private final RevenueReportService revenueReports;
    private final OrderReportService orderReports;
    private final RestaurantReportService restaurantReports;
    private final RiderReportService riderReports;
    private final CustomerReportService customerReports;

    public ReportService(
        OrderRepository orderRepository,
        RiderRepository riderRepository,
        CustomerRepository customerRepository)
    {
        orderRepository = Validator.validateNotNull(
            orderRepository, "Order repository");
        riderRepository = Validator.validateNotNull(
            riderRepository, "Rider repository");
        customerRepository = Validator.validateNotNull(
            customerRepository, "Customer repository");

        revenueReports = new RevenueReportService(orderRepository);
        orderReports = new OrderReportService(orderRepository);
        restaurantReports =
            new RestaurantReportService(orderRepository);
        riderReports = new RiderReportService(
            orderRepository, riderRepository);
        customerReports = new CustomerReportService(
            orderRepository, customerRepository);
    }

    // Revenue

    public BigDecimal totalRevenue(
        LocalDate fromDate,
        LocalDate toDate)
    {
        return (revenueReports.totalRevenue(fromDate, toDate));
    }

    public List<RevenueReportService.RestaurantRevenue>
        topRestaurantsByRevenue(YearMonth month)
    {
        return (revenueReports.topRestaurantsByRevenue(month));
    }

    public RevenueReportService.RestaurantRevenueBreakdown revenueFor(
        Restaurant restaurant,
        LocalDate fromDate,
        LocalDate toDate)
    {
        return (revenueReports.revenueFor(
            restaurant, fromDate, toDate));
    }

    // Orders

    public List<Order> ordersFor(
        Restaurant restaurant,
        LocalDate fromDate,
        LocalDate toDate)
    {
        return (orderReports.ordersFor(
            restaurant, fromDate, toDate));
    }

    public List<OrderReportService.DistrictAverageOrderValue>
        averageOrderValueByDistrict()
    {
        return (orderReports.averageOrderValueByDistrict());
    }

    public List<OrderReportService.StatusCount> orderCountByStatus()
    {
        return (orderReports.orderCountByStatus());
    }

    public Optional<OrderReportService.OrderedMenuItem>
        mostFrequentlyOrderedMenuItem()
    {
        return (orderReports.mostFrequentlyOrderedMenuItem());
    }

    public Optional<OrderReportService.PeakOrderHour> peakOrderingHour()
    {
        return (orderReports.peakOrderingHour());
    }

    // Restaurants

    public List<RestaurantReportService.QualifiedRestaurant>
        highlyRatedRestaurants()
    {
        return (restaurantReports.highlyRatedRestaurants());
    }

    // Riders

    public List<RiderReportService.RiderDeliveryStats>
        riderDeliveryStatistics()
    {
        return (riderReports.riderDeliveryStatistics());
    }

    // Customers

    public CustomerReportService.CustomerOrderHistory customerOrderHistory(
        Customer customer)
    {
        return (customerReports.customerOrderHistory(customer));
    }

    public List<CustomerReportService.InactiveCustomer>
        customersNotOrderedRecently()
    {
        return (customerReports.customersNotOrderedRecently());
    }
}
