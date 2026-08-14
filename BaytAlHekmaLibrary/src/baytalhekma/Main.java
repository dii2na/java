package baytalhekma;

import static baytalhekma.utils.ConsoleUtils.*;
import baytalhekma.utils.InputReader;
import baytalhekma.utils.Validator;
import baytalhekma.services.Library;
import baytalhekma.models.results.ReturnBreakdown;
import baytalhekma.models.members.Member;
import baytalhekma.enums.ItemStatus;
import baytalhekma.models.items.*;
import java.util.Scanner;

public class Main
{
    // Constants

    private static final int EXIT_CHOICE = 0;
    private static final int MENU_OPTION_COUNT = 10;

    // Menu Helpers

    private static void displayWelcome()
    {
        println(sectionTitle("Welcome to Bayt Al Hekma Library"));
    }

    private static void displayMenu()
    {
        String[] options;
        int index;

        options = new String[] {
                "View Catalogue",
                "Register Member",
                "Borrow Item",
                "Return Item",
                "Renew Loan",
                "Search Item by ID",
                "View Items by Status",
                "Pay Outstanding Fines",
                "View All Members",
                "Library Report"
        };
        println();
        println(sectionTitle("BAYT AL HEKMA LIBRARY"));
        for (index = 0; index < options.length; index++)
            println("  %2d.  %s".formatted(index + 1, options[index]));
        println("  %2d.  %s".formatted(0, "Exit"));
        println();
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
                printInvalidInput("Please enter y or n.");
                continue;
            }
            switch (input.charAt(0))
            {
                case 'y' ->
                {
                    println("Goodbye!");
                    return true;
                }
                case 'n' -> { return false; }
                default -> printInvalidInput("Please enter y or n.");
            }
        }
    }

    // Shared Input Helpers

    private static String readName(Scanner scanner)
    {
        return (InputReader.readValidatedString(
                scanner,
                "Name",
                value -> Validator.validateLetters(
                        value,
                        "Name",
                        Member.MIN_NAME_LENGTH,
                        Member.MAX_NAME_LENGTH)));
    }

    private static String readMembershipId(Scanner scanner)
    {
        return (InputReader.readValidatedString(
                scanner,
                "Membership ID",
                value -> Validator.validateAlphanumeric(
                        value,
                        "Membership ID",
                        Member.MEMBERSHIP_ID_LENGTH,
                        Member.MEMBERSHIP_ID_LENGTH)));
    }

    private static int readCatalogueId(Scanner scanner)
    {
        return (InputReader.readIntPositive(scanner, "Catalogue ID"));
    }

    private static ItemStatus readItemStatus(Scanner scanner)
    {
        return (InputReader.readEnum(
                scanner,
                "Choose item status:",
                ItemStatus.values()));
    }

    private static int readMenuChoice(Scanner scanner)
    {
        return (InputReader.readIntInRange(
                scanner, "Choice", EXIT_CHOICE, MENU_OPTION_COUNT));
    }

    // Registration

    private static void registerMember(Scanner scanner, Library library)
    {
        String name;
        String membershipId;
        Member member;

        name = readName(scanner);
        membershipId = readMembershipId(scanner);
        member = new Member(name, membershipId);
        library.registerMember(member);
        println("Member registered successfully." + newLine());
        print(member);
    }

    // Loan Operations

    private static void borrowItem(Scanner scanner, Library library)
    {
        int catalogueId;
        String membershipId;
        LibraryItem item;

        catalogueId = readCatalogueId(scanner);
        membershipId = readMembershipId(scanner);
        item = library.lendItem(catalogueId, membershipId);
        println("Item borrowed successfully.");
        println("Loan period: " + item.getLoanPeriod() + " days.");
    }

    private static void returnItem(Scanner scanner, Library library)
    {
        int catalogueId;
        int overdueDays;
        ReturnBreakdown breakdown;

        catalogueId = readCatalogueId(scanner);
        overdueDays = InputReader.readIntNonNegative(scanner, "Overdue days");
        breakdown = library.returnItem(catalogueId, overdueDays);
        println(sectionTitle("Return Successful"));
        println(fieldLine("Base Fine", money(breakdown.getBaseFine()) + " EGP"));
        println(fieldLine("Administrative Charge", money(breakdown.getAdministrativeCharge()) + " EGP"));
        println(fieldLine("Total Fine", money(breakdown.getTotalFine()) + " EGP"));
        println(fieldLine("New Balance", money(breakdown.getNewBalance()) + " EGP"));
    }

    private static void renewItem(Scanner scanner, Library library)
    {
        int catalogueId;
        int remainingRenewals;

        catalogueId = readCatalogueId(scanner);
        remainingRenewals = library.renewItem(catalogueId);
        println("Loan renewed successfully.");
        println("Renewals remaining: " + remainingRenewals + ".");
    }

    private static void payFine(Scanner scanner, Library library)
    {
        String membershipId;
        double amount;
        double newBalance;

        membershipId = readMembershipId(scanner);
        amount = InputReader.readDoublePositive(scanner, "Payment amount");
        newBalance = library.payFine(membershipId, amount);
        println("Payment successful.");
        println("New balance: " + money(newBalance) + " EGP");
    }

    // Display Operations

    private static void printCatalogueHeader()
    {
        println(formatRow(
                "ID", "Category", "Title", "Status",
                "Borrower", "Loan", "Fine", "Details"));
    }

    private static void displayItemList(String title, LibraryItem[] items)
    {
        println(sectionTitle(title));
        printCatalogueHeader();
        printList(items);
    }

    private static void viewCatalogue(Library library)
    {
        LibraryItem[] items;

        items = library.getCatalogue();
        if (items.length == 0)
        {
            println("No items found.");
            return;
        }
        displayItemList("Library Catalogue", items);
    }

    private static void searchItem(Scanner scanner, Library library)
    {
        int catalogueId;
        LibraryItem item;

        catalogueId = readCatalogueId(scanner);
        item = library.findItemById(catalogueId);
        if (item == null)
        {
            println("Item not found.");
            return;
        }
        println(sectionTitle("Search Result"));
        printCatalogueHeader();
        println(item);
    }

    private static void viewItemsByStatus(
        Scanner scanner,
        Library library)
    {
        ItemStatus status;
        LibraryItem[] items;

        status = readItemStatus(scanner);
        items = library.getItemsByStatus(status);
        if (items.length == 0)
        {
            println("No items found with status: " + status);
            return;
        }
        displayItemList("Items with Status: " + status, items);
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
        seedLibrary(library);
        displayWelcome();
        while (running)
        {
            displayMenu();
            try
            {
                choice = readMenuChoice(scanner);
                switch (choice)
                {
                    case EXIT_CHOICE -> running = !shouldExit(scanner);
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
                println("Error: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
            }
        }
    }

    // Sample Data

    private static void seedLibrary(Library library)
    {
        LibraryItem[] items;
        Member[] members;

        items = new LibraryItem[] {
                new Book(1, "Clean Code", "Robert C. Martin", 464),
                new Book(2, "The Pragmatic Programmer", "David Thomas", 352),
                new Magazine(3, "National Geographic", 241),
                new Magazine(4, "Scientific American", 320),
                new DVD(5, "The Matrix", 136),
                new DVD(6, "Interstellar", 169)
        };
        for (LibraryItem item : items)
            library.registerItem(item);
        members = new Member[] {
                new Member("Amr Hassan", "M001"),
                new Member("Sara Ali", "M002"),
                new Member("Ahmed Omar", "M003", 40.00, 0)
        };
        for (Member member : members)
            library.registerMember(member);
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
