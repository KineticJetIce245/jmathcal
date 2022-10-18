package Jmathcal.Plotter;

public enum PlotterSign {
    POS, NEG, ZERO, NOT_REAL;

    public String toString() {
        String reVal = "NR";
        switch (this) {
            case POS:
                reVal = "+";
                break;

            case NEG:
                reVal = "-";
                break;

            case ZERO:
                reVal = "0";
                break;

            default:
                break;
        }

        return reVal;
    }
}
