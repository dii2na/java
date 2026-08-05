package bank.accounts;

import static utils.ConsoleUtils.*;

import utils.Validator;
import bank.customers.Customer;

public abstract class Account
{
    // Fields

    private static int accountCounter = 0;

    private final int accountNumber;
    private double balance;
    private final AccountType type;
    private AccountStatus status;
    private int transactionCount;
    private final Customer customer;

    // Constructor

    public Account(Customer customer, double balance, AccountType type)
    {
        this.type = Validator.validateNotNull(type, "Account type cannot be null");
        this.balance = Validator.validateMinimum(balance, type.getMinimumOpeningBalance(), "Balance");
        this.status = AccountStatus.ACTIVE;
        this.transactionCount = 0;
        this.customer = Validator.validateNotNull(customer, "Customer cannot be null");
        this.accountNumber = ++accountCounter;
    }

    // Getters

    public int getAccountNumber()
    {
        return (accountNumber);
    }

    public double getBalance()
    {
        return (balance);
    }

    public AccountType getType()
    {
        return (type);
    }

    public AccountStatus getStatus()
    {
        return (status);
    }

    public Customer getCustomer()
    {
        return (customer);
    }

    // Balance & Transaction Helpers

    protected void increaseBalance(double amount)
    {
        balance += Validator.validatePositive(amount, "Amount");
    }

    public abstract boolean canDecreaseBalance(double amount);

    protected void decreaseBalance(double amount)
    {
        balance -= Validator.validatePositive(amount, "Amount");
    }

    public boolean tryDecreaseBalance(double amount)
    {
        if (!isActive() || !canDecreaseBalance(amount))
            return (false);
        decreaseBalance(amount);
        return (true);
    }

    protected void increaseTransactionCount()
    {
        transactionCount++;
    }

    protected void decreaseTransactionCount()
    {
        if (transactionCount > 0)
            transactionCount--;
    }

    public void rollbackWithdrawal(double amount)
    {
        increaseBalance(amount);
        decreaseTransactionCount();
    }

    public void checkActive()
    {
        if (!isActive())
            throw new IllegalStateException(status.getMessage());
    }

    public void checkWithdrawalState()
    {
        checkActive();
    }

    protected void checkTransactionAllowed(double amount)
    {
        checkActive();
        Validator.validateMinimum(amount, type.getMinimumTransactionAmount(), "Amount");
    }

    // Status Management

    public void freezeAccount()
    {
        if (isClosed())
            throw new IllegalStateException("Account is closed");
        if (isFrozen())
            throw new IllegalStateException("Account already frozen");
        status = AccountStatus.FROZEN;
    }

    public void activateAccount()
    {
        if (isClosed())
            throw new IllegalStateException("Account is closed");
        if (isActive())
            throw new IllegalStateException("Account already activated");
        status = AccountStatus.ACTIVE;
    }

    public void closeAccount()
    {
        if (isClosed())
            throw new IllegalStateException("Account already closed");
        if (balance != 0.0)
            throw new IllegalStateException("Account balance must be zero");
        status = AccountStatus.CLOSED;
    }

    public boolean isActive()
    {
        return hasStatus(AccountStatus.ACTIVE);
    }

    public boolean isFrozen()
    {
        return hasStatus(AccountStatus.FROZEN);
    }

    public boolean isClosed()
    {
        return hasStatus(AccountStatus.CLOSED);
    }

    private boolean hasStatus(AccountStatus status)
    {
        return this.status == status;
    }

    // Transaction Operations

    public final double deposit(double amount)
    {
        checkTransactionAllowed(amount);
        increaseBalance(amount);
        increaseTransactionCount();
        return (balance);
    }

    public final double withdraw(double amount)
    {
        checkTransactionAllowed(amount);
        applyWithdrawalRules(amount);
        decreaseBalance(amount);
        increaseTransactionCount();
        return (balance);
    }

    protected abstract void applyWithdrawalRules(double amount);

    // Object Methods

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append("  Account #%d | %s | Owner: %s%n"
                .formatted(accountNumber, type, customer.getName()));
        info.append(separator());
        info.append(fieldLine("Balance", money(balance)));
        info.append(fieldLine("Status", status));
        info.append(fieldLine("Transactions", transactionCount));
        return (info.toString());
    }
}
