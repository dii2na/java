package baytalhekma.models.items;

import baytalhekma.enums.ItemCategory;
import baytalhekma.interfaces.Renewable;
import baytalhekma.utils.Validator;

public class Magazine extends LibraryItem implements Renewable
{
    // Constants

    private static final int LOAN_PERIOD = 7;
    private static final double FINE_RATE = 3.00;
    private static final double MAX_FINE = 30.00;
    private static final int RENEWAL_LIMIT = 1;

    // Fields

    private final int issueNumber;

    // Constructors

    public Magazine(int catalogueId, String title, int issueNumber)
    {
        super(catalogueId, title);
        this.issueNumber = Validator.validatePositive(issueNumber, "Issue number");
    }

    // Getters

    public int getIssueNumber()
    {
        return (issueNumber);
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
        return (Math.min(overdueDays * FINE_RATE, MAX_FINE));
    }

    @Override
    public ItemCategory getCategory()
    {
        return (ItemCategory.MAGAZINE);
    }

    @Override
    public boolean renew()
    {
        return (recordRenewal(RENEWAL_LIMIT));
    }

    @Override
    public int getRenewalLimit()
    {
        return (RENEWAL_LIMIT);
    }

    @Override
    public String toString()
    {
        return (formatItemRow("Issue " + issueNumber));
    }
}