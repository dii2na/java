package baytalhekma;

import static baytalhekma.utils.ConsoleUtils.*;
import baytalhekma.utils.InputReader;
import baytalhekma.utils.Validator;
import baytalhekma.services.Library;
import baytalhekma.models.results.ReturnBreakdown;
import baytalhekma.enums.ItemStatus;
import baytalhekma.models.items.LibraryItem;
import java.util.Scanner;

public class Main
{
    private static void displayWelcome()
    {
        println(sectionTitle("Welcome to Bayt Al Hekma Library"));
    }

    private static void displayMenu()
    {
        println("""
                
                BAYT AL HEKMA LIBRARY
                
                1. View Catalogue
                2. Register Member
                3. Borrow Item
                4. Return Item
                5. Renew Loan
                6. Search Item by ID
                7. View Items by Status
                8. Pay Outstanding Fines
                9. View All Members
                10. Library Report
                0. Exit
                """);
    }

    private static boolean shouldExit(Scanner scanner)
    {
        String input;

        while (true)
        {
            println("Are you sure you want to exit? (y/n)");
            input = scanner.nextLine().trim().toLowerCase();
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
                    return true;
                }
                case 'n' -> return false;
                default -> println("Invalid input. Please enter y or n.");
            }
        }
    }

    // Registration

    private static void registerMember(Scanner scanner, Library library)
    {
        String name;
        String membershipId;
        MembershipType membershipType;
        Member member;

        name = InputReader.readValidatedString(
                scanner,
                "Name",
                value -> Validator.validateLetters(value, "Name", 2, 50));
        membershipId = InputReader.readValidatedString(
                scanner,
                "Membership ID",
                value -> Validator.validateAlphanumeric(
                        value, "Membership ID", 1, 20));
        membershipType = InputReader.readEnum(
                scanner,
                "Choose membership type:",
                MembershipType.values());
        member = new Member(name, membershipId, membershipType);
        library.registerMember(member);
        println("Member registered successfully." + newLine());
        print(member);
    }


    private static void borrowItem(Scanner scanner, Library library)
    {
        int catalogueId;
        String membershipId;
        LibraryItem item;

        catalogueId = InputReader.readIntPositive(
                scanner, "catalogue ID");
        membershipId = InputReader.readValidatedString(
                scanner,
                "Membership ID",
                value -> Validator.validateAlphanumeric(
                        value, "Membership ID", 1, 20));
        item = library.lendItem(catalogueId, membershipId);
        println("Item borrowed successfully.");
        println("Loan period: " + item.getLoanPeriod() + " days.");
    }

    // Display Operations

    private static void viewCatalogue(Library library)
    {
        LibraryItem[] items;

        items = library.getCatalogue();
        if (items.length == 0)
        {
            println("No items found.");
            return;
        }
        println(sectionTitle("Library Catalogue"));
        printList(items);
    }

    private static void returnItem(Scanner scanner, Library library)
    {
        int catalogueId;
        int overdueDays;
        ReturnBreakdown breakdown;

        catalogueId = InputReader.readIntPositive(
                scanner, "catalogue ID");
        overdueDays = InputReader.readIntNonNegative(
                scanner, "overdue days");
        breakdown = library.returnItem(catalogueId, overdueDays);
        println(sectionTitle("Return Successful"));
        println(fieldLine("Base Fine", money(breakdown.getBaseFine()) + " EGP"));
        println(fieldLine("Administrative Charge",money(breakdown.getAdministrativeCharge()) + " EGP"));
        println(fieldLine("Total Fine", money(breakdown.getTotalFine()) + " EGP"));
        println(fieldLine("New Balance", money(breakdown.getNewBalance()) + " EGP"));
    }

    private static void renewItem(Scanner scanner, Library library)
    {
        int catalogueId;
        int remainingRenewals;

        catalogueId = InputReader.readIntPositive(scanner, "catalogue ID");
        remainingRenewals = library.renewItem(catalogueId);
        println("Loan renewed successfully.");
        println("Renewals remaining: " + remainingRenewals);
    }

    private static void searchItem(Scanner scanner, Library library)
    {
        int catalogueId;
        LibraryItem item;

        catalogueId = InputReader.readIntPositive(scanner, "catalogue ID");
        item = library.findItemById(catalogueId);
        if (item == null)
        {
            println("Item not found.");
            return;
        }
        print(item);
    }

    private static void viewItemsByStatus(
        Scanner scanner,
        Library library)
    {
        ItemStatus status;
        LibraryItem[] items;

        status = InputReader.readEnum(
                scanner,
                "Choose item status:",
                ItemStatus.values());
        items = library.getItemsByStatus(status);
        if (items.length == 0)
        {
            println("No items found with status: " + status);
            return;
        }
        printList(items);
    }

    private static void payFine(Scanner scanner, Library library)
    {
        String membershipId;
        double amount;
        double newBalance;

        membershipId = InputReader.readValidatedString(
                scanner,
                "membership ID",
                value -> Validator.validateAlphanumeric(
                        value, "Membership ID", 1, 20));
        amount = InputReader.readDoublePositive(scanner, "payment amount");
        newBalance = library.payFine(membershipId, amount);
        println("Payment successful.");
        println("New balance: " + money(newBalance) + " EGP");
    }

    private static void viewAllMembers(Library library)
    {
        Member[] members;

        members = library.getMembers();
        if (members.length == 0)
        {
            println("No members found.");
            return;
        }
        printList(members);
    }

    private static void libraryReport(Library library)
    {
        println(library.getLibraryReport());
    }

    private static void runLibrarySystem(Scanner scanner, Library library)
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
                    case 1 -> viewCatalogue(library);
                    case 2 -> registerMember(scanner, library);
                    case 3 -> borrowItem(scanner, library);
                    case 4 -> returnItem(scanner, library);
                    case 5 -> renewItem(scanner, library);
                    case 6 -> searchItem(scanner, library);
                    case 7 -> viewItemsByStatus(scanner, library);
                    case 8 -> payFine(scanner, library);
                    case 9 -> viewAllMembers(library);
                    case 10 -> libraryReport(library);
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
            Library library = new Library();
            runLibrarySystem(scanner, library);
        }
    }
}