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
                    public void close() throws IOException {}
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
        System.out.println(expr.calculate(mc));
    }

    /**
     * Parse an expression from its flatten form.
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

        // Numbers
        Pattern numPattern = Pattern.compile("^\\d+(\\.\\d+)?");
        Matcher numMatcher = numPattern.matcher(exprBuffer);

        while (exprBuffer.length() > 0) {
            token.delete(0, token.length());

            System.out.print(tokensList);
            System.out.println(operationsStack);
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
                        numMatcher = numPattern.matcher(exprBuffer);
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

                numMatcher = numPattern.matcher(exprBuffer);

                // see if is a number
            } else if (numMatcher.lookingAt()) {

                token.append(exprBuffer.substring(numMatcher.start(), numMatcher.end()));
                ExprNumber tokenVal = new ExprNumber(token.toString()+"+0i");
                tokensList.add(tokenVal);
                exprBuffer.delete(0, token.length());
                token.delete(0, token.length());

                numMatcher = numPattern.matcher(exprBuffer);
                continue;

                // separator
            } else if (exprBuffer.charAt(0) == ';' || exprBuffer.charAt(0) == ',') {
                exprBuffer.delete(0, 1);
                numMatcher = numPattern.matcher(exprBuffer);

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

                    // + - * / ^
                } else if (f.getType().precedence != 0 && !operationsStack.isEmpty()) {
                    while (!operationsStack.isEmpty() && operationsStack.peek().compPrecedence(f) >= 0) {
                        tokensList.add(operationsStack.pop());
                    }
                    operationsStack.push(f);

                    // (
                } else {
                    operationsStack.push(f);
                }

                // refresh numMatcher
                exprBuffer.delete(0, 1);
                numMatcher = numPattern.matcher(exprBuffer);

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

                    numMatcher = numPattern.matcher(exprBuffer);
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
                        varPool.new Variable(token.toString());
                        tokensList.add(varPool.getVariable(token.toString()));
                    }
                    Pattern letPattern = Pattern.compile("^[a-zA-Z0-9\\\\\\(]");
                    Matcher letMatcher = letPattern.matcher(exprBuffer);
                    if (letMatcher.find())
                        exprBuffer.insert(0, "*");

                    numMatcher = numPattern.matcher(exprBuffer);
                }

            } else {
                throw new ExprSyntaxErrorException("Invalided character exception.");
            }
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

        Pattern mulPattern = Pattern.compile("\\d[A-Za-z\\\\\\(]");
        Matcher mulMatcher = mulPattern.matcher(buffer);
        while (mulMatcher.find()) {
            buffer.insert(mulMatcher.start() + 1, "*");
        }

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
                ExprFunction f = (ExprFunction)element;
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
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        if (this.tokens.size() == 1) {
            if (!(this.tokens.get(0) instanceof ExprFunction)){
                if (this.tokens.get(0) instanceof VariablePool.Variable)
                    ((VariablePool.Variable)this.tokens.get(0)).askForValue(this.bridge, mc);
                return new ExprNumber(this.tokens.get(0).toStrVal(mc));
            }
            throw new ExprSyntaxErrorException();
        }
        LinkedList<ExprElements> parameters = new LinkedList<ExprElements>();
        Iterator<ExprElements> i = this.tokens.iterator();
        while (i.hasNext()) {
            ExprElements element = i.next();
            if (element instanceof VariablePool.Variable) {
                ((VariablePool.Variable)element).askForValue(this.bridge, mc);
            }
            parameters.add(element);
        }
        parameters.removeLast();
        return ((ExprFunction)this.tokens.getLast()).calculate(parameters, calPrecision);
    }

    public void insert(int index, Expressions expr) {
        Iterator<ExprElements> i = expr.tokens.iterator();
        while (i.hasNext()) {
            this.tokens.add(index, i.next());
        }
    }

}
