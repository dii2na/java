package fooddelivery.repository;

import fooddelivery.model.customer.Customer;
import java.util.Optional;

public class CustomerRepository
    extends KeyedRepository<Customer>
{
    public CustomerRepository()
    {
        super("Customer", "Customer ID");
    }

    // Finding a customer by its key

    @Override
    protected String keyOf(Customer customer)
    {
        return (customer.getId());
    }

    public Optional<Customer> findById(String id)
    {
        return (findByKey(id));
    }
}
