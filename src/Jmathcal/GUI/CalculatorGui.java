package Jmathcal.GUI;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.awt.Dimension;
import java.awt.CardLayout;


public class CalculatorGui extends JFrame{

    private static final long serialVersionUID = 5398064627126749344L;

    public static final Dimension DEF_DIMENSION = new Dimension(990, 540);

    public static void main(String[] args) {

        Properties launchInfo = new Properties();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config/calculator/launchInfo.xml");
            launchInfo.loadFromXML(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ErrorGui.launch(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            ErrorGui.launch(e.toString());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        JFrame myFrame = new CalculatorGui(new File(launchInfo.getProperty("langFilePath")));
    }
    
    public CalculatorGui(File langPath) {

        Properties langDisplay = new Properties();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(langPath);
            langDisplay.loadFromXML(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ErrorGui.launch(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            ErrorGui.launch(e.toString());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        JFrame appFrame = new JFrame(langDisplay.getProperty("calGuiFrameTitle"));
        appFrame.setSize(DEF_DIMENSION);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setLocationRelativeTo(null);

        JMenuBar mainMenuBar = new JMenuBar();
        appFrame.setJMenuBar(mainMenuBar);


        CardLayout mainCardLayout = new CardLayout();
        appFrame.setLayout(mainCardLayout);
        appFrame.setVisible(true);
    }
}
