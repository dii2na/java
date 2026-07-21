class Student 
{
    // Fields
    private int id;
    private String name;
    private float[] grades;
    private float finalGrade;

    // private Helper Methods
    private void checkGrades(float[] grades) 
    {
        if (grades == null || grades.length != 2)
            throw new IllegalArgumentException("Grades array must contain exactly 2 elements.");
    }

    private float calculateFinalGrade() 
    {
        float sum;

        sum = 0;
        for (float grade : grades)
            sum += grade;
         return  (sum / grades.length);
    }

    // Constructors
    public Student(int id, String name, float[] grades) 
    {
        checkGrades(grades);
        this.grades = grades.clone();
        this.id = id;
        this.name = name;
        finalGrade = calculateFinalGrade();
    }

    public Student(Student other)
    {
        if (other == null)
            throw new IllegalArgumentException("Can not copy from a null Student object.");
        checkGrades(other.grades);
        this.id = other.id;
        this.name = other.name;
        this.grades = other.grades.clone();
        this.finalGrade = other.finalGrade;
    }

    // Public Methods
    public int getId() 
    {
        return (id);
    }

    public String getName() 
    {
        return (name);
    }

    public float[] getGrades() 
    {
        return (grades.clone());
    }

    public float getFinalGrade() 
    {
        return (finalGrade);
    }

    public String gradeStatus()
    {
        if (finalGrade >= 90)
            return ("Excellent");
        else if (finalGrade >= 75)
            return ("Very Good");
        else if (finalGrade >= 60)
            return ("Pass");
        else
            return  ("Fail");
    }

    public void displayInfo()
    {
        String status;

        status = gradeStatus();
        System.out.println("""
                Student ID: %d
                Student Name: %s
                Student Final Grade: %.2f
                Student Status: %s
                """.formatted(id, name, finalGrade, status));
    }
}