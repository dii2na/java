package bank.accounts;

import static utils.ConsoleUtils.*;

import bank.customers.Customer;

public class CurrentAccount extends Account
{
    // Fields

    private static final double OVERDRAFT_LIMIT = 1000;

    // Constructor

    public CurrentAccount(Customer customer, double balance)
    {
        super(customer, balance, AccountType.CURRENT);
    }

    // Overdraft Behavior

    public boolean isUsingOverdraft()
    {
        return (getBalance() < 0);
    }

    // Withdrawal Rules

    @Override
    public boolean canDecreaseBalance(double amount)
    {
        return (getBalance() - amount >= -OVERDRAFT_LIMIT);
    }

    @Override
    protected void applyWithdrawalRules(double amount)
    {
        if (!canDecreaseBalance(amount))
            throw new IllegalArgumentException("Overdraft limit exceeded");
    }

    // Object Methods

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(super.toString());
        info.append(fieldLine("Overdraft Limit", money(OVERDRAFT_LIMIT)));
        info.append(fieldLine("Using Overdraft", yesNo(isUsingOverdraft())));
        return (info.toString());
    }
}