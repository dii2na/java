package utils;

import static utils.ConsoleUtils.*;

public class Validator
{
    // Numeric Validation

    public static int validatePositive(int value, String fieldName)
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

    public static double validatePositive(double value, String fieldName)
    {
        if (value <= 0)
            throw new IllegalArgumentException(fieldName + " must be positive");
        return (value);
    }

    public static double validateNonNegative(double value, String fieldName)
    {
        if (value < 0)
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        return (value);
    }

    public static double validateMinimum(double value, double minimum, String fieldName)
    {
        if (value < minimum)
        {
            throw new IllegalArgumentException(
                    fieldName + " must be at least " + money(minimum));
        }
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

    private static String validateLength(String value, String fieldName,
                                         int minLength, int maxLength)
    {
        if (value.length() < minLength || value.length() > maxLength)
            throw new IllegalArgumentException(
                    fieldName + " length must be between "
                            + minLength + " and " + maxLength);
        return (value);
    }

    public static String validateAlphanumeric(String value, String fieldName,
                                              int minLength, int maxLength)
    {
        validateString(value, fieldName, false);
        value = validateLength(value, fieldName, minLength, maxLength);
        if (!value.matches("[a-zA-Z0-9]+"))
            throw new IllegalArgumentException(
                    fieldName + " must contain only letters and digits");
        return (value);
    }

    public static String validateNumeric(String value, String fieldName,
                                         int minLength, int maxLength)
    {
        validateString(value, fieldName, false);
        value = validateLength(value, fieldName, minLength, maxLength);
        if (!value.matches("[+-]?\\d+"))
            throw new IllegalArgumentException(
                    fieldName + " must contain only digits");
        return (value);
    }

    public static String validateOptionalNumeric(String value, String fieldName,
                                                 int minLength, int maxLength)
    {
        value = validateString(value, fieldName, true);
        if (value.isBlank())
            return (value);
        return (validateNumeric(value, fieldName, minLength, maxLength));
    }

    public static String validateLetters(String value, String fieldName,
                                         int minLength, int maxLength)
    {
        validateString(value, fieldName, false);
        value = validateLength(value, fieldName, minLength, maxLength);
        if (!value.matches("[a-zA-Z '.-]+"))
            throw new IllegalArgumentException(
                    fieldName + " must contain only letters, spaces, hyphens, and apostrophes");
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
