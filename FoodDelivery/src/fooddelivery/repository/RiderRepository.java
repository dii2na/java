package fooddelivery.repository;

import fooddelivery.model.rider.Rider;
import fooddelivery.utils.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RiderRepository
{
    private final Map<String, Rider> riders;

    public RiderRepository()
    {
        riders = new LinkedHashMap<>();
    }

    public void save(Rider rider)
    {
        rider = Validator.validateNotNull(
            rider, "Rider");

        if (exists(rider.getId()))
            throw new IllegalArgumentException(
                "Rider ID already exists: "
                + rider.getId());

        riders.put(rider.getId(), rider);
    }

    public Optional<Rider> findById(String id)
    {
        id = Validator.validateString(id, "Rider ID");

        return (Optional.ofNullable(riders.get(id)));
    }

    public Collection<Rider> findAll()
    {
        return (Collections.unmodifiableCollection(
            riders.values()));
    }

    public boolean exists(String id)
    {
        id = Validator.validateString(id, "Rider ID");

        return (riders.containsKey(id));
    }
}