package Number;

import Number.RealAnalytic.Rational.RationalInputType;
import Number.RealAnalytic.Rational.RationalNum;

public class Test {
    public static void main(String[] args) {
        RationalNum num1 = new RationalNum("6.489", RationalInputType.DECIMAL);
        RationalNum num2 = new RationalNum("1/9", RationalInputType.INT_FRACTION);
        System.out.println(num1 + "+" + num2 + "=" + num1.add(num2));
    }
}
