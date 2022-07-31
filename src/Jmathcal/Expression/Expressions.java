package Jmathcal.Expression;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expressions {
    // Reverse Poland notation
    private ExprElements[] tokens;

    public Expressions(ExprElements[] tokens) {
        this.tokens = tokens;
    }

    public static void main(String args[]) {
        parseFromFlattenExpr("2.3/6");
    }

    /**
     * <ul>
     * <li>
     * Examples :
     * <ul>
     * <li>2*3-4/5sin(sqrt(6^(8/0.36))</li>
     * </ul>
     * </li>
     * </ul>
     * 
     * @param expression
     * @return
     */
    public static Expressions parseFromFlattenExpr(String expression) {

        Properties keyWords = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config/calculator/flattenExpr.xml");
            keyWords.loadFromXML(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        StringBuffer exprBuffer = new StringBuffer(expression);
        StringBuffer token = new StringBuffer();
        ArrayList<ExprElements> tokensList = new ArrayList<ExprElements>();

        Pattern numPattern = Pattern.compile("^\\d+(\\.\\d+)?");
        Matcher numMatcher = numPattern.matcher(exprBuffer);

        while (exprBuffer.length() > 0) {
            if (numMatcher.lookingAt()) {
                token.append(exprBuffer.substring(numMatcher.start(), numMatcher.end()));
                System.out.println(token);
            }
            break;
        }

        ExprElements[] tokens = new ExprElements[tokensList.size()];
        tokens = tokensList.toArray(tokens);
        return new Expressions(tokens);
    }

}
