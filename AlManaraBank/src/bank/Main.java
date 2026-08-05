package bank;

import static utils.ConsoleUtils.*;

import java.util.Scanner;
import java.util.function.Consumer;
import bank.services.Bank;
import utils.Validator;
import utils.InputReader;
import bank.customers.*;
import bank.accounts.*;

public class Main
{
    // Welcome & Menu

    private static void displayWelcome()
    {
        println(sectionTitle("Welcome to Al Manara Bank"));
    }

    private static void displayMenu()
    {
        println("""
            
            AL MANARA BANK
            1. Register New Customer
            2. Open New Account
            3. Deposit Money
            4. Withdraw Money
            5. Transfer Between Accounts
            6. Display Customer Accounts
            7. Display All Branch Accounts
            8. Search Account by Number
            9. Search Accounts by Type
            10. List All Customers
            11. Close an Account
            12. Freeze an Account
            13. Activate an Account
            14. Process Monthly Update
            0. Exit
            """);
    }

    private static boolean shouldExit(Scanner scanner)
    {
        while (true)
        {
            println("Are you sure you want to exit? (y/n)");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty())
            {
                println("Invalid input.");
                continue;
            }
            switch (input.charAt(0))
            {
                case 'y' ->
                {
                    println("Goodbye!");
                    return (true);
                }
                case 'n' -> { return (false); }
                default -> println("Invalid input. Please enter y or n.");
            }
        }
    }

    // Registration

    private static void registerCustomer(Scanner scanner, Bank bank)
    {
        String name;
        String nationalId;
        String phone;
        CustomerTier tier;
        Customer customer;

        bank.validateCustomerCapacity();
        nationalId = InputReader.readValidatedString(
                scanner,
                "National ID",
                value -> Validator.validateAlphanumeric(value, "National ID", 6, 15));
        bank.validateNewNationalId(nationalId);
        name = InputReader.readValidatedString(
                scanner,
                "Name",
                value -> Validator.validateLetters(value, "Name", 2, 50));
        phone = InputReader.readValidatedString(
                scanner,
                "Phone",
                value -> Validator.validateOptionalNumeric(value, "Phone", 7, 15));
        tier = InputReader.readEnum(scanner, "Choose customer tier:", CustomerTier.values());
        customer = bank.registerCustomer(nationalId, name, phone, tier);
        println("Customer registered successfully." + newLine());
        print(customer);
    }

    // Account Operations

    private static void openAccount(Scanner scanner, Bank bank)
    {
        int customerId;
        int months;
        double balance;
        AccountType type;
        Account account;

        months = 0;
        bank.validateAccountCapacity();
        customerId = InputReader.readIntPositive(scanner, "customer ID");
        bank.getCustomerOrThrow(customerId);
        type = InputReader.readEnum(scanner, "Choose account type:", AccountType.values());
        balance = InputReader.readDoubleNonNegative(scanner, "opening balance");
        if (type == AccountType.FIXED_DEPOSIT)
            months = InputReader.readIntPositive(scanner, "deposit period months");
        account = bank.openAccount(customerId, type, balance, months);
        println("Account opened successfully." + newLine());
        print(account);
    }

    private static void processTransaction(Scanner scanner, Bank bank, boolean isDeposit)
    {
        int accountNumber;
        double amount;
        double newBalance;
        String transactionType;
        Account account;

        transactionType = isDeposit ? "Deposit" : "Withdrawal";
        accountNumber = InputReader.readIntPositive(scanner, "account number");
        account = bank.getAccountOrThrow(accountNumber, "Account not found");
        if (isDeposit)
            account.checkActive();
        else
            account.checkWithdrawalState();
        amount = InputReader.readDoublePositive(scanner, transactionType + " amount");
        if (isDeposit)
            newBalance = bank.deposit(accountNumber, amount);
        else
            newBalance = bank.withdraw(accountNumber, amount);
        println(transactionType + " successful. New balance: "
                + money(newBalance));
    }

    private static void transfer(Scanner scanner, Bank bank)
    {
        int sourceNumber;
        int destinationNumber;
        double amount;

        sourceNumber = InputReader.readIntPositive(scanner, "source account number");
        destinationNumber = InputReader.readIntPositive(scanner, "destination account number");
        bank.validateTransfer(sourceNumber, destinationNumber);
        amount = InputReader.readDoublePositive(scanner, "transfer amount");
        bank.transfer(sourceNumber, destinationNumber, amount);
        println("Transfer successful.");
    }

    private static void updateAccountStatus(
            Scanner scanner,
            Consumer<Integer> operation,
            String state)
    {
        int accountNumber;

        accountNumber = InputReader.readIntPositive(scanner, "account number");
        operation.accept(accountNumber);
        println("Account " + state + " successfully.");
    }

    private static void closeAccount(Scanner scanner, Bank bank)
    {
        updateAccountStatus(scanner, bank::closeAccount,"closed");
    }

    private static void freezeAccount(Scanner scanner, Bank bank)
    {
        updateAccountStatus(scanner, bank::freezeAccount, "frozen");
    }

    private static void activateAccount(Scanner scanner, Bank bank)
    {
        updateAccountStatus(scanner, bank::activateAccount, "activated");
    }

    // Display Operations

    private static void displayCustomerAccounts(Scanner scanner, Bank bank)
    {
        int customerId;
        Customer customer;

        customerId = InputReader.readIntPositive(scanner, "customer ID");
        customer = bank.getCustomerOrThrow(customerId);
        print(customer);
        printSeparator();
        println("  Combined Balance: " + money(
                bank.getCombinedBalance(customer.getAccounts())));
    }

    private static boolean tryDisplayAccounts(Account[] accounts, String message)
    {
        if (accounts.length == 0)
        {
            println("No accounts found.");
            return (false);
        }
        if (!message.isBlank())
            println(sectionTitle(message));
        printList(accounts);
        return (true);
    }

    private static void displayAllAccounts(Bank bank)
    {
        Account[] accounts;

        accounts = bank.getAccounts();
        if (!tryDisplayAccounts(accounts, "All Branch Accounts"))
            return;
        print(bank.getSummary(accounts));
    }

    private static void listAllCustomers(Bank bank)
    {
        Customer[] customers;

        customers = bank.getCustomers();
        if (customers.length == 0)
        {
            println("No customers found.");
            return;
        }
        printList(customers);
    }

    private static void searchAccountByNumber(Scanner scanner, Bank bank)
    {
        int accountNumber;
        Account account;

        accountNumber = InputReader.readIntPositive(scanner, "account number");
        account = bank.getAccountOrThrow(accountNumber, "Account not found");
        print(account);
        printSeparator();
    }

    private static void searchAccountsByType(Scanner scanner, Bank bank)
    {
        AccountType type;
        Account[] accounts;

        type = InputReader.readEnum(
                scanner, "Choose account type:", AccountType.values());
        accounts = bank.findAccountsByType(type);
        if (!tryDisplayAccounts(accounts, "Accounts Type: " + type))
            return;
        print(bank.getSummary(accounts));
    }

    // Time Management
    private static void processMonthlyUpdate(Bank bank)
    {
        print(bank.processMonthlyUpdate());
    }

    // Main Loop

    private static void runBankSystem(Scanner scanner, Bank bank)
    {
        boolean running;
        int choice;

        running = true;
        displayWelcome();
        while (running)
        {
            displayMenu();
            try
            {
                choice = InputReader.readIntNonNegative(scanner, "choice");
                switch (choice)
                {
                    case 0 -> running = !shouldExit(scanner);
                    case 1 -> registerCustomer(scanner, bank);
                    case 2 -> openAccount(scanner, bank);
                    case 3, 4 -> processTransaction(scanner, bank, choice == 3);
                    case 5 -> transfer(scanner, bank);
                    case 6 -> displayCustomerAccounts(scanner, bank);
                    case 7 -> displayAllAccounts(bank);
                    case 8 -> searchAccountByNumber(scanner, bank);
                    case 9 -> searchAccountsByType(scanner, bank);
                    case 10 -> listAllCustomers(bank);
                    case 11 -> closeAccount(scanner, bank);
                    case 12 -> freezeAccount(scanner, bank);
                    case 13 -> activateAccount(scanner, bank);
                    case 14 -> processMonthlyUpdate(bank);
                    default -> println("Invalid choice.");
                }
            }
            catch (IllegalArgumentException | IllegalStateException e)
            {
                println(e.getMessage() != null ? e.getMessage() : e.toString());
            }
        }
    }

    // Entry Point

    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            Bank bank = new Bank();
            runBankSystem(scanner, bank);
        }
    }
}
