package Jmathcal.Plotter;

import java.math.MathContext;
import java.util.ArrayList;

import Jmathcal.Expression.Expressions;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlotterPlane {
    /**
     * The size of the plane (units)
     */
    private double[] length;
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
    public PlotterSign[][] signMatrix;

    /**
     * The size of a grid (1st generation units)
     */
    private double[] gridSize = new double[2];
    /**
     * Number of pixel : The length of the plane
     */
    private double scale;

    private int currentDepth;

    private Expressions expr;
    private ExprRealDBLParser DBLExpr;

    public PlotterPlane(double[] length, double[] origin, int[] planeSize, int[] resolution, int maxDepth) {
        this.resolution = resolution;
        this.length = length;
        this.origin = origin;
        this.planeSize = planeSize;
        this.maxDepth = maxDepth;
        this.currentDepth = maxDepth;
        // Fro instance 100pixels/20units = 5pixels/units
        double xScale = (double) planeSize[0] / (double) length[0];
        // Fro instance 100pixels/10units = 10pixels/units
        double yScale = (double) planeSize[1] / (double) length[1];
        // Choose the smallest scale : 5pixels/unites
        if (xScale > yScale) {
            this.scale = yScale;
            double oriLength = length[0];
            this.length[0] = planeSize[0] / scale;
            this.origin[0] = this.origin[0] - (this.length[0] - oriLength) / 2;
        } else {
            this.scale = xScale;
            double oriLength = length[1];
            this.length[1] = planeSize[1] / scale;
            this.origin[1] = this.origin[1] - (this.length[1] - oriLength) / 2;
        }
        System.out.println(this.origin[0] + " " + this.origin[1]);

        gridsNum[0] = resolution[0] << currentDepth;
        gridsNum[1] = resolution[1] << currentDepth;

        // 1st division
        gridSize[0] = (double) this.length[0] / (double) gridsNum[0];
        gridSize[1] = (double) this.length[1] / (double) gridsNum[1];
        for (double i : gridSize)
            if (i == 0)
                throw new TooSmallDivisionsException();
    }

    public void inputExpr(Expressions expr) {
        MathContext mc = new MathContext(15);
        currentDepth = maxDepth;
        grids = new boolean[gridsNum[0]][gridsNum[1]];
        signMatrix = new PlotterSign[gridsNum[0] + 1][gridsNum[1] + 1];
        this.expr = expr;
        this.expr.calculate(mc);
        try {
            this.DBLExpr = new ExprRealDBLParser(expr);
        } catch (ExprRealDBLParser.NotRealDBLException e) {
            throw new ArithmeticException("Must input real value.");
        }
        for (int i = 0; i < resolution[0]; i++) {
            for (int j = 0; j < resolution[1]; j++) {
                int iPos = i << currentDepth;
                int jPos = j << currentDepth;
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
                    int curtLocX = iPos + ((int) (k / 2) << currentDepth);
                    int curtLocY = jPos + ((int) (k % 2) << currentDepth);
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
                PlotterSign leftU = signMatrix[iPos][jPos + (1 << currentDepth)];
                PlotterSign rightB = signMatrix[iPos + (1 << currentDepth)][jPos];
                PlotterSign rightU = signMatrix[iPos + (1 << currentDepth)][jPos + (1 << currentDepth)];

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
        currentDepth--;
    }

    public boolean[][] subdivide() {
        int count = 0;
        for (; currentDepth >= 0; currentDepth--) {
            // every unsubsidized grid
            for (int i = 0; i < (resolution[0] << count); i++) {
                for (int j = 0; j < (resolution[1] << count); j++) {
                    // getting its position
                    int iPos = i << (currentDepth + 1);
                    int jPos = j << (currentDepth + 1);
                    if (!grids[iPos][jPos])
                        continue;

                    // calculating the missing points
                    for (int k = 1; k <= 7; k++) {
                        if (k == 2 || k == 6)
                            continue;
                        int curtLocX = iPos + ((int) (k / 3) << currentDepth);
                        int curtLocY = jPos + ((int) (k % 3) << currentDepth);

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
                        int curtLocX = iPos + ((int) (k / 2) << currentDepth);
                        int curtLocY = jPos + ((int) (k % 2) << currentDepth);

                        PlotterSign leftB = signMatrix[curtLocX][curtLocY];
                        PlotterSign leftU = signMatrix[curtLocX][curtLocY + (1 << currentDepth)];
                        PlotterSign rightB = signMatrix[curtLocX + (1 << currentDepth)][curtLocY];
                        PlotterSign rightU = signMatrix[curtLocX + (1 << currentDepth)][curtLocY + (1 << currentDepth)];

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

    public ArrayList<Node> drawGraphic(Color color) {
        double xSize = gridSize[0] * scale > 1.5 ? gridSize[0] * scale : 1.5;
        double ySize = gridSize[1] * scale > 1.5 ? gridSize[1] * scale : 1.5;
        ArrayList<Node> reVal = new ArrayList<Node>();
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                if (!grids[i][j])
                    continue;

                double curtPosX = (gridSize[0]) * i + origin[0];
                double curtPosY = (gridSize[1]) * j + origin[1];
                Rectangle point = new Rectangle(curtPosX * scale, -curtPosY * scale, xSize, ySize);
                point.setFill(color);
                reVal.add(point);
            }
        }

        return reVal;
    }

    public ArrayList<Node> generateGrid(boolean ifNum, boolean ifPrimaryGrid, boolean ifSecondaryGrid) {
        ArrayList<Node> reVal = new ArrayList<Node>();
        /*
         * The grids are rendered before functions and placed on another Group.
         * The Group will automatically replace the origin (0,0) to make the children
         * fit inside
         * That is why here the (0,0) of the plane is actually (0,0) of the Group
         */

        double[] gridScale = new double[2];
        for (int i = 0; i < 2; i++) {
            double[] possibleScale = { 1.0, 2.0, 2.5, 5.0 };
            int magDif = (int) Math.log10(length[i]) - 1;
            double adjLength = length[i] / Math.pow(10, magDif);
            gridScale[i] = 5.0;
            for (double j : possibleScale) {
                int gridsLinesNum = (int) (adjLength / j);
                if (gridsLinesNum >= 10 && gridsLinesNum <= 20) {
                    gridScale[i] = j * Math.pow(10, magDif);
                    break;
                }
            }
        }

        if (ifSecondaryGrid) {
            this.putGridLines(reVal, gridScale[0], 5, Color.web("#d7d7d7"), 0.75, true, false);
            this.putGridLines(reVal, gridScale[1], 5, Color.web("#d7d7d7"), 0.75, false, false);
        }
        if (ifPrimaryGrid) {
            this.putGridLines(reVal, gridScale[0], 1, Color.web("#bdbdbd"), 2, true, ifNum);
            this.putGridLines(reVal, gridScale[1], 1, Color.web("#bdbdbd"), 2, false, ifNum);
        }
        if (origin[1] <= 0 && origin[1] >= -length[1]) {
            Rectangle rex = new Rectangle(origin[0] * scale, -1, planeSize[0], 2);
            reVal.add(rex);
        }
        if (origin[0] <= 0 && origin[0] >= -length[0]) {
            Rectangle rey = new Rectangle(-1, -planeSize[1] - origin[1] * scale, 2, planeSize[1]);
            reVal.add(rey);
        }
        return reVal;
    }

    private void putGridLines(ArrayList<Node> array, double gridScale, double gridDiv, Color color, double lineWidth,
            boolean ifX, boolean ifNum) {

        // Important: can directly do x * scale, because the (0,0) of the plane is the
        // same as the (0,0) of the Group
        int i = ifX ? 0 : 1;
        double adj = 0;
        double curtLocPixel = 0;
        int count = 0;
        double actualScale = gridScale / gridDiv;
        double newOrigin = ((int) ((planeSize[i] / 2 / scale + origin[i]) / actualScale)) * actualScale;
        newOrigin = ifX ? newOrigin : -newOrigin;
        double curtLoc = newOrigin;

        // Grids
        while ((adj = count * actualScale) < length[i]) {
            double sign = Math.signum(curtLoc - newOrigin) == 0 ? -1 : Math.signum(curtLoc - newOrigin);
            curtLoc = curtLoc - adj * sign;
            curtLocPixel = curtLoc * scale;
            Rectangle gridLines;
            if (ifX) {
                gridLines = new Rectangle(curtLocPixel - lineWidth / 2, -planeSize[1] - origin[1] * scale, lineWidth,
                        planeSize[1]);
                gridLines.setFill(color);
            } else {
                gridLines = new Rectangle(origin[0] * scale, curtLocPixel - lineWidth / 2, planeSize[0], lineWidth);
                gridLines.setFill(color);
            }
            array.add(gridLines);
            count++;
            if ((gridDiv == 1) && ifNum) {
                Label num;
                if (ifX) {
                    num = new Label(String.valueOf(curtLoc));
                    num.setLayoutX(curtLocPixel);
                    if (origin[1] < 0 && origin[1] >= -length[1]) {
                        num.setLayoutY((-origin[1] * scale < 50) ? -20 : 10);
                    } else {
                        num.setLayoutY(-origin[1] * scale - 20);
                    }
                    num.setMinSize(20, 20);
                } else {
                    if (curtLoc == 0)
                        continue;
                    num = new Label(String.valueOf(-curtLoc));
                    if (origin[0] < 0 && origin[0] >= -length[0]) {
                        num.setLayoutX((-origin[0] * scale < 100) ? -30 : 5);
                    } else {
                        num.setLayoutX(origin[0] * scale + 5);
                    }
                    num.setLayoutY(curtLocPixel);
                    num.setMinSize(20, 20);
                }
                array.add(num);
            }
        }
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
