package Jmathcal.Number;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.Function.Trigo;

public class Test {
    public static void main(String[] args) {
        MathContext mc = new MathContext(16, RoundingMode.HALF_UP);
        System.out.println(Trigo.sin(new BigDecimal("3.14159"), mc));
    }
}
