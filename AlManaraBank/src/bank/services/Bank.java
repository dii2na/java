package bank.services;

import static utils.ConsoleUtils.*;

import java.util.Arrays;
import utils.Validator;
import bank.accounts.*;
import bank.customers.*;
import bank.interfaces.MonthlyUpdatable;

public class Bank
{
    // Fields

    private static final int MAX_CUSTOMERS = 100;
    private static final int MAX_ACCOUNTS = 500;

    static
    {
        Validator.validatePositive(MAX_CUSTOMERS, "MAX_CUSTOMERS");
        Validator.validatePositive(MAX_ACCOUNTS, "MAX_ACCOUNTS");
    }

    private final Customer[] customers;
    private final Account[] accounts;
    private int customerCount;
    private int accountCount;
    private int currentMonth;

    // Constructor

    public Bank()
    {
        customers = new Customer[MAX_CUSTOMERS];
        accounts = new Account[MAX_ACCOUNTS];
        customerCount = 0;
        accountCount = 0;
        currentMonth = 0;
    }

    // Retrieval & Summary

    public Customer[] getCustomers()
    {
        return (Arrays.copyOf(customers, customerCount));
    }

    public Account[] getAccounts()
    {
        return (Arrays.copyOf(accounts, accountCount));
    }

    public double getCombinedBalance(Account[] accounts)
    {
        double total;

        total = 0;
        for (int i = 0; i < accounts.length; i++)
            total += accounts[i].getBalance();
        return (total);
    }

    public String getSummary(Account[] accounts)
    {
        return (separator()
                + "  Accounts: %d | Combined Balance: %s%n"
                .formatted(accounts.length, money(getCombinedBalance(accounts))));
    }

    // Customer Operations

    public void validateCustomerCapacity()
    {
        if (customerCount >= MAX_CUSTOMERS)
            throw new IllegalStateException("Customer storage is full. Cannot register more customers.");
    }

    public void validateNewNationalId(String nationalId)
    {
        if (findCustomerByNationalId(nationalId) != null)
            throw new IllegalArgumentException("National ID already exists");
    }

    public Customer registerCustomer(String nationalId, String name, String phone, CustomerTier tier)
    {
        Customer customer;

        validateCustomerCapacity();
        validateNewNationalId(nationalId);
        customer = new Customer(nationalId, name, phone, tier);
        customers[customerCount++] = customer;
        return (customer);
    }

    public Customer findCustomerByNationalId(String nationalId)
    {
        for (int i = 0; i < customerCount; i++)
        {
            if (customers[i].getNationalId().equals(nationalId))
                return (customers[i]);
        }
        return (null);
    }

    public Customer findCustomerById(int customerId)
    {
        for (int i = 0; i < customerCount; i++)
        {
            if (customers[i].getCustomerId() == customerId)
                return (customers[i]);
        }
        return (null);
    }

    public Customer getCustomerOrThrow(int customerId)
    {
        return (Validator.validateNotNull(findCustomerById(customerId), "Customer not found"));
    }

    // Account Lookup

    public Account findAccountByNumber(int accountNumber)
    {
        for (int i = 0; i < accountCount; i++)
        {
            if (accounts[i].getAccountNumber() == accountNumber)
                return (accounts[i]);
        }
        return (null);
    }

    public Account getAccountOrThrow(int accountNumber, String notFoundMessage)
    {
        return (Validator.validateNotNull(findAccountByNumber(accountNumber), notFoundMessage));
    }

    public Account[] findAccountsByType(AccountType type)
    {
        Account[] result;
        int count;

        result = new Account[accountCount];
        count = 0;
        for (int i = 0; i < accountCount; i++)
        {
            if (accounts[i].getType() == type)
                result[count++] = accounts[i];
        }
        return (Arrays.copyOf(result, count));
    }

    // Account Operations

    public void validateAccountCapacity()
    {
        if (accountCount >= MAX_ACCOUNTS)
            throw new IllegalStateException("Account storage is full. Cannot open more accounts.");
    }

    public Account openAccount(int customerId, AccountType type, double balance, int months)
    {
        Customer customer;
        Account account;

        validateAccountCapacity();
        customer = getCustomerOrThrow(customerId);
        type = Validator.validateNotNull(type, "Account type cannot be null");
        switch (type)
        {
            case SAVINGS -> account = new SavingsAccount(customer, balance);
            case CURRENT -> account = new CurrentAccount(customer, balance);
            case FIXED_DEPOSIT -> account = new FixedDepositAccount(customer, balance, months);
            default -> throw new IllegalArgumentException("Invalid account type");
        }
        customer.addAccount(account);
        accounts[accountCount++] = account;
        return (account);
    }

    public void closeAccount(int accountNumber)
    {
        getAccountOrThrow(accountNumber, "Account not found").closeAccount();
    }

    public void freezeAccount(int accountNumber)
    {
        getAccountOrThrow(accountNumber, "Account not found").freezeAccount();
    }

    public void activateAccount(int accountNumber)
    {
        getAccountOrThrow(accountNumber, "Account not found").activateAccount();
    }

    public double deposit(int accountNumber, double amount)
    {
        return (getAccountOrThrow(accountNumber, "Account not found").deposit(amount));
    }

    public double withdraw(int accountNumber, double amount)
    {
        return (getAccountOrThrow(accountNumber, "Account not found").withdraw(amount));
    }

    public void validateTransfer(int sourceNumber, int destinationNumber)
    {
        Account sourceAccount;
        Account destinationAccount;

        sourceAccount = getAccountOrThrow(sourceNumber, "Source account not found");
        destinationAccount = getAccountOrThrow(destinationNumber, "Destination account not found");
        if (sourceAccount == destinationAccount)
            throw new IllegalArgumentException("Cannot transfer to the same account");
        sourceAccount.checkWithdrawalState();
        destinationAccount.checkActive();
    }

    public void transfer(int sourceNumber, int destinationNumber, double amount)
    {
        Account sourceAccount;
        Account destinationAccount;
        boolean withdrawn;

        validateTransfer(sourceNumber, destinationNumber);
        sourceAccount = getAccountOrThrow(sourceNumber, "Source account not found");
        destinationAccount = getAccountOrThrow(destinationNumber, "Destination account not found");
        withdrawn = false;
        try
        {
            sourceAccount.withdraw(amount);
            withdrawn = true;
            destinationAccount.deposit(amount);
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            if (!withdrawn)
                throw new IllegalStateException("Transfer failed (Source): " + e.getMessage());
            sourceAccount.rollbackWithdrawal(amount);
            throw new IllegalStateException("Transfer failed (Destination): " + e.getMessage());
        }
    }

    // Time Management

    private String processMonthlyCustomerUpdates()
    {
        StringBuilder updates;

        updates = new StringBuilder();
        for (int i = 0; i < customerCount; i++)
            updates.append("    - ").append(customers[i].processMonthlyUpdate()).append("\n");
        return (updates.toString());
    }

    private boolean canRunMonthlyUpdate(Account account)
    {
        if (!account.isActive())
            return (false);
        return (!(account instanceof FixedDepositAccount fixedDeposit)
                || !fixedDeposit.isMatured());
    }

    private String processMonthlyAccountUpdates()
    {
        StringBuilder updates;
        int skipped;
        String update;
        Account account;

        updates = new StringBuilder();
        skipped = 0;
        for (int i = 0; i < accountCount; i++)
        {
            account = accounts[i];
            if (account instanceof MonthlyUpdatable updatable)
            {
                if (!canRunMonthlyUpdate(account))
                {
                    skipped++;
                    continue;
                }
                update = updatable.processMonthlyUpdate();
                if (!update.isEmpty())
                    updates.append("    - ").append(update).append("\n");
            }
        }
        if (skipped > 0)
            updates.append("    - Skipped %d account(s) (frozen/closed or matured)\n"
                    .formatted(skipped));
        return (updates.toString());
    }

    private String processAnnualAccountUpdate()
    {
        StringBuilder updates;
        double totalInterest;
        Account account;
        double before;
        double interest;

        updates = new StringBuilder();
        totalInterest = 0;
        for (int i = 0; i < accountCount; i++)
        {
            account = accounts[i];
            if (account instanceof InterestBearing interestAccount && account.isActive())
            {
                before = account.getBalance();
                interest = interestAccount.applyAnnualInterest(account);
                totalInterest += interest;
                updates.append("    - %s #%d: +%s (%s -> %s)\n"
                        .formatted(account.getType(), account.getAccountNumber(),
                                money(interest), money(before),
                                money(account.getBalance())));
            }
        }
        if (totalInterest > 0)
            updates.append("    - Total interest applied: %s\n"
                    .formatted(money(totalInterest)));
        return (updates.toString());
    }

    public String processMonthlyUpdate()
    {
        StringBuilder report;

        currentMonth++;
        report = new StringBuilder();
        report.append(sectionTitle("MONTHLY UPDATE - Month #%d".formatted(currentMonth)));
        report.append("  Customer Fees:%n".formatted());
        report.append(processMonthlyCustomerUpdates());
        report.append("  Account Updates:%n".formatted());
        report.append(processMonthlyAccountUpdates());
        if (currentMonth % 12 == 0)
        {
            report.append("  Annual Interest (Year %d):%n".formatted(currentMonth / 12));
            report.append(processAnnualAccountUpdate());
        }
        report.append(getSummary(getAccounts()));
        report.append(DOUBLE_SEPARATOR).append(newLine());
        return (report.toString());
    }
}
