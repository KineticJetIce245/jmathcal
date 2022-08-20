package Jmathcal.Number;

public class ValueOutOfRangeException extends ArithmeticException {
    
    @java.io.Serial
    private static final long serialVersionUID = 4593681281781513154L;

    

    /**
     * Constructs a {@code ValueOutOfRangeException} with no detail
     * message.
     */
    public ValueOutOfRangeException() {
        super("inputted value or result is out of range.");
    }

    /**
     * Constructs a {@code ValueOutOfRangeException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public ValueOutOfRangeException(String s) {
        super(s);
    }
}
