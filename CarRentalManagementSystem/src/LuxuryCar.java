public class LuxuryCar extends Car
{
    // Static Fields

    private static final int MIN_RENTAL_DAYS = 3;

    // Fields

    private double insuranceFee;

    // Constructor

    public LuxuryCar(int id, String brand, String model, int year,
                    double pricePerDay, double insuranceFee)
    {
        super(id, brand, model, year, pricePerDay);
        this.insuranceFee = Validator.validateNonNegative(insuranceFee, "Insurance fee");
    }

    // Getters

    public static int getMinRentalDays()
    {
        return (MIN_RENTAL_DAYS);
    }

    public double getInsuranceFee()
    {
        return (insuranceFee);
    }

    // Setters

    public void setInsuranceFee(double insuranceFee)
    {
        this.insuranceFee = Validator.validateNonNegative(insuranceFee, "Insurance fee");
    }

    // Cost Calculation

    @Override
    public double calculateRentalCost(int days)
    {
        double subtotal;

        Validator.validatePositive(days, "Rental days");
        if (days < MIN_RENTAL_DAYS)
            throw new IllegalArgumentException("Luxury car must be rented for at least 3 days");
        subtotal = (getPricePerDay() * days) + insuranceFee;
        return addTax(subtotal);
    }

    @Override
    public String toString()
    {
        return super.toString()
                .replace("----------------------------------------\n", 
                        "Insurance Fee: $" + String.format("%.2f", insuranceFee) 
                        + "\n----------------------------------------\n");
    }
}
