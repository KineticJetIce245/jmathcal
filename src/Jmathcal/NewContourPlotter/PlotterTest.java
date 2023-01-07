package Jmathcal.NewContourPlotter;

import java.awt.Dimension;
import java.util.ArrayList;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class PlotterTest extends Application {
    public static final Dimension DEF_DIMENSION = new Dimension(990, 540);

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

        for (int i = myPlane.signMatrix[0].length - 1; i > -1; i--) {
            for (int j = 0; j < myPlane.signMatrix.length; j++) {
                System.out.print(myPlane.signMatrix[j][i] == null ? "n" : myPlane.signMatrix[j][i]);
            }
            System.out.println("");
        }

        ArrayList<ArrayList<Double>> list = myPlane.getContourArrays();
        System.out.println(list.size());
        BorderPane layout = new BorderPane();
        Group root = new Group();
        interface Painter {
            void draw();
        }
        Painter p = new Painter() {
            @Override
            public void draw() {
                for (int i = 0; i < list.size(); i++) {
                    Polyline polyline = new Polyline();
                    Double[] pointArray = new Double[list.get(i).size()];
                    pointArray = list.get(i).toArray(pointArray);
                    polyline.getPoints().addAll(pointArray);
                    root.getChildren().add(polyline);
                }
            }
        };
        p.draw();

        Scene calScene = new Scene(root, 1080, 720);

        primaryStage.setScene(calScene);
        primaryStage.setTitle("HI");
        primaryStage.show();
    }
}
