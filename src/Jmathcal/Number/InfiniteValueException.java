package Jmathcal.Number;
/**
 * Thrown when infinite value occurs in calculations. 
 * The sign of the infinite is represented by {@code ifPositive}.
 */
public class InfiniteValueException extends ArithmeticException{

    // Serialization
    private static final long serialVersionUID = -4256255149625549448L;
    public final boolean ifPositive;

    /**
     * Constructs an {@code InfiniteValueException} with no detail
     * message.
     */
    public InfiniteValueException() {
        super("the result is positive infinite");
        ifPositive = true;
    }

    /**
     * Constructs an {@code InfiniteValueException} with
     * it's sign.
     */
    public InfiniteValueException(boolean ifPositive) {
        super("the result is " + (ifPositive ? "positive" : "negative") +" infinite" );
        this.ifPositive = ifPositive;
    }

    /**
     * Constructs an {@code InfiniteValueException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public InfiniteValueException(String s) {
        super(s);
        ifPositive = true;
    }
    /**
     * Constructs an {@code InfiniteValueException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public InfiniteValueException(boolean ifPositive, String s) {
        super(s);
        this.ifPositive = ifPositive;
    }
    
}
