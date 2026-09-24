package fooddelivery.repository;

import fooddelivery.model.customer.Customer;
import fooddelivery.utils.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CustomerRepository
{
    private final Map<String, Customer> customers;

    public CustomerRepository()
    {
        customers = new LinkedHashMap<>();
    }

    public void save(Customer customer)
    {
        customer = Validator.validateNotNull(
            customer, "Customer");

        if (exists(customer.getId()))
            throw new IllegalArgumentException(
                "Customer ID already exists: "
                + customer.getId());

        customers.put(customer.getId(), customer);
    }

    public Optional<Customer> findById(String id)
    {
        id = Validator.validateString(id, "Customer ID");

        return (Optional.ofNullable(customers.get(id)));
    }

    public Collection<Customer> findAll()
    {
        return (Collections.unmodifiableCollection(
            customers.values()));
    }

    public boolean exists(String id)
    {
        id = Validator.validateString(id, "Customer ID");

        return (customers.containsKey(id));
    }
}