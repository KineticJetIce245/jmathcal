package Jmathcal.NewPlotter;

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

    public boolean ifPointPasses(PlotterSign b) {
        if (this == ZERO || b == ZERO) {
            return true;
        }
        if (this.compareTo(b) != 0) {
            return true;
        }
        return false;
    }
}
