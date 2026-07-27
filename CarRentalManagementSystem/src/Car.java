import java.time.Year;

public class Car
{
    // Static Fields
    
    private static int carCount = 0;
    private static final double TAX_RATE = 0.14;

    // Fields

    private final int id;
    private final String brand;
    private final String model;
    private final int year;
    private double pricePerDay;
    private boolean available;

    // Constructor

    public Car(int id, String brand, String model, int year, double pricePerDay)
    {
        this.id = Validator.validatePositive(id, "ID");
        this.brand = Validator.validateString(brand, "Brand");
        this.model = Validator.validateString(model, "Model");
        this.year = Validator.validateIntRange(year, 1990, Year.now().getValue(), "Year");
        this.pricePerDay = Validator.validatePositive(pricePerDay, "Price per day");
        available = true;
        carCount++;
    }

    // Static Getters

    public static int getCarCount()
    {
        return (carCount);
    }

    public static double getTaxRate()
    {
        return (TAX_RATE);
    }

    // Getters

    public int getId()
    {
        return (id);
    }

    public String getBrand()
    {
        return (brand);
    }

    public String getModel()
    {
        return (model);
    }

    public int getYear()
    {
        return (year);
    }

    public double getPricePerDay()
    {
        return (pricePerDay);
    }

    public boolean isAvailable()
    {
        return (available);
    }

    // Setters

    public void setPricePerDay(double pricePerDay)
    {
        this.pricePerDay = Validator.validatePositive(pricePerDay, "Price per day");
    }

    // Rental Logic

    public void rentCar()
    {
        if (!available)
            throw new IllegalStateException("Car is already rented");
        available = false;
    }

    public void returnCar()
    {
        if (available)
            throw new IllegalStateException("Car is already available");
        available = true;
    }

    // Cost Calculation

    protected double addTax(double amount)
    {
        return amount + (amount * TAX_RATE);
    }

    public double calculateRentalCost(int days)
    {
        double subtotal;

        Validator.validatePositive(days, "Rental Days");
        subtotal = pricePerDay * days;
        return (addTax(subtotal));
    }

    // toString

    @Override
    public String toString()
    {
        return """
                ----------------------------------------
                ID: %d
                Brand: %s
                Model: %s
                Year: %d
                Price/Day: $%.2f
                Status: %s
                ----------------------------------------
                """.formatted(
                    id,
                    brand,
                    model,
                    year,
                    pricePerDay,
                    available ? "Available" : "Rented"
                );
    }
}
