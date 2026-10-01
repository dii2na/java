package fooddelivery.repository;

import fooddelivery.model.rider.Rider;
import java.util.Optional;

public class RiderRepository
    extends KeyedRepository<Rider>
{
    public RiderRepository()
    {
        super("Rider", "Rider ID");
    }

    // Finding a rider by its key

    @Override
    protected String keyOf(Rider rider)
    {
        return (rider.getId());
    }

    public Optional<Rider> findById(String id)
    {
        return (findByKey(id));
    }
}
