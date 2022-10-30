package Jmathcal.Plotter;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Renderer;

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
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle2D.Double rectangle = new Rectangle2D.Double(50, 75, 100, 250);
        // x = 0 and y = 0 is on the top left of the component
        graphics2d.setColor(new Color(100, 149, 237));
        graphics2d.fill(rectangle);

        Ellipse2D.Double e = new Ellipse2D.Double(200, 100, 500, 60);
        graphics2d.setColor(new Color(20, 60, 180));
        graphics2d.fill(e);

        Line2D.Double line = new Line2D.Double(100, 200, 300, 400);
        graphics2d.setColor(new Color(0, 0, 0));
        graphics2d.draw(line);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        PlotterGraph pg = new PlotterGraph(100, 200);
        f.setSize(640, 800);
        f.setTitle("Drawing in Java");
        f.add(pg);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
