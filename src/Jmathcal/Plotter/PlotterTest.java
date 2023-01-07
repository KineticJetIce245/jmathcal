package Jmathcal.Plotter;

import java.util.ArrayList;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PlotterTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        int[] length = { 10, 10 };
        double[] origin = { -5, -5 };
        int[] planeSize = { 1080, 720 };
        int[] resolution = { 10, 10 };
        int maxDepth = 2;
        PlotterPlane myPlane = new PlotterPlane(length, origin, planeSize, resolution, maxDepth, 0.002);
        VariablePool vp = new VariablePool();
        Expressions lExpr = Expressions.parseFromFlattenExpr("y sin x+ x cosy", vp, IOBridge.DFLT_BRIDGE);
        Expressions rExpr = Expressions.parseFromFlattenExpr("1", vp, IOBridge.DFLT_BRIDGE);
        myPlane.inputExpr(lExpr, rExpr);
        myPlane.subdivide();
        Group g = new Group();

        /*
        Node[] nodesList = myPlane.drawGraphic();
        for (int i = 0; i < nodesList.length; i++) {
            g.getChildren().add(nodesList[i]);
        }
        */

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
        mainLayout.setCenter(g);

        Rectangle re = new Rectangle(0,0,5,10);
        Rectangle re1 = new Rectangle(-15,-10,15,10);

        
        g.getChildren().addAll(re,re1);

        Scene calScene = new Scene(mainLayout, 1080, 720);

        primaryStage.setScene(calScene);
        primaryStage.setTitle("HI");
        primaryStage.show();
    }

}
