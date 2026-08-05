package bank.customers;

import static utils.ConsoleUtils.*;

import java.util.Arrays;
import utils.Validator;
import bank.accounts.Account;
import bank.interfaces.MonthlyUpdatable;

public class Customer implements MonthlyUpdatable
{
    // Fields

    private static final int MAX_ACCOUNTS_PER_CUSTOMER = 50;
    private static int customerCounter = 0;

    static
    {
        Validator.validatePositive(MAX_ACCOUNTS_PER_CUSTOMER, "MAX_ACCOUNTS_PER_CUSTOMER");
    }

    private final int customerId;
    private final String nationalId;
    private String name;
    private String phone;
    private CustomerTier tier;
    private final Account[] accounts;
    private int accountCount;

    // Constructor

    public Customer(String nationalId, String name, String phone, CustomerTier tier)
    {
        this.nationalId = Validator.validateAlphanumeric(nationalId, "National ID", 6, 15);
        this.name = Validator.validateLetters(name, "Name", 2, 50);
        this.phone = Validator.validateOptionalNumeric(phone, "Phone", 7, 15);
        this.tier = Validator.validateNotNull(tier, "Customer tier cannot be null");
        this.accounts = new Account[MAX_ACCOUNTS_PER_CUSTOMER];
        this.accountCount = 0;
        this.customerId = ++customerCounter;
    }

    // Getters

    public int getCustomerId()
    {
        return (customerId);
    }

    public String getNationalId()
    {
        return (nationalId);
    }

    public String getName()
    {
        return (name);
    }

    public String getPhone()
    {
        return (phone);
    }

    public CustomerTier getTier()
    {
        return (tier);
    }

    public Account[] getAccounts()
    {
        return (Arrays.copyOf(accounts, accountCount));
    }

    public int getOpenAccountsCount()
    {
        int count;

        count = 0;
        for (int i = 0; i < accountCount; i++)
        {
            if (!accounts[i].isClosed())
                count++;
        }
        return (count);
    }

    // Account Information

    public String getAccountsInfo()
    {
        StringBuilder accountsInfo;

        if (accountCount == 0)
            return ("  No accounts found\n");
        accountsInfo = new StringBuilder();
        for (int i = 0; i < accountCount; i++)
            accountsInfo.append(accounts[i]);
        return (accountsInfo.toString());
    }

    // Account Management

    public void addAccount(Account account)
    {
        if (accountCount >= accounts.length)
            throw new IllegalStateException("Customer reached the maximum number of accounts");
        accounts[accountCount] = Validator.validateNotNull(account, "Account cannot be null");
        accountCount++;
    }

    // Time Management

    private String applyMonthlyFee()
    {
        double monthlyFee;

        monthlyFee = tier.getMonthlyFee();
        if (monthlyFee <= 0.00)
            return ("Customer #%d (%s): no monthly fee (%s tier)"
                    .formatted(customerId, name, tier));
        for (int i = 0; i < accountCount; i++)
        {
            if (accounts[i].tryDecreaseBalance(monthlyFee))
            {
                return ("Customer #%d (%s): monthly fee %s applied to account #%d"
                        .formatted(customerId, name, money(monthlyFee),
                                accounts[i].getAccountNumber()));
            }
        }
        return ("Customer #%d (%s): could not apply monthly fee %s (no eligible account)"
                .formatted(customerId, name, money(monthlyFee)));
    }

    @Override
    public String processMonthlyUpdate()
    {
        return (applyMonthlyFee());
    }

    // Object Methods

    @Override
    public String toString()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append("  Customer #%d | %s Tier%n".formatted(customerId, tier));
        info.append(separator());
        info.append(fieldLine("Name", name));
        info.append(fieldLine("Phone", phone.isEmpty()? "Not provided" : phone));
        info.append(fieldLine("Number of Accounts", accountCount));
        info.append(fieldLine("Open Accounts", getOpenAccountsCount()));
        info.append(getAccountsInfo());
        return (info.toString());
    }
}
