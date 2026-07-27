public class Customer
{
    // Static fields

    public static final int NO_CAR = -1;
    private static int customerCount = 0;

    // Fields

    private final int id;
    private String name;
    private String phone;
    private int rentedCarId;
    private int rentedDays;
    private double totalPaid;

    // Constructor

    public Customer(int id, String name, String phone)
    {
        this.id = Validator.validatePositive(id, "ID");
        this.name = Validator.validateString(name, "Name");
        this.phone = Validator.validatePhone(phone);
        rentedCarId = NO_CAR;
        rentedDays = 0;
        totalPaid = 0;
        customerCount++;
    }

    // Static Getters

    public static int getCustomerCount()
    {
        return (customerCount);
    }

    // Getters

    public int getId()
    {
        return (id);
    }

    public String getName()
    {
        return (name);
    }

    public String getPhone()
    {
        return (phone);
    }

    public int getRentedCarId()
    {
        return (rentedCarId);
    }

    public int getRentedDays()
    {
        return (rentedDays);
    }

    public double getTotalPaid()
    {
        return (totalPaid);
    }

    public boolean hasRentedCar()
    {
        return rentedCarId != -1;
    }

    // Setters

    public void setName(String name)
    {
        this.name = Validator.validateString(name, "Name");
    }

    public void setPhone(String phone)
    {
        this.phone = Validator.validatePhone(phone);
    }

    // Rental Logic

    public void rentCar(int carId, int days, double cost)
    {
        if (hasRentedCar())
            throw new IllegalStateException("Customer already has a car");
        this.rentedCarId = carId;
        this.rentedDays = days;
        this.totalPaid += cost;
    }

    public void returnCar()
    {
        if (!hasRentedCar())
            throw new IllegalStateException("Customer has no rented car");
        rentedCarId = NO_CAR;
        rentedDays = 0;
    }

    // toString

    @Override
    public String toString()
    {
        return """
            ----------------------------------------
              Customer ID: %d
              Name: %s
              Phone: %s
            ----------------------------------------
            """.formatted(
                id,
                name,
                phone
            );
    }
}
