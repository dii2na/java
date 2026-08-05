package bank.accounts;

import utils.Validator;

public enum AccountType
{
    SAVINGS("Savings Account", 100, 10, 0.05),
    CURRENT("Current Account", 500, 10, 0.0),
    FIXED_DEPOSIT("Fixed Deposit", 1000, 100, 0.07);

    private final String label;
    private final double minimumOpeningBalance;
    private final double minimumTransactionAmount;
    private final double annualInterestRate;

    AccountType(String label, double minimumOpeningBalance, double minimumTransactionAmount,
                double annualInterestRate)
    {
        this.label = Validator.validateString(label, "Label", false);
        this.minimumOpeningBalance = Validator.validatePositive(
                minimumOpeningBalance, "Minimum opening balance");
        this.minimumTransactionAmount =  Validator.validatePositive(
                minimumTransactionAmount, "Minimum transaction amount");
        this.annualInterestRate = Validator.validateNonNegative(
                annualInterestRate, "Interest rate");
    }

    @Override
    public String toString()
    {
        return (label);
    }

    public double getMinimumOpeningBalance()
    {
        return (minimumOpeningBalance);
    }

    public double getMinimumTransactionAmount()
    {
        return (minimumTransactionAmount);
    }

    public double getAnnualInterestRate()
    {
        return (annualInterestRate);
    }
}
