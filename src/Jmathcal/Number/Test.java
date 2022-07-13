package Jmathcal.Number;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;


public class Test {
    public static void main(String[] args) {
        MathContext mc1 = new MathContext(102, RoundingMode.HALF_UP);
        BigDecimal b1 = new BigDecimal("-5.2336");
        System.out.println(Trigo.cos(b1, mc1));
        b1.pow(5);
    }
}
