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

    public static boolean ifPointPasses(PlotterSign a, PlotterSign b) {
        if (a == ZERO || b == ZERO) {
            return true;
        }
        if (a != NOT_REAL && b != NOT_REAL) {
            if (a.compareTo(b) != 0) {
                return true;
            }
            return false;
        }
        return false;
    }
}
