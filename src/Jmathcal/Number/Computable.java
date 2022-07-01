package Jmathcal.Number;

/**
 * This interface imposes each class that implements it to be able to do
 * the four basic arithmetic operations.
 * @author      KineticJetIce245
 */
public interface Computable<T> {
    /**
     * Returns the value of {@code (this + augend)}.
     * @param augend value to be added.
     * @return {@code this + augend}
     */
    public T add(T augend);

    /**
     * Returns the value of {@code (this - augend)}.
     * @param subtrahend value subtracted from the original value.
     * @return {@code this - augend}
     */
    public T subtract(T subtrahend);

    /**
     * Returns the value of {@code (this * augend)}.
     * @param multiplicand value to be multiplied.
     * @return {@code this * augend}
     */
    public T multiply(T multiplicand);

    /**
     * Returns the value of {@code (this / augend)}.
     * @param divisor value divided from the original value.
     * @return {@code this / augend}
     */
    public T divide(T divisor);
}
