package Jmathcal.Number;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public class Test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StringBuffer buffer = new StringBuffer(sc.nextLine());
        Pattern mulPattern = Pattern.compile("(\\d|%)[A-Za-z\\\\\\(]");
        Matcher mulMatcher = mulPattern.matcher(buffer);
        while (mulMatcher.find()) {
            buffer.insert(mulMatcher.start() + 1, "*");
            mulMatcher = mulPattern.matcher(buffer);
        }
        System.out.println(buffer);
    }
}
