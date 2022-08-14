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

    private static class ParserInfo {
        public StringBuffer exprBuffer;
        public LinkedList<ExprElements> tokensList = new LinkedList<ExprElements>();
        public Stack<ExprFunction> operationsStack = new Stack<ExprFunction>();
        public Properties keyWords;
        public ParserInfo(StringBuffer expressions, Properties keyWords) {
            this.keyWords = keyWords;
            this.exprBuffer = expressions;
        }
        /**
         * Check if the first char is '-'. If it is, handle the '-'.
         */
        public void checkNegativeSign() {
            if (exprBuffer.length() > 0 && exprBuffer.charAt(0) == '-') {
                exprBuffer.delete(0, 1);
                tokensList.add(new ExprNumber("0+0i"));
                operationsStack.push(new ExprFunction(ExprFunction.OpsType.SUB));
            }
        }

        /**
         * Check if the first char is '\'. If it is, handle the '\'.
         */
        public boolean checkBackSlash() {
            char firstChar = this.exprBuffer.charAt(0);
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
                    Matcher keywordMat = keywordPat.matcher(this.exprBuffer);

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

                insertMUL();
                return true;
            }
            return false;
        }

        public boolean checkNumber() {
            Pattern numPattern = Pattern.compile("^\\d+(\\.\\d+)?");
            Matcher numMatcher = numPattern.matcher(this.exprBuffer);
            if (numMatcher.lookingAt()) {
                StringBuffer token = new StringBuffer();
                token.append(exprBuffer.substring(numMatcher.start(), numMatcher.end()));
                ExprNumber tokenVal = new ExprNumber(token.toString() + "+0i");
                tokensList.add(tokenVal);
                exprBuffer.delete(0, token.length());
                return true;
            }
            return false;
        }

        public boolean checkSeparator() {
            if (exprBuffer.charAt(0) == ';' || exprBuffer.charAt(0) == ',') {
                exprBuffer.delete(0, 1);
                this.checkNegativeSign();
                return true;
            }
            return false;
        }

        public boolean checkArithmetic() {
            char firstChar = this.exprBuffer.charAt(0);
            if (keyWords.getProperty(String.valueOf(firstChar)) != null) {

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

                    
                } else {
                    // + - * / ^ %
                    if (f.getType().precedence != 0) {
                        while (!operationsStack.isEmpty() && operationsStack.peek().compPrecedence(f) >= 0) {
                            tokensList.add(operationsStack.pop());
                        }
                    }
                    operationsStack.push(f);
                    exprBuffer.delete(0, 1);
                    if (f.getType() == ExprFunction.OpsType.PER_CEN) {
                        this.insertMUL();
                    } else {
                        this.checkNegativeSign();
                    }
                }

                return true;
            }
            return false;
        }

        public boolean checkLetter(VariablePool varPool) {
            char firstChar = this.exprBuffer.charAt(0);
            if ((65 <= firstChar && firstChar <= 90) || (97 <= firstChar && firstChar <= 122)) {
                StringBuffer token = new StringBuffer();
                Pattern valPattern = Pattern.compile("^[a-zA-Z]_(\\()?[A-Za-z0-9]+(\\))?");
                Matcher valMatcher = valPattern.matcher(exprBuffer);
                if (valMatcher.find()) {
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
                    this.insertMUL();
                    return true;
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
                        this.checkNegativeSign();
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
                    this.insertMUL();
                }
                return true;
            }
            return false;
        }

        public void insertMUL() {
            Pattern letPattern = Pattern.compile("^[a-zA-Z0-9\\\\\\(]");
            Matcher letMatcher = letPattern.matcher(exprBuffer);
            if (letMatcher.find())
                exprBuffer.insert(0, "*");
        }
    
    }

    /**
     * Parse an expression from its flatten form to reverse poland notation.
     * The parser will first delete any space in the expression.
     * <p>
     * <b>Syntax:</b>
     * <ul>
     * <li>Arithmetic operations:
     * <p>
     * Keywords: {@code +,-,*,/,^,(,)}
     * <ul>
     * <li>{@code 1 -2 * 3} will be parsed to {@code 1,2,3,*,-}.</li>
     * <li>{@code 1/3 +0.1} will be parsed to {@code 1,3,/,0.1,+}.</li>
     * <li>{@code 4*3^0.9/6} will be parsed to {@code 4,3,0.9,^,*,6,/}.</li>
     * <li><b>Important:</b> when using "-" as negative sign, it adds parentheses
     * which enclose all operations following and having higher precedence then negative sign.
     * <b>The negative sign can not be stacked, meaning that inputting expressions
     * as "1---3" will lead to syntax problem.</b></li>
     * <li>{@code 4+-1} will be transformed to {@code 4+(0-1)} then parsed
     * to {@code 4,0,1,-,+}.</li>
     * <li>{@code 5*-6/2^7} will be transformed to {@code 5*(0-6/2^7)} then parsed
     * to {@code 5,0,6,2,7,^,/,-,*}.</li>
     * <li><b>Important:</b> Adding parenthesis but not closing them will not lead
     * to any syntax problem. However, the inverse is not permitted.</li>
     * <li>{@code 3-(6/9*(3+2} will be transformed to {@code 3-(6/9*(3+2))} then
     * parsed to {@code 3,6,9,/,3,2,+,*,-}.</li>
     * <li>{@code 8-2/5)} will not be successfully parsed.</li>
     * </ul>
     * </li>
     * <li>Trigonometry functions:
     * <p>
     * Keywords： {@code sin, cos, tan, arcsin, arccos, arctan}
     * <ul>
     * <li>Theses functions have the same precedence to addition and
     * subtraction.</li>
     * <li>{@code si nx y^3 + 9} will be parsed to {@code x,y,3,^,*,sin,9,+}.</li>
     * <li>{@code t an(x -6)^ 3-9 /4} will be parsed to
     * {@code x,6,-,3,^,tan,9,4,/,-}.</li>
     * <li>{@code ar cs in ta n9 /4} will be parsed to
     * {@code 9,4,/,tan,arcsin}.</li>
     * </ul>
     * </li>
     * <li>Logarithm:
     * <p>
     * Keywords: {@code ln, log()}
     * <ul>
     * <li>{@code ln1.9^0.39*3} will be parsed to {@code 1.9,0.39,^,3,*,ln}.</li>
     * <li>{@code log(3,13*0.39)^3.1} will be parsed to
     * {@code 3,13,0.39,*,log,3.1,^}.</li>
     * <li>{@code log(3)} will not be successfully parsed.</li>
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

        ParserInfo parserInfo = new ParserInfo(new StringBuffer(expression), keyWords);

        // Eliminate the negative sign
        parserInfo.checkNegativeSign();

        while (parserInfo.exprBuffer.length() > 0) {
            System.out.print(parserInfo.tokensList);
            System.out.println(parserInfo.operationsStack);

            // see if is constant, all constant starts with \
            if(parserInfo.checkBackSlash())
                continue;

            // see if is number
            if(parserInfo.checkNumber())
                continue;

            // see if is ';' or ','
            if(parserInfo.checkSeparator())
                continue;

            // see if is +,-,*,/,^,%,(,)
            if(parserInfo.checkArithmetic())
                continue;

            if(parserInfo.checkLetter(varPool))
                continue;

            throw new ExprSyntaxErrorException("Invalided character exception.");
        }

        while (!parserInfo.operationsStack.isEmpty()) {
            ExprFunction f = parserInfo.operationsStack.pop();
            if (f.getType() != ExprFunction.OpsType.OPEN_P) {
                parserInfo.tokensList.add(f);
            }
        }
        Expressions reVal = new Expressions(parserInfo.tokensList, varPool, bridge);
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

        Pattern mulPattern = Pattern.compile("(\\d|%)[A-Za-z\\\\\\(]");
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
        if (((ExprFunction) this.tokens.getLast()).getType().parameterNum != parameters.size())
            throw new ExprSyntaxErrorException();
        return ((ExprFunction) this.tokens.getLast()).calculate(parameters, mc).round(mc);
    }

    public void insert(int index, Expressions expr) {
        Iterator<ExprElements> i = expr.tokens.iterator();
        while (i.hasNext()) {
            this.tokens.add(index, i.next());
        }
    }

}
