package bank.accounts;

import static utils.ConsoleUtils.*;

import utils.Validator;
import bank.customers.Customer;
import bank.interfaces.MonthlyUpdatable;

public class FixedDepositAccount extends Account implements MonthlyUpdatable, InterestBearing
{
    // Fields

    private int depositPeriodMonths;
    private int remainingMonths;

    // Constructor

    public FixedDepositAccount(Customer customer, double balance, int depositPeriodMonths)
    {
        super(customer, balance, AccountType.FIXED_DEPOSIT);
        this.depositPeriodMonths = Validator.validatePositive(depositPeriodMonths, "Deposit period");
        this.remainingMonths = this.depositPeriodMonths;
    }

    // Getters

    public int getElapsedMonths()
    {
        return (depositPeriodMonths - remainingMonths);
    }

    // Maturity

    public boolean isMatured()
    {
        return (remainingMonths <= 0);
    }

    // Withdrawal Rules

    @Override
    public boolean canDecreaseBalance(double amount)
    {
        return (isMatured() && getBalance() >= amount);
    }

    @Override
    public void checkWithdrawalState()
    {
        super.checkWithdrawalState();
        if (!isMatured())
            throw new IllegalStateException(
                    "Deposit is not matured. Remaining months: " + remainingMonths);
    }

    @Override
    protected void applyWithdrawalRules(double amount)
    {
        checkWithdrawalState();
        if (!canDecreaseBalance(amount))
            throw new IllegalArgumentException("Insufficient balance");
    }

    // Closing Behavior

    @Override
    public void closeAccount()
    {
        if (!isMatured())
            throw new IllegalStateException(
                    "Cannot close fixed deposit. Remaining months: " + remainingMonths);
        super.closeAccount();
    }

    // Time Management

    private void passMonth()
    {
        checkActive();
        if (remainingMonths <= 0)
            throw new IllegalStateException("Fixed deposit is already matured");
        remainingMonths--;
    }

    @Override
    public String processMonthlyUpdate()
    {
        int monthsBefore;

        monthsBefore = remainingMonths;
        passMonth();
        return ("Fixed Deposit #%d: months remaining %d -> %d"
                .formatted(getAccountNumber(), monthsBefore, remainingMonths));
    }

    // Object Methods

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(super.toString());
        info.append(fieldLine("Annual Interest Rate", percent(getAnnualInterestRate(this))));
        info.append(fieldLine("Deposit Period", depositPeriodMonths + " months"));
        info.append(fieldLine("Remaining Months", remainingMonths));
        info.append(fieldLine("Elapsed Months", getElapsedMonths()));
        info.append(fieldLine("Matured", yesNo(isMatured())));
        return (info.toString());
    }
}