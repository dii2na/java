package baytalhekma.models.items;

import baytalhekma.enums.ItemCategory;
import baytalhekma.interfaces.Renewable;
import baytalhekma.utils.Validator;

public class Book extends LibraryItem implements Renewable
{
    // Constants

    private static final int LOAN_PERIOD = 14;
    private static final double FINE_RATE = 5.00;
    private static final int RENEWAL_LIMIT = 2;

    // Fields

    private final String author;
    private final int pageCount;

    // Constructors

    public Book(int catalogueId, String title, String author, int pageCount)
    {
        super(catalogueId, title);
        this.author = Validator.validateString(author, "Author", false);
        this.pageCount = Validator.validatePositive(pageCount, "Page count");
    }

    // Getters

    public String getAuthor()
    {
        return (author);
    }

    public int getPageCount()
    {
        return (pageCount);
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
        return (ItemCategory.BOOK);
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
        return (formatItemRow(author + ", " + pageCount + " pages"));
    }
}