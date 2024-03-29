package pers.kineticjetice245.jmathcal.expression;

public class VariableLabelOccupiedException extends IllegalArgumentException{
    private static final long serialVersionUID = -2838586925448409049L;

    /**
     * Constructs a {@code VariableLabelOccupiedException} with no
     * detail message.
     */
    public VariableLabelOccupiedException() {
        super();
    }

    /**
     * Constructs a {@code VariableLabelOccupiedException} with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public VariableLabelOccupiedException(String s) {
        super(s);
    }

}
