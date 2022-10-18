package Jmathcal.Expression;

import java.io.File;
import java.io.Serializable;
import java.math.MathContext;
import java.util.HashMap;
import java.util.LinkedList;

import Jmathcal.IOControl.IOBridge;

public class ExprFunction implements Serializable, ExprElements {

    private static final long serialVersionUID = -4376930957526847386L;
    private final OpsType type;
    private static final IOBridge TEMP_BRIDGE = new IOBridge(){
        public String askForInput(String msg) {
            return null;
        };
        public void outSendMessage(String msg) {};
        public java.util.HashMap<String,java.io.File> getPropertiesLoc() {
            HashMap<String, File> reVal = new HashMap<String, File>();
            reVal.put("configPath", IOBridge.configPath);
            reVal.put("greekLetPath", IOBridge.greekLetPath);
            return reVal;
        };
    };

    public ExprFunction(OpsType type) {
        this.type = type;
    }

    public OpsType getType() {
        return this.type;
    }

    public int compPrecedence(ExprFunction o) {
        return Integer.valueOf(this.type.precedence).compareTo(o.type.precedence);
    }

    public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
        return this.getType().getCalBridge().calculate(parameters, mc);
    }

    @Override
    public String toString() {
        return type.name();
    }

    @Override
    public ExprNumber toNumber(MathContext mc) {
        return null;
    }

    public static enum OpsType {

        ADD(2, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).add(parameters.get(1).toNumber(mc), mc);
            }
        }),

        SUB(2, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).subtract(parameters.get(1).toNumber(mc), mc);
            }
        }),

        MUL(2, 2, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).multiply(parameters.get(1).toNumber(mc), mc);

            }
        }),

        DIV(2, 2, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).divide(parameters.get(1).toNumber(mc), mc);
            }
        }),

        POW(2, 3, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).pow(parameters.get(1).toNumber(mc), mc);
            }
        }),

        PER_CEN(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).percent(mc);
            }
        }),

        SQRT(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).sqrt(mc);
            }
        }),

        SIN(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).sin(mc);
            }
        }),

        COS(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).cos(mc);
            }
        }),

        TAN(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).tan(mc);
            }
        }),

        ASIN(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).arcsin(mc);
            }
        }),

        ACOS(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).arccos(mc);
            }
        }),

        ATAN(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).arctan(mc);
            }
        }),

        LOG(2, 0, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(1).toNumber(mc).log(parameters.get(0).toNumber(mc), mc);
            }
        }),

        LN(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).ln(mc);
            }
        }),

        SUM(4, 0, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                final String ERROR_MSG = "Syntax error on \"sum(\" method, should be : " +
                        "sum(variable,start integer,end integer,expression), " +
                        "where start integer should be smaller than end integer.";

                ExprNumber reVal = new ExprNumber("0+0i");
                ExprNumber valueOfVariable;
                String label;
                VariablePool currentVp;
                try {
                    int startInt = Integer.valueOf(parameters.get(1).toNumber(mc).intValue());
                    int endInt = Integer.valueOf(parameters.get(2).toNumber(mc).intValue());
                    if (!(parameters.get(0) instanceof VariablePool.Variable)) {
                        throw new ExprSyntaxErrorException(ERROR_MSG);
                    } else {
                        valueOfVariable = parameters.get(0).toNumber(mc);
                        label = ((VariablePool.Variable) parameters.get(0)).label;
                        currentVp = ((VariablePool.Variable) parameters.get(0)).getPool();

                    }
                    if (!(parameters.get(3) instanceof Expressions)) {
                        if (!(parameters.get(3) instanceof VariablePool.Variable))
                            throw new ExprSyntaxErrorException(ERROR_MSG);

                        LinkedList<ExprElements> tokens = new LinkedList<ExprElements>();
                        tokens.add(new ExprNumber("1+0i"));
                        tokens.add(parameters.get(3));
                        tokens.add(new ExprFunction(OpsType.MUL));
                        parameters.set(3, new Expressions(tokens,
                                currentVp,
                                null));

                    } else if (startInt > endInt) {
                        throw new ExprSyntaxErrorException(ERROR_MSG);
                    }

                    while (startInt <= endInt) {
                        ((VariablePool.Variable) parameters.get(0))
                                .setValue(new ExprNumber(String.valueOf(startInt) + "+0i"));
                        ExprNumber num = ((Expressions) parameters.get(3)).calculate(mc);
                        reVal = reVal.add(num, mc);
                        startInt++;
                    }
                    // reset variable's value
                    currentVp.getVariable(label).setValue(valueOfVariable);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new ExprSyntaxErrorException(ERROR_MSG);
                }
                return reVal;
            };
        }),

        PRO(4, 0, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                final String ERROR_MSG = "Syntax error on \"pro(\" method, should be : " +
                        "sum(variable,start integer,end integer,expression), " +
                        "where start integer should be smaller than end integer.";

                ExprNumber reVal = new ExprNumber("1+0i");
                ExprNumber valueOfVariable;
                String label;
                VariablePool currentVp;
                try {
                    int startInt = Integer.valueOf(parameters.get(1).toNumber(mc).intValue());
                    int endInt = Integer.valueOf(parameters.get(2).toNumber(mc).intValue());
                    if (!(parameters.get(0) instanceof VariablePool.Variable)) {
                        throw new ExprSyntaxErrorException(ERROR_MSG);
                    } else {
                        valueOfVariable = parameters.get(0).toNumber(mc);
                        label = ((VariablePool.Variable) parameters.get(0)).label;
                        currentVp = ((VariablePool.Variable) parameters.get(0)).getPool();

                    }
                    if (!(parameters.get(3) instanceof Expressions)) {
                        if (!(parameters.get(3) instanceof VariablePool.Variable))
                            throw new ExprSyntaxErrorException(ERROR_MSG);

                        LinkedList<ExprElements> tokens = new LinkedList<ExprElements>();
                        tokens.add(new ExprNumber("1+0i"));
                        tokens.add(parameters.get(3));
                        tokens.add(new ExprFunction(OpsType.MUL));
                        parameters.set(3, new Expressions(tokens,
                                currentVp,
                                null));

                    } else if (startInt > endInt) {
                        throw new ExprSyntaxErrorException(ERROR_MSG);
                    }

                    while (startInt <= endInt) {
                        ((VariablePool.Variable) parameters.get(0))
                                .setValue(new ExprNumber(String.valueOf(startInt) + "+0i"));
                        ExprNumber num = ((Expressions) parameters.get(3)).calculate(mc);
                        reVal = reVal.multiply(num, mc);
                        startInt++;
                    }
                    // reset variable's value
                    currentVp.getVariable(label).setValue(valueOfVariable);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new ExprSyntaxErrorException(ERROR_MSG);
                }
                return reVal;
            };
        }),

        CSC(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                String strFormula = "1/sinx";
                VariablePool varPool = new VariablePool();
                Expressions formula = Expressions.parseFromFlattenExpr(strFormula, varPool, TEMP_BRIDGE);
                varPool.setValueOf("x", parameters.get(0).toNumber(mc));
                return formula.calculate(mc);
            }
        }),

        SEC(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                String strFormula = "1/cosx";
                VariablePool varPool = new VariablePool();
                Expressions formula = Expressions.parseFromFlattenExpr(strFormula, varPool, TEMP_BRIDGE);
                varPool.setValueOf("x", parameters.get(0).toNumber(mc));
                return formula.calculate(mc);
            }
        }),

        COT(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                String strFormula = "1/tanx";
                VariablePool varPool = new VariablePool();
                Expressions formula = Expressions.parseFromFlattenExpr(strFormula, varPool, TEMP_BRIDGE);
                varPool.setValueOf("x", parameters.get(0).toNumber(mc));
                return formula.calculate(mc);
            }
        }),


        SINH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                String strFormula = "-\\isin(\\ix)";
                VariablePool varPool = new VariablePool();
                Expressions formula = Expressions.parseFromFlattenExpr(strFormula, varPool, TEMP_BRIDGE);
                varPool.setValueOf("x", parameters.get(0).toNumber(mc));
                return formula.calculate(mc);
            }
        }),

        COSH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                String strFormula = "cos(\\ix)";
                VariablePool varPool = new VariablePool();
                Expressions formula = Expressions.parseFromFlattenExpr(strFormula, varPool, TEMP_BRIDGE);
                varPool.setValueOf("x", parameters.get(0).toNumber(mc));
                return formula.calculate(mc);
            }
        }),

        TANH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                String strFormula = "(sinhx)/(coshx)";
                VariablePool varPool = new VariablePool();
                Expressions formula = Expressions.parseFromFlattenExpr(strFormula, varPool, TEMP_BRIDGE);
                varPool.setValueOf("x", parameters.get(0).toNumber(mc));
                return formula.calculate(mc);
            }
        }),

        OPEN_P(0, 0, new CalBridge() {
            public ExprNumber calculate(java.util.LinkedList<ExprElements> parameters, MathContext mc) {
                throw new ExprSyntaxErrorException();
            };
        }),

        CLOSE_P(0, 0, new CalBridge() {
            public ExprNumber calculate(java.util.LinkedList<ExprElements> parameters, MathContext mc) {
                throw new ExprSyntaxErrorException();
            };
        });

        public final int parameterNum;
        public final int precedence;
        private final CalBridge calculator;

        private interface CalBridge {
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc);
        }

        OpsType(int parameterNum, int precedence, CalBridge calculator) {
            this.parameterNum = parameterNum;
            this.precedence = precedence;
            this.calculator = calculator;
        }

        public CalBridge getCalBridge() {
            return this.calculator;
        }

    }

}
