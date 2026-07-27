public class RentalSystem
{
    // Fields

    private final Car[] cars;
    private final Customer[] customers;
    private int carIndex;
    private int customerIndex;
    private double totalIncome;

    // Constructor
    
    public RentalSystem(int maxCars, int maxCustomers)
    {
        Validator.validatePositive(maxCars, "Max cars");
        Validator.validatePositive(maxCustomers, "Max customers");
        cars = new Car[maxCars];
        customers = new Customer[maxCustomers];
        carIndex = 0;
        customerIndex = 0;
        totalIncome = 0;
    }

    // Private Helpers

    private Customer getCustomer(int id)
    {
        Customer customer;

        customer = findCustomerById(id);
        if (customer == null)
            throw new IllegalArgumentException("Customer not found");
        return (customer);
    }

    private Car getCar(int id)
    {
        Car car;

        car = findCarById(id);
        if (car == null)
            throw new IllegalArgumentException("Car not found");
        return (car);
    }

    private void addCarToFleet(Car car)
    {
        if (carIndex >= cars.length)
            throw new IllegalStateException("No more space available");
        if (carExists(car.getId()))
            throw new IllegalArgumentException("Car ID already exists");
        cars[carIndex] = car;
        carIndex++;
    }

    // Car Queries

    public Car findCarById(int id)
    {
        for (int i = 0; i < carIndex; i++)
        {
            if (cars[i].getId() == id)
                return (cars[i]);
        }
        return (null);
    }

    public boolean carExists(int id)
    {
        return (findCarById(id) != null);
    }

    public boolean isCarAvailable(int id)
    {
        Car car;

        car = findCarById(id);
        return (car != null && car.isAvailable());
    }

    public Car[] getAllCars()
    {
        Car[] result;

        result = new Car[carIndex];
        System.arraycopy(cars, 0, result, 0, carIndex);
        return (result);
    }

    public Car[] getAvailableCars()
    {
        int count;
        Car[] available;

        count = 0;
        for (int i = 0; i < carIndex; i++)
        {
            if (cars[i].isAvailable())
                count++;
        }
        available = new Car[count];
        for (int i = 0, j = 0; i < carIndex; i++)
        {
            if (cars[i].isAvailable())
                available[j++] = cars[i];
        }
        return (available);
    }

    public Car[] findCarsByBrand(String brand)
    {
        int count;
        Car[] matches;

        count = 0;
        for (int i = 0; i < carIndex; i++)
        {
            if (cars[i].getBrand().equalsIgnoreCase(brand))
                count++;
        }
        matches = new Car[count];
        for (int i = 0, j = 0; i < carIndex; i++)
        {
            if (cars[i].getBrand().equalsIgnoreCase(brand))
                matches[j++] = cars[i];
        }
        return (matches);
    }

    // Customer Queries

    public Customer findCustomerById(int id)
    {
        for (int i = 0; i < customerIndex; i++)
        {
            if (customers[i].getId() == id)
                return (customers[i]);
        }
        return (null);
    }

    public boolean customerExists(int id)
    {
        return (findCustomerById(id) != null);
    }

    public boolean isCustomerRentingCar(int id)
    {
        Customer customer;

        customer = findCustomerById(id);
        return (customer != null && customer.hasRentedCar());
    }

    public Customer[] getCustomers()
    {
        Customer[] result;

        result = new Customer[customerIndex];
        System.arraycopy(customers, 0, result, 0, customerIndex);
        return (result);
    }

    public Car findCustomerCar(int customerId)
    {
        Customer customer;

        customer = findCustomerById(customerId);
        if (customer == null || !customer.hasRentedCar())
            return (null);
        return (findCarById(customer.getRentedCarId()));
    }

    // Car Management

    public void addCar(int id, String brand, String model, int year, double pricePerDay)
    {
        addCarToFleet(new Car(id, brand, model, year, pricePerDay));
    }

    public void addLuxuryCar(int id, String brand, String model,
                            int year, double pricePerDay, double insuranceFee)
    {
        addCarToFleet(new LuxuryCar(id, brand, model, year,
                                    pricePerDay, insuranceFee));
    }

    // Customer Management

    public void addCustomer(int id, String name, String phone)
    {
        if (customerIndex >= customers.length)
            throw new IllegalStateException("No more space available");
        if (customerExists(id))
            throw new IllegalArgumentException("Customer ID already exists");
        customers[customerIndex] = new Customer(id, name, phone);
        customerIndex++;
    }

    // Rental Operations

    public String rentCar(int customerId, int carId, int days)
    {
        Customer customer;
        Car car;
        double cost;

        customer = getCustomer(customerId);
        car = getCar(carId);
        if (!car.isAvailable())
            throw new IllegalStateException("Car is already rented");
        cost = car.calculateRentalCost(days);
        customer.rentCar(carId, days, cost);
        car.rentCar();
        totalIncome += cost;
        return (getReceipt(customer, car, days, cost));
    }

    public String returnCar(int customerId)
    {
        Customer customer;
        Car car;

        customer = getCustomer(customerId);
        if (!customer.hasRentedCar())
            throw new IllegalStateException("Customer has no rented car");
        car = getCar(customer.getRentedCarId());
        customer.returnCar();
        car.returnCar();
        return (customer.getName() + " returned " +
                car.getBrand() + " " + car.getModel() + " successfully.");
    }

    private String getReceipt(Customer customer, Car car, int days, double cost)
    {
        return """
                ========================================
                           RENTAL RECEIPT
                ========================================
                  Customer: %s
                  Car: %s %s (%d)
                  Rental Days: %d
                  Total Cost: $%.2f
                ========================================
                """.formatted(customer.getName(), car.getBrand(),
                              car.getModel(), car.getYear(), days, cost);
    }

    // Statistics

    public int countRentedCars()
    {
        int count;

        count = 0;
        for (int i = 0; i < carIndex; i++)
        {
            if (!cars[i].isAvailable())
                count++;
        }
        return (count);
    }

    public Car getMostExpensiveCar()
    {
        Car expensiveCar;

        if (carIndex == 0)
            return (null);
        expensiveCar = cars[0];
        for (int i = 1; i < carIndex; i++)
        {
            if (cars[i].getPricePerDay() > expensiveCar.getPricePerDay())
                expensiveCar = cars[i];
        }
        return (expensiveCar);
    }

    public double calculateAveragePrice()
    {
        double total;

        if (carIndex == 0)
            return (0);
        total = 0;
        for (int i = 0; i < carIndex; i++)
        {
            total += cars[i].getPricePerDay();
        }
        return (total / carIndex);
    }

    public String getStatistics()
    {
        if (carIndex == 0)
            return ("No cars in the fleet.");

        return """
                ========================================
                          FLEET STATISTICS
                ========================================
                  Total Cars: %d
                  Rented Cars: %d
                  Available Cars: %d
                  Most Expensive Car: %s %s (%d) - $%.2f/day
                  Average Price per Day: $%.2f
                ========================================
                """.formatted(carIndex, countRentedCars(),
                              carIndex - countRentedCars(),
                              getMostExpensiveCar().getBrand(),
                              getMostExpensiveCar().getModel(),
                              getMostExpensiveCar().getYear(),
                              getMostExpensiveCar().getPricePerDay(),
                              calculateAveragePrice());
    }

    public String getSummary()
    {
        return """
                ========================================
                           EXIT SUMMARY
                ========================================
                  Total Cars: %d
                  Total Customers: %d
                  Total Income: $%.2f
                ========================================
                """.formatted(carIndex, customerIndex, totalIncome);
    }

    // Getters

    public int getCarCount()
    {
        return (carIndex);
    }

    public int getCustomerCount()
    {
        return (customerIndex);
    }

    public double getTotalIncome()
    {
        return (totalIncome);
    }
}
