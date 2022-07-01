package Jmathcal.Number;

import Jmathcal.Number.RealAnalytic.*;
import Jmathcal.Number.RealAnalytic.Rational.RationalInputType;

public class Test {
    public static void main(String[] args) {
        AnalyticExp num1 = new AnalyticExp("2", RationalInputType.INT);
        AnalyticExp num2 = new AnalyticExp("4", RationalInputType.INT);
        AnalyticExp num3 = new AnalyticExp("5", RationalInputType.INT);
        AnalyticExp num4 = new AnalyticExp("6/5", RationalInputType.INT_FRACTION);
        System.out.println((num1.negate().divide(num3).multiply(num4.add(num2))));
    }
}
