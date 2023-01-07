package Jmathcal.Plotter;

import java.math.MathContext;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;
import javafx.scene.Node;

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
    /**
     * The depth of the subdivision
     */
    private int maxDepth;
    /**
     * The number of grids on each side (after last subdivision)
     */
    private int[] gridsNum = new int[2];
    /**
     * This matrix telling whether a grid is going to be drawn or not
     */
    private boolean[][] grids;
    /**
     * This matrix contains the signs.
     */
    // TODO
    public PlotterSign[][] signMatrix;

    /**
     * The size of a grid (1st generation units)
     */
    private double[] gridSize = new double[2];
    /**
     * The size of a grid (number of sub-grids that a grid contains)
     */
    private int gridCount;
    /**
     * Number of pixel : The length of the plane
     */
    private double scale;

    private double step;

    private Expressions expr;
    private ExprRealDBLParser DBLExpr;

    public PlotterPlane(int[] length, double[] origin, int[] planeSize, int[] resolution, int maxDepth, double step) {
        this.resolution = resolution;
        this.length = length;
        this.origin = origin;
        this.planeSize = planeSize;
        this.maxDepth = maxDepth;
        this.step = step;
        // Fro instance 100pixels/20units = 5pixels/units
        double xScale = (double) planeSize[0] / (double) length[0];
        // Fro instance 100pixels/10units = 10pixels/units
        double yScale = (double) planeSize[1] / (double) length[1];
        // Choose the smallest scale : 5pixels/unites
        this.scale = xScale > yScale ? yScale : xScale;

        gridsNum[0] = resolution[0] << maxDepth;
        gridsNum[1] = resolution[1] << maxDepth;

        // 1st division
        gridSize[0] = (double) length[0] / (double) gridsNum[0];
        gridSize[1] = (double) length[1] / (double) gridsNum[1];
        for (double i : gridSize)
            if (i == 0)
                throw new TooSmallDivisionsException();
        grids = new boolean[gridsNum[0]][gridsNum[1]];
        // -2 -> Not real; -1 -> Negative; 0 -> 0; 1 -> Positive
        signMatrix = new PlotterSign[gridsNum[0] + 1][gridsNum[1] + 1];
        gridCount = 1 << (maxDepth);
    }

    public void inputExpr(Expressions leftExpr, Expressions rightExpr) {
        MathContext mc = new MathContext(15);
        this.expr = Expressions.subtractExpr(rightExpr, leftExpr);
        this.expr.calculate(mc);
        try {
            this.DBLExpr = new ExprRealDBLParser(expr);
        } catch (ExprRealDBLParser.NotRealDBLException e) {
            throw new ArithmeticException("Must input real value.");
        }
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
                        double result = Double.valueOf("NaN");
                        try {
                            result = DBLExpr.evaluateXY((gridSize[0]) * curtLocX + origin[0],
                                    (gridSize[1]) * curtLocY + origin[1]);
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

                        if (!Double.isNaN(result) && !Double.isInfinite(result)) {
                            if (result > 0) {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.POS;
                            } else if (result == 0) {
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

    public boolean[][] subdivide() {
        int count = 0;
        for (; maxDepth >= 0; maxDepth--) {
            // every unsubsidized grid
            for (int i = 0; i < (resolution[0] << count); i++) {
                for (int j = 0; j < (resolution[1] << count); j++) {
                    // getting its position
                    int iPos = i << (maxDepth + 1);
                    int jPos = j << (maxDepth + 1);
                    if (!grids[iPos][jPos])
                        continue;

                    // calculating the missing points
                    for (int k = 1; k <= 7; k++) {
                        if (k == 2 || k == 6)
                            continue;
                        int curtLocX = iPos + ((int) (k / 3) << maxDepth);
                        int curtLocY = jPos + ((int) (k % 3) << maxDepth);

                        double result = Double.valueOf("NaN");
                        try {
                            result = this.DBLExpr.evaluateXY((gridSize[0]) * curtLocX + origin[0],
                                    (gridSize[1]) * curtLocY + origin[1]);
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

                        if (Double.isFinite(result)) {
                            if (result > 0) {
                                signMatrix[curtLocX][curtLocY] = PlotterSign.POS;
                            } else if (result == 0) {
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

        return this.grids;
    }

    public Node[] drawGraphic() {
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                
            }
        }

        return null;
    }

    public static void main(String[] args) {
        int[] length = { 10, 10 };
        double[] origin = { -1, -1 };
        int[] planeSize = { 20, 20 };
        int[] resolution = { 500, 500 };
        int maxDepth = 1;
        PlotterPlane myPlane = new PlotterPlane(length, origin, planeSize, resolution, maxDepth, 0.002);
        VariablePool vp = new VariablePool();
        Expressions lExpr = Expressions.parseFromFlattenExpr("y", vp, IOBridge.DFLT_BRIDGE);
        Expressions rExpr = Expressions.parseFromFlattenExpr("sinx", vp, IOBridge.DFLT_BRIDGE);
        myPlane.inputExpr(lExpr, rExpr);
        myPlane.subdivide();
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
}
