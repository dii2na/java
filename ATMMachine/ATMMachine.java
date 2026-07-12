import java.util.Scanner;
import java.util.InputMismatchException;

public class ATMMachine
{
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);

        final short CORRECT_PIN = 1234;
        final double INITIAL_BALANCE = 2500.75;

        short attempts;
        short choice;
        short enteredPin;

        double balance;
        double amount;

        int transactionCount;

        attempts = 3;
        balance = INITIAL_BALANCE;
        transactionCount = 0;
	try
	{
		while (attempts-- > 0)
		{
		    System.out.print("Enter your 4-digit PIN: ");
		    enteredPin = input.nextShort();

		    if (enteredPin == CORRECT_PIN)
		    {
		        System.out.println("Login successful.");
		        break;
		    }
		    else
		    {
		        System.out.println("Incorrect PIN.");
		    }
		}

		if (attempts < 0)
		{
		    System.out.println("Your account has been locked.");
		    input.close();
		    return;
		}

		do
		{
		    System.out.println("""

				========= ATM =========
				1. Check Balance
				2. Deposit
				3. Withdraw
				4. Show Account Status
				5. Exit
				=======================
				""");

		    System.out.print("Choose an option: ");
		    choice = input.nextShort();

		    switch (choice)
		    {
		        case 1:
		            System.out.println("Current Balance: " + balance);
		            break;

		        case 2:
		            System.out.print("Enter deposit amount: ");
		            amount = input.nextDouble();

		            if (amount <= 0)
		            {
		                System.out.println("Invalid amount.");
		            }
		            else
		            {
		                balance += amount;
		                transactionCount++;

		                System.out.println("Updated Balance: " + balance);
		            }
		            break;

		        case 3:
		            System.out.print("Enter withdrawal amount: ");
		            amount = input.nextDouble();

		            if (amount == 0)
		            {
		                System.out.println("Transaction cancelled.");
		            }
		            else if (amount < 0)
		            {
		                System.out.println("Invalid amount.");
		            }
		            else if (amount > balance)
		            {
		                System.out.println("Insufficient balance.");
		            }
		            else 
		            {
		                balance -= amount;
		                transactionCount++;

		        	System.out.println("Updated Balance: " + balance);
		                if (balance == 0)
		                {
		                    System.out.println("Warning: Your account is empty.");
		                }
		            }
		            break;

		        case 4:
		            if (balance >= 5000)
		            {
		                System.out.println("VIP Customer");
		            }
		            else if (balance >= 1000)
		            {
		                System.out.println("Regular Customer");
		            }
		            else
		            {
		                System.out.println("Low Balance");
		            }
		            break;

		        case 5:
		            System.out.println("Thank you for using our ATM.");
		            System.out.println("Successful Transactions: " + transactionCount);
		            break;

		        default:
		            System.out.println("Invalid option.");
		    }

		}
		while (choice != 5);
	}
        catch (InputMismatchException e)
        {
            System.out.println("Invalid input.");
	}
        input.close();
    }
}
