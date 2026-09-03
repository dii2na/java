package ROM.utils;

import java.util.function.Predicate;

public class Validator
{
    // Numeric Validation

    public static int validatePositive(int value, String fieldName)
    {
        return validate(
            value,
            number -> number > 0,
            fieldName + " must be positive");
    }

    public static double validatePositive(double value, String fieldName)
    {
        return validate(
            value,
            number -> number > 0,
            fieldName + " must be positive");
    }

    public static double validateNonNegative(double value, String fieldName)
    {
        return validate(
            value,
            number -> number >= 0,
            fieldName + " cannot be negative");
    }

    // String Validation

    public static String validateString(String value, String fieldName, boolean optional)
    {
        validateNotNull(value, fieldName);
        value = value.trim();
        if (!optional && value.isBlank())
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        return (value);
    }

    // Object Validation

    public static <T> T validateNotNull(T value, String message)
    {
        if (value == null)
            throw new IllegalArgumentException(message);
        return (value);
    }

    private static <T> T validate(
        T value,
        Predicate<T> condition,
        String message)
    {
        if (!condition.test(value))
            throw new IllegalArgumentException(message);
        return (value);
    }
}
