package bank.customers;

import utils.Validator;

public enum CustomerTier
{
    STANDARD("Standard", 0.0, 0.00),
    SILVER("Silver", 5.0, 0.01),
    GOLD("Gold", 10.0, 0.02);

    private final String label;
    private final double monthlyFee;
    private final double interestBonus;

    CustomerTier(String label, double monthlyFee, double interestBonus)
    {
        this.label = label;
        this.monthlyFee = Validator.validateNonNegative(
                monthlyFee, "Monthly fee");
        this.interestBonus = Validator.validateNonNegative(
                interestBonus, "Interest bonus");
    }

    @Override
    public String toString()
    {
        return (label);
    }

    public double getMonthlyFee()
    {
        return (monthlyFee);
    }

    public double getInterestBonus()
    {
        return (interestBonus);
    }
}