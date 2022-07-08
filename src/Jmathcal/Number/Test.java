package Jmathcal.Number;

import java.math.BigDecimal;
import java.math.RoundingMode;

import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public class Test {
    public static void main(String[] args) {
        ComplexNum a = new ComplexNum("-1.54", "2.64", 16);
        ComplexNum b = new ComplexNum("56.21", "-6.9", 16);
        ComplexNum c = a.divide(b, 16);
        System.out.println(Exp.exp(a, 16));
        System.out.println(Exp.exp(b, 16));
        System.out.println(c);
        System.out.println(c.getRValue());
        System.out.println(c.getPhiValue());
        System.out.println(Trigo.cos(new BigDecimal("-10697933"), 16));
        ComplexNum d = new ComplexNum("0", "1", 16);
        System.out.println(System.currentTimeMillis());
        System.out.println(Trigo.tan(d, 16));
        System.out.println(System.currentTimeMillis());
    }
}
