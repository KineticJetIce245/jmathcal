package Jmathcal.Expression;

import java.io.Serializable;
import java.math.MathContext;
import java.util.LinkedList;

import Jmathcal.Number.Function.AngleType;

public class ExprFunction implements Serializable, ExprElements {

    private static final long serialVersionUID = -4376930957526847386L;
    private final OpsType type;

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
                return parameters.get(0).toNumber(mc).csc(mc);
            }
        }),

        SEC(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).sec(mc);
            }
        }),

        COT(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).cot(mc);
            }
        }),

        ACSC(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).arccsc(mc);
            }  
        }),

        ASEC(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).csc(mc);
            }  
        }),

        ACOT(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).csc(mc);
            }  
        }),

        SINH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).sinh(mc);
            }
        }),

        COSH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).cosh(mc);
            }
        }),

        TANH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).tanh(mc);
            }
        }),

        ARSINH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).arsinh(mc);
            }
        }),

        ARCOSH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).arcosh(mc);
            }
        }),

        ARTANH(1, 1, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).artanh(mc);
            }
        }),

        DEG(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).angleConvert(AngleType.DEG, AngleType.RAD, mc);
            }
        }),

        GRAD(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).angleConvert(AngleType.GRAD, AngleType.RAD, mc);
            }
        }),

        TO_DEG(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).angleConvert(AngleType.RAD, AngleType.DEG, mc);
            }
        }),

        TO_GRAD(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).angleConvert(AngleType.RAD, AngleType.GRAD, mc);
            }
        }),

        ABS(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).abs(mc);
            }
        }),

        SGN(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).sgn(mc);
            }
        }),

        RAN(2, 0, new CalBridge() {
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return ExprNumber.random(parameters.get(0).toNumber(mc), parameters.get(1).toNumber(mc), mc);
            }
        }),

        POLR(2, 0 , new CalBridge() {
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return ExprNumber.polarR(parameters.get(0).toNumber(mc), parameters.get(1).toNumber(mc), mc);
            }
        }),

        POLT(2, 0 , new CalBridge() {
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return ExprNumber.polarTheta(parameters.get(0).toNumber(mc), parameters.get(1).toNumber(mc), mc);
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
