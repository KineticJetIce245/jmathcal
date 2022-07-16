package Jmathcal.Number;

import java.math.BigDecimal;

import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;
import Jmathcal.Number.RealAnalytic.AnalyticExp;
import Jmathcal.Number.RealAnalytic.Rational.RationalInputType;
import Jmathcal.Number.RealAnalytic.Rational.RationalNum;


public class Test {
    public static void main(String[] args) {
        AnalyticExp e1 = new AnalyticExp(new RationalNum("2/6", RationalInputType.INT_FRACTION));
        AnalyticExp e2 = new AnalyticExp(new RationalNum("9", RationalInputType.INT));
        AnalyticExp e3 = new AnalyticExp(new RationalNum("-5", RationalInputType.INT));
        AnalyticExp e4 = new AnalyticExp(new RationalNum("99", RationalInputType.INT));
        AnalyticExp e5 = new AnalyticExp(new RationalNum("13", RationalInputType.INT));
        AnalyticExp e6 = e1.add(e2.multiply(e3.pow((e4.divide(e3)).divide(e5))));
        System.out.println(e6);
        System.out.println(e6.compute(16));
        ComplexNum c1 = new ComplexNum("-5").pow(new ComplexNum("0.2"));
        System.out.println(c1);
        System.out.println(c1.getRValue());
        System.out.println(c1.getPhiValue());
        System.out.println(Trigo.rArcsin(new BigDecimal("1"), 16));
        System.out.println(Exp.rPow(new BigDecimal("0.5"), new BigDecimal("0.5"), 39));
    }
}
