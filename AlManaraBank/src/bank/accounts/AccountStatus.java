package bank.accounts;

public enum AccountStatus
{
    ACTIVE("Account is active"),
    FROZEN("Account is frozen"),
    CLOSED("Account is closed");

    private final String message;

    AccountStatus(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return (message);
    }

    @Override
    public String toString()
    {
        return (message);
    }
}