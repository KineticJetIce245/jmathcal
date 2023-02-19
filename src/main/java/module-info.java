module pers.kineticjetice245.jmathcal {
    requires java.base;
    requires java.desktop;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;

    opens pers.kineticjetice245.jmathcal.gui to javafx.fxml;
    exports pers.kineticjetice245.jmathcal.gui;
}
