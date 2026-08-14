package baytalhekma.models.items;

import static baytalhekma.utils.ConsoleUtils.*;
import baytalhekma.enums.ItemStatus;
import baytalhekma.enums.ItemCategory;
import baytalhekma.utils.Validator;

public abstract class LibraryItem
{
    // Constants

    private static final String LIBRARY_NAME = "Bayt Al Hekma Library";
    private static final double ADMINISTRATIVE_CHARGE = 2.00;
    private static int cataloguedCount;

    // Fields

    private final int catalogueId;
    private final String title;
    private ItemStatus status;
    private String borrowerName;
    private String borrowerId;
    private int renewalCount;

    // Constructors

    public LibraryItem(int catalogueId, String title)
    {
        this.catalogueId = Validator.validatePositive(catalogueId, "Catalogue ID");
        this.title = Validator.validateString(title, "Title", false);
        this.status = ItemStatus.AVAILABLE;
        this.borrowerName = null;
        this.renewalCount = 0;
        cataloguedCount++;
    }

    // Getters

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

    // Abstract Methods

    public abstract int getLoanPeriod();

    public abstract double calculateItemFine(int overdueDays);

    public abstract ItemCategory getCategory();

    // Status Management

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

    // Borrowing and Returns

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

    // Renewal Support

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

    // Fines

    public double calculateFine(int overdueDays)
    {
        Validator.validateNonNegative(overdueDays, "Overdue days");
        if (overdueDays == 0)
            return (0.0);
        return (ADMINISTRATIVE_CHARGE + calculateItemFine(overdueDays));
    }

    // Display

    protected String formatItemRow(String details)
    {
        return (formatRow(
                catalogueId,
                getCategory(),
                title,
                status,
                borrowerName == null ? "None" : borrowerName,
                getLoanPeriod() + " days",
                money(calculateFine(1)),
                details));
    }

    @Override
    public String toString()
    {
        return (formatItemRow("—"));
    }
}