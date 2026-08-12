package baytalhekma.models.results;

import utils.Validator;

public class ReturnBreakdown
{
    private final double baseFine;
    private final double administrativeCharge;
    private final double totalFine;
    private double newBalance;


    public ReturnBreakdown(
            double baseFine,
            double administrativeCharge,
            double totalFine)
    {
        this.baseFine = Validator.validateNonNegative(
                baseFine, "Base fine");
        this.administrativeCharge = Validator.validateNonNegative(
                administrativeCharge, "Administrative charge");
        this.totalFine = Validator.validateNonNegative(
                totalFine, "Total fine");
    }

    public double getBaseFine()
    {
        return (baseFine);
    }

    public double getAdministrativeCharge()
    {
        return (administrativeCharge);
    }

    public double getTotalFine()
    {
        return (totalFine);
    }

    public double getNewBalance()
    {
        return (newBalance);
    }

    public void setNewBalance(double newBalance)
    {
        this.newBalance = Validator.validateNonNegative(
                newBalance, "New balance");
    }
}