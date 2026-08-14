package baytalhekma.models.members;

import static baytalhekma.utils.ConsoleUtils.*;
import baytalhekma.utils.Validator;

public class Member
{
    // Constants

    public static final int MEMBERSHIP_ID_LENGTH = 4;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_ITEMS = 3;
    private static final double MAX_BALANCE = 100.0;

    // Fields

    private String name;
    private final String membershipId;
    private double balanceOwed;
    private int itemsHeld;

    // Constructors

    public Member(String name, String membershipId)
    {
        this(name, membershipId, 0.0, 0);
    }

    // Member carried over from the old card index

    public Member(String name, String membershipId,
            double balanceOwed, int itemsHeld)
    {
        this.name = Validator.validateLetters(
                name, "Name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        this.membershipId = Validator.validateAlphanumeric(
                membershipId, "Membership ID",
                MEMBERSHIP_ID_LENGTH, MEMBERSHIP_ID_LENGTH);
        this.balanceOwed = Validator.validateNonNegative(
                balanceOwed, "Balance owed");
        this.itemsHeld = Validator.validateInRange(
                itemsHeld, 0, MAX_ITEMS, "Items held");
    }

    // Getters and Setters

    public String getName()
    {
        return (name);
    }

    public String getMembershipId()
    {
        return (membershipId);
    }

    public double getBalanceOwed()
    {
        return (balanceOwed);
    }

    public int getItemsHeld()
    {
        return (itemsHeld);
    }

    public void setName(String name)
    {
        this.name = Validator.validateString(name, "Name", false);
    }

    // Fees and Payments

    public void chargeFine(double amount)
    {
        amount = Validator.validatePositive(amount, "Fine amount");
        balanceOwed += amount;
    }

    public void payFine(double amount)
    {
        amount = Validator.validatePositive(amount, "Payment amount");
        if (amount > balanceOwed)
            throw new IllegalArgumentException(
                    "Payment amount cannot exceed balance owed.");
        balanceOwed -= amount;
    }

    // Borrowing and Returns

    public boolean canBorrow()
    {
        return (itemsHeld < MAX_ITEMS && balanceOwed <= MAX_BALANCE);
    }

    public void recordBorrowing()
    {
        if (!canBorrow())
        {
            throw new IllegalStateException(
                    "Member cannot borrow. Items held: %d/%d, balance owed: %.2f EGP."
                            .formatted(itemsHeld, MAX_ITEMS, balanceOwed));
        }
        itemsHeld++;
    }

    public void recordReturn()
    {
        if (itemsHeld <= 0)
        {
            throw new IllegalStateException(
                    "Cannot record return. Items held: %d/%d."
                            .formatted(itemsHeld, MAX_ITEMS));
        }
        itemsHeld--;
    }

    // Display

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append("  Member").append(newLine());
        info.append(separator());
        info.append(fieldLine("Name", name));
        info.append(fieldLine("Membership ID", membershipId));
        info.append(fieldLine("Items Held", itemsHeld));
        info.append(fieldLine("Balance Owed", money(balanceOwed) + " EGP"));
        return (info.toString());
    }
}

