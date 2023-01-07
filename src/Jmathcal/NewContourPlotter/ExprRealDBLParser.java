package Jmathcal.NewContourPlotter;

import java.math.MathContext;
import java.util.HashMap;
import java.util.LinkedList;

import Jmathcal.Expression.Constant;
import Jmathcal.Expression.ExprElements;
import Jmathcal.Expression.ExprFunction;
import Jmathcal.Expression.ExprNumber;
import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariableLabelOccupiedException;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;

public class ExprRealDBLParser {
    public static void main(String[] args) {
        String exprStr = IOBridge.DFLT_BRIDGE.askForInput("Input");
        Expressions expr = Expressions.parseFromFlattenExpr(exprStr, new VariablePool(), IOBridge.DFLT_BRIDGE);
        expr.calculate(new MathContext(12));
        ExprRealDBLParser erdp = new ExprRealDBLParser(expr);
        System.out.println(erdp.resultExpr);
        System.out.println(System.currentTimeMillis());
        System.out.println(erdp.evaluateXY(1, 1));
        System.out.println(System.currentTimeMillis());
        System.out.println(erdp.evaluateXY(1, 2));
        System.out.println(System.currentTimeMillis());
        System.out.println(erdp.evaluateXY(5, 2));
        System.out.println(System.currentTimeMillis());

    }

    private LinkedList<ExprElements> unpackedExpr = new LinkedList<ExprElements>();
    private LinkedList<DBLElements> resultExpr = new LinkedList<DBLElements>();
    private DBLVarPool vp = new DBLVarPool();

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
    }

    public double evaluateXY(double x, double y) {
        if (vp.contains("x"))
            vp.setValueOf("x", x);
        if (vp.contains("y"))
            vp.setValueOf("y", y);

        LinkedList<DBLElements> tempList = new LinkedList<>();
        tempList.addAll(this.resultExpr);

        while (tempList.size() > 1) {
            for (int i = 0; i < tempList.size(); i++) {
                DBLElements e = tempList.get(i);
                DBLWarp result = null;
                if (e instanceof DBLFunc) {
                    tempList.remove(i);
                    DBLFunc f = (DBLFunc) e;
                    int paramNum = f.getType().parameterNum;
                    DBLWarp[] params = new DBLWarp[paramNum];
                    for (int j = 0; j < paramNum; j++) {
                        i--;
                        params[j] = new DBLWarp(tempList.remove(i).evaluate());
                    }
                    DBLWarp.Calculator calculator = DBLWarp.DBLOpr.valueOf(f.getType().name()).calculator;
                    result = calculator.run(params);
                    tempList.add(i, result);
                }
            }
        }

        return tempList.getFirst().evaluate();
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

    private interface DBLElements {
        double evaluate();
    }

    private static class DBLWarp implements DBLElements {
        public final double value;

        public DBLWarp(double value) {
            this.value = value;
        }

        public interface Calculator {
            DBLWarp run(DBLWarp[] params);
        }

        public static enum DBLOpr {

            ADD(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[0].value + params[1].value);
                }
            }),
            SUB(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[1].value - params[0].value);
                }
            }),
            MUL(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[0].value * params[1].value);
                }
            }),
            DIV(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[1].value / params[0].value);
                }
            }),
            PER_CEN(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[0].value / 100);
                }
            }),
            POW(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.pow(params[1].value, params[0].value));
                }
            }),
            SQRT(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.sqrt(params[0].value));
                }
            }),
            SIN(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.sin(params[0].value));
                }
            }),
            COS(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.cos(params[0].value));
                }
            }),
            TAN(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.tan(params[0].value));
                }
            }),
            ASIN(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.asin(params[0].value));
                }
            }),
            ACOS(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.acos(params[0].value));
                }
            }),
            ATAN(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.atan(params[0].value));
                }
            }),
            LOG(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.log(params[0].value) / Math.log(params[1].value));
                }
            }),
            LN(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.log(params[0].value));
                }
            }),
            SINH(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.sinh(params[0].value));
                }
            }),
            COSH(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.cosh(params[0].value));
                }
            }),
            TANH(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.tanh(params[0].value));
                }
            }),
            CSC(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(1 / Math.sin(params[0].value));
                }
            }),
            SEC(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(1 / Math.cos(params[0].value));
                }
            }),
            COT(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(1 / Math.tan(params[0].value));
                }
            }),
            ACSC(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.asin(1 / params[0].value));
                }
            }),
            ASEC(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.acos(1 / params[0].value));
                }
            }),
            ACOT(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.atan(1 / params[0].value));
                }
            }),
            ARSINH(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    double x = params[0].value;
                    return new DBLWarp(Math.log(x + Math.sqrt(x * x + 1.0)));
                }
            }),
            ARCOSH(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    double x = params[0].value;
                    return new DBLWarp(Math.log(x + Math.sqrt(x * x - 1.0)));
                }
            }),
            ARTANH(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    double x = params[0].value;
                    return new DBLWarp(0.5 * Math.log((x + 1.0) / (x - 1.0)));
                }
            }),
            DEG(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[0].value*Math.PI/180);
                }
            }),
            GRAD(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[0].value*Math.PI/200);
                }
            }),
            TO_DEG(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[0].value/Math.PI*180);
                }
            }),
            TO_GRAD(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(params[0].value/Math.PI*200);
                }
            }),
            ABS(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.abs(params[0].value));
                }
            }),
            SGN(new Calculator() {
                @Override
                public DBLWarp run(DBLWarp[] params) {
                    return new DBLWarp(Math.signum(params[0].value));
                }
            })
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

        }
    }
}
