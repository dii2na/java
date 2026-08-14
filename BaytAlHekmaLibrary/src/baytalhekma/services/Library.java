package baytalhekma.services;

import static baytalhekma.utils.ConsoleUtils.*;
import baytalhekma.models.items.LibraryItem;
import baytalhekma.models.members.Member;
import baytalhekma.enums.ItemStatus;
import baytalhekma.interfaces.Renewable;
import baytalhekma.models.results.ReturnBreakdown;
import baytalhekma.utils.Validator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Library
{
    // Constants

    private static final int MAX_CATALOGUE_SIZE = 100;
    private static final int MAX_MEMBER_COUNT = 100;
    private static final int REPORT_OVERDUE_DAYS = 5;

    // Fields

    private final LibraryItem[] catalogue;
    private final Member[] members;
    private int catalogueCount;
    private int memberCount;

    // Constructors

    public Library()
    {
        catalogue = new LibraryItem[MAX_CATALOGUE_SIZE];
        members = new Member[MAX_MEMBER_COUNT];
        catalogueCount = 0;
        memberCount = 0;
    }

    // Catalogue Registration

    private void checkCatalogueCapacity()
    {
        if (catalogueCount >= MAX_CATALOGUE_SIZE)
            throw new IllegalStateException("Catalogue is full. Cannot register more items.");
    }

    private void checkNewCatalogueId(int catalogueId)
    {
        if (findItemById(catalogueId) != null)
            throw new IllegalArgumentException("Catalogue ID already exists.");
    }

    public LibraryItem findItemById(int catalogueId)
    {
        for (int i = 0; i < catalogueCount; i++)
        {
            if (catalogue[i].getCatalogueId() == catalogueId)
                return (catalogue[i]);
        }
        return (null);
    }

    private LibraryItem requireItem(int catalogueId)
    {
        LibraryItem item;

        item = findItemById(catalogueId);
        if (item == null)
        {
            throw new IllegalArgumentException(
                    "No item found with catalogue ID " + catalogueId + ".");
        }
        return (item);
    }

    private Member requireMember(String membershipId)
    {
        Member member;

        member = findMemberById(membershipId);
        if (member == null)
        {
            throw new IllegalArgumentException(
                    "No member found with membership ID " + membershipId + ".");
        }
        return (member);
    }

    public void registerItem(LibraryItem item)
    {
        Validator.validateNotNull(item, "Library item");
        checkCatalogueCapacity();
        checkNewCatalogueId(item.getCatalogueId());
        catalogue[catalogueCount] = item;
        catalogueCount++;
    }

    // Member Registration

    private void checkMemberCapacity()
    {
        if (memberCount >= MAX_MEMBER_COUNT)
            throw new IllegalStateException(
                    "Member register is full. Cannot register more members.");
    }

    private void checkNewMembershipId(String membershipId)
    {
        if (findMemberById(membershipId) != null)
            throw new IllegalArgumentException("Membership ID already exists.");
    }

    public Member findMemberById(String membershipId)
    {
        for (int i = 0; i < memberCount; i++)
        {
            if (members[i].getMembershipId().equals(membershipId))
                return (members[i]);
        }
        return (null);
    }

    public void registerMember(Member member)
    {
        Validator.validateNotNull(member, "Member");
        checkMemberCapacity();
        checkNewMembershipId(member.getMembershipId());
        members[memberCount] = member;
        memberCount++;
    }

    // Listings

    public LibraryItem[] getCatalogue()
    {
        return (Arrays.copyOf(catalogue, catalogueCount));
    }

    public LibraryItem[] getItemsByStatus(ItemStatus status)
    {
        List<LibraryItem> items;

        Validator.validateNotNull(status, "Item status");
        items = new ArrayList<>();
        for (int i = 0; i < catalogueCount; i++)
        {
            if (catalogue[i].hasStatus(status))
                items.add(catalogue[i]);
        }
        return (items.toArray(new LibraryItem[0]));
    }

    public Member[] getMembers()
    {
        return (Arrays.copyOf(members, memberCount));
    }

    // Statistics

    public int getItemsOnLoanCount()
    {
        int count;

        count = 0;
        for (int i = 0; i < catalogueCount; i++)
        {
            if (catalogue[i].isOnLoan())
                count++;
        }
        return (count);
    }

    public double getLoanRate()
    {
        if (catalogueCount == 0)
            return (0.0);
        return ((double) getItemsOnLoanCount() / catalogueCount * 100);
    }

    public double getTotalOutstanding()
    {
        double total;

        total = 0.0;
        for (int i = 0; i < memberCount; i++)
            total += members[i].getBalanceOwed();

        return (total);
    }

    public double calculateProjectedFines(int overdueDays)
    {
        double total;

        Validator.validateNonNegative(overdueDays, "Overdue days");
        total = 0.0;
        for (int i = 0; i < catalogueCount; i++)
        {
            if (catalogue[i].isOnLoan())
                total += catalogue[i].calculateFine(overdueDays);
        }
        return (total);
    }

    // Borrowing

    public LibraryItem lendItem(int catalogueId, String membershipId)
    {
        LibraryItem item;
        Member member;

        item = requireItem(catalogueId);
        member = requireMember(membershipId);
        if (!item.isAvailable())
            throw new IllegalStateException(
                    "Item is not available for borrowing.");
        member.recordBorrowing();
        item.lendToMember(member.getName(), member.getMembershipId());
        return (item);
    }

    // Returns

    private ReturnBreakdown calculateReturnBreakdown(
        LibraryItem item,
        int overdueDays)
    {
        double baseFine;
        double totalFine;
        double administrativeCharge;

        baseFine = item.calculateItemFine(overdueDays);
        totalFine = item.calculateFine(overdueDays);
        administrativeCharge = totalFine - baseFine;

        return new ReturnBreakdown(
            baseFine,
            administrativeCharge,
            totalFine);
    }

    public ReturnBreakdown returnItem(int catalogueId, int overdueDays)
    {
        LibraryItem item;
        Member member;
        ReturnBreakdown breakdown;

        item = requireItem(catalogueId);
        if (!item.isOnLoan())
            throw new IllegalStateException("Item is not currently on loan");
        member = requireMember(item.getBorrowerId());
        breakdown = calculateReturnBreakdown(item, overdueDays);
        if (breakdown.getTotalFine() > 0)
            member.chargeFine(breakdown.getTotalFine());
        breakdown.setNewBalance(member.getBalanceOwed());
        member.recordReturn();
        item.returnItem();
        return (breakdown);
    }

    // Renewals

    public int renewItem(int catalogueId)
    {
        LibraryItem item;
        Renewable renewable;

        item = requireItem(catalogueId);
        if (!(item instanceof Renewable))
            throw new IllegalStateException(
                    "This item type cannot be renewed.");
        renewable = (Renewable) item;
        if (!renewable.renew())
            throw new IllegalStateException(
                    "Item cannot be renewed. It may not be on loan or may have reached its renewal limit.");
        return (renewable.getRenewalLimit() - item.getRenewalCount());
    }

    // Payments

    public double payFine(String membershipId, double amount)
    {
        Member member;

        member = requireMember(membershipId);
        member.payFine(amount);
        return (member.getBalanceOwed());
    }

    // Report

    public String getLibraryReport()
    {
        StringBuilder report;

        report = new StringBuilder();
        report.append(sectionTitle("Library Report"));
        report.append(fieldLine("Catalogue Size", catalogueCount));
        report.append(fieldLine("Items Ever Catalogued", LibraryItem.getCataloguedCount()));
        report.append(fieldLine("Items On Loan", getItemsOnLoanCount()));
        report.append(fieldLine("Loan Rate", percentage(getLoanRate())));
        report.append(fieldLine("Total Outstanding", money(getTotalOutstanding()) + " EGP"));
        report.append(fieldLine("Projected Fines (" + REPORT_OVERDUE_DAYS + " days)",
                money(calculateProjectedFines(REPORT_OVERDUE_DAYS)) + " EGP"));
        return (report.toString());
    }
}