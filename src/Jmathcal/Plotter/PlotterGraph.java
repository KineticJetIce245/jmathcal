package Jmathcal.Plotter;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.*;// for Color and Graphics class
import java.awt.geom.*;// shapes and paths


public class PlotterGraph extends JComponent {
    @java.io.Serial
    private static final long serialVersionUID = -5472621267021596921L;

    private int width;
    private int height;

    public PlotterGraph(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // Overwriting paintComponent method
    // Graphics g will be automatically created
    protected void paintComponent(Graphics g) {
        Graphics2D graphics2d = (Graphics2D) g;
        Rectangle2D.Double retangle = new Rectangle2D.Double(50, 75, 100, 250);
        // x = 0 and y = 0 is on the top left of the component
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setSize(640, 800);
        f.setTitle("Drawing in Java");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
