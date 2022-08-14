package Jmathcal.Number;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        StringBuffer buffer = new StringBuffer(input);
        Pattern powPattern = Pattern.compile("\\d\\\\E");
        Matcher powMatcher = powPattern.matcher(buffer);
        while (powMatcher.find()) {
            buffer.replace(powMatcher.start() + 1, powMatcher.end(), "*10^");
        }
        System.out.println(buffer);
        sc.close();
    }
}
