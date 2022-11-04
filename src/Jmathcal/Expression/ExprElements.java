package Jmathcal.Expression;

import java.math.MathContext;

/**
 * ExprElements is an interface used in the class
 * {@code Expressions} in order to store in an array
 * the elements of an expression. They can be a number,
 * an operation or a sub-expression.
 */
public interface ExprElements {
    public ExprNumber toNumber(MathContext mc);
}
