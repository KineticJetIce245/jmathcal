package Jmathcal.Number;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;


public class Test {
    public static void main(String[] args) {
        MathContext mc1 = new MathContext(18, RoundingMode.HALF_UP);
        ComplexNum b1 = new ComplexNum("-6.94463");
        ComplexNum b2 = new ComplexNum("0.5");
        System.out.println(Exp.pow(b1, b2, mc1));
        System.out.println(new BigDecimal("21.33369").scale());
        System.out.println(Math.log(0.255));
    }
}
