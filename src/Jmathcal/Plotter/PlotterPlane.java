package Jmathcal.Plotter;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;

public class PlotterPlane {

    /**
     * PlaneInfo
     */
    public static class PlaneInfo {
    
        private int[] resolution;
        private int[] length;
        private double[] origin;

        public PlaneInfo(int[] resolution, int[] length, double[] origin) {
            this.resolution = resolution;
            this.length = length;
            this.origin = origin;
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
    
    public static void main(String[] args) {
        VariablePool vp = new VariablePool();       
        Expressions expr1 = Expressions.parseFromFlattenExpr("y^2-cosx-siny+cosh4", vp, IOBridge.DFLT_BRIDGE);
        Expressions expr2 = Expressions.parseFromFlattenExpr("3^2", vp, IOBridge.DFLT_BRIDGE);
        int[] resolutionXY = {40,40};
        int[] lengthXY = {10,10};
        double[] origin = {-5,-1};
        PlaneInfo planeInfo = new PlaneInfo(resolutionXY, lengthXY, origin);
        PlotterPlane myPlane = new PlotterPlane(planeInfo, expr1, expr2, 10);
    }
  
    public PlotterPlane(PlaneInfo planeInfo, Expressions expr1, Expressions expr2, int depth) {

        Expressions function = Expressions.subtractExpr(expr1, expr2);
        
        double[] sizeOfSquare = new double[2];
        sizeOfSquare[0] = (double)planeInfo.getLen(0) / (double)planeInfo.getRes(0);
        sizeOfSquare[1] = (double)planeInfo.getLen(1) / (double)planeInfo.getRes(1);

        System.out.println(sizeOfSquare[0]);

        PlottingSqr[][] sqrMatrix = new PlottingSqr[planeInfo.getRes(0)][planeInfo.getRes(1)];
        PlotterSign[][] signMatrix = new PlotterSign[planeInfo.getRes(0)+1][planeInfo.getRes(1)+1];

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
                int[] sqrLoc = {i, j};
                currentLoc[0] = sizeOfSquare[0]*i + xOri;
                currentLoc[1] = sizeOfSquare[1]*j + yOri;
                sqrMatrix[i][j] = new PlottingSqr(sqrLoc, currentLoc, sizeOfSquare);
                boolean canSubdivide = sqrMatrix[i][j].computeSign(signMatrix, function);

            }
        }

        for (int i = planeInfo.getRes(1) - 1; i >= 0; i--) {
            for (int j = 0; j < planeInfo.getRes(0); j++) {
                System.out.print(signMatrix[j][i]);
            }
            System.out.println("\n");
        }


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
         * @param   s   the detail message.
         */
        public WrongPlaneSettingException(String s) {
            super(s);
        }
    }
}