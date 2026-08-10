package baytalhekma.models.items;

import static utils.ConsoleUtils.*;
import baytalhekma.enums.ItemStatus;
import baytalhekma.enums.ItemCategory;
import utils.Validator;
import java.util.ArrayList;
import java.util.List;


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
    private String borrowerId;
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

    public String getBorrowerId()
    {
        return (borrowerId);
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

    public final void returnItem()
    {
        if (!isOnLoan())
            throw new IllegalStateException("Item is not currently on loan");
        markAvailable();
        borrowerName = null;
        borrowerId = null;
        renewalCount = 0;
    }

    public void lendToMember(String memberName, String memberId)
    {
        if (!isAvailable())
            throw new IllegalStateException("Item is not available for borrowing");
        this.borrowerName = Validator.validateString(memberName, "Member name", false);
        this.borrowerId = Validator.validateString(memberId, "Membership ID", false);
        changeStatus(ItemStatus.ON_LOAN);
    }

    protected boolean canRenew(int renewalLimit)
    {
        return (isOnLoan() && renewalCount < renewalLimit);
    }

    protected boolean recordRenewal(int renewalLimit)
    {
        if (!canRenew(renewalLimit))
            return (false);
        renewalCount++;
        return (true);
    }

    public double calculateItemFine(int overdueDays)
    {
        Validator.validateNonNegative(overdueDays, "Overdue days");
        return (overdueDays * fineRate);
    }

    public double calculateFine(int overdueDays)
    {
        Validator.validateNonNegative(overdueDays, "Overdue days");
        if (overdueDays == 0)
            return (0.0);
        return (ADMINISTRATIVE_CHARGE + calculateItemFine(overdueDays));
    }

    protected String formatItemRow(Object... extraValues)
    {
        List<Object> values;

        values = new ArrayList<>(List.of(
                catalogueId,
                title,
                category,
                status,
                borrowerName == null ? "None" : borrowerName,
                loanPeriod + " days",
                money(calculateFine(1))
        ));
        values.addAll(List.of(extraValues));
        return (formatRow(values.toArray()));
    }

    @Override
    public String toString()
    {
        return (formatItemRow());
    }
}