package fooddelivery.repository;

import fooddelivery.utils.Validator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class KeyedRepository<T>
{
    private final Map<String, T> items;
    private final String entityLabel;
    private final String keyLabel;

    protected KeyedRepository(
        String entityLabel,
        String keyLabel)
    {
        this.entityLabel = entityLabel;
        this.keyLabel = keyLabel;
        this.items = new LinkedHashMap<>();
    }

    // What each repository must provide

    protected abstract String keyOf(T item);

    protected String normalizeKey(String key)
    {
        return (key);
    }

    // Shared behaviour

    public void save(T item)
    {
        String key;

        item = Validator.validateNotNull(item, entityLabel);
        key = keyOf(item);

        if (exists(key))
            throw new IllegalArgumentException(
                keyLabel + " already exists: " + key);

        items.put(key, item);
    }

    public Optional<T> findByKey(String key)
    {
        key = Validator.validateString(key, keyLabel);

        return (Optional.ofNullable(
            items.get(normalizeKey(key))));
    }

    public Collection<T> findAll()
    {
        return (Collections.unmodifiableCollection(
            items.values()));
    }

    public boolean exists(String key)
    {
        key = Validator.validateString(key, keyLabel);

        return (items.containsKey(normalizeKey(key)));
    }

    public void remove(String key)
    {
        key = normalizeKey(
            Validator.validateString(key, keyLabel));

        if (!items.containsKey(key))
            throw new IllegalArgumentException(
                entityLabel + " does not exist: " + key);

        items.remove(key);
    }
}
