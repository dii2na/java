package baytalhekma.services;

import baytalhekma.models.items.LibraryItem;
import baytalhekma.models.members.Member;
import baytalhekma.enums.ItemStatus;
import baytalhekma.interfaces.Renewable;
import baytalhekma.models.results.ReturnBreakdown;
import utils.Validator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Library
{
    private static final int MAX_CATALOGUE_SIZE = 100;
    private static final int MAX_MEMBER_COUNT = 100;
    private static final int REPORT_OVERDUE_DAYS = 5;

    static
    {
        Validator.validatePositive(MAX_CATALOGUE_SIZE, "MAX_CATALOGUE_SIZE");
        Validator.validatePositive(MAX_MEMBER_COUNT, "MAX_MEMBER_COUNT");
        Validator.validatePositive(REPORT_OVERDUE_DAYS, "REPORT_OVERDUE_DAYS");
    }

    private final LibraryItem[] catalogue;
    private final Member[] members;
    private int catalogueCount;
    private int memberCount;

    public Library()
    {
        catalogue = new LibraryItem[MAX_CATALOGUE_SIZE];
        members = new Member[MAX_MEMBER_COUNT];
        catalogueCount = 0;
        memberCount = 0;
    }

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

    public void registerItem(LibraryItem item)
    {
        Validator.validateNotNull(item, "Library item");
        checkCatalogueCapacity();
        checkNewCatalogueId(item.getCatalogueId());
        catalogue[catalogueCount] = item;
        catalogueCount++;
    }

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

    public LibraryItem lendItem(int catalogueId, String membershipId)
    {
        LibraryItem item;
        Member member;

        item = Validator.validateNotNull(
                findItemById(catalogueId), "Library item");
        member = Validator.validateNotNull(
                findMemberById(membershipId), "Member");
        if (!item.isAvailable())
            throw new IllegalStateException(
                    "Item is not available for borrowing.");
        member.recordBorrowing();
        item.lendToMember(member.getName(), member.getMembershipId());
        return (item);
    }

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

        item = Validator.validateNotNull(findItemById(catalogueId), "Library item");
        if (!item.isOnLoan())
            throw new IllegalStateException("Item is not currently on loan");
        member = Validator.validateNotNull(findMemberById(item.getBorrowerId()), "Member");
        breakdown = calculateReturnBreakdown(item, overdueDays);
        member.chargeFine(breakdown.getTotalFine());
        breakdown.setNewBalance(member.getBalanceOwed());
        member.recordReturn();
        item.returnItem();
        return (breakdown);
    }

    public int renewItem(int catalogueId)
    {
        LibraryItem item;
        Renewable renewable;

        item = Validator.validateNotNull(
                findItemById(catalogueId), "Library item");
        if (!(item instanceof Renewable))
            throw new IllegalStateException(
                    "This item type cannot be renewed.");
        renewable = (Renewable) item;
        if (!renewable.renew())
            throw new IllegalStateException(
                    "Item cannot be renewed. It may not be on loan or may have reached its renewal limit.");
        return (renewable.getRenewalLimit() - item.getRenewalCount());
    }

    public int payFine(String membershipId, double amount)
    {
        Member member;

        member = Validator.validateNotNull(
                findMemberById(membershipId), "Member");
        member.payFine(amount);
        return (member.getBalanceOwed());
    }

    public String getLibraryReport()
    {
        StringBuilder report;

        report = new StringBuilder();
        report.append(sectionTitle("Library Report"));
        report.append(fieldLine("Catalogue Size", catalogueCount));
        report.append(fieldLine(
                "Items Ever Catalogued",
                LibraryItem.getCataloguedCount()));
        report.append(fieldLine(
                "Items On Loan",
                getItemsOnLoanCount()));
        report.append(fieldLine(
                "Loan Rate",
                percentage(getLoanRate())));
        report.append(fieldLine(
                "Total Outstanding",
                money(getTotalOutstanding()) + " EGP"));
        report.append(fieldLine(
                "Projected Fines (" + REPORT_OVERDUE_DAYS + " days)",
                money(calculateProjectedFines(REPORT_OVERDUE_DAYS)) + " EGP"));
        return (report.toString());
    }
}