package Jmathcal.Plotter;

import java.io.File;
import java.util.HashMap;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;

public class PlotterPlane {
    
    public static void main(String[] args) {
        VariablePool vp = new VariablePool();       
        Expressions expr1 = Expressions.parseFromFlattenExpr("5x", vp, IOBridge.DFLT_BRIDGE);
        Expressions expr2 = Expressions.parseFromFlattenExpr("y", vp, IOBridge.DFLT_BRIDGE);
        int[] resolutionXY = {10,10};
        int[] lengthXY = {10,10};
        PlotterPlane myPlane = new PlotterPlane(resolutionXY, lengthXY, expr1, expr2);
    }

    private int[] resolutionXY;
    private int[] lengthXY;
    
    public PlotterPlane(int[] resolutionXY, int[] lengthXY, Expressions expr1, Expressions expr2) {

        Expressions function = Expressions.subtractExpr(expr1, expr2);

        this.resolutionXY = resolutionXY;
        this.lengthXY = lengthXY;
        
        double[] sizeOfSquare = new double[2];
        sizeOfSquare[0] = lengthXY[0] / resolutionXY[0];
        sizeOfSquare[1] = lengthXY[1] / resolutionXY[1];

        PlottingSqr[][] sqrMatrix = new PlottingSqr[resolutionXY[0]][resolutionXY[1]];
        PlotterSign[][] signMatrix = new PlotterSign[resolutionXY[0]+1][resolutionXY[1]+1];

        /**
         * Build a matrix of of plotting divisions
         * for example : if the x resolution = 10
         * then the x-axis will be divided by 10.
         * Each plotting division is labeled the
         * coordinate of the left bottom coin of the
         * division.
         */
        for (int i = 0; i < resolutionXY[0]; i++) {
            for (int j = 0; j < resolutionXY[1]; j++) {
                double[] currentLoc = new double[2];
                int[] sqrLoc = {i, j};
                currentLoc[0] = sizeOfSquare[0]*i;
                currentLoc[1] = sizeOfSquare[1]*j;
                sqrMatrix[i][j] = new PlottingSqr(sqrLoc, currentLoc, sizeOfSquare);
                sqrMatrix[i][j].computeSign(signMatrix, function);
            }
        }

        for (int i = resolutionXY[1] - 1; i >= 0; i--) {
            for (int j = 0; j < resolutionXY[0]; j++) {
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