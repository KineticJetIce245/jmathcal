package Jmathcal.Expression;

import java.io.Serializable;
import java.math.MathContext;
import java.util.LinkedList;

import Jmathcal.Number.Complex.ComplexDbl;
import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

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
    public String toStrVal(MathContext mc) {
        return this.toStrVal(mc);
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

        SQRT(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                return parameters.get(0).toNumber(mc).sqrt(mc);
            }
        }),
        
        SIN(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Trigo.sin(a)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    return new ExprNumber(Trigo.sin(a, mc).toString());
                }
            }
        }),

        COS(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Trigo.cos(a)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    return new ExprNumber(Trigo.cos(a, mc).toString());
                }
            }
        }),

        TAN(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Trigo.tan(a)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    return new ExprNumber(Trigo.tan(a, mc).toString());
                }
            }
        }),

        ASIN(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Trigo.arcsin(a)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    return new ExprNumber(Trigo.arcsin(a, mc).toString());
                }
            }
        }),

        ACOS(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Trigo.arccos(a)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    return new ExprNumber(Trigo.arccos(a, mc).toString());
                }
            }
        }),

        ATAN(1, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Trigo.arctan(a)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    return new ExprNumber(Trigo.arctan(a, mc).toString());
                }
            }
        }),

        LOG(2, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    ComplexDbl b = new ComplexNum(new StringBuffer(parameters.get(1).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Exp.log(a, b)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    ComplexNum b = new ComplexNum(new StringBuffer(parameters.get(1).toStrVal(mc)));
                    return new ExprNumber((Exp.log(a, b, mc)).toString());
                }
            }
        }),

        LN(2, 4, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                if (mc.getPrecision() < DBL) {
                    ComplexDbl a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc))).toComplexDbl();
                    return new ExprNumber((Exp.ln(a)).toString());
                } else {
                    ComplexNum a = new ComplexNum(new StringBuffer(parameters.get(0).toStrVal(mc)));
                    return new ExprNumber((Exp.ln(a, mc)).toString());
                }
            }
        }),


        SUM(4, 0, new CalBridge() {
            @Override
            public ExprNumber calculate(LinkedList<ExprElements> parameters, MathContext mc) {
                final String ERROR_MSG = 
                        "Syntax error on \"sum(\" method, should be : " +
                        "sum(variable,start integer,end integer,expression), " +
                        "where start integer should be smaller than end integer";
                              
                try {
                    int startInt = Integer.valueOf(parameters.get(1).toStrVal(mc));
                    int endInt = Integer.valueOf(parameters.get(0).toStrVal(mc));
                    if (!(parameters.get(0) instanceof VariablePool.Variable)) {
                        throw new ExprSyntaxErrorException(ERROR_MSG);
                    } else if (!(parameters.get(3) instanceof Expressions)) {
                        throw new ExprSyntaxErrorException(ERROR_MSG);
                    } else if (startInt > endInt) {
                        throw new ExprSyntaxErrorException(ERROR_MSG);
                    }

                    ExprNumber reVal = new ExprNumber("0");

                    while (startInt <= endInt) {
                        ((VariablePool.Variable)parameters.get(0)).setValue(new ExprNumber(String.valueOf(startInt)));
                        ExprNumber num = ((Expressions)parameters.get(3)).calculate(mc);
                        if (mc.getPrecision() < DBL) {
                            reVal = new ExprNumber(reVal.toComplexDbl().add(num.toComplexDbl()).toString());
                        }
                        startInt++;
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new ExprSyntaxErrorException(ERROR_MSG);
                }

                return null;
            };
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
        })
        ;

        public static int DBL = 10;
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
