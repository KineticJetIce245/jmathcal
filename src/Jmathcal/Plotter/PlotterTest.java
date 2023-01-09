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
        double[] origin = { -5, -5 };
        int[] planeSize = { 1080, 720 };
        int[] resolution = { 200, 200 };
        int maxDepth = 4;
        PlotterPlane myPlane = new PlotterPlane(length, origin, planeSize, resolution, maxDepth);
        VariablePool vp = new VariablePool();

        PointGroup pg = new PointGroup();
        pg.setUpGrid(myPlane);
        Expressions expr = Expressions.parseFromFlattenExpr("ysinx+xcosy-1", vp, IOBridge.DFLT_BRIDGE);
        pg.addFunc(expr, Color.web("#ff0000"));
        expr = Expressions.parseFromFlattenExpr("xcosx-ycosy-1", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#df003f"));
        expr = Expressions.parseFromFlattenExpr("xcosx-ycosy+1", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#bf005f"));
        expr = Expressions.parseFromFlattenExpr("xcosx-ycosy+3", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#9f007f"));
        expr = Expressions.parseFromFlattenExpr("xcosx-ycosy+5", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#7f009f"));
        expr = Expressions.parseFromFlattenExpr("xcosx-ycosy+7", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#5f00bf"));
        expr = Expressions.parseFromFlattenExpr("xcosx-ycosy+9", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#3f00df"));
        expr = Expressions.parseFromFlattenExpr("xcosx-ycosy+11", vp, IOBridge.DFLT_BRIDGE);
        //pg.addFunc(expr, Color.web("#0000ff"));

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
