package Jmathcal.Number;

import Jmathcal.Number.RealAnalytic.*;
import Jmathcal.Number.RealAnalytic.Rational.RationalInputType;

public class Test {
    public static void main(String[] args) {
        AnalyticExp exp1 = new AnalyticExp("0.98", RationalInputType.DECIMAL);
        AnalyticExp exp2 = new AnalyticExp("0.36", RationalInputType.DECIMAL);
        AnalyticExp exp3 = new AnalyticExp("1.68", RationalInputType.DECIMAL);
        System.out.println(exp1.subtract(exp3).add(exp2));
        AnalyticExp exp4 = exp1.subtract(exp3).add(exp2).multiply(exp3);
        AnalyticExp exp5 = exp3.clone();
        System.out.println(exp4);
    }
}
