package Jmathcal.Expression;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Jmathcal.Number.Complex.ComplexNum;

public class Expressions {
    // Reverse Poland notation
    private ExprElements[] tokens;

    public Expressions(ExprElements[] tokens) {
        this.tokens = tokens;
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        String a = sc.nextLine();
        sc.close();
        VariablePool vp = new VariablePool();
        System.out.println(parseFromFlattenExpr(a, vp));
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
    public static Expressions parseFromFlattenExpr(String expression, VariablePool varPool) {

        Properties keyWords = getKeyWords();
        expression = formattingFlattenExpr(expression);

        StringBuffer exprBuffer = new StringBuffer(expression);
        StringBuffer token = new StringBuffer();
        ArrayList<ExprElements> tokensList = new ArrayList<ExprElements>();
        Stack<ExprFunction> operationsStack = new Stack<ExprFunction>();

        Pattern numPattern = Pattern.compile("^\\d+(\\.\\d+)?");
        Matcher numMatcher = numPattern.matcher(exprBuffer);

        while (exprBuffer.length() > 0) {
            token.delete(0, token.length());

            System.out.print(tokensList);
            System.out.println(operationsStack);
            char firstChar = exprBuffer.charAt(0);
            // see if is \
            if (firstChar == 92) {
            
            // see if is a number
            } else if (numMatcher.lookingAt()) {

                token.append(exprBuffer.substring(numMatcher.start(), numMatcher.end()));
                ComplexNum tokenVal = new ComplexNum(token.toString());
                tokensList.add(tokenVal);
                exprBuffer.delete(0, token.length());
                token.delete(0, token.length());

                numMatcher = numPattern.matcher(exprBuffer);

                if (exprBuffer.length() > 0 && exprBuffer.charAt(0) == '.')
                    throw new ExprSyntaxErrorException();
                continue;

            } else if (exprBuffer.charAt(0) == ';' || exprBuffer.charAt(0) == ',') {
                exprBuffer.delete(0, 1);
                numMatcher = numPattern.matcher(exprBuffer);
            
            // see if is + - * / ^ ( )
            } else if (keyWords.getProperty(String.valueOf(firstChar)) != null) {

                String opType = keyWords.getProperty(String.valueOf(exprBuffer.charAt(0)));
                ExprFunction f = new ExprFunction(OpsType.valueOf(opType));
                
                // )
                if (f.getType() == OpsType.CLOSE_P) {
                    // popping all operation utile (
                    if (operationsStack.isEmpty())
                        throw new ExprSyntaxErrorException();
                    while (operationsStack.peek().getType().precedence != 0) {
                        tokensList.add(operationsStack.pop());
                        if (operationsStack.isEmpty())
                            throw new ExprSyntaxErrorException();
                    }
                    // popping (
                    if (operationsStack.peek().getType() == OpsType.OPEN_P) {
                        operationsStack.pop();
                    } else {
                        tokensList.add(operationsStack.pop());
                    }

                // + - * / ^ 
                } else if (f.getType().precedence != 0 && !operationsStack.isEmpty()) {
                    while (operationsStack.peek().compPrecedence(f) >= 0) {
                        tokensList.add(operationsStack.pop());
                        if (operationsStack.isEmpty())
                            break;
                    }
                    operationsStack.push(f);
                
                // (
                } else {
                    operationsStack.push(f);
                }

                // refresh numMatcher
                exprBuffer.delete(0, 1);
                numMatcher = numPattern.matcher(exprBuffer);

            } else if ((65 <= firstChar && firstChar <= 90) || (97 <= firstChar && firstChar <= 122)) {
                
                // variable with subscript
                Pattern valPattern = Pattern.compile("^[a-zA-Z]\\\\_(\\()?[A-Za-z0-9]+(\\))?");
                Matcher valMatcher = valPattern.matcher(exprBuffer);
                if (valMatcher.find()) {
                    token.delete(0, token.length());
                    token.append(exprBuffer.substring(0, valMatcher.end()));

                    exprBuffer.delete(0, valMatcher.end());
                    // if already contains variable with the same label
                    if (varPool.contains(token.toString())) {
                        tokensList.add(varPool.getVariable(token.toString()));
                    } else {
                        varPool.addVariable(new Variable(token.toString()));
                        tokensList.add(varPool.getVariable(token.toString()));
                    }

                    Pattern letPattern = Pattern.compile("^[a-zA-Z0-9]");
                    Matcher letMatcher = letPattern.matcher(exprBuffer);
                    if (letMatcher.find()) exprBuffer.insert(0, "*");

                    numMatcher = numPattern.matcher(exprBuffer);
                    continue;
                }

                // functions
                Iterator<String> iterator = keyWords.stringPropertyNames().iterator();
                String keyword;
                boolean ifFound = false;
                while (iterator.hasNext()) {
                    keyword = iterator.next();
                    if (keyword.length() == 1) continue;

                    Pattern keywordPat = Pattern.compile(keyword);
                    Matcher keywordMat = keywordPat.matcher(exprBuffer);

                    if (keywordMat.lookingAt()) {
                        ExprFunction f = new ExprFunction(OpsType.valueOf(keyWords.getProperty(keyword)));
                        operationsStack.push(f);
                        exprBuffer.delete(0, keyword.replace("\\", "").length());
                        numMatcher = numPattern.matcher(exprBuffer);
                        ifFound = true;
                        break;
                    }
                }

                // variable
                if (!ifFound) {
                    token.delete(0, token.length());
                    token.append(exprBuffer.charAt(0));
                    exprBuffer.delete(0, 1);
                    if (varPool.contains(token.toString())) {
                        tokensList.add(varPool.getVariable(token.toString()));
                    } else {
                        varPool.addVariable(new Variable(token.toString()));
                        tokensList.add(varPool.getVariable(token.toString()));
                    }
                    Pattern letPattern = Pattern.compile("^[a-zA-Z0-9]");
                    Matcher letMatcher = letPattern.matcher(exprBuffer);
                    if (letMatcher.find()) exprBuffer.insert(0, "*");

                    numMatcher = numPattern.matcher(exprBuffer);
                }

            } else {
                throw new ExprSyntaxErrorException();
            }
        }

        while (!operationsStack.isEmpty()) {
            tokensList.add(operationsStack.pop());
        }

        ExprElements[] tokens = new ExprElements[tokensList.size()];
        tokens = tokensList.toArray(tokens);
        return new Expressions(tokens);
    }

    public static String formattingFlattenExpr(String expr) {

        Pattern spacePattern = Pattern.compile("\\s");
        Matcher spaceMatcher = spacePattern.matcher(expr);
        expr = spaceMatcher.replaceAll("");

        StringBuffer buffer = new StringBuffer(expr);

        Pattern mulPattern = Pattern.compile("\\d[A-Za-z]");
        Matcher mulMatcher = mulPattern.matcher(buffer);
        while (mulMatcher.find()) {
            buffer.insert(mulMatcher.start() + 1, "*");
        }

        return buffer.toString();
    }

    private static Properties getKeyWords() {
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

        return keyWords;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.tokens);
    }

}
