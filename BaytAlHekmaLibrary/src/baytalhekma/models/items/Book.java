package baytalhekma.models.items;

import baytalhekma.enums.ItemCategory;
import baytalhekma.interfaces.Renewable;
import utils.Validator;

public class Book extends LibraryItem implements Renewable
{
    private static final int LOAN_PERIOD = 14;
    private static final double FINE_RATE = 5.00;
    private static final int RENEWAL_LIMIT = 2;

    static
    {
        Validator.validatePositive(LOAN_PERIOD, "LOAN_PERIOD");
        Validator.validatePositive(FINE_RATE, "FINE_RATE");
        Validator.validatePositive(RENEWAL_LIMIT, "RENEWAL_LIMIT");
    }

    private final String author;
    private final int pageCount;

    public Book(String title, String author, int pageCount)
    {
        super(title, LOAN_PERIOD, FINE_RATE, ItemCategory.BOOK);
        this.author = Validator.validateString(author, "Author", false);
        this.pageCount = Validator.validatePositive(pageCount, "Page count");
    }

    public String getAuthor()
    {
        return (author);
    }

    public int getPageCount()
    {
        return (pageCount);
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
        return (formatItemRow(author, pageCount));
    }
}