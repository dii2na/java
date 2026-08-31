package EOM.utils;

public class Validator
{
    // Numeric Validation

    public static int validatePositive(int value, String fieldName)
    {
        if (value <= 0)
            throw new IllegalArgumentException(fieldName + " must be positive");
        return (value);
    }

    public static double validatePositive(double value, String fieldName)
    {
        if (value <= 0)
            throw new IllegalArgumentException(fieldName + " must be positive");
        return (value);
    }

    public static int validateNonNegative(int value, String fieldName)
    {
        if (value < 0)
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        return (value);
    }

    public static double validateNonNegative(double value, String fieldName)
    {
        if (value < 0)
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        return (value);
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
}
