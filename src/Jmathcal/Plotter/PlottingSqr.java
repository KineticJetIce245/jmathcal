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
    private boolean canSubdivide;

    public PlottingSqr(int[] sqrLoc, double[] location, double[] size) {
        this.sqrLoc = sqrLoc;
        this.location = location;
        this.size = size;
        this.signs = new PlotterSign[4];
    }

    public void computeSign(PlotterSign[][] resultMatrix, Expressions Expr) {
        PlotterSign currentSign = null;
        if (resultMatrix[sqrLoc[0]][sqrLoc[1]] == null) {
            currentSign = findSign(Expr, location);
            resultMatrix[sqrLoc[0]][sqrLoc[1]] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0]][sqrLoc[1]];
        }
        signs[0] = currentSign;

        if (resultMatrix[sqrLoc[0] + 1][sqrLoc[1]] == null) {
            double[] nextLoc = { location[0] + size[0], location[1] };
            currentSign = findSign(Expr, nextLoc);
            resultMatrix[sqrLoc[0] + 1][sqrLoc[1]] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0] + 1][sqrLoc[1]];
        }
        signs[1] = currentSign;

        if (resultMatrix[sqrLoc[0]][sqrLoc[1] + 1] == null) {
            double[] nextLoc = { location[0], location[1] + size[1] };
            currentSign = findSign(Expr, nextLoc);
            resultMatrix[sqrLoc[0]][sqrLoc[1] + 1] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0]][sqrLoc[1] + 1];
        }
        signs[2] = currentSign;

        if (resultMatrix[sqrLoc[0] + 1][sqrLoc[1] + 1] == null) {
            double[] nextLoc = { location[0] + size[0], location[1] + size[1] };
            currentSign = findSign(Expr, nextLoc);
            resultMatrix[sqrLoc[0] + 1][sqrLoc[1] + 1] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0] + 1][sqrLoc[1] + 1];
        }
        signs[3] = currentSign;

        for (PlotterSign i : signs) if (i == PlotterSign.ZERO) canSubdivide = true;

        
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
