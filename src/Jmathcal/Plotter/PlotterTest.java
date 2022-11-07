package Jmathcal.Plotter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;

import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;

public class PlotterTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                constructUI();

            }
        });
    }
    

    private static void constructUI() {
        VariablePool vp = new VariablePool();
        Expressions rightExpr = Expressions.parseFromFlattenExpr("x^2", vp, IOBridge.DFLT_BRIDGE);
        Expressions leftExpr = Expressions.parseFromFlattenExpr("y", vp, IOBridge.DFLT_BRIDGE);

        int[] resolution = {25,25};
        int[] length = {20, 20};
        double[] origin = {-10, -10};
        int[] planeSize = {1000, 1000};
        
        PlotterPlane.PlaneInfo planeInfo = new PlotterPlane.PlaneInfo(resolution, length, origin, planeSize);

        PlotterPlane myPlane = new PlotterPlane(planeInfo, rightExpr, leftExpr);

        JFrame jf = new JFrame();
        //jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CardLayout card = new CardLayout();
        //jf.setLayout(card);
        jf.add(new PlotterGraph(myPlane.toDrawList, planeInfo));
        jf.setVisible(true);
        jf.pack();

        for (int i = 0; i < 6; i++) {
            //card.last(jf.getContentPane());
            jf = new JFrame();
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.add(new PlotterGraph(myPlane.subdivide(), planeInfo));
            jf.setVisible(true);
            jf.pack();
        }
    }
}
