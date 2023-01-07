package Jmathcal.OldPlotter;

import javax.swing.JComponent;

import java.awt.*;// for Color and Graphics class
import java.awt.geom.*;// shapes and paths
import java.util.ArrayList;

public class PlotterGraph extends JComponent {
    @java.io.Serial
    private static final long serialVersionUID = -5472621267021596921L;

    private ArrayList<PlottingSqr> toDrawList;
    private PlotterPlane.PlaneInfo planeInfo;
    private final double SCALE_FAC;
    private int xAxisLen;
    private int yAxisLen;

    public PlotterGraph(ArrayList<PlottingSqr> toDrawList, PlotterPlane.PlaneInfo planeInfo) {
        this.toDrawList = toDrawList;
        this.planeInfo = planeInfo;
        this.SCALE_FAC = planeInfo.getScale();

        xAxisLen = (int)(planeInfo.getLen(0)*SCALE_FAC);
        yAxisLen = (int)(planeInfo.getLen(1)*SCALE_FAC);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(xAxisLen, yAxisLen);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setColor(Color.GRAY);

        double xFullLen = planeInfo.getLen(0);
        double yFullLen = planeInfo.getLen(1);

        double xOri = planeInfo.getOri(0);
        double yOri = planeInfo.getOri(1);
        graphics2d.translate(-xOri * SCALE_FAC, (yFullLen + yOri) * SCALE_FAC);

        Line2D.Double xAxis = new Line2D.Double(xOri * SCALE_FAC, 0, (xFullLen + xOri) * SCALE_FAC, 0);
        graphics2d.draw(xAxis);

        Line2D.Double yAxis = new Line2D.Double(0, -(yFullLen + yOri) * SCALE_FAC, 0, -yOri * SCALE_FAC);
        graphics2d.draw(yAxis);

        graphics2d.setColor(Color.RED);
        if (toDrawList.isEmpty())
            return;
        
        double xSize = toDrawList.get(0).getSize(0) * SCALE_FAC;
        double ySize = toDrawList.get(0).getSize(1) * SCALE_FAC;
        if (xSize < 1 || ySize < 1) {
            for (PlottingSqr i : toDrawList) {
                double xLoc = i.getUpLeftLoc(0) * SCALE_FAC;
                double yLoc = - i.getUpLeftLoc(1) * SCALE_FAC;
                Line2D.Double line = new Line2D.Double(xLoc, yLoc, xLoc, yLoc);
                graphics2d.draw(line);
            }
        } else {
            for (PlottingSqr i : toDrawList) {
                double xLoc = i.getUpLeftLoc(0) * SCALE_FAC;
                double yLoc = - i.getUpLeftLoc(1) * SCALE_FAC;
                Rectangle2D.Double rectangle = new Rectangle2D.Double(xLoc, yLoc, xSize, ySize);
                graphics2d.fill(rectangle);
            }
        }

    }
}
