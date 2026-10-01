package fooddelivery.service.dispatch;

import fooddelivery.model.customer.LoyaltyTier;
import fooddelivery.model.order.Order;
import fooddelivery.model.order.OrderStatus;
import fooddelivery.utils.Validator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class ReadyOrderQueue
{
    private final Queue<Entry> entries;
    private final Map<Order, Entry> entriesByOrder;

    public ReadyOrderQueue()
    {
        Comparator<Entry> order;

        order = Comparator
            .comparingInt(Entry::getGoldRank)
            .thenComparing(
                entry -> entry.getOrder().getPlacedAt())
            .thenComparing(
                entry -> entry.getOrder().getId());
        entries = new PriorityQueue<>(order);
        entriesByOrder = new HashMap<>();
    }

    public void add(Order order)
    {
        Entry entry;

        order = Validator.validateNotNull(order, "Order");
        if (order.getStatus() != OrderStatus.READY)
            throw new IllegalArgumentException(
                "Only READY orders can be added to the queue");
        if (order.getRider().isPresent())
            throw new IllegalArgumentException(
                "Order already has a rider");
        if (entriesByOrder.containsKey(order))
            throw new IllegalArgumentException(
                "Order is already waiting in the queue");
        entry = new Entry(order, goldRank(order));
        entries.offer(entry);
        entriesByOrder.put(order, entry);
    }

    public Order poll()
    {
        Entry entry;

        entry = entries.poll();
        if (entry == null)
            return (null);
        entriesByOrder.remove(entry.getOrder());
        return (entry.getOrder());
    }

    public boolean remove(Order order)
    {
        Entry entry;

        order = Validator.validateNotNull(
            order, "Order");
        entry = entriesByOrder.remove(order);
        if (entry == null)
            return (false);

        return (entries.remove(entry));
    }

    public Order peek()
    {
        Entry entry;

        entry = entries.peek();
        if (entry == null)
            return (null);

        return (entry.getOrder());
    }

    public boolean isEmpty()
    {
        return (entries.isEmpty());
    }

    public int size()
    {
        return (entries.size());
    }

    private static int goldRank(Order order)
    {
        if (order.getCustomer().getLoyaltyTier()
                == LoyaltyTier.GOLD)
            return (0);
        return (1);
    }

    private static final class Entry
    {
        private final Order order;
        private final int goldRank;

        Entry(Order order, int goldRank)
        {
            this.order = order;
            this.goldRank = goldRank;
        }

        Order getOrder()
        {
            return (order);
        }

        int getGoldRank()
        {
            return (goldRank);
        }
    }
}
