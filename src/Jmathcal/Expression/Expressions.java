package Jmathcal.Expression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Jmathcal.IOControl.IOBridge;

public class Expressions implements ExprElements {

    public static int PRECI = 10;
    public static File configPath = new File("config/calculator/flattenExpr.xml");

    // Reverse Poland notation
    private LinkedList<ExprElements> tokens;
    private VariablePool varPool;
    private IOBridge bridge;

    public Expressions(LinkedList<ExprElements> tokens, VariablePool vp, IOBridge bridge) {
        this.tokens = tokens;
        this.varPool = vp;
        this.bridge = bridge;
    }

    public static void main(String args[]) {
        MathContext calMc = new MathContext(18, RoundingMode.HALF_UP);
        MathContext mc = new MathContext(16, RoundingMode.HALF_UP);

        IOBridge panel = new IOBridge() {

            @Override
            public void outSendMessage(String msg) {
                System.out.println(msg);
            }

            @Override
            public String askForInput(String msg) {
                System.out.println(msg);
                InputStreamReader inputStream = new InputStreamReader(System.in) {
                    @Override
                    public void close() throws IOException {
                    }
                };
                Scanner sc = new Scanner(inputStream);
                String input = sc.nextLine();
                sc.close();
                return input;
            }

            @Override
            public File getPropertiesLoc() {
                return configPath;
            }

        };
        String a = panel.askForInput("Input: ");
        VariablePool vp = new VariablePool();
        Expressions expr = parseFromFlattenExpr(a, vp, panel);
        System.out.println(expr);
        System.out.println(expr.calculate(calMc).round(mc));
    }

    /**
     * Parse an expression from its flatten form to reverse poland notation.
     * <p>
     * <b>Syntax:</b>
     * <ul>
     * <li>Arithmetic operations:
     * <p>
     * Keywords: {@code +,-,*,/,^,(,)}
     * <ul>
     * <li>{@code 1 -2 * 3} will be parsed as {@code 1,2,3,*,-}</li>
     * <li>{@code 1/3 +0.1} will be parsed as {@code 1,3,/,0.1,+}</li>
     * <li>{@code 4*3^0.9/6} will be parsed as {@code 4,3,0.9,^,*,6,/}</li>
     * <li><b>Important:</b> when using "-" as negative sign,
     * it adds parentheses which encloses all operations following
     * the "-" sign and having higher order then "-" sign.</li>
     * <li>{@code 4+-1} will be transformed to {@code 4+(0-1)} then parsed
     * as {@code 4,0,1,-,+}</li>
     * <li>{@code 5*-6/2^7} will be transformed to {@code 5*(0-6/2^7)} then parsed
     * as {@code 5,0,6,2,7,^,/,-,*}</li>
     * <li>For parentheses, it is permitted to <b></b>
     * </ul>
     * </li>
     * <li>Trigonometry functions:
     * <p>
     * Keywords： {@code sin, cos, tan, arcsin, arccos, arctan}
     * <ul>
     * <li>{@code si nx y^3 + 9} will be parsed as {@code x,y,3,^,*,sin,9,+}</li>
     * <li>{@code t an(x -6)^ 3-9 /4} will be parsed as
     * {@code x,6,-,3,^,tan,9,4,/,-}</li>
     * <li>{@code ar cs in ta n9 /4} will be parsed as {@code 9,4,/,tan,arcsin}</li>
     * </ul>
     * </li>
     * <li>:
     * <ul>
     * <li>{@code si nx y^3 + 9} will be parsed as {@code x,y,3,^,*,sin,9,+}</li>
     * <li>{@code t an(x -6)^ 3-9 /4} will be parsed as
     * {@code x,6,-,3,^,tan,9,4,/,-}</li>
     * <li>{@code ar cs in ta n9 /4} will be parsed as {@code 9,4,/,tan,arcsin}</li>
     * </ul>
     * </li>
     * </li>
     * </ul>
     * 
     * @param expression
     * @param varPool
     * @return
     */
    public static Expressions parseFromFlattenExpr(String expression, VariablePool varPool, IOBridge bridge) {

        Properties keyWords = getKeyWords(bridge.getPropertiesLoc());
        expression = formattingFlattenExpr(expression);

        StringBuffer exprBuffer = new StringBuffer(expression);
        StringBuffer token = new StringBuffer();
        LinkedList<ExprElements> tokensList = new LinkedList<ExprElements>();
        Stack<ExprFunction> operationsStack = new Stack<ExprFunction>();

        // negative test
        if (exprBuffer.charAt(0) == '-') {
            exprBuffer.delete(0, 1);
            tokensList.add(new ExprNumber("0+0i"));
            operationsStack.push(new ExprFunction(ExprFunction.OpsType.SUB));
        }

        // Numbers
        Pattern numPattern = Pattern.compile("^\\d+(\\.\\d+)?");
        Matcher numMatcher = numPattern.matcher(exprBuffer);

        while (exprBuffer.length() > 0) {
            token.delete(0, token.length());
            char firstChar = exprBuffer.charAt(0);

            // see if is constant, all constant starts with \
            if (firstChar == 92) {
                Iterator<String> iterator = keyWords.stringPropertyNames().iterator();
                String keyword;
                boolean ifFound = false;
                while (iterator.hasNext()) {
                    // one char keyword causes problems
                    keyword = iterator.next();
                    if (keyword.length() == 1)
                        continue;

                    Pattern keywordPat = Pattern.compile(keyword);
                    Matcher keywordMat = keywordPat.matcher(exprBuffer);

                    if (keywordMat.lookingAt()) {
                        Constant c = new Constant(Constant.Constants.valueOf(keyWords.getProperty(keyword)));
                        tokensList.add(c);
                        // delete the right amount of char
                        exprBuffer.delete(0, keyword.replace("\\\\", "\\").length());
                        ifFound = true;
                        break;
                    }
                }
                if (!ifFound)
                    throw new ExprSyntaxErrorException("Constant not found exception.");

                Pattern letPattern = Pattern.compile("^[a-zA-Z0-9\\\\\\(]");
                Matcher letMatcher = letPattern.matcher(exprBuffer);
                if (letMatcher.find())
                    exprBuffer.insert(0, "*");

                // see if is a number
            } else if (numMatcher.lookingAt()) {

                token.append(exprBuffer.substring(numMatcher.start(), numMatcher.end()));
                ExprNumber tokenVal = new ExprNumber(token.toString() + "+0i");
                tokensList.add(tokenVal);
                exprBuffer.delete(0, token.length());
                token.delete(0, token.length());

                continue;

                // separator
            } else if (exprBuffer.charAt(0) == ';' || exprBuffer.charAt(0) == ',') {
                exprBuffer.delete(0, 1);

                // negative test
                if (exprBuffer.length() > 0 && exprBuffer.charAt(0) == '-') {
                    exprBuffer.delete(0, 1);
                    tokensList.add(new ExprNumber("0+0i"));
                    operationsStack.push(new ExprFunction(ExprFunction.OpsType.SUB));
                }
                // see if is + - * / ^ ( )
            } else if (keyWords.getProperty(String.valueOf(firstChar)) != null) {

                String opType = keyWords.getProperty(String.valueOf(exprBuffer.charAt(0)));
                ExprFunction f = new ExprFunction(ExprFunction.OpsType.valueOf(opType));

                // )
                if (f.getType() == ExprFunction.OpsType.CLOSE_P) {
                    // popping all operation utile (
                    if (operationsStack.isEmpty())
                        throw new ExprSyntaxErrorException("Unexpected \")\".");
                    while (operationsStack.peek().getType().precedence != 0) {
                        tokensList.add(operationsStack.pop());
                        if (operationsStack.isEmpty())
                            throw new ExprSyntaxErrorException("Unexpected \")\".");
                    }
                    // popping (
                    if (operationsStack.peek().getType() == ExprFunction.OpsType.OPEN_P) {
                        operationsStack.pop();
                    } else {
                        // if is function with multiple parameters
                        tokensList.add(operationsStack.pop());
                    }
                    exprBuffer.delete(0, 1);

                    // + - * / ^ %
                } else if (f.getType().precedence != 0 && !operationsStack.isEmpty()) {
                    while (!operationsStack.isEmpty() && operationsStack.peek().compPrecedence(f) >= 0) {
                        tokensList.add(operationsStack.pop());
                    }
                    operationsStack.push(f);
                    exprBuffer.delete(0, 1);
                    if (f.getType() == ExprFunction.OpsType.PER_CEN) {
                        Pattern letPattern = Pattern.compile("^[a-zA-Z0-9\\\\\\(]");
                        Matcher letMatcher = letPattern.matcher(exprBuffer);
                        if (letMatcher.find())
                            exprBuffer.insert(0, "*");
                    }

                    // negative test
                    if (exprBuffer.length() > 0 && exprBuffer.charAt(0) == '-') {
                        exprBuffer.delete(0, 1);
                        tokensList.add(new ExprNumber("0+0i"));
                        operationsStack.push(new ExprFunction(ExprFunction.OpsType.SUB));
                    }

                    // (
                } else {
                    operationsStack.push(f);
                    exprBuffer.delete(0, 1);
                    // negative test
                    if (exprBuffer.length() > 0 && exprBuffer.charAt(0) == '-') {
                        exprBuffer.delete(0, 1);
                        tokensList.add(new ExprNumber("0+0i"));
                        operationsStack.push(new ExprFunction(ExprFunction.OpsType.SUB));
                    }
                }

                // letters
            } else if ((65 <= firstChar && firstChar <= 90) || (97 <= firstChar && firstChar <= 122)) {

                // variable with subscript
                Pattern valPattern = Pattern.compile("^[a-zA-Z]_(\\()?[A-Za-z0-9]+(\\))?");
                Matcher valMatcher = valPattern.matcher(exprBuffer);
                if (valMatcher.find()) {
                    token.delete(0, token.length());
                    token.append(exprBuffer.substring(0, valMatcher.end()));

                    exprBuffer.delete(0, valMatcher.end());
                    // if already contains variable with the same label
                    if (varPool.contains(token.toString())) {
                        tokensList.add(varPool.getVariable(token.toString()));
                    } else {
                        varPool.new Variable(token.toString());
                        tokensList.add(varPool.getVariable(token.toString()));
                    }
                    // check if there is a letter, number after
                    Pattern letPattern = Pattern.compile("^[a-zA-Z0-9\\\\\\(]");
                    Matcher letMatcher = letPattern.matcher(exprBuffer);
                    if (letMatcher.find())
                        exprBuffer.insert(0, "*");
                    continue;
                }

                // functions
                Iterator<String> iterator = keyWords.stringPropertyNames().iterator();
                String keyword;
                boolean ifFound = false;
                while (iterator.hasNext()) {
                    keyword = iterator.next();
                    if (keyword.length() == 1)
                        continue;

                    Pattern keywordPat = Pattern.compile(keyword);
                    Matcher keywordMat = keywordPat.matcher(exprBuffer);

                    if (keywordMat.lookingAt()) {
                        ExprFunction f = new ExprFunction(ExprFunction.OpsType.valueOf(keyWords.getProperty(keyword)));
                        operationsStack.push(f);
                        exprBuffer.delete(0, keyword.replace("\\", "").length());

                        // negative test
                        if (exprBuffer.length() > 0 && exprBuffer.charAt(0) == '-') {
                            exprBuffer.delete(0, 1);
                            tokensList.add(new ExprNumber("0+0i"));
                            operationsStack.push(new ExprFunction(ExprFunction.OpsType.SUB));
                        }
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
                        varPool.new Variable(token.toString());
                        tokensList.add(varPool.getVariable(token.toString()));
                    }
                    Pattern letPattern = Pattern.compile("^[a-zA-Z0-9\\\\\\(]");
                    Matcher letMatcher = letPattern.matcher(exprBuffer);
                    if (letMatcher.find())
                        exprBuffer.insert(0, "*");
                }

            } else {
                throw new ExprSyntaxErrorException("Invalided character exception.");
            }
            System.out.print(tokensList);
            System.out.println(operationsStack);

            // reset numMatcher
            numMatcher = numPattern.matcher(exprBuffer);
        }

        while (!operationsStack.isEmpty()) {
            ExprFunction f = operationsStack.pop();
            if (f.getType() != ExprFunction.OpsType.OPEN_P) {
                tokensList.add(f);
            }
        }
        Expressions reVal = new Expressions(tokensList, varPool, bridge);
        reVal.encapsulateParts();
        return reVal;
    }

    public static String formattingFlattenExpr(String expr) {

        Pattern spacePattern = Pattern.compile("\\s");
        Matcher spaceMatcher = spacePattern.matcher(expr);
        expr = spaceMatcher.replaceAll("");

        StringBuffer buffer = new StringBuffer(expr);

        Pattern powPattern = Pattern.compile("\\dE(\\+)?");
        Matcher powMatcher = powPattern.matcher(buffer);
        while (powMatcher.find()) {
            buffer.replace(powMatcher.start() + 1, powMatcher.end(), "*10^");
        }

        Pattern mulPattern = Pattern.compile("\\d[A-Za-z\\\\\\(]");
        Matcher mulMatcher = mulPattern.matcher(buffer);
        while (mulMatcher.find()) {
            buffer.insert(mulMatcher.start() + 1, "*");
        }

        System.out.println(buffer);
        return buffer.toString();
    }

    private static Properties getKeyWords(File path) {
        Properties keyWords = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
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
        return this.tokens.toString();
    }

    public ExprNumber toNumber(MathContext mc) {
        return this.calculate(mc);
    }

    private void encapsulateParts() {
        Iterator<ExprElements> i = this.tokens.iterator();
        while (true) {
            ExprElements element = i.next();
            if (this.tokens.indexOf(element) == this.tokens.size() - 1) {
                if (element instanceof ExprFunction) {
                    break;
                } else if (this.tokens.size() == 1 && !(element instanceof ExprFunction)) {
                    break;
                } else {
                    throw new ExprSyntaxErrorException("Unfinished expression");
                }
            }
            if (element instanceof ExprFunction) {
                ExprFunction f = (ExprFunction) element;
                // it's always the first element that it gets
                int index = this.tokens.indexOf(element);
                LinkedList<ExprElements> expr = new LinkedList<ExprElements>();
                for (int j = 0; j < f.getType().parameterNum; j++) {
                    expr.add(this.tokens.remove(index - f.getType().parameterNum));
                }
                expr.add(element);
                this.tokens.set(index - f.getType().parameterNum, new Expressions(expr, this.varPool, this.bridge));
                i = this.tokens.iterator();
            }
        }
    }

    public ExprNumber calculate(MathContext mc) {
        if (this.tokens.size() == 1) {
            if (!(this.tokens.get(0) instanceof ExprFunction)) {
                if (this.tokens.get(0) instanceof VariablePool.Variable)
                    ((VariablePool.Variable) this.tokens.get(0)).askForValue(this.bridge, mc);
                return this.tokens.get(0).toNumber(mc);
            }
            throw new ExprSyntaxErrorException();
        }
        LinkedList<ExprElements> parameters = new LinkedList<ExprElements>();
        Iterator<ExprElements> i = this.tokens.iterator();
        while (i.hasNext()) {
            ExprElements element = i.next();
            if (element instanceof VariablePool.Variable) {
                if (((VariablePool.Variable) element).getValue() == null)
                    ((VariablePool.Variable) element).askForValue(this.bridge, mc);
            }
            parameters.add(element);
        }
        parameters.removeLast();
        return ((ExprFunction) this.tokens.getLast()).calculate(parameters, mc).round(mc);
    }

    public void insert(int index, Expressions expr) {
        Iterator<ExprElements> i = expr.tokens.iterator();
        while (i.hasNext()) {
            this.tokens.add(index, i.next());
        }
    }

}
