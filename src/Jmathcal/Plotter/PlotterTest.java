package Jmathcal.Plotter;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PlotterTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        double[] length = { 20, 20 };
        double[] origin = { -10, -10 };
        int[] planeSize = { 1080, 720 };
        int[] resolution = { 500, 500 };
        int maxDepth = 2;
        PlotterPlane myPlane = new PlotterPlane(length, origin, planeSize, resolution, maxDepth);
        VariablePool vp = new VariablePool();

        PointGroup pg = new PointGroup();
        pg.setUpGrid(myPlane, true, true, true);
        Expressions expr = Expressions.parseFromFlattenExpr("sqrt(x^2+y^2)-sin(8arctan(y/x))", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#af3f5f"));
        /*
        expr = Expressions.parseFromFlattenExpr("-2sqrt(x^2+y^2)-sin(8arctan(y/x))", vp, IOBridge.DFLT_BRIDGE);
        pg.addFunc(expr, Color.web("#6f209f"));
        expr = Expressions.parseFromFlattenExpr("1/2sqrt(x^2+y^2)-sin(8arctan(y/x))", vp, IOBridge.DFLT_BRIDGE);
        pg.addFunc(expr, Color.web("#df2f4f"));
        expr = Expressions.parseFromFlattenExpr("-2/3sqrt(x^2+y^2)-sin(8arctan(y/x))", vp, IOBridge.DFLT_BRIDGE);
        pg.addFunc(expr, Color.web("#ff000f"));
        */
        expr = Expressions.parseFromFlattenExpr("((6/5)^4)/x-y", vp, IOBridge.DFLT_BRIDGE);
        pg.addFunc(expr, Color.web("#6f209f"));

        // Calculator scene
        Button buttonToGphMenu = new Button("AL");
        Button buttonToCalMenu = new Button("BE");
        buttonToGphMenu.setStyle("-fx-background-color: transparent;");
        buttonToCalMenu.setStyle("-fx-background-color: transparent;");

        // Set up the menu
        VBox mainMenu = new VBox();
        mainMenu.getChildren().addAll(buttonToCalMenu, buttonToGphMenu);
        VBox.setMargin(mainMenu, new Insets(0));
        mainMenu.setMinWidth(80);
        mainMenu.setPadding(new Insets(0));
        mainMenu.setStyle("-fx-background-color: #dddddd;");

        BorderPane mainLayout = new BorderPane();
        BorderPane.setMargin(mainLayout, new Insets(0));
        mainLayout.setLeft(mainMenu);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setCenter(pg.getPlane());

        Scene calScene = new Scene(mainLayout, 1080, 720);

        primaryStage.setScene(calScene);
        primaryStage.setTitle("HI");
        primaryStage.show();
    }

}
