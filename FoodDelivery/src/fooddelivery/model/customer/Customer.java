package fooddelivery.model.customer;

import fooddelivery.exception.InsufficientWalletException;
import fooddelivery.utils.Validator;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Customer
{
    private final String id;
    private final String name;
    private final String mobile;
    private final Set<Address> addresses;
    private BigDecimal wallet;
    private int completedOrderCount;
    private final Deque<SearchRecord> searchHistory;

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
        this.addresses = new LinkedHashSet<>();
        this.addresses.add(Validator.validateNotNull(address, "Address"));
        this.completedOrderCount = 0;
        this.searchHistory = new ArrayDeque<>();
    }

    // Reading the customer

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

    // Addresses, wallet and search history

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

    public boolean hasSufficientBalance(BigDecimal amount)
    {
        amount = Validator.validatePositive(
            amount, "Amount to check");

        return (wallet.compareTo(amount) >= 0);
    }

    public void pay(BigDecimal amount)
    {
        amount = Validator.validatePositive(amount, "Amount to pay");

        if (!hasSufficientBalance(amount))
            throw new InsufficientWalletException(
                "Insufficient funds in wallet");

        wallet = wallet.subtract(amount);
    }

    public void addSearchRecord(SearchRecord searchRecord)
    {
        searchRecord = Validator.validateNotNull(
            searchRecord, "Search record");

        searchHistory.addFirst(searchRecord);

        if (searchHistory.size() > 5)
            searchHistory.removeLast();
    }

    public List<SearchRecord> getSearchHistory()
    {
        return (Collections.unmodifiableList(
            new ArrayList<>(searchHistory)));
    }

    // Textual representation

    @Override
    public String toString()
    {
        return ("Customer{id=%s, name=%s, mobile=%s, tier=%s, wallet=%s}"
            .formatted(
                id,
                name,
                mobile,
                getLoyaltyTier(),
                wallet));
    }

    // Identity

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
