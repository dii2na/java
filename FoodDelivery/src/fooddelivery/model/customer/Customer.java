package fooddelivery.model.customer;

import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Customer
{
    private final String id;
    private final String name;
    private final String mobile;
    private final Set<Address> addresses;
    private BigDecimal wallet;
    private int completedOrderCount;

    public Customer(
        String id,
        String name,
        String mobile,
        Address address)
    {
        this.id = Validator.validateString(id, "Customer ID");
        this.name = Validator.validateString(name, "Customer name");
        this.mobile = CustomerValidator.validateEgyptianMobile(mobile);
        this.wallet = BigDecimal.ZERO;
        this.addresses = new HashSet<>();
        this.addresses.add(Validator.validateNotNull(address, "Address"));
        this.completedOrderCount = 0;
    }

    public String getId()
    {
        return (id);
    }

    public String getName()
    {
        return (name);
    }

    public String getMobile()
    {
        return (mobile);
    }

    public BigDecimal getWallet()
    {
        return (wallet);
    }

    public int getCompletedOrderCount()
    {
        return (completedOrderCount);
    }

    public Set<Address> getAddresses()
    {
        return (Collections.unmodifiableSet(addresses));
    }

    public LoyaltyTier getLoyaltyTier()
    {
        return (LoyaltyTier.fromCompletedOrderCount(completedOrderCount));
    }

    public void addAddress(Address address)
    {
        address = Validator.validateNotNull(address, "Address");
        addresses.add(address);
    }

    public void addToWallet(BigDecimal amount)
    {
        amount = Validator.validatePositive(amount, "Amount to add to wallet");
        wallet = wallet.add(amount);
    }

    public void incrementCompletedOrderCount()
    {
        completedOrderCount++;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return (true);

        if (!(object instanceof Customer other))
            return (false);

        return (id.equals(other.id));
    }

    @Override
    public int hashCode()
    {
        return (id.hashCode());
    }
}