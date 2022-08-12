package Jmathcal.Number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {
    public static void main(String[] args) {
        Pattern pat = Pattern.compile("^(\\+|\\-)?\\d+(\\.\\d+)?");
        Matcher mat = pat.matcher("-1.2");
        System.out.println(mat.matches());
    }
}
