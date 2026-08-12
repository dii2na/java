package baytalhekma.models.members;

import static utils.ConsoleUtils.*;
import utils.Validator;

public class Member
{
    private String name;
    private final String membershipId;
    private double balanceOwed;
    private int itemsHeld;

    public Member(String name, String membershipId)
    {
        this(name, membershipId, 0.0, 0);
    }

    public Member(String name, String membershipId,
            double balanceOwed, int itemsHeld)
    {
        this.name = Validator.validateLetters(name, "Name", 2, 50);
        this.membershipId = Validator.validateAlphanumeric(
                membershipId, "Membership ID", 4, 4);
        this.balanceOwed = Validator.validateNonNegative(
                balanceOwed, "Balance owed");
        this.itemsHeld = Validator.validateInRange(
                itemsHeld, 0, 3, "Items held");
    }

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

    public boolean canBorrow()
    {
        return (itemsHeld < 3 && balanceOwed <= 100.0);
    }

    public void recordBorrowing()
    {
        if (!canBorrow())
        {
            throw new IllegalStateException(
                    "Member cannot borrow. Items held: %d/3, balance owed: %.2f EGP."
                            .formatted(itemsHeld, balanceOwed));
        }
        itemsHeld++;
    }

    public void recordReturn()
    {
        if (itemsHeld <= 0)
        {
            throw new IllegalStateException(
                    "Cannot record return. Items held: %d/3.".formatted(itemsHeld));
        }
        itemsHeld--;
    }

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append("  Member%n");
        info.append(separator());
        info.append(fieldLine("Name", name));
        info.append(fieldLine("Membership ID", membershipId));
        info.append(fieldLine("Items Held", itemsHeld));
        info.append(fieldLine("Balance Owed", money(balanceOwed) + " EGP"));
        return (info.toString());
    }
}

