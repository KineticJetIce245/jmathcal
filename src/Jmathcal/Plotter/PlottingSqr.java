package Jmathcal.Plotter;

import java.math.MathContext;

import Jmathcal.Expression.ExprNumber;
import Jmathcal.Expression.Expressions;
import Jmathcal.Number.Complex.ComplexDbl;

public class PlottingSqr {
    private int[] sqrLoc;
    private double[] location;
    private double[] size;
    private PlotterSign[] signs;

    public PlottingSqr(int[] sqrLoc, double[] location, double[] size) {
        this.sqrLoc = sqrLoc;
        this.location = location;
        this.size = size;
    }

    public void computeSign(PlotterSign[][] resultMatrix, Expressions Expr) {
        if (resultMatrix[sqrLoc[0]][sqrLoc[1]] == null) {
            resultMatrix[sqrLoc[0]][sqrLoc[1]] = findSign(Expr, location);
        }
        if (resultMatrix[sqrLoc[0]+1][sqrLoc[1]] == null) {
            double[] nextLoc = {location[0] + size[0], location[1]};
            resultMatrix[sqrLoc[0]+1][sqrLoc[1]] = findSign(Expr, nextLoc);
        }
        if (resultMatrix[sqrLoc[0]][sqrLoc[1]+1] == null) {
            double[] nextLoc = {location[0], location[1] + size[1]};
            resultMatrix[sqrLoc[0]][sqrLoc[1]+1] = findSign(Expr, nextLoc);
        }
        if (resultMatrix[sqrLoc[0]+1][sqrLoc[1]+1] == null) {
            double[] nextLoc = {location[0] + size[0], location[1] + size[1]};
            resultMatrix[sqrLoc[0]+1][sqrLoc[1]+1] = findSign(Expr, nextLoc);
        }
    }

    private PlotterSign findSign(Expressions Expr, double[] position) {
        if (Expr.getVP().contains("x")) {
            Expr.getVP().setValueOf("x", new ExprNumber(new ComplexDbl(position[0]).toString()));
        }
        if (Expr.getVP().contains("y")) {
            Expr.getVP().setValueOf("y", new ExprNumber(new ComplexDbl(position[1]).toString()));
        }
        ExprNumber result = null;
        try {
            result = Expr.calculate(new MathContext(10));
        } catch (Jmathcal.Number.InfiniteValueException e) {
            return PlotterSign.NOT_REAL;
        } catch (Jmathcal.Number.UndefinedValueException e) {
            return PlotterSign.NOT_REAL;
        } catch (Jmathcal.Number.ValueOutOfRangeException e) {
            return PlotterSign.NOT_REAL;
        } catch (java.lang.ArithmeticException e) {
            return PlotterSign.NOT_REAL;
        }

        if (result != null && result.isReal()) {
            ComplexDbl resultDBL = result.toComplexDbl();
            if (resultDBL.getRealValue() > 0) {
                return PlotterSign.POS;
            } else if (resultDBL.getRealValue() == 0) {
                return PlotterSign.ZERO;
            } else {
                return PlotterSign.NEG;
            }
        } else {
            return PlotterSign.NOT_REAL;
        }
    }
}
