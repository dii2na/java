package baytalhekma.models.members;

import baytalhekma.enums.MembershipType;

public class Member {
    private String name;
    private final String membershipId;
    private final MembershipType membershipType;
    private double balanceOwed;
    private int itemsHeld;

    public Member(String name, String membershipId, MembershipType membershipType)
    {
        this(name, membershipId, membershipType, 0.0, 0);
    }

    public Member(String name, String membershipId, MembershipType membershipType,
            double balanceOwed, int itemsHeld)
    {
        this.name = Validator.validateString(name, "Name", false);
        this.membershipId = Validator.validateAlphanumeric(
                membershipId, "Membership ID", 4, 4);
        this.membershipType = Validator.validateNotNull(
                membershipType, "Membership type");
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

    public MembershipType getMembershipType()
    {
        return (membershipType);
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

    public boolean payFine(double amount)
    {
        amount = Validator.validatePositive(amount, "Payment amount");
        if (amount > balanceOwed)
            return (false);
        balanceOwed -= amount;
        return (true);
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
        info.append("  Member | %s%n".formatted(membershipType));
        info.append(separator());
        info.append(fieldLine("Name", name));
        info.append(fieldLine("Membership ID", membershipId));
        info.append(fieldLine("Items Held", itemsHeld));
        info.append(fieldLine("Balance Owed", money(balanceOwed) + " EGP"));
        return (info.toString());
    }

}

