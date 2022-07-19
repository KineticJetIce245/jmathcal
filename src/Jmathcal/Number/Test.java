package Jmathcal.Number;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public class Test {
    public static void main(String[] args) {
        MathContext mc2 = new MathContext(16, RoundingMode.HALF_UP);
        ComplexNum num1 = new ComplexNum("2","4");
        System.out.println(Trigo.arcsin(num1, mc2));
    }
}
