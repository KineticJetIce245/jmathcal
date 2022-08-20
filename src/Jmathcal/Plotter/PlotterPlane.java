package Jmathcal.Plotter;

import java.beans.Expression;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;

public class PlotterPlane {

    private int length;
    private int height;
    private int xAxeLength;
    private int yAxeLength;
    private double xStartVal;
    private double xEndVal;
    private double xIntervalSize;


    public PlotterPlane(double xStartVal, double xEndVal, double xIntervalSize) {
        this.xStartVal = xStartVal;
        this.xEndVal = xEndVal;
        this.xIntervalSize = xIntervalSize;
        this.xAxeLength = (int)((xEndVal - xStartVal)/xIntervalSize);
    }

    private PlotterPlane getThis() {
        return this;
    }

    public class PlanePointMap {
        private double[] valueList;
        public PlotterPlane getPlane(){
            return getThis();
        }
        private PlanePointMap() {
            this.valueList = new double[this.getPlane().xAxeLength];
        }
        public static PlanePointMap findPoints(Expressions leftExpr, Expressions rightExpr) {
            VariablePool plotPool = new VariablePool();
            plotPool.combinePool(leftExpr.getVP());
            plotPool.combinePool(rightExpr.getVP());
            
        } 
    }
}