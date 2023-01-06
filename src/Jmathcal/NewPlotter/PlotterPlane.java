package Jmathcal.NewPlotter;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.Map;

import Jmathcal.Expression.ExprNumber;
import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;
import Jmathcal.Number.Complex.ComplexDbl;

public class PlotterPlane {
    /**
     * The size of the plane (units)
     */
    private int[] length;
    /**
     * The coordinate of the lower left corner of the plane
     */
    private double[] origin;
    /**
     * The size of the plane (in pixel)
     */
    private int[] planeSize;
    /**
     * Number of grids per side (before subdivision)
     */
    private int[] resolution;

    private Expressions expr;

    /**
     * The depth of the subdivision
     */
    private int maxDepth;

    /**
     * This matrix telling whether a grid is going to be drawn or subdivided
     */
    private boolean[][] grids;
    /**
     * This matrix contains the signs.
     */
    private PlotterSign[][] signMatrix;

    /**
     * The size of a grid (1st generation units)
     */
    private double[] gridSize = new double[2];
    /**
     * The size of a grid (number of subgrids that a grid contains)
     */
    private int gridCount;
    /**
     * Number of pixel : The length of the plane
     */
    private double scale;

    public PlotterPlane(int[] length, double[] origin, int[] planeSize, int[] resolution, int maxDepth) {
        this.resolution = resolution;
        this.length = length;
        this.origin = origin;
        this.planeSize = planeSize;
        this.maxDepth = maxDepth;
        // Fro instance 100pixels/20units = 5pixels/units
        double xScale = (double) planeSize[0] / (double) length[0];
        // Fro instance 100pixels/10units = 10pixels/units
        double yScale = (double) planeSize[1] / (double) length[1];
        // Choose the smallest scale : 5pixels/unites
        this.scale = xScale > yScale ? yScale : xScale;

        // 1st division
        gridSize[0] = (double) length[0] / (double) (resolution[0] << maxDepth);
        gridSize[1] = (double) length[1] / (double) (resolution[1] << maxDepth);
        for (double i : gridSize)
            if (i == 0)
                throw new TooSmallDivisionsException();
        grids = new boolean[resolution[0] << maxDepth][resolution[1] << maxDepth];
        // -2 -> Not real; -1 -> Negative; 0 -> 0; 1 -> Positive
        signMatrix = new PlotterSign[(resolution[0] << maxDepth) + 1][(resolution[1] << maxDepth) + 1];
        gridCount = 1 << (maxDepth);
    }

    public void inputExpr(Expressions leftExpr, Expressions rightExpr) {
        this.expr = Expressions.subtractExpr(rightExpr, leftExpr);
        for (int i = 0; i < resolution[0]; i++) {
            for (int j = 0; j < resolution[1]; j++) {
                int iPos = i << maxDepth;
                int jPos = j << maxDepth;
                // there are four corners
                /**
                 * k=0 ： (0,0)
                 * k=1 : (0,1)
                 * k=2 : (1,0)
                 * k=3 : (1,1)
                 * x = (int)(k/2)
                 * y = (int)(k%2)
                 */
                for (int k = 0; k < 4; k++) {
                    int curtLocX = iPos + ((int) (k / 2) << maxDepth);
                    int curtLocY = jPos + ((int) (k % 2) << maxDepth);
                    if (signMatrix[curtLocX][curtLocY] == null) {
                        if (expr.getVP().contains("x")) {
                            expr.getVP().setValueOf("x",
                                    new ExprNumber(new ComplexDbl((gridSize[0]) * curtLocX)));
                        }
                        if (expr.getVP().contains("y")) {
                            expr.getVP().setValueOf("y",
                                    new ExprNumber(new ComplexDbl((gridSize[1]) * curtLocY)));
                        }
                        ExprNumber result = null;
                        MathContext mc = new MathContext(15);
                        try {
                            result = expr.calculate(mc);
                        } catch (Jmathcal.Number.InfiniteValueException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (Jmathcal.Number.UndefinedValueException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (Jmathcal.Number.ValueOutOfRangeException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (java.lang.ArithmeticException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (java.lang.NumberFormatException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        }

                        if (result != null && result.isRealDBL(15)) {
                            ComplexDbl resultDBL = result.toComplexDbl();
                            if (resultDBL.getRealValue() > 0) {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.POS;
                            } else if (resultDBL.getRealValue() == 0) {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.ZERO;
                            } else {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.NEG;
                            }
                        } else {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        }
                    }

                }
                PlotterSign leftB = signMatrix[iPos][jPos];
                PlotterSign leftU = signMatrix[iPos][jPos + (1 << maxDepth)];
                PlotterSign rightB = signMatrix[iPos + (1 << maxDepth)][jPos];
                PlotterSign rightU = signMatrix[iPos + (1 << maxDepth)][jPos + (1 << maxDepth)];

                grids[iPos][jPos] = false;
                if (leftB.ifPointPasses(leftU))
                    grids[iPos][jPos] = true;
                if (leftB.ifPointPasses(rightB))
                    grids[iPos][jPos] = true;
                if (rightU.ifPointPasses(rightB))
                    grids[iPos][jPos] = true;
                if (rightU.ifPointPasses(leftU))
                    grids[iPos][jPos] = true;

            }
        }
        maxDepth--;
    }

    public void subdivide() {
        int count = 0;
        for (; maxDepth >= 0; maxDepth--) {
            // every unsubsidized grid
            for (int i = 0; i < (resolution[0] << count); i++) {
                for (int j = 0; j < (resolution[1] << count); j++) {
                    // getting its position
                    int iPos = i << (maxDepth+1);
                    int jPos = j << (maxDepth+1);
                    if (!grids[iPos][jPos])
                        continue;

                    // calculating the missing points
                    for (int k = 1; k <= 7; k++) {
                        if (k == 2 || k == 6)
                            continue;
                        int curtLocX = iPos + ((int) (k / 3) << maxDepth);
                        int curtLocY = jPos + ((int) (k % 3) << maxDepth);

                        if (expr.getVP().contains("x")) {
                            expr.getVP().setValueOf("x",
                                    new ExprNumber(new ComplexDbl((gridSize[0]) * curtLocX)));
                        }
                        if (expr.getVP().contains("y")) {
                            expr.getVP().setValueOf("y",
                                    new ExprNumber(new ComplexDbl((gridSize[1]) * curtLocY)));
                        }

                        ExprNumber result = null;
                        MathContext mc = new MathContext(15);
                        try {
                            result = expr.calculate(mc);
                        } catch (Jmathcal.Number.InfiniteValueException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (Jmathcal.Number.UndefinedValueException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (Jmathcal.Number.ValueOutOfRangeException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (java.lang.ArithmeticException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        } catch (java.lang.NumberFormatException e) {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        }

                        if (result != null && result.isRealDBL(15)) {
                            ComplexDbl resultDBL = result.toComplexDbl();
                            if (resultDBL.getRealValue() > 0) {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.POS;
                            } else if (resultDBL.getRealValue() == 0) {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.ZERO;
                            } else {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.NEG;
                            }
                        } else {
                            signMatrix[curtLocX][curtLocY] = PlotterSign.NOT_REAL;
                        }
                    }

                    for (int k = 0; k < 4; k++) {
                        int curtLocX = iPos + ((int) (k / 2) << maxDepth);
                        int curtLocY = jPos + ((int) (k % 2) << maxDepth);

                        PlotterSign leftB = signMatrix[curtLocX][curtLocY];
                        PlotterSign leftU = signMatrix[curtLocX][curtLocY + (1 << maxDepth)];
                        PlotterSign rightB = signMatrix[curtLocX + (1 << maxDepth)][curtLocY];
                        PlotterSign rightU = signMatrix[curtLocX + (1 << maxDepth)][curtLocY + (1 << maxDepth)];
        
                        grids[curtLocX][curtLocY] = false;
                        if (leftB.ifPointPasses(leftU))
                            grids[curtLocX][curtLocY] = true;
                        if (leftB.ifPointPasses(rightB))
                            grids[curtLocX][curtLocY] = true;
                        if (rightU.ifPointPasses(rightB))
                            grids[curtLocX][curtLocY] = true;
                        if (rightU.ifPointPasses(leftU))
                            grids[curtLocX][curtLocY] = true;
                    }
                }
            }
            count++;
        }
    }

    public ArrayList<Double> getContourArrays() {

        ArrayList<ArrayList<Double>> reVal = new ArrayList<ArrayList<Double>>();

        double step = 0.2;
        ExprNumber smallNumber = new ExprNumber("0.00001+0i");
        MathContext mc = new MathContext(16);
        Expressions dfx = this.expr.findDerivative("x", smallNumber, mc);
        Expressions dfy = this.expr.findDerivative("y", smallNumber, mc);

        interface NewtonRaphsonMethod {
            double findZeroOnX(double x, double y);
            double findZeroOnY(double x, double y);
        }
        NewtonRaphsonMethod nrm = new NewtonRaphsonMethod() {
            @Override
            public double findZeroOnX(double x, double y) {
                ExprNumber X = new ExprNumber(new ComplexDbl(x));
                ExprNumber Y = new ExprNumber(new ComplexDbl(y));
                double resultDBL;
                int loopCount = 50;
                do {
                    if (loopCount < 0) {
                        throw new ArithmeticException("Taking too much step");
                    }
                    ExprNumber result = expr.evaluateXY(X, Y, mc);

                    if (!result.isRealDBL(15))
                        throw new ArithmeticException("Not real value");

                    resultDBL = result.toComplexDbl().getRealValue();
                    X = X.subtract(expr.evaluateXY(X, Y, mc).divide(dfx.evaluateXY(X, Y, mc), mc), mc);
                    X = new ExprNumber(new ComplexDbl(X.toComplexDbl().getRealValue()));

                    if (!X.isRealDBL(15))
                        throw new ArithmeticException("Not real value");
                    loopCount--;
                } while (Math.abs(resultDBL) > 0.0000001);
                x = X.toComplexDbl().getRealValue();
                return x;
            }
            @Override
            public double findZeroOnY(double x, double y) {
                ExprNumber X = new ExprNumber(new ComplexDbl(x));
                ExprNumber Y = new ExprNumber(new ComplexDbl(y));
                double resultDBL;
                int loopCount = 50;
                do {
                    if (loopCount < 0) {
                        throw new ArithmeticException("Taking too much step");
                    }
                    ExprNumber result = expr.evaluateXY(X, Y, mc);

                    if (!result.isRealDBL(15))
                        throw new ArithmeticException("Not real value");

                    resultDBL = result.toComplexDbl().getRealValue();
                    Y = Y.subtract(expr.evaluateXY(X, Y, mc).divide(dfy.evaluateXY(X, Y, mc), mc), mc);
                    Y = new ExprNumber(new ComplexDbl(Y.toComplexDbl().getRealValue()));

                    if (!Y.isRealDBL(15))
                        throw new ArithmeticException("Not real value");
                    loopCount--;
                } while (Math.abs(resultDBL) > 0.0000001);
                y = Y.toComplexDbl().getRealValue();
                return y;
            }
        };

        interface NextContourPointGetter {double[] getPoint(double x, double y);}
        NextContourPointGetter ncpg = new NextContourPointGetter() {
            @Override
            public double[] getPoint(double x, double y) {
                ExprNumber xLoc = new ExprNumber(new ComplexDbl(x));
                ExprNumber yLoc = new ExprNumber(new ComplexDbl(y));
                ExprNumber H = dfy.evaluateXY(xLoc, yLoc, mc);
                ExprNumber K = dfx.evaluateXY(xLoc, yLoc, mc);
                if (!H.isRealDBL(15))
                    return null;
                if (!K.isRealDBL(15))
                    return null;
                double doubleH = H.toComplexDbl().getRealValue();
                double doubleK = -K.toComplexDbl().getRealValue();
                double h,k;
                if (Math.abs(doubleH) > Math.abs(doubleK)) {
                    h = step*Math.signum(doubleH);
                    k = h*doubleK/doubleH;
                } else {
                    k= step*Math.signum(doubleK);
                    h= k*doubleH/doubleK;
                }
                x = nrm.findZeroOnX(x+h, y+k);
                double[] reVal = {x, y+k};
                return reVal;
            }
        };

        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                ArrayList<Double> curve = new ArrayList<Double>();
                if (!grids[i][j]) continue;
                double curtX = i*gridSize[0];
                double curtY = (j+0.5)*gridSize[0];
                double[] curtXY;
                try {
                    curtY = nrm.findZeroOnY(curtX, curtY);
                    curve.add(curtX);
                    curve.add(curtY);
                    curtXY = ncpg.getPoint(curtX, curtY);
                    curve.add(curtXY[0]);
                    curve.add(curtXY[1]);
                    curtXY = ncpg.getPoint(curtXY[0], curtXY[1]);
                    curve.add(curtXY[0]);
                    curve.add(curtXY[1]);
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                } finally {
                    grids[i][j] = false; 
                }
                return curve;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        int[] length = { 4, 4 };
        double[] origin = { 0, 0 };
        int[] planeSize = { 20, 20 };
        int[] resolution = { 1, 1 };
        int maxDepth = 4;
        PlotterPlane myPlane = new PlotterPlane(length, origin, planeSize, resolution, maxDepth);
        VariablePool vp = new VariablePool();
        Expressions lExpr = Expressions.parseFromFlattenExpr("sinx", vp, IOBridge.DFLT_BRIDGE);
        Expressions rExpr = Expressions.parseFromFlattenExpr("y", vp, IOBridge.DFLT_BRIDGE);
        myPlane.inputExpr(lExpr, rExpr);
        myPlane.subdivide();
        for (int i = myPlane.signMatrix[0].length - 1; i > -1; i--) {
            for (int j = 0; j < myPlane.signMatrix.length; j++) {
                System.out.print(myPlane.signMatrix[j][i] == null ? "n" : myPlane.signMatrix[j][i]);
            }
            System.out.println("");
        }

        System.out.println(myPlane.getContourArrays());
    }

    public class TooSmallDivisionsException extends ArithmeticException {

        @java.io.Serial
        private static final long serialVersionUID = -6120003610046574725L;

        /**
         * Constructs a {@code TooSmallDivisionsException} with no
         * detail message.
         */
        public TooSmallDivisionsException() {
            super();
        }

        /**
         * Constructs a {@code TooSmallDivisionsException} with the
         * specified detail message.
         *
         * @param s the detail message.
         */
        public TooSmallDivisionsException(String s) {
            super(s);
        }
    }

    private class PlotterGrid {
        public final int x;
        public final int y;

        public PlotterGrid(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PlotterGrid other = (PlotterGrid) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            return true;
        }
    }
}
