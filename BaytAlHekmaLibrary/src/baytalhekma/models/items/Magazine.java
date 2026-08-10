package baytalhekma.models.items;

import baytalhekma.enums.ItemCategory;
import baytalhekma.interfaces.Renewable;
import utils.Validator;

public class Magazine extends LibraryItem implements Renewable
{
    private static final int LOAN_PERIOD = 7;
    private static final double FINE_RATE = 3.00;
    private static final double MAX_FINE = 30.00;
    private static final int RENEWAL_LIMIT = 1;

    static
    {
        Validator.validatePositive(LOAN_PERIOD, "LOAN_PERIOD");
        Validator.validatePositive(FINE_RATE, "FINE_RATE");
        Validator.validatePositive(MAX_FINE, "MAX_FINE");
        Validator.validatePositive(RENEWAL_LIMIT, "RENEWAL_LIMIT");
    }

    private final int issueNumber;

    public Magazine(String title, int issueNumber)
    {
        super(title, LOAN_PERIOD, FINE_RATE, ItemCategory.MAGAZINE);
        this.issueNumber = Validator.validatePositive(issueNumber, "Issue number");
    }

    public int getIssueNumber()
    {
        return (issueNumber);
    }

    @Override
    public double calculateItemFine(int overdueDays)
    {
        Validator.validateNonNegative(overdueDays, "Overdue days");
        return (Math.min(overdueDays * FINE_RATE, MAX_FINE));
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
        return (formatItemRow(issueNumber));
    }
}