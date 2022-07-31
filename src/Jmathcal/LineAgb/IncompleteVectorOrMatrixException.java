package Jmathcal.LineAgb;

public class IncompleteVectorOrMatrixException extends ArithmeticException{
    @java.io.Serial
    private static final long serialVersionUID = 8588624887419689392L;

    /**
     * Constructs an {@code IncompleteVectorOrMatrixException} with no detail
     * message.
     */
    public IncompleteVectorOrMatrixException() {
        super();
    }

    /**
     * Constructs an {@code IncompleteVectorOrMatrixException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public IncompleteVectorOrMatrixException(String s) {
        super(s);
    }
}
