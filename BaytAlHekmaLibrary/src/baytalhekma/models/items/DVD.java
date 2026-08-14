package baytalhekma.models.items;

import baytalhekma.enums.ItemCategory;
import baytalhekma.utils.Validator;

public class DVD extends LibraryItem
{
    // Constants

    private static final int LOAN_PERIOD = 3;
    private static final double FINE_RATE = 15.00;

    // Fields

    private final int runtime;

    // Constructors

    public DVD(int catalogueId, String title, int runtime)
    {
        super(catalogueId, title);
        this.runtime = Validator.validatePositive(runtime, "Runtime");
    }

    // Getters

    public int getRuntime()
    {
        return (runtime);
    }

    // Overrides

    @Override
    public int getLoanPeriod()
    {
        return (LOAN_PERIOD);
    }

    @Override
    public double calculateItemFine(int overdueDays)
    {
        Validator.validateNonNegative(overdueDays, "Overdue days");
        return (overdueDays * FINE_RATE);
    }

    @Override
    public ItemCategory getCategory()
    {
        return (ItemCategory.DVD);
    }

    @Override
    public String toString()
    {
        return (formatItemRow(runtime + " min"));
    }
}