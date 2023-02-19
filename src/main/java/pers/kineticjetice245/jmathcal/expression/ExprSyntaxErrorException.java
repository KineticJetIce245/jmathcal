package pers.kineticjetice245.jmathcal.expression;
import java.lang.IllegalArgumentException;

public class ExprSyntaxErrorException extends IllegalArgumentException{
    
    private static final long serialVersionUID = 4140149208884343478L;

    /**
     * Constructs an {@code ExprSyntaxErrorException} with no
     * detail message.
     */
    public ExprSyntaxErrorException() {
        super();
    }

    /**
     * Constructs an {@code ExprSyntaxErrorException} with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public ExprSyntaxErrorException(String s) {
        super(s);
    }
}
