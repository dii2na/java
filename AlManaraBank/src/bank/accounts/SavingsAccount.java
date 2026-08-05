package bank.accounts;

import static utils.ConsoleUtils.*;

import bank.customers.Customer;
import bank.interfaces.MonthlyUpdatable;

public class SavingsAccount extends Account implements MonthlyUpdatable, InterestBearing
{
    // Fields

    private int monthlyWithdrawalCount;

    // Constructor

    public SavingsAccount(Customer customer, double balance)
    {
        super(customer, balance, AccountType.SAVINGS);
        this.monthlyWithdrawalCount = 0;
    }

    // Withdrawal Rules

    @Override
    public boolean canDecreaseBalance(double amount)
    {
        return (getBalance() >= amount);
    }

    @Override
    protected void applyWithdrawalRules(double amount)
    {
        if (!canDecreaseBalance(amount))
            throw new IllegalArgumentException("Insufficient balance");
        monthlyWithdrawalCount++;
    }

    // Withdrawal Rollback

    @Override
    public void rollbackWithdrawal(double amount)
    {
        super.rollbackWithdrawal(amount);
        if (monthlyWithdrawalCount > 0)
            monthlyWithdrawalCount--;
    }

    // Time Management

    @Override
    public String processMonthlyUpdate()
    {
        int countBefore;

        checkActive();
        countBefore = monthlyWithdrawalCount;
        if (countBefore == 0)
            return ("");
        monthlyWithdrawalCount = 0;
        return ("Savings Account #%d: monthly withdrawal counter reset (%d -> 0)"
                .formatted(getAccountNumber(), countBefore));
    }

    // Object Methods

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(super.toString());
        info.append(fieldLine("Annual Interest Rate", percent(getAnnualInterestRate(this))));
        info.append(fieldLine("Monthly Withdrawals", monthlyWithdrawalCount));
        return (info.toString());
    }
}