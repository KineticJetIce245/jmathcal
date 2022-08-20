package Jmathcal.Number;

public class UndefinedValueException extends ArithmeticException {

    @java.io.Serial
    private static final long serialVersionUID = 6815317066627873006L;

    /**
     * Constructs an {@code UndefinedValueException} with no detail
     * message.
     */
    public UndefinedValueException() {
        super("the result is not defined.");
    }

    /**
     * Constructs an {@code UndefinedValueException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public UndefinedValueException(String s) {
        super(s);
    }
}
