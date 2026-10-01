package fooddelivery.exception;

public class ConsoleInputClosedException
    extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public ConsoleInputClosedException()
    {
        super ("Input stream closed before a value was entered");
    }
}
