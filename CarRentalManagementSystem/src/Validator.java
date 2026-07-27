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

    public static double validateNonNegative(double value, String fieldName)
    {
        if (value < 0)
            throw new IllegalArgumentException(fieldName + " cannot be negative");

        return (value);
    }

    public static int validateIntRange(int value, int min, int max, String fieldName)
    {
        if (value < min || value > max)
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);

        return (value);
    }

    // String Validation

    public static String validateString(String value, String fieldName)
    {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException(fieldName + " cannot be empty");

        return (value.trim());
    }

    public static String validatePhone(String phone)
    {
        if (!phone.matches("\\+?\\d+"))
            throw new IllegalArgumentException("Phone must contain only numbers");
        if (phone.startsWith("+"))
        {
            if (phone.length() - 1 != 12)
                throw new IllegalArgumentException("Invalid international phone number");
        }
        else
        {
            if (phone.length() != 10)
                throw new IllegalArgumentException("Invalid phone number");
        }
        return (phone.trim());
    }
}
