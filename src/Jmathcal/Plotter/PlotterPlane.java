package Jmathcal.Plotter;

import java.beans.Expression;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;

public class PlotterPlane {
    
    public static void main(String[] args) {
        
    }

    private int[] resolutionXY;
    private int[] lengthXY;
    
    public PlotterPlane(int[] resolutionXY, int[] lengthXY) {
        this.resolutionXY = resolutionXY;
        this.lengthXY = lengthXY;
        
        double[] sizeOfSquare = new double[2];
        sizeOfSquare[0] = lengthXY[0] / resolutionXY[0];
        sizeOfSquare[1] = lengthXY[1] / resolutionXY[1];

        PlottingSqr[][] sqrMatrix = new PlottingSqr[resolutionXY[0]][resolutionXY[1]];
        double[][] signMatrix = new double[resolutionXY[0]+1][resolutionXY[1]+1];
        double[] currentLoc = new double[2];

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
                currentLoc[0] = sizeOfSquare[0]*i;
                currentLoc[1] = sizeOfSquare[1]*j;
                sqrMatrix[i][j] = new PlottingSqr(currentLoc, sizeOfSquare);
            }
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