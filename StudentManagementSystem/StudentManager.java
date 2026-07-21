class StudentManager 
{
    // Fields
    private Student[] students;

    // Private Helper Methods
    private boolean containsNullStudent(Student[] students)    
    {
        for (Student student : students) 
        {
            if (student == null)
                return true;
        }
        return false;
    }

    private void checkStudents(Student[] students) 
    {
        if (students == null || students.length == 0)
            throw new IllegalArgumentException("Students array must not be null or empty.");
        if (containsNullStudent(students))
            throw new IllegalArgumentException("Student array must not contain null elements.");
    }

    private void mergeSort(int left, int right)
    {
        int mid;

        if (left < right)
        {
            mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }
    
    private void merge(int left, int mid, int right)
    {
        int leftSize;
        int rightSize;
        int i;
        int j;
        int k;

        leftSize = mid - left + 1;
        rightSize = right - mid;
        k = left;
        Student[] leftArray = new Student[leftSize];
        Student[] rightArray = new Student[rightSize];

        for (i = 0; i < leftSize; i++)
            leftArray[i] = students[left + i];
        for (j = 0; j < rightSize; j++)
            rightArray[j] = students[mid + 1 + j];
        i = 0;
        j = 0;
        while (i < leftSize && j < rightSize)
        {
            if (leftArray[i].getFinalGrade() >= rightArray[j].getFinalGrade())
            {
                students[k] = leftArray[i];
                i++;
            }
            else
            {
                students[k] = rightArray[j];
                j++;
            }
            k++;
        }
        while (i < leftSize)
        {
            students[k] = leftArray[i];
            i++;
            k++;
        }
        while (j < rightSize)
        {
            students[k] = rightArray[j];
            j++;
            k++;
        }
    }

    // Constructors
    public StudentManager(Student[] students) 
    {
        checkStudents(students);
        this.students = new Student[students.length];
        for (int i = 0; i < students.length; i++)
            this.students[i] = new Student(students[i]);
    }

    public StudentManager(StudentManager other) 
    {
        if (other == null)
            throw new IllegalArgumentException("Can not copy from a null StudentManager object.");
        checkStudents(other.students);
        this.students = new Student[other.students.length];
        for (int i = 0; i < other.students.length; i++)
            this.students[i] = new Student(other.students[i]);
    }

    // Public Methods
    public void displayStudents()
    {
        for (Student student : students) 
            student.displayInfo();
    }

    public float calculateAverageGrade()
    {
        float sum;

        sum = 0;
        for (Student student : students)
            sum += student.getFinalGrade();
        return (sum / students.length);
    }

    public Student findHighestGradeStudent()
    {
        Student highestGradeStudent;

        highestGradeStudent = students[0];
        for (int i = 1; i < students.length; i++) 
        {
            if (students[i].getFinalGrade() > highestGradeStudent.getFinalGrade())
                highestGradeStudent = students[i];
        }
        return (highestGradeStudent);
    }

    public Student searchStudentById(int id)
    {
        for (Student student : students) 
        {
            if (student.getId() == id)
                return (student);
        }
        return (null);
    }

    public int[] countPassedAndFailedStudents()
    {
        int passed;
        int failed;

        passed = 0;
        failed = 0;
        for (Student student : students) 
        {
            if (student.gradeStatus().equals("Fail"))
                failed++;
            else
                passed++;
        }
        return (new int[]{passed, failed});
    }

    public void sortStudentsByGrade()
    {
        mergeSort(0, students.length - 1);
    }
}