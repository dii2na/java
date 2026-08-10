package baytalhekma.models.items;

import baytalhekma.enums.ItemCategory;
import utils.Validator;

public class DVD extends LibraryItem
{
    private static final int LOAN_PERIOD = 3;
    private static final double FINE_RATE = 15.00;

    static
    {
        Validator.validatePositive(LOAN_PERIOD, "LOAN_PERIOD");
        Validator.validatePositive(FINE_RATE, "FINE_RATE");
    }

    private final int runtime;

    public DVD(String title, int runtime)
    {
        super(title, LOAN_PERIOD, FINE_RATE, ItemCategory.DVD);
        this.runtime = Validator.validatePositive(runtime, "Runtime");
    }

    public int getRuntime()
    {
        return (runtime);
    }

    @Override
    public String toString()
    {
        return (formatItemRow(runtime));
    }
}