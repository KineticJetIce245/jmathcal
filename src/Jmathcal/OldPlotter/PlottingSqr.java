package Jmathcal.OldPlotter;

import java.math.MathContext;
import java.util.ArrayList;

import Jmathcal.Expression.ExprNumber;
import Jmathcal.Expression.Expressions;
import Jmathcal.Number.Complex.ComplexDbl;

public class PlottingSqr {
    private int[] sqrLoc;
    private double[] location;
    private double[] size;
    /**
     * (x,y); (x+,y); (x,y+); (x+,y+)
     */
    private PlotterSign[] signs;
    private boolean canSubdivide;
    private double[] upLeftLoc = new double[2];
    private Expressions expr;

    public PlottingSqr(int[] sqrLoc, double[] location, double[] size) {
        this.sqrLoc = sqrLoc;
        this.location = location;
        this.size = size;
        this.signs = new PlotterSign[4];
        this.upLeftLoc[0] = location[0];
        this.upLeftLoc[1] = location[1] + size[1];
    }

    public boolean computeSign(PlotterSign[][] resultMatrix, Expressions expr) {
        this.expr = expr;

        canSubdivide = false;
        PlotterSign currentSign = null;
        if (resultMatrix[sqrLoc[0]][sqrLoc[1]] == null) {
            currentSign = findSign(location);
            resultMatrix[sqrLoc[0]][sqrLoc[1]] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0]][sqrLoc[1]];
        }
        signs[0] = currentSign;

        if (resultMatrix[sqrLoc[0] + 1][sqrLoc[1]] == null) {
            double[] nextLoc = { location[0] + size[0], location[1] };
            currentSign = findSign(nextLoc);
            resultMatrix[sqrLoc[0] + 1][sqrLoc[1]] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0] + 1][sqrLoc[1]];
        }
        signs[1] = currentSign;

        if (resultMatrix[sqrLoc[0]][sqrLoc[1] + 1] == null) {
            double[] nextLoc = { location[0], location[1] + size[1] };
            currentSign = findSign(nextLoc);
            resultMatrix[sqrLoc[0]][sqrLoc[1] + 1] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0]][sqrLoc[1] + 1];
        }
        signs[2] = currentSign;

        if (resultMatrix[sqrLoc[0] + 1][sqrLoc[1] + 1] == null) {
            double[] nextLoc = { location[0] + size[0], location[1] + size[1] };
            currentSign = findSign(nextLoc);
            resultMatrix[sqrLoc[0] + 1][sqrLoc[1] + 1] = currentSign;
        } else {
            currentSign = resultMatrix[sqrLoc[0] + 1][sqrLoc[1] + 1];
        }
        signs[3] = currentSign;

        for (PlotterSign i : signs) {
            if (i == PlotterSign.ZERO) {
                canSubdivide = true;
                return canSubdivide;
            }
        }
        if (PlotterSign.ifPointPasses(signs[0], signs[1])) {
            canSubdivide = true;
        }
        if (PlotterSign.ifPointPasses(signs[0], signs[2])) {
            canSubdivide = true;
        }
        if (PlotterSign.ifPointPasses(signs[3], signs[1])) {
            canSubdivide = true;
        }
        if (PlotterSign.ifPointPasses(signs[3], signs[2])) {
            canSubdivide = true;
        }
        return canSubdivide;
    }

    private PlotterSign findSign(double[] position) {
        if (expr.getVP().contains("x")) {
            expr.getVP().setValueOf("x", new ExprNumber(new ComplexDbl(position[0]).toString()));
        }
        if (expr.getVP().contains("y")) {
            expr.getVP().setValueOf("y", new ExprNumber(new ComplexDbl(position[1]).toString()));
        }
        ExprNumber result = null;
        try {
            result = expr.calculate(new MathContext(15));
        } catch (Jmathcal.Number.InfiniteValueException e) {
            return PlotterSign.NOT_REAL;
        } catch (Jmathcal.Number.UndefinedValueException e) {
            return PlotterSign.NOT_REAL;
        } catch (Jmathcal.Number.ValueOutOfRangeException e) {
            return PlotterSign.NOT_REAL;
        } catch (java.lang.ArithmeticException e) {
            return PlotterSign.NOT_REAL;
        } catch (java.lang.NumberFormatException e) {
            return PlotterSign.NOT_REAL;
        }

        if (result != null && result.isRealDBL(15)) {
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

    public double getLoc(int index) {
        return this.location[index];
    }

    public double getUpLeftLoc(int index) {
        return this.upLeftLoc[index];
    }

    public double getSize(int index) {
        return this.size[index];
    }

    public ArrayList<PlottingSqr> subdivide() {
        PlotterSign[][] resultMatrix = new PlotterSign[3][3];
        resultMatrix[0][0] = signs[0];
        resultMatrix[2][0] = signs[1];
        resultMatrix[0][2] = signs[2];
        resultMatrix[2][2] = signs[3];
    
        double[] newSize = {size[0]/2.0, size[1]/2.0};

        ArrayList<PlottingSqr> sqrs = new ArrayList<PlottingSqr>();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int[] currentSqrLoc = {i,j};
                double[] currentLoc = {location[0] + i*newSize[0], location[1] + j*newSize[1]};
                PlottingSqr sqr = new PlottingSqr(currentSqrLoc, currentLoc, newSize);
                if (sqr.computeSign(resultMatrix, expr)) {
                    sqrs.add(sqr);
                }
            }
        }

    /*
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double[] currentLoc = new double[2];
                int[] sqrLoc = {i, j};
                currentLoc[0] = newSize[0] * i + location[0];
                currentLoc[1] = newSize[1] * j + location[0];
                PlottingSqr sqr = new PlottingSqr(sqrLoc, currentLoc, newSize);
                boolean canSubdivide = sqr.computeSign(resultMatrix, expr);
                if (canSubdivide)
                    sqrs.add(sqr);
            }
        }
    */

        return sqrs;
    }
}
