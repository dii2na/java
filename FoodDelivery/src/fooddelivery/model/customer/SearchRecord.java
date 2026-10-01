package fooddelivery.model.customer;

import fooddelivery.utils.Validator;
import java.time.LocalDateTime;

public class SearchRecord
{
    private final String description;
    private final LocalDateTime searchedAt;

    public SearchRecord(
        String description,
        LocalDateTime searchedAt)
    {
        this.description = Validator.validateString(
            description, "Search description");

        this.searchedAt = Validator.validateNotNull(
            searchedAt, "Search time");
    }

    public String getDescription()
    {
        return (description);
    }

    public LocalDateTime getSearchedAt()
    {
        return (searchedAt);
    }

    @Override
    public String toString()
    {
        return ("SearchRecord{description=%s, searchedAt=%s}"
            .formatted(description, searchedAt));
    }
}
