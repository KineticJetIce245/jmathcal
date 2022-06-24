package JMathcal.Number;

import JMathcal.Number.Rational.InputType;
import JMathcal.Number.Rational.RationalNum;

public class Test {
    public static void main(String[] args) {
        RationalNum num1 = new RationalNum("49.09R8", InputType.RECURRING_DECIMAL);
        System.out.println(num1.reduce());
    }
}
