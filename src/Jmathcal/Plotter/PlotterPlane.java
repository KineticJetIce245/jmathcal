package Jmathcal.Plotter;

import java.util.ArrayList;

import Jmathcal.Expression.Expressions;

public class PlotterPlane {

    public ArrayList<PlottingSqr> toDrawList;
    public PlaneInfo planeInfo;
    public Expressions expr;

    /**
     * PlaneInfo
     */
    public static class PlaneInfo {

        /**
         * Number of square per side
         */
        private int[] resolution;
        /**
         * The mathematic size of the plane
         */
        private int[] length;
        /**
         * The coordinate of the left bottom of the plane
         */
        private double[] origin;
        /**
         * The size of the plane (in pixel)
         */
        private int[] planeSize;
        private double scale;

        public PlaneInfo(int[] resolution, int[] length, double[] origin, int[] planeSize) {
            this.resolution = resolution;
            this.length = length;
            this.origin = origin;
            this.planeSize = planeSize;
            double xScale = (double) planeSize[0] / (double) length[0];
            double yScale = (double) planeSize[1] / (double) length[1];
            this.scale = xScale > yScale ? yScale : xScale;
        }

        public double getScale() {
            return this.scale;
        }

        public int getScrSize(int index) {
            return planeSize[index];
        }

        public int getRes(int index) {
            return resolution[index];
        }

        public int getLen(int index) {
            return length[index];
        }

        public double getOri(int index) {
            return origin[index];
        }
    }

    public PlotterPlane(PlaneInfo planeInfo, Expressions expr1, Expressions expr2) {
        this.planeInfo = planeInfo;

        Expressions function = Expressions.subtractExpr(expr1, expr2);
        this.expr = function;

        double[] sizeOfSquare = new double[2];
        sizeOfSquare[0] = (double) planeInfo.getLen(0) / (double) planeInfo.getRes(0);
        sizeOfSquare[1] = (double) planeInfo.getLen(1) / (double) planeInfo.getRes(1);
        for (double i : sizeOfSquare)
            if (i == 0)
                throw new TooSmallDivisionsException();

        //PlottingSqr[][] sqrMatrix = new PlottingSqr[planeInfo.getRes(0)][planeInfo.getRes(1)];
        PlotterSign[][] signMatrix = new PlotterSign[planeInfo.getRes(0) + 1][planeInfo.getRes(1) + 1];

        this.toDrawList = new ArrayList<PlottingSqr>();

        /**
         * Build a matrix of of plotting divisions
         * for example : if the x resolution = 10
         * then the x-axis will be divided by 10.
         * Each plotting division is labeled the
         * coordinate of the left bottom coin of the
         * division.
         */
        double xOri = planeInfo.getOri(0);
        double yOri = planeInfo.getOri(1);
        for (int i = 0; i < planeInfo.getRes(0); i++) {
            for (int j = 0; j < planeInfo.getRes(1); j++) {
                double[] currentLoc = new double[2];
                int[] sqrLoc = { i, j };
                currentLoc[0] = sizeOfSquare[0] * i + xOri;
                currentLoc[1] = sizeOfSquare[1] * j + yOri;
                /*
                sqrMatrix[i][j] = new PlottingSqr(sqrLoc, currentLoc, sizeOfSquare);
                boolean canSubdivide = sqrMatrix[i][j].computeSign(signMatrix, function);
                if (canSubdivide)
                    this.toDrawList.add(sqrMatrix[i][j]);
                */
                PlottingSqr sqr = new PlottingSqr(sqrLoc, currentLoc, sizeOfSquare);
                boolean canSubdivide = sqr.computeSign(signMatrix, function);
                if (canSubdivide)
                    this.toDrawList.add(sqr);
                
            }
        }
    }

    public ArrayList<PlottingSqr> subdivide() {
        ArrayList<PlottingSqr> subdivideList = new ArrayList<PlottingSqr>();
        for (PlottingSqr i : toDrawList) {
            for (PlottingSqr j : i.subdivide()) {
                subdivideList.add(j);
            }
        }
        toDrawList = subdivideList;
        return subdivideList;
    }

    public class WrongPlaneSettingException extends IllegalArgumentException {

        @java.io.Serial
        private static final long serialVersionUID = -6286288328490453673L;

        /**
         * Constructs a {@code WrongPlaneSettingException} with no
         * detail message.
         */
        public WrongPlaneSettingException() {
            super();
        }

        /**
         * Constructs a {@code WrongPlaneSettingException} with the
         * specified detail message.
         *
         * @param s the detail message.
         */
        public WrongPlaneSettingException(String s) {
            super(s);
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