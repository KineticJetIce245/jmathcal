package Jmathcal.Number;

import java.math.BigDecimal;

import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public class Test {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(Trigo.sin(new BigDecimal("3.14159"), 16));
        System.out.println(System.currentTimeMillis());
        System.out.println(Trigo.cos(new BigDecimal("3.14159"), 16));
        System.out.println(System.currentTimeMillis());
        System.out.println(Trigo.tan(new BigDecimal("3.14159"), 16));
        System.out.println(System.currentTimeMillis());
        System.out.println(Trigo.rArcsin(new BigDecimal("-0.5"), 16));
        System.out.println(System.currentTimeMillis());
        System.out.println(Trigo.rArccos(new BigDecimal("-0.5"), 16));
        System.out.println(System.currentTimeMillis());
    }
}
