package Jmathcal.Expression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Jmathcal.Expression.ExprFunction.OpsType;
import Jmathcal.IOControl.IOBridge;

/**
 * The {@code Expression} class stores an expression
 * and evaluates the expression with required
 * precision.
 * To create an {@code Expression} instance, input
 * the flatten form in {@code String} of the expression.</p>
 * <ul><li>See the method: {@code parseFromFlattenExpr()}</li></ul>
 */
public class Expressions implements ExprElements {

    public static int PRECI = 10;

    // Reverse Poland notation
    private LinkedList<ExprElements> tokens;
    private VariablePool varPool;
    private IOBridge bridge;
    private ExprNumber valueOfExpression;

    public Expressions(LinkedList<ExprElements> tokens, VariablePool vp, IOBridge bridge) {
        this.tokens = tokens;
        this.varPool = vp;
        this.bridge = bridge;
    }

    public static void main(String args[]) {
        
        MathContext calMc = new MathContext(26, RoundingMode.HALF_UP);
        MathContext mc = new MathContext(16, RoundingMode.HALF_UP);

        IOBridge panel = IOBridge.DFLT_BRIDGE;
        String a = panel.askForInput("Input: ");
        VariablePool vp = new VariablePool();
        Expressions expr = parseFromFlattenExpr(a, vp, panel);
        System.out.println(expr);
        System.out.println(System.currentTimeMillis());
        System.out.println(expr.calculate(calMc).round(mc));
        System.out.println(System.currentTimeMillis());
        Expressions dExpr = expr.findDerivative("x", new ExprNumber("0.0000000000001+0i"), mc);
        System.out.println(dExpr);
        System.out.println(System.currentTimeMillis());
        System.out.println(dExpr.calculate(calMc).round(mc));
        System.out.println(System.currentTimeMillis());

    }

    /**
     * Inner class to prase the String
     */
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
        public boolean checkBackSlash(VariablePool varPool) {
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
                    Pattern constantKeyPat = Pattern.compile(keyword);
                    Matcher constantKeyMat = constantKeyPat.matcher(this.exprBuffer);

                    if (constantKeyMat.lookingAt()) {
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
                while ((!operationsStack.isEmpty()) && operationsStack.peek().getType().precedence > 0) {
                    tokensList.add(operationsStack.pop());
                }
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
                    insertMUL();

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
                Pattern valPattern = Pattern.compile("[a-zA-Z]_\\([A-Za-z0-9\\*]+\\)");
                Matcher valMatcher = valPattern.matcher(exprBuffer);
                if (valMatcher.lookingAt()) {
                    String tempString = exprBuffer.substring(valMatcher.start(), valMatcher.end());
                    tempString = tempString.replace("*", "");
                    token.append(tempString);

                    exprBuffer.delete(valMatcher.start(), valMatcher.end());
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
                String keyword = null;
                boolean ifFound = false;
                String longestKeyW = "";
                while (iterator.hasNext()) {
                    keyword = iterator.next();
                    if (keyword.length() == 1)
                        continue;
                    Pattern keywordPat = Pattern.compile(keyword);
                    Matcher keywordMat = keywordPat.matcher(exprBuffer);

                    if (keywordMat.lookingAt()) {
                        ifFound = true;
                        longestKeyW = longestKeyW.length() >= keyword.length() ? longestKeyW : keyword;
                    }
                }

                if (ifFound) {
                    ExprFunction f = new ExprFunction(ExprFunction.OpsType.valueOf(keyWords.getProperty(longestKeyW)));
                    operationsStack.push(f);
                    exprBuffer.delete(0, longestKeyW.replace("\\", "").length());
                    // negative test
                    this.checkNegativeSign();

                } else { // single letter variable
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

        public boolean checkMultiLet(VariablePool varPool) {
            if (this.exprBuffer.charAt(0) != 91)
                return false;
            Pattern multiLetPat = Pattern.compile("\\[[A-Za-z]+\\](_\\([A-Za-z0-9]+\\))?");
            Matcher multiLetMat = multiLetPat.matcher(this.exprBuffer);
            if (!multiLetMat.lookingAt())
                throw new ExprSyntaxErrorException("Invalid multiple letters variable input, check your input around \"[]\"");
                
            String multiLetVar = this.exprBuffer.substring(multiLetMat.start(), multiLetMat.end());
            multiLetVar = multiLetVar.replace("*", "");

            VariablePool.Variable var = varPool.contains(multiLetVar) ?
                    varPool.getVariable(multiLetVar) : varPool.new Variable(multiLetVar);

            tokensList.add(var);
            this.exprBuffer.delete(multiLetMat.start(), multiLetMat.end());
            this.insertMUL();
            return true;
        }

        public void insertMUL() {
            Pattern letPattern = Pattern.compile("^[a-zA-Z0-9\\[\\(\\\\]");
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
     * which enclose all operations following and having higher precedence then
     * negative sign.
     * <b>The negative sign can not be stacked, meaning that inputting expressions
     * as "1---3" will lead to syntax problem.</b></li>
     * <li>{@code 4+-1} will be transformed to {@code 4+(0-1)} then parsed
     * to {@code 4,0,1,-,+}.</li>
     * <li>{@code 5*-6/2^7} will be transformed to {@code 5*(0-6/2^7)} then parsed
     * to {@code 5,0,6,2,7,^,/,-,*}.</li>
     * <li>{@code 4*1.36^-3*2.4-7} will be transformed to {@code 4*1.36^(0-3*2.4)-7}
     * then parsed to {@code 4,1.36,0,3,2.4,*,-,^,*,7,-}.</li>
     * <li><b>Important:</b> Adding parenthesis but not closing them will not lead
     * to any syntax problem. However, the inverse is not permitted.</li>
     * <li>{@code 3-(6/9*(3+2} will be transformed to {@code 3-(6/9*(3+2))} then
     * parsed to {@code 3,6,9,/,3,2,+,*,-}.</li>
     * <li>{@code 8-2/5)} will not be successfully parsed.</li>
     * </ul>
     * </li>
     * <li>Trigonometry functions:
     * <p>
     * Keywords： {@code sin, cos, tan, arcsin, arccos, arctan}<p>
     * {@code sinh, cosh, tanh, arsinh, arcosh, artanh}
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
     * Keywords: {@code ln, log(base, power)}
     * <ul>
     * <li>{@code ln1.9^0.39*3} will be parsed to {@code 1.9,0.39,^,3,*,ln}.</li>
     * <li>{@code log(3,13*0.39)^3.1} will be parsed to
     * {@code 3,13,0.39,*,log,3.1,^}.</li>
     * <li>{@code log(3)} will not be successfully parsed.</li>
     * </ul>
     * </li>
     * <li>Constants:
     * <p>
     * Keywords: {@code \i, \e, \pi, \g, \G}
     * <p>
     * Constants as {@code e} or {@code π} are calculated during calculation.
     * <ul>
     * <li>{@code (5+1.3\i)/(4-\i)} will be parsed as
     * {@code 5,1.3,\i,*,+,4,\i,-,/}</li>
     * <li>{@code \i} is the imaginary unit.</li>
     * <li>{@code \e} is the Euler's number.{@value e = 2.718281...}.</li>
     * <li>{@code \pi} is π. {@value π = 3.141592...}.</li>
     * <li>{@code \g} is the standard acceleration due to gravity of earth.
     * {@value g = 9.80665(m/s²)}.</li>
     * <li>{@code \G} is the gravitational constant. {@value G = 6.67430E-11(m³/(kg*s²))}.</li>
     * </ul>
     * </li>
     * <li>Summation and product operator:
     * <p>
     * Keywords:
     * <p>
     * {@code sum(variable, start integer, end integer, expression),}
     * <p>
     * {@code pro(variable, start integer, end integer, expression)}
     * <ul>
     * <li>{@code sum(x, 4, 13-3, x^3)} will be parsed to
     * {@code x,4,[13,3,-],[x,3,^],sum}.</li>
     * <li>{@code pro(i, 1, 3, 5i+y)*9.3} will be parsed to
     * {@code i,1,3,[5,i,*,y,+],pro,9.3,*}.</li>
     * </ul>
     * </li>
     * <li>Variable:
     * <ul>
     * <li>{@code 5xy-4sin-x+5\i} will be parsed as {@code 5,x,*,y,*,4,0,x,-,sin,*,-,5,\i,*,+}</li>
     * </ul>
     * </li>
     * </li>
     * </ul>
     * 
     * @param expression expression to be parsed
     * @param varPool    set of variables
     * @param bridge     control of input and output
     * @return parsed expression in {@code Expressions}
     */
    public static Expressions parseFromFlattenExpr(String expression, VariablePool varPool, IOBridge bridge) {

        Properties keyWords = getKeyWords(bridge.getPropertiesLoc().get("keyWordsPath"));
        expression = formattingFlattenExpr(expression);

        ParserInfo parserInfo = new ParserInfo(new StringBuffer(expression), keyWords);

        // Eliminate the negative sign
        parserInfo.checkNegativeSign();

        while (parserInfo.exprBuffer.length() > 0) {
            // see if is constant, all constant starts with \
            if (parserInfo.checkBackSlash(varPool))
                continue;

            // see if is number
            if (parserInfo.checkNumber())
                continue;

            // see if is ';' or ','
            if (parserInfo.checkSeparator())
                continue;

            // see if is +,-,*,/,^,%,(,)
            if (parserInfo.checkArithmetic())
                continue;

            if (parserInfo.checkLetter(varPool))
                continue;
            
            if (parserInfo.checkMultiLet(varPool))
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
            powMatcher = powPattern.matcher(buffer);
        }

        // remove the possibility of recognizing x_2x as x_2*x
        // TODO
        Pattern mulPattern = Pattern.compile("(\\d|%)[A-Za-z\\[\\\\\\(]");
        Matcher mulMatcher = mulPattern.matcher(buffer);
        while (mulMatcher.find()) {
            buffer.insert(mulMatcher.start() + 1, "*");
            mulMatcher = mulPattern.matcher(buffer);
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

    private void encapsulateParts() throws ExprSyntaxErrorException {
        if (this.tokens.isEmpty()) throw new ExprSyntaxErrorException("Input is void.");
        try {
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
        } catch (NoSuchElementException e) {
            throw new ExprSyntaxErrorException();
        } catch (IndexOutOfBoundsException e) {
            throw new ExprSyntaxErrorException("the minus sings: \"-\" and the positive sings: \"+\" can not be stacked");
        }
    }

    /**
     * Calculates the expression with {@code mc} as the
     * precision used in the computation. Thus, it is 
     * recommended to round the answer after to eliminate
     * the uncertainty.
     * @param mc
     * @return the result of the expression in {@code ExprNumber}
     */
    public ExprNumber calculate(MathContext mc) {
        //System.out.println(this);
        if (this.tokens.size() == 1) {
            if (!(this.tokens.get(0) instanceof ExprFunction)) {
                if (this.tokens.get(0) instanceof VariablePool.Variable){
                    VariablePool.Variable variable = (VariablePool.Variable) this.tokens.get(0);
                    if (variable.getValue() == null)
                    variable.askForValue(this.bridge, mc);
                }
                this.valueOfExpression = this.tokens.get(0).toNumber(mc);
                return valueOfExpression;
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
        this.valueOfExpression = ((ExprFunction)this.tokens.getLast()).calculate(parameters, mc).round(mc);
        return valueOfExpression;
    }

    public String toAnsString(MathContext mc) {
        if (valueOfExpression == null) {
            throw new AnswerNotCalculatedException();
        }
        return valueOfExpression.round(mc).toAnsString();
    }

    public static Expressions subtractExpr(Expressions expr1, Expressions expr2) throws NonIdenticalIOBridgeException{      

        if (expr1.bridge != expr2.bridge) {
            throw new NonIdenticalIOBridgeException();
        }

        LinkedList<ExprElements> tokens = new LinkedList<ExprElements>();
        for (ExprElements i : expr1.tokens) {
            tokens.add(i);
        }
        for (ExprElements i : expr2.tokens) {
            tokens.add(i);
        }
        tokens.add(new ExprFunction(OpsType.SUB));
        VariablePool vp = new VariablePool();
        vp.combinePool(expr1.varPool);
        vp.combinePool(expr2.varPool);
        Expressions reVal = new Expressions(tokens, vp, expr1.bridge);
        reVal.encapsulateParts();
        return reVal;
    }

    public Expressions findDerivative(String varLabel, ExprNumber increment, MathContext mc) {
        LinkedList<ExprElements> exprList = new LinkedList<ExprElements>();
        this.unpackTo(exprList);
        for (int i = 0; i < exprList.size() - 1; i++) {
            ExprElements curtElement = exprList.get(i);
            if (!(curtElement instanceof VariablePool.Variable))
                continue;
            if (((VariablePool.Variable)curtElement).label.equals(varLabel)) {
                exprList.add(i, increment);
                exprList.add(i+2, new ExprFunction(OpsType.ADD));
                i = i+2;
            }
        }
        exprList.addAll(this.tokens);
        exprList.add(new ExprFunction(OpsType.SUB));
        exprList.add(increment);
        exprList.add(new ExprFunction(OpsType.DIV));
        Expressions reVal = new Expressions(exprList, this.varPool, this.bridge);
        reVal.encapsulateParts();
        return reVal;
    }

    /**
     * Unpack the expression and add the expression in {@code targetList}.
     * @param targetList
     */
    public void unpackTo(LinkedList<ExprElements> targetList) {
        Iterator<ExprElements> i = this.tokens.iterator();
        while (i.hasNext()) {
            ExprElements curtElement = i.next();
            if (curtElement instanceof Expressions) {
                ((Expressions)curtElement).unpackTo(targetList);
                continue;
            }
            targetList.add(curtElement);
        }
    }

    public ExprNumber evaluateXY(ExprNumber x, ExprNumber y, MathContext mc) {
        if (this.getVP().contains("x"))
            this.getVP().setValueOf("x", x);
        if (this.getVP().contains("y"))
            this.getVP().setValueOf("y", y);
        return this.calculate(mc);
    }

    public VariablePool getVP() {
        return this.varPool;
    }

    public static class AnswerNotCalculatedException extends RuntimeException {
        
        @java.io.Serial
        private static final long serialVersionUID = -9049581804282439246L;

        /**
         * Constructs a {@code AnswerNotCalculatedException} with no
         * detail message.
         */
        public AnswerNotCalculatedException() {
            super();
        }

        /**
         * Constructs a {@code AnswerNotCalculatedException} with the
         * specified detail message.
         *
         * @param   s   the detail message.
         */
        public AnswerNotCalculatedException(String s) {
            super(s);
        }        
    }

    public static class NonIdenticalIOBridgeException extends RuntimeException{
    
        @java.io.Serial
        private static final long serialVersionUID = -1927629612380072710L;
   
        /**
         * Constructs an {@code NonIdenticalIOBridgeException} with no
         * detail message.
         */
        public NonIdenticalIOBridgeException() {
            super();
        }
    
        /**
         * Constructs an {@code NonIdenticalIOBridgeException} with the
         * specified detail message.
         *
         * @param   s   the detail message.
         */
        public NonIdenticalIOBridgeException(String s) {
            super(s);
        }
    }

}
