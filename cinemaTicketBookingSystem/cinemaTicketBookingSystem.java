import java.util.*;

public class cinemaTicketBookingSystem
{
    static int readNumber(Scanner sc, String type, boolean allowZero)
    {
        int num;

        do
        {
            try
            {
                System.out.print("Enter the number of " + type + ": ");
                num = sc.nextInt();
                if (!allowZero && num <= 0)
                    System.out.println("\nNumber must be positive.");
                if (allowZero && num < 0)
                    System.out.println("Number must not be negative.");
            }
            catch (InputMismatchException e)
            {
                System.out.println("Please enter numbers only.");
                sc.nextLine();
                num = -1;
            }

        }
        while (allowZero? num < 0 : num <= 0);
        sc.nextLine();

        return num;
    }

    static char[][] initializeSeats(Scanner sc)
    {
        int rows;
        int cols;

        rows = readNumber(sc, "rows", false);
        cols = readNumber(sc, "columns", false);
        char[][] seats = new char[rows][cols];
        for (char[] seat : seats)
        {
            Arrays.fill(seat, 'O');
        }

        return seats;
    }

    static String[] initializeMovies(Scanner sc)
    {
        int numOfMovies;

        numOfMovies = readNumber(sc, "movies", false);
        String[] movieNames = new String[numOfMovies];

        System.out.println("Enter the names of the movies:");
        for (int i = 0; i < numOfMovies; i++)
        {
            do
            {
                movieNames[i] = sc.nextLine();
                if(movieNames[i].isBlank())
                    System.out.println("Movie name can't be empty");
            }
            while(movieNames[i].isBlank());
        }
        return movieNames;
    }

    static void displayMenu()
    {
        System.out.println("""
            
            ===== Cinema Menu =====
            1. Display Seats
            2. Book Seat
            3. Cancel Booking
            4. Show all movies
            5. Show number of available and booked seats
            0. Exit
            Choose:
            """);
    }

    static void displaySeats(char[][] seats)
    {
            System.out.print("\n===== Seats =====\n  ");
            for (int j = 0; j < seats[0].length; j++)
            {
                System.out.print((j + 1) + " ");
            }
            System.out.println();
            for (int i = 0; i < seats.length; i++)
            {
                System.out.print((i + 1) + " ");
                for (int j = 0; j < seats[i].length; j++)
                {
                    System.out.print(seats[i][j] + " ");
                }
                System.out.println();
            }
    }

    static int[] readSeat(Scanner sc, char[][] seats)
    {
        int seatNumber;
        int row;
        int col;
        int digits;

        System.out.println("Note: Enter seat as row and column (e.g., 11).");
        seatNumber = readNumber(sc, "seat", false);
        digits = String.valueOf(seatNumber).length();
        row = seatNumber / 10 - 1;
        col = seatNumber % 10 - 1;

        if (digits != 2 ||
            row >= seats.length || row < 0 ||
            col >= seats[0].length || col < 0)
        {
            System.out.println("Invalid seat.");
            return null;
        }

        return new int[]{row, col};
    }

    static void bookSeat(Scanner sc, char[][] seats)
    {
        changeSeatStatus(sc,
                        seats,
                	'X',
                        "Seat already booked.",
                        "Seat booked successfully.");
    }

    static void cancelBookedSeat(Scanner sc, char[][] seats)
    {
        changeSeatStatus(sc,
                        seats,
                	'O',
                	"Seat already available.",
                	"Booking cancelled successfully.");
    }

    static void changeSeatStatus(Scanner sc, char[][] seats, char newStatus,
                                 String failedMessage, String successMessage)
    {
        int[] seatNumber;
        int row;
        int col;

        seatNumber = readSeat(sc, seats);
        if (seatNumber == null)
            return;
        row = seatNumber[0];
        col = seatNumber[1];
        if (seats[row][col] == newStatus)
        {
            System.out.println(failedMessage);
            return;
        }
        seats[row][col] = newStatus;
        System.out.println(successMessage);
	displaySeats(seats);
    }

    static void showMovies(String[] movieNames)
    {
        System.out.println("\n===== Movies =====");
        for (int i = 0; i < movieNames.length; i++)
        {
            System.out.println((i + 1) + ". " + movieNames[i]);
        }
    }

    static void showSeatStatus(char[][] seats)
    {
        int available;
        int booked;
        int totalSeats;
        double occupancy;

        available = 0;
        booked = 0;
        for (char[] row :  seats)
        {
            for (char seat : row)
            {
                if (seat == 'O')
                    available++;
                else
                    booked++;
            }
        }
        totalSeats = available + booked;
        occupancy = (double) booked / totalSeats * 100;
        displaySeats(seats);
        System.out.println("Available seats: " + available);
        System.out.println("Booked seats: " + booked);
        if (occupancy > 80)
            System.out.println("Almost Full!");
    }

    static void startCinemaSystem(Scanner sc, char[][] seats, String[] movieNames)
    {
        int choice;

        do
        {
            displayMenu();
            choice = readNumber(sc, "your choice", true);
             switch (choice)
             {
                 case 0 -> { return; }
                 case 1 -> displaySeats(seats);
                 case 2 -> bookSeat(sc, seats);
                 case 3 -> cancelBookedSeat(sc, seats);
                 case 4 -> showMovies(movieNames);
                 case 5 -> showSeatStatus(seats);
                 default -> System.out.println("Invalid choice");
             }
        }
        while (true);
    }

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        char[][] seats = initializeSeats(sc);
        String[] movieNames = initializeMovies(sc);

        startCinemaSystem(sc, seats, movieNames);
        sc.close();
    }
}

