package baytalhekma.models.items;

import static utils.ConsoleUtils.*;
import baytalhekma.enums.ItemStatus;
import baytalhekma.enums.ItemCategory;
import utils.Validator;

public abstract class LibraryItem
{

    private static final String LIBRARY_NAME = "Bayt Al Hekma Library";
    private static final double ADMINISTRATIVE_CHARGE = 2.00;
    private static int cataloguedCount;

    static
    {
        Validator.validateString(LIBRARY_NAME, "LIBRARY_NAME", false);
        Validator.validatePositive(ADMINISTRATIVE_CHARGE, "ADMINISTRATIVE_CHARGE");
    }

    private final int catalogueId;
    private final String title;
    private ItemStatus status;
    private String borrowerName;
    private int renewalCount;
    private final int loanPeriod;
    private final double fineRate;
    private final ItemCategory category;

    public LibraryItem(String title, int loanPeriod, double fineRate, ItemCategory category)
    {
        this.title = Validator.validateString(title, "Title", false);
        this.loanPeriod = Validator.validatePositive(loanPeriod, "Loan period");
        this.fineRate = Validator.validatePositive(fineRate, "Fine rate");
        this.category = Validator.validateNotNull(category, "Category");
        this.status = ItemStatus.AVAILABLE;
        this.borrowerName = null;
        this.renewalCount = 0;
        this.catalogueId = ++cataloguedCount;
    }

    public static String getLibraryName()
    {
        return (LIBRARY_NAME);
    }

    public static double getAdministrativeCharge()
    {
        return (ADMINISTRATIVE_CHARGE);
    }

    public static int getCataloguedCount()
    {
        return (cataloguedCount);
    }

    public int getCatalogueId()
    {
        return (catalogueId);
    }

    public String getTitle()
    {
        return (title);
    }

    public ItemStatus getStatus()
    {
        return (status);
    }

    public String getBorrowerName()
    {
        return (borrowerName);
    }

    public int getRenewalCount()
    {
        return (renewalCount);
    }

    public int getLoanPeriod()
    {
        return (loanPeriod);
    }

    public double getFineRate()
    {
        return (fineRate);
    }

    public ItemCategory getCategory()
    {
        return (category);
    }

    private void changeStatus(ItemStatus status)
    {
        this.status = Validator.validateNotNull(status, "Item status");
    }

    public void reserve()
    {
        changeStatus(ItemStatus.RESERVED);
    }

    public void markLost()
    {
        changeStatus(ItemStatus.LOST);
    }

    private void markAvailable()
    {
        changeStatus(ItemStatus.AVAILABLE);
    }

    public boolean hasStatus(ItemStatus expectedStatus)
    {
        return (status == expectedStatus);
    }

    public boolean isOnLoan()
    {
        return (hasStatus(ItemStatus.ON_LOAN));
    }

    public boolean isAvailable()
    {
        return (hasStatus(ItemStatus.AVAILABLE));
    }

    private void checkOnLoan()
    {
        if (!isOnLoan())
            throw new IllegalStateException("Item is not currently on loan");
    }

    public final void returnItem()
    {
        checkOnLoan();
        markAvailable();
        borrowerName = null;
        renewalCount = 0;
    }

    public void lendToMember(String memberName)
    {
        if (!isAvailable())
            throw new IllegalStateException("Item is not available for borrowing");
        this.borrowerName = Validator.validateString(memberName, "Member name", false);
        changeStatus(ItemStatus.ON_LOAN);
    }

    public void recordRenewal()
    {
        checkOnLoan();
        renewalCount++;
    }

    public double calculateItemFine(int overdueDays)
    {
        Validator.validateNonNegative(overdueDays, "Overdue days");
        return (overdueDays * fineRate);
    }

    public double calculateFine(int overdueDays)
    {
        return (ADMINISTRATIVE_CHARGE + calculateItemFine(overdueDays));
    }

    @Override
    public String toString()
    {
        return (formatRow(
                catalogueId,
                title,
                category,
                status,
                borrowerName == null ? "None" : borrowerName,
                loanPeriod + " days",
                money(calculateFine(1))));
    }
}