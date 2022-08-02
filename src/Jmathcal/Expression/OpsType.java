package Jmathcal.Expression;

import java.math.MathContext;

import Jmathcal.Number.Complex.ComplexDbl;
import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public enum OpsType {
    ADD(2, 1, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return parameters[0].add(parameters[1]).round(mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return parameters[0].add(parameters[1]);
        }
    }),

    SUB(2, 1, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return parameters[0].subtract(parameters[1]).round(mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return parameters[0].subtract(parameters[1]);
        }
    }),

    MUL(2, 2, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return parameters[0].multiply(parameters[1]).round(mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return parameters[0].multiply(parameters[1]);
        }
    }),

    DIV(2, 2, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return parameters[0].divide(parameters[1], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return parameters[0].divide(parameters[1]);
        }
    }),

    POW(2, 3, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return parameters[0].pow(parameters[1], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Exp.pow(parameters[0],parameters[1]);
        }
    }),

    SQRT(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Exp.pow(parameters[0], new ComplexNum("0.5"), mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Exp.pow(parameters[0],new ComplexDbl(0.5));
        }
    }),

    SIN(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Trigo.sin(parameters[0], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Trigo.sin(parameters[0]);
        }
    }),

    COS(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Trigo.cos(parameters[0], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Trigo.cos(parameters[0]);
        }
    }),

    TAN(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Trigo.tan(parameters[0], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Trigo.tan(parameters[0]);
        }
    }),

    ASIN(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Trigo.arcsin(parameters[0], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Trigo.arcsin(parameters[0]);
        }
    }),

    ACOS(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Trigo.arccos(parameters[0], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Trigo.arccos(parameters[0]);
        }
    }),
    
    ATAN(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Trigo.arctan(parameters[0], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Trigo.arctan(parameters[0]);
        }
    }),

    LOG(2, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Exp.log(parameters[0], parameters[1], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Exp.log(parameters[0], parameters[1]);
        }
    }),

    LN(1, 4, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return Exp.ln(parameters[0], mc);
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return Exp.ln(parameters[0]);
        }
    }),

    OPEN_P(0, 0, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return null;
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return null;
        }
    }),

    CLOSE_P(0, 0, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return null;
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return null;
        }
    }),

    SUM(4, 0, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return null;
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return null;
        }
    }),

    PRO(4, 0, new Calculator() {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
            return null;
        }
        public ComplexDbl calculate(ComplexDbl[] parameters) {
            return null;
        }
    });

    public final int parameterNum;
    public final int precedence;
    private final Calculator calculator;

    private interface Calculator {
        public ComplexNum calculate(ComplexNum[] parameters, MathContext mc);
        public ComplexDbl calculate(ComplexDbl[] parameters);
    }

    OpsType(int parameterNum, int precedence, Calculator calculator) {
        this.parameterNum = parameterNum;
        this.precedence = precedence;
        this.calculator = calculator;
    }

    public ComplexNum calculate(ComplexNum[] parameters, MathContext mc) {
        return this.calculator.calculate(parameters, mc);
    }

    public ComplexDbl calculate(ComplexDbl[] parameters) {
        return this.calculator.calculate(parameters);
    }

}
