package Jmathcal.Plotter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import Jmathcal.Expression.Expressions;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class PointGroup {

    private Group mainGroup = new Group();
    private Group gridGroup = new Group();
    private HashMap<Expressions, Group> funcGroup = new HashMap<Expressions, Group>();
    private PlotterPlane plane;

    public PointGroup() {}
    public void setUpGrid(PlotterPlane plane, boolean ifNum, boolean ifPrimaryGrid, boolean ifSecondaryGrid) {
        this.plane = plane;
        ArrayList<Node> nodesList = plane.generateGrid(ifNum, ifPrimaryGrid, ifSecondaryGrid);
        for (Node n : nodesList) {
            gridGroup.getChildren().add(n);
        }
        mainGroup.getChildren().add(gridGroup);
    }
    public void addFunc(Expressions expr, Color color) {
        plane.inputExpr(expr);
        plane.subdivide();
        ArrayList<Node> nodesList = plane.drawGraphic(color);
        Group currentGroup = new Group();
        for (int i = 0; i < nodesList.size(); i++) {
            currentGroup.getChildren().add(nodesList.get(i));
        }
        funcGroup.put(expr, currentGroup);
        mainGroup.getChildren().add(currentGroup);
    }
    public Group getPlane() {
        return this.mainGroup;
    }
    public void changeFunc(Expressions oriExpr, Expressions newExpr, Color newColor) {
        Set<Expressions> keySet = funcGroup.keySet();
        if (!keySet.contains(oriExpr)) {
            plane.inputExpr(newExpr);
            plane.subdivide();
            ArrayList<Node> nodesList = plane.drawGraphic(newColor);
            Group currentGroup = new Group();
            for (int i = 0; i < nodesList.size(); i++) {
                currentGroup.getChildren().add(nodesList.get(i));
            }
            funcGroup.put(newExpr, currentGroup);
            mainGroup.getChildren().add(currentGroup);
        } else {
            Group oldGp = funcGroup.remove(oriExpr);
            mainGroup.getChildren().remove(oldGp);
            
            plane.inputExpr(newExpr);
            plane.subdivide();
            ArrayList<Node> nodesList = plane.drawGraphic(newColor);
            Group currentGroup = new Group();
            for (int i = 0; i < nodesList.size(); i++) {
                currentGroup.getChildren().add(nodesList.get(i));
            }
            funcGroup.put(newExpr, currentGroup);
            mainGroup.getChildren().add(currentGroup);
        }
    }
}
