import java.util.InputMismatchException;
import java.util.Scanner;

public class Main 
{
    // input methods
    private static int readNumber(Scanner scanner, String prompt, int min, int max)
    {
        int num;

        while (true)
        {
            try
            {
                System.out.print("Enter the number of " + prompt + ": ");
                num = scanner.nextInt();
                scanner.nextLine();
                if (num >= min && num <= max)
                    return num;
                System.out.println("Invalid range.");

            }
            catch (InputMismatchException e)
            {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }
    }

    private static Student readStudent(Scanner scanner, Student[] students)
    {
        int id;
        String name;
        float[] grades = new float[2];

        while (true)
        {
            id = readNumber(scanner, "student ID", 1, Integer.MAX_VALUE);
            if (!idExists(students, id))
                break;
            System.out.println("Student ID already exists. Please enter a unique ID.");
        }
        while (true)
        {
            System.out.print("Enter student name: ");
            name = scanner.nextLine();
            if (!name.trim().isEmpty())
                break;
            System.out.println("Name cannot be empty. Please enter a valid name.");
        }
        grades[0] = readNumber(scanner, "first grade (0-100)", 0, 100);
        grades[1] = readNumber(scanner, "second grade (0-100)", 0, 100);

        return new Student(id, name, grades);
    }

    // validation methods
    private static boolean idExists(Student[] students, int id)
    {
        for (Student student : students)
        {
            if (student != null && student.getId() == id)
                return true;
        }
        return false;
    }    

    // display methods
    private static void displayStudents(StudentManager manager)
    {
        manager.displayStudents();
    }

    private static void displayAverageGrade(StudentManager manager)
    {
        System.out.printf("Average grade: %.2f%n", manager.calculateAverageGrade());
    }

    private static void displayHighestStudent(StudentManager manager)
    {
        Student student;

        student = manager.findHighestGradeStudent();
        System.out.printf(
            "Highest grade student ID: %d with grade %.2f%n",
            student.getId(),
            student.getFinalGrade()
        );
    }

    private static void displaySearchStudent(Scanner scanner, StudentManager manager)
    {
        int id;
        Student student;

        id = readNumber(scanner, "student ID", 1, Integer.MAX_VALUE);
        student = manager.searchStudentById(id);
        if (student != null)
            student.displayInfo();
        else
            System.out.println("Student not found.");
    }

    private static void displayPassedFailedStudents(StudentManager manager)
    {
        int[] passedFailedCounts;
        passedFailedCounts = manager.countPassedAndFailedStudents();

        System.out.println("Number of students who passed: " + passedFailedCounts[0]);
        System.out.println("Number of students who failed: " + passedFailedCounts[1]);
    }

    private static void displayMenu()
    {
        System.out.println("""
            
            ===== Student Management System =====
            1. Display all students
            2. Calculate average grade
            3. Find highest grade student
            4. Search student by ID
            5. Count passed and failed students
            6. Sort students by grade
            0. Exit
            """);
    }

    // operation methods
    private static void sortStudents(StudentManager manager)
    {
        manager.sortStudentsByGrade();
        System.out.println("Students sorted successfully.");
    }

    // exit method
    private static void exitProgram(Scanner scanner)
    {
        String input;
        char response; 

        while (true)
        {
            System.out.println("Are you sure you want to exit? (y/n)");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty())
            {
                System.out.println("Invalid input.");
                continue;
            }
            response = input.charAt(0);         
            switch (response)
            {
                case 'y':
                    System.out.println("Exiting the program. Goodbye!");
                    scanner.close();
                    System.exit(0);
                case 'n':
                    return;
                default:
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    // Main method
    public static void main(String[] args) 
    {
        Scanner scanner;
        int numOfStudents;
        int choice;

        scanner = new Scanner(System.in);   
        numOfStudents = readNumber(scanner, "students", 1, 100);
        Student[] students = new Student[numOfStudents];
        for (int i = 0; i < numOfStudents; i++)
        {
            System.out.print("Student " + (i + 1) + ":\n");
            students[i] = readStudent(scanner, students);
            System.out.println("Student added successfully.\n");
        }
        StudentManager manager = new StudentManager(students);

        while (true)
        {
            displayMenu();
            choice = readNumber(scanner, "your choice", 0, 6);
            switch (choice)
            {
                case 0 -> exitProgram(scanner);
                case 1 -> displayStudents(manager);
                case 2 -> displayAverageGrade(manager);
                case 3 -> displayHighestStudent(manager);
                case 4 -> displaySearchStudent(scanner, manager);
                case 5 -> displayPassedFailedStudents(manager);
                case 6 -> sortStudents(manager);
            }
        }
    }
}