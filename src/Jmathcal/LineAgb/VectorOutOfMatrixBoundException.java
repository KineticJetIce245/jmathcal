package Jmathcal.LineAgb;

public class VectorOutOfMatrixBoundException extends IndexOutOfBoundsException{
    
    @java.io.Serial
    private static final long serialVersionUID = 8104080419533871649L;

    /**
     * Constructs an {@code VectorOutOfMatrixBoundException} with no detail message.
     */
    public VectorOutOfMatrixBoundException() {
        super();
    }

    /**
     * Constructs an {@code VectorOutOfMatrixBoundException} with the specified detail
     * message.
     *
     * @param s the detail message
     */
    public VectorOutOfMatrixBoundException(String s) {
        super(s);
    }
    
}
