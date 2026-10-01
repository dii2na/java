package fooddelivery.console.area;

import fooddelivery.console.ConsoleContext;
import fooddelivery.exception.FoodDeliveryException;
import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.model.rider.Rider;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import static fooddelivery.console.ConsoleContext.RiderDeliverySummary;
import static fooddelivery.console.ConsoleFormat.*;
import static fooddelivery.utils.ConsoleUtils.*;
import static fooddelivery.utils.InputReader.*;

public class RiderArea
{
    private static final String TITLE = "MASR DELIVERY - RIDER";
    private static final int EXIT_CHOICE = 7;

    private final ConsoleContext context;

    public RiderArea(ConsoleContext context)
    {
        this.context = context;
    }

    public void start(Scanner scanner)
    {
        boolean running;

        running = true;
        println(sectionTitle(TITLE));

        while (running)
        {
            printMenuOptions();
            printSystemStatus();

            try
            {
                switch (readIntInRange(
                    scanner, "Choice", 1, EXIT_CHOICE))
                {
                    case 1 -> toggleDuty(scanner);
                    case 2 -> acceptNextReadyDelivery();
                    case 3 -> viewAssignedOrder(scanner);
                    case 4 -> markPickedUp(scanner);
                    case 5 -> markDelivered(scanner);
                    case 6 -> personalStatistics(scanner);
                    case EXIT_CHOICE -> running = false;
                }
            }
            catch (FoodDeliveryException |
                   IllegalArgumentException |
                   IllegalStateException e)
            {
                printError(e);
            }
        }
    }

    private void printMenuOptions()
    {
        printMenu(
            TITLE,
            EXIT_CHOICE,
            "Back to Main Menu",
            "Go On / Off Duty",
            "Accept Next Ready Delivery",
            "View Assigned Order",
            "Mark Order Picked Up",
            "Mark Order Delivered",
            "My Delivery Statistics");
    }

    private void printSystemStatus()
    {
        printBanner(
            "Riders", context.allRiders().size(),
            "Available",
            context.allRiders().stream()
                .filter(Rider::isAvailable)
                .count() + " riders",
            "Ready Queue",
            context.readyQueue().size() + " waiting");
    }

    // Duty and dispatch

    private Rider chooseRider(Scanner scanner)
    {
        if (context.allRiders().isEmpty())
            throw new IllegalArgumentException(
                "No riders are registered yet");

        println(sectionTitle("Riders"));
        printRiders(context.allRiders());

        return (context.findRider(
            readString(scanner, "Rider ID")));
    }

    private void toggleDuty(Scanner scanner)
    {
        Rider rider;

        rider = chooseRider(scanner);
        rider.setAvailable(!rider.isAvailable());

        printStatusBanner(
            rider.getName() + " is now "
                + (rider.isAvailable()
                    ? "ON DUTY"
                    : "OFF DUTY"));
    }

    private void acceptNextReadyDelivery()
    {
        Order dispatched;

        if (context.readyQueue().isEmpty())
            throw new IllegalArgumentException(
                "No READY orders are waiting for a rider");

        dispatched = context.assignNextRider();

        printStatusBanner(
            "Order " + dispatched.getId()
                + " assigned to "
                + dispatched.getRider()
                    .map(Rider::getName)
                    .orElse("a rider"));
    }

    // Delivery progress

    private void viewAssignedOrder(Scanner scanner)
    {
        Rider rider;
        Order order;

        rider = chooseRider(scanner);
        order = requireActiveOrder(rider);

        println(sectionTitle("Assigned Order"));
        printOrderDetails(order);
        printOrderItems(order);
        printPriceBreakdown(order, context.quote(order));
    }

    private void markPickedUp(Scanner scanner)
    {
        Rider rider;
        Order order;

        rider = chooseRider(scanner);
        order = requireActiveOrder(rider);
        requireStatus(order, OrderStatus.ASSIGNED);

        context.advanceOrder(
            order.getId(),
            OrderStatus.OUT_FOR_DELIVERY);

        printStatusBanner(
            "Order " + order.getId()
                + " is out for delivery");
    }

    private void markDelivered(Scanner scanner)
    {
        Rider rider;
        Order order;

        rider = chooseRider(scanner);
        order = requireActiveOrder(rider);
        requireStatus(order, OrderStatus.OUT_FOR_DELIVERY);

        context.advanceOrder(
            order.getId(),
            OrderStatus.DELIVERED);

        printStatusBanner(
            "Order " + order.getId() + " delivered");
        print(fieldLine(
            "Lifetime Deliveries",
            rider.getCompletedDeliveriesCount()));
    }

    // Personal statistics

    private void personalStatistics(Scanner scanner)
    {
        Rider rider;
        RiderDeliverySummary summary;
        List<Order> mine;

        rider = chooseRider(scanner);
        summary = context.riderSummary(rider);

        println(sectionTitle("Delivery Statistics"));
        printRiderSummary(rider, summary);

        mine = context.ordersFor(rider);

        if (!mine.isEmpty())
        {
            println();
            printOrders(mine);
        }
    }

    // Internal checks

    private Order requireActiveOrder(Rider rider)
    {
        Optional<Order> order;

        order = rider.getActiveOrder();
        if (order.isEmpty())
            throw new IllegalArgumentException(
                rider.getName()
                    + " has no active order");

        return (order.get());
    }

    private static void requireStatus(
        Order order,
        OrderStatus expected)
    {
        if (order.getStatus() != expected)
            throw new IllegalArgumentException(
                "Order " + order.getId() + " is "
                    + order.getStatus()
                    + ", expected " + expected);
    }
}
