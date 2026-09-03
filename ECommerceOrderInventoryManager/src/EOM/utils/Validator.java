package EOM.utils;

import java.util.function.Predicate;

public class Validator
{
    // Generic Validation

    public static <T> T validate(
        T value,
        Predicate<T> condition,
        String message)
    {
        if (!condition.test(value))
            throw new IllegalArgumentException(message);

        return (value);
    }

    // Numeric Validation

    public static int validatePositive(
        int value, String fieldName)
    {
        return (validate(
            value,
            number -> number > 0,
            fieldName + " must be positive"));
    }

    public static double validatePositive(
        double value, String fieldName)
    {
        return (validate(
            value,
            number -> number > 0,
            fieldName + " must be positive"));
    }

    public static int validateNonNegative(
        int value, String fieldName)
    {
        return (validate(
            value,
            number -> number >= 0,
            fieldName + " cannot be negative"));
    }

    // String Validation

    public static String validateString(
        String value, String fieldName)
    {
        validateNotNull(value, fieldName);
        value = value.trim();

        return (validate(
            value,
            text -> !text.isBlank(),
            fieldName + " cannot be empty"));
    }

    // Object Validation

    public static <T> T validateNotNull(
        T value, String message)
    {
        return (validate(
            value,
            object -> object != null,
            message));
    }
}

