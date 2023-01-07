package Jmathcal.Plotter;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import Jmathcal.Expression.Constant;
import Jmathcal.Expression.ExprElements;
import Jmathcal.Expression.ExprFunction;
import Jmathcal.Expression.ExprNumber;
import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariableLabelOccupiedException;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;

public class ExprRealDBLParser implements DBLElements {
    public static void main(String[] args) {
        String exprStr = IOBridge.DFLT_BRIDGE.askForInput("Input");
        Expressions expr = Expressions.parseFromFlattenExpr(exprStr, new VariablePool(), IOBridge.DFLT_BRIDGE);
        expr.calculate(new MathContext(12));
        ExprRealDBLParser erdp = new ExprRealDBLParser(expr);
        System.out.println(erdp);
    }

    private LinkedList<ExprElements> unpackedExpr = new LinkedList<ExprElements>();
    private LinkedList<DBLElements> resultExpr = new LinkedList<DBLElements>();
    private DBLVarPool vp = new DBLVarPool();

    private ExprRealDBLParser(LinkedList<DBLElements> eList, DBLVarPool vp) {
        this.resultExpr = eList;
        this.vp = vp;
        this.packForSpecFunc();
    }

    public ExprRealDBLParser(Expressions expr) {
        expr.unpackTo(this.unpackedExpr);
        for (int i = 0; i < unpackedExpr.size(); i++) {
            ExprElements curtElement = unpackedExpr.get(i);
            if (curtElement instanceof VariablePool.Variable) {
                VariablePool.Variable curtVar = (VariablePool.Variable) curtElement;
                if (vp.contains(curtVar.label)) {
                    resultExpr.add(vp.getVariable(curtVar.label));
                    continue;
                }
                DBLVarPool.DBLVar dblVar = vp.new DBLVar(curtVar.label);
                if (!curtVar.getValue().isRealDBL(15)) {
                    throw new NotRealDBLException();
                }
                dblVar.setValue(curtVar.getValue().toComplexDbl().getRealValue());
                resultExpr.add(dblVar);
                continue;
            }
            if (curtElement instanceof ExprNumber) {
                ExprNumber curtNum = (ExprNumber) curtElement;
                if (!curtNum.isRealDBL(15)) {
                    throw new NotRealDBLException();
                }
                resultExpr.add(new DBLWarp(curtNum.toComplexDbl().getRealValue()));
                continue;
            }
            if (curtElement instanceof Constant) {
                Constant curtCst = (Constant) curtElement;
                ExprNumber curtNum = curtCst.toNumber(new MathContext(16));
                if (!curtNum.isRealDBL(15)) {
                    throw new NotRealDBLException();
                }
                resultExpr.add(new DBLWarp(curtNum.toComplexDbl().getRealValue()));
                continue;
            }
            if (curtElement instanceof ExprFunction) {
                resultExpr.add(new DBLFunc(((ExprFunction) curtElement).getType()));
                continue;
            }
        }
        this.packForSpecFunc();
    }

    public void packForSpecFunc() {
        Iterator<DBLElements> i = this.resultExpr.listIterator();
        while (true) {
            DBLElements element = i.next();
            if (this.resultExpr.indexOf(element) == this.resultExpr.size() - 1) {
                if (element instanceof DBLFunc) {
                    break;
                } else if (this.resultExpr.size() == 1 && !(element instanceof DBLFunc)) {
                    break;
                }
            }
            if (element instanceof DBLFunc) {
                DBLFunc f = (DBLFunc) element;
                // it's always the first element that it gets
                int index = this.resultExpr.indexOf(element);
                LinkedList<DBLElements> subExpr = new LinkedList<DBLElements>();
                for (int j = 0; j < f.getType().parameterNum; j++) {
                    subExpr.add(this.resultExpr.remove(index - f.getType().parameterNum));
                }
                subExpr.add(element);
                resultExpr.set(index - f.getType().parameterNum, new ExprRealDBLParser(subExpr, this.vp));
                i = this.resultExpr.listIterator();
            }
        }
        this.unpack();
    }

    private void unpack() {
        DBLFunc f = (DBLFunc) this.resultExpr.getLast();
        switch (f.getType()) {
            case PRO:
                for (DBLElements i : this.resultExpr) {
                    if (i instanceof ExprRealDBLParser) {
                        for (DBLElements j : ((ExprRealDBLParser) i).resultExpr) {
                            if (j instanceof ExprRealDBLParser)
                                ((ExprRealDBLParser) j).unpack();
                        }
                    }
                }
                break;
            case SUM:
                for (DBLElements i : this.resultExpr) {
                    if (i instanceof ExprRealDBLParser) {
                        for (DBLElements j : ((ExprRealDBLParser) i).resultExpr) {
                            if (j instanceof ExprRealDBLParser)
                                ((ExprRealDBLParser) j).unpack();
                        }
                    }
                }
                break;

            default:
                for (DBLElements i : this.resultExpr) {
                    if (i instanceof ExprRealDBLParser)
                        ((ExprRealDBLParser) i).unpack();
                }
                break;
        }
    }

    public double evaluateXY(double x, double y) {
        if (vp.contains("x"))
            vp.setValueOf("x", x);
        if (vp.contains("y"))
            vp.setValueOf("y", y);
        return this.evaluate();
    }

    private class DBLFunc extends ExprFunction implements DBLElements {
        @java.io.Serial
        private static final long serialVersionUID = -2651587069164366076L;

        public DBLFunc(OpsType type) {
            super(type);
        }

        @Override
        public double evaluate() {
            return 0;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public class NotRealDBLException extends ArithmeticException {
        @java.io.Serial
        private static final long serialVersionUID = -7221839986982460882L;

        /**
         * Constructs a {@code NotRealDBLException} with no
         * detail message.
         */
        public NotRealDBLException() {
            super();
        }

        /**
         * Constructs a {@code NotRealDBLException} with the
         * specified detail message.
         *
         * @param s the detail message.
         */
        public NotRealDBLException(String s) {
            super(s);
        }
    }

    @Override
    public String toString() {
        return this.resultExpr.toString();
    }

    private static class DBLWarp implements DBLElements {
        public final double value;

        public DBLWarp(double value) {
            this.value = value;
        }

        public interface Calculator {
            DBLWarp run(DBLElements[] params);
        }

        public static enum DBLOpr {

            ADD(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() + params[1].evaluate());
                }
            }),
            SUB(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() - params[1].evaluate());
                }
            }),
            MUL(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() * params[1].evaluate());
                }
            }),
            DIV(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() / params[1].evaluate());
                }
            }),
            PER_CEN(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() / 100);
                }
            }),
            POW(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.pow(params[0].evaluate(), params[1].evaluate()));
                }
            }),
            SQRT(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.sqrt(params[0].evaluate()));
                }
            }),
            SIN(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.sin(params[0].evaluate()));
                }
            }),
            COS(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.cos(params[0].evaluate()));
                }
            }),
            TAN(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.tan(params[0].evaluate()));
                }
            }),
            ASIN(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.asin(params[0].evaluate()));
                }
            }),
            ACOS(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.acos(params[0].evaluate()));
                }
            }),
            ATAN(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.atan(params[0].evaluate()));
                }
            }),
            LOG(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.log(params[1].evaluate()) / Math.log(params[0].evaluate()));
                }
            }),
            LN(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.log(params[0].evaluate()));
                }
            }),
            SINH(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.sinh(params[0].evaluate()));
                }
            }),
            COSH(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.cosh(params[0].evaluate()));
                }
            }),
            TANH(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.tanh(params[0].evaluate()));
                }
            }),
            CSC(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(1 / Math.sin(params[0].evaluate()));
                }
            }),
            SEC(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(1 / Math.cos(params[0].evaluate()));
                }
            }),
            COT(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(1 / Math.tan(params[0].evaluate()));
                }
            }),
            ACSC(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.asin(1 / params[0].evaluate()));
                }
            }),
            ASEC(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.acos(1 / params[0].evaluate()));
                }
            }),
            ACOT(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.atan(1 / params[0].evaluate()));
                }
            }),
            ARSINH(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    double x = params[0].evaluate();
                    return new DBLWarp(Math.log(x + Math.sqrt(x * x + 1.0)));
                }
            }),
            ARCOSH(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    double x = params[0].evaluate();
                    return new DBLWarp(Math.log(x + Math.sqrt(x * x - 1.0)));
                }
            }),
            ARTANH(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    double x = params[0].evaluate();
                    return new DBLWarp(0.5 * Math.log((x + 1.0) / (x - 1.0)));
                }
            }),
            DEG(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() * Math.PI / 180);
                }
            }),
            GRAD(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() * Math.PI / 200);
                }
            }),
            TO_DEG(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() / Math.PI * 180);
                }
            }),
            TO_GRAD(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(params[0].evaluate() / Math.PI * 200);
                }
            }),
            ABS(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.abs(params[0].evaluate()));
                }
            }),
            SGN(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    return new DBLWarp(Math.signum(params[0].evaluate()));
                }
            }),
            PRO(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    double reVal = 1;
                    double valueOfVariable = params[0].evaluate();
                    DBLVarPool.DBLVar variable = (DBLVarPool.DBLVar) params[0];
                    String label = variable.label;
                    DBLVarPool currentVp = variable.getPool();
                    int startInt = (int) params[1].evaluate();
                    int endInt = (int) params[2].evaluate();

                    while (startInt <= endInt) {
                        variable.setValue(startInt);
                        double resultOfExpr = params[3].evaluate();
                        reVal = reVal * resultOfExpr;
                        startInt++;
                    }
                    // reset variable's value
                    currentVp.getVariable(label).setValue(valueOfVariable);
                    return new DBLWarp(reVal);
                }
            }),
            SUM(new Calculator() {
                @Override
                public DBLWarp run(DBLElements[] params) {
                    double reVal = 0;
                    double valueOfVariable = params[0].evaluate();
                    DBLVarPool.DBLVar variable = (DBLVarPool.DBLVar) params[0];
                    String label = variable.label;
                    DBLVarPool currentVp = variable.getPool();
                    int startInt = (int) params[1].evaluate();
                    int endInt = (int) params[2].evaluate();

                    while (startInt <= endInt) {
                        variable.setValue(startInt);
                        double resultOfExpr = params[3].evaluate();
                        reVal = reVal + resultOfExpr;
                        startInt++;
                    }
                    // reset variable's value
                    currentVp.getVariable(label).setValue(valueOfVariable);
                    return new DBLWarp(reVal);
                }
            }),
            ;

            public final Calculator calculator;

            DBLOpr(Calculator calculator) {
                this.calculator = calculator;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(this.value);
        }

        @Override
        public double evaluate() {
            return value;
        }
    }

    private class DBLVarPool {

        private HashMap<String, DBLVar> variablePool = new HashMap<String, DBLVar>();

        public DBLVarPool() {
        }

        public boolean contains(String valLabel) {
            return this.variablePool.containsKey(valLabel);
        }

        public DBLVar getVariable(String varLabel) {
            return this.variablePool.get(varLabel);
        }

        private DBLVarPool getOuter() {
            return this;
        }

        @Override
        public String toString() {
            return this.variablePool.toString();
        }

        public void setValueOf(String label, double value) {
            this.getVariable(label).setValue(value);
        }

        public class DBLVar implements DBLElements {

            private double value;
            private String name;
            public final String label;

            public DBLVar(String label) {
                this.label = label;
                if (getOuter().contains(this.label))
                    throw new VariableLabelOccupiedException();
                getOuter().variablePool.put(label, this);
            }

            public void setValue(double value) {
                this.value = value;
            }

            public String toString() {
                return "(" + this.label + ", " + this.name + ", " + this.value + ")";
            }

            @Override
            public double evaluate() {
                return value;
            }

            public DBLVarPool getPool() {
                return getOuter();
            }

        }
    }

    @Override
    public double evaluate() {
        LinkedList<DBLElements> tempList = new LinkedList<DBLElements>();
        tempList.addAll(this.resultExpr);

        while (tempList.size() > 1) {
            for (int i = 0; i < tempList.size(); i++) {
                DBLElements e = tempList.get(i);
                DBLWarp result = null;
                if (e instanceof DBLFunc) {
                    tempList.remove(i);
                    DBLFunc f = (DBLFunc) e;
                    int paramNum = f.getType().parameterNum;
                    DBLElements[] params = new DBLElements[paramNum];
                    for (int j = paramNum-1; j >= 0; j--) {
                        i--;
                        params[j] = tempList.remove(i);
                    }
                    DBLWarp.Calculator calculator = DBLWarp.DBLOpr.valueOf(f.getType().name()).calculator;
                    result = calculator.run(params);
                    tempList.add(i, result);
                }
            }
        }
        return tempList.getFirst().evaluate();
    }
}
