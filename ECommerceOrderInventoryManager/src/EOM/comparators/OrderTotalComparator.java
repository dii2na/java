package EOM.comparators;

import EOM.models.Order;
import java.util.Comparator;

public class OrderTotalComparator implements Comparator<Order>
{
    @Override
    public int compare(Order first, Order second)
    {
        return (Double.compare(first.getTotal(), second.getTotal()));
    }
}