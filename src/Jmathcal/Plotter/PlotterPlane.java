package Jmathcal.Plotter;

import java.beans.Expression;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;

public class PlotterPlane {
    
    public static void main(String[] args) {
        
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