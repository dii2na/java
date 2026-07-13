import java.util.Scanner;
import java.util.InputMismatchException;

class StudentGradeManagementSystem
{
	static void inputStudents(Scanner input, String[] studentsNames, short[][] studentsGrades)
	{

		short grade;

		for (int i = 0; i < studentsNames.length; i++)
		{
			System.out.print("PLease enter the student Name: ");
			studentsNames[i] = input.nextLine();
			for (int j = 0; j < studentsGrades[i].length; j++)
			{	
				do
				{
					try
					{
						System.out.print("Enter the grade for subject " + (j + 1) + ": ");
						grade = input.nextShort();

						if (grade < 0 || grade > 100)
							System.out.println("Invalid grade");
					}
					  catch(InputMismatchException e)
					    {
						System.out.println("Please enter a number.");
						input.nextLine(); 
						grade = -1; 
					    }
				}
				while (grade < 0 || grade > 100);
				studentsGrades[i][j] = grade;
			}
			input.nextLine();
		}
	}

	static void showStudentsNames(String[] studentsNames)
	{
	    for(int i = 0; i < studentsNames.length; i++)
	    {
		System.out.println(studentsNames[i]);
	    }
	}

	static void showStudentInfo(String name, short[] grades)
	{
	    System.out.print(name + "\t");

	    for (int j = 0; j < grades.length; j++)
	    {
		System.out.print(grades[j] + "\t\t");
	    }

	    System.out.println();
	}

	static void showStudentsGrades(String[] studentsNames, short[][] studentsGrades)
	{
	    System.out.println("Name\tSubject 1\tSubject 2\tSubject 3");
	    System.out.println("-----------------------------------------------");

	    for (int i = 0; i < studentsNames.length; i++)
	    {
		showStudentInfo(studentsNames[i], studentsGrades[i]);
	    }
	}

	static void searchStudent(Scanner input, String[] studentsNames, short[][] studentsGrades)
	{
		String searchName;

		input.nextLine(); 
		System.out.print("Enter student name: ");
		searchName = input.nextLine();
		for (int i = 0; i < studentsNames.length; i++)
		{
			if (studentsNames[i].equals(searchName))
			{
				showStudentInfo(studentsNames[i], studentsGrades[i]);
				return ;
			}
		}
		System.out.println("Student not found.");
	}

	static void countPassedStudents(String[] studentsNames, short[][] studentsGrades)
	{
		short totalPassed;
		boolean passedAllSubj;
		
		totalPassed = 0;
		for (int i = 0; i< studentsNames.length; i++)
		{
			passedAllSubj = true;
			System.out.print(studentsNames[i] + " ");
			for (int j = 0; j< studentsGrades[i].length; j++)
			{
				if (studentsGrades[i][j] < 50)
				{
					System.out.println("Failed");
					passedAllSubj = false;
					break;
				}
			}
			if (passedAllSubj)
			{
				System.out.println("Passed");
				totalPassed++;
			}
		}
		System.out.println("Total passed: " + totalPassed);
	}
	
	static void showAverage(short[][] studentsGrades)
	{
		short sum;
		float avg;

		System.out.println("Subject\tAverage");
		System.out.println("----------------");	
		for (int i = 0; i < studentsGrades[0].length; i++)
		{
			sum = 0;
			for (int j = 0; j < studentsGrades.length; j++)
			{
				sum += studentsGrades[j][i];
			}
			avg = (float) sum / studentsGrades.length;
			System.out.println("Subject " + (i + 1) + "\t" + avg);
		}

	}

	static void showHighestGrade(short[][] studentsGrades)
	{
		short max;
		
		System.out.println("Subject\tHighest-Grade");
		System.out.println("---------------------");
		for (int i = 0; i < studentsGrades[0].length; i++)		
		{
			max = studentsGrades[0][i];
			for (int j = 1; j < studentsGrades.length; j++)
			{
				if (studentsGrades[j][i] > max)
					max = studentsGrades[j][i];
			}
			System.out.println("Subject " + (i + 1) + "\t" + max);
		}
	}

	static char getLetterGrade(short grade)
	{
		if (grade >= 85)
			return 'A';
		else if (grade >= 75)
			return 'B';
		else if (grade >= 65)
			return 'C';
		else if (grade >= 50)
			return 'D';
		else
			return 'F';
		
	}

	static void showLetterGrades(String[] studentsNames, short[][] studentsGrades)
	{	
		System.out.println("Name\tSubject 1\tSubject 2\tSubject 3");
	    	System.out.println("-----------------------------------------------");

		for (int i = 0; i < studentsNames.length; i++)
	    	{
			System.out.print(studentsNames[i] + "\t");
			for (int j = 0; j < studentsGrades[i].length; j++)
				System.out.print(getLetterGrade(studentsGrades[i][j]) + "\t");
			System.out.println();
	    	}
	}

	static void showMenu(Scanner input, String[] studentsNames, short[][] studentsGrades)
	{
		short choice;

		do
		{
			System.out.print("""

					1. Show All Students names.
					2. Show all Students grades in each subject.
					3. Search Student by name.
					4. Count Passed Students
					5. Show Average for each subject.
					6. Show Highest grade in each subject.
					7. Show Letter Grades.
					0. Exit

					""");

			choice = input.nextShort();
			switch (choice)
			{
				case 0 -> { return ;}
				case 1 -> showStudentsNames(studentsNames);
				case 2 -> showStudentsGrades(studentsNames, studentsGrades);
				case 3 -> searchStudent(input, studentsNames, studentsGrades);
				case 4 -> countPassedStudents(studentsNames, studentsGrades);
				case 5 -> showAverage(studentsGrades);
				case 6 -> showHighestGrade(studentsGrades);
				case 7 -> showLetterGrades(studentsNames, studentsGrades);
				default -> System.out.println("Invalid choice");
			}
			

		}
		while (choice != 0);
	}

	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		String studentsNames[] = new String[5];
		short studentsGrades[][] = new short[5][3];

		
		inputStudents(input, studentsNames, studentsGrades);
		showMenu(input, studentsNames, studentsGrades);
		
	}
}
