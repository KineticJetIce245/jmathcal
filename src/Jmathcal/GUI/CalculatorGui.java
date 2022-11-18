package Jmathcal.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import Jmathcal.IOControl.IOBridge;

import java.awt.Dimension;

public class CalculatorGui extends Application {
    @java.io.Serial
    private static final long serialVersionUID = 5398064627126749344L;

    public static final Dimension DEF_DIMENSION = new Dimension(990, 540);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties launchInfo = getLaunchInfo();
        Properties langDisplay = getLangFile(new File(launchInfo.get("langFilePath").toString()));

        Stage window;
        Scene calScene, gphScene;

        window = primaryStage;

        Label label1 = new Label("Hello scene1");
        Button buttonToGphMenu = new Button(langDisplay.getProperty("Calculator_Menu_Graphics"));

        VBox layout1 = new VBox();
        layout1.getChildren().addAll(label1, buttonToGphMenu);
        calScene = new Scene(layout1, 1080, 720);

        Label label2 = new Label("Hello scene2");
        Button buttonToCalMenu = new Button(langDisplay.getProperty("Calculator_Menu_Calculator"));

        StackPane layout2 = new StackPane();
        layout2.getChildren().addAll(label2, buttonToCalMenu);
        gphScene = new Scene(layout2, 1080, 720);

        buttonToGphMenu.setOnAction(e -> {
            window.setScene(gphScene);
        });

        buttonToCalMenu.setOnAction(e -> {
            window.setScene(calScene);
        });

        window.setScene(calScene);
        window.setTitle(langDisplay.getProperty("Calculator_Window_Title"));
        window.show();
    }

    /**
     * Get the path of essential files
     * 
     * @return {@code Properties} that contains the path of essential files
     */
    private Properties getLaunchInfo() throws FileNotFoundException {
        Properties launchInfo = new Properties();
        FileInputStream infoFis = null;
        try {
            infoFis = new FileInputStream(IOBridge.LAUNCH_INFO_PATH);
            launchInfo.loadFromXML(infoFis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            AlertBox.display("Error", "The calculator can not find the required files to launch!");
            throw new FileNotFoundException();
        } catch (IOException e) {
            e.printStackTrace();
            AlertBox.display("Error", "The calculator can not find the required files to launch!");
            throw new FileNotFoundException();
        } finally {
            if (infoFis != null) {
                try {
                    infoFis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return launchInfo;
    }

    private Properties getLangFile(File langFilePath) throws FileNotFoundException{
        Properties langFileProperties = new Properties();
        FileInputStream langFis = null;
        try {
            langFis = new FileInputStream(langFilePath);
            langFileProperties.loadFromXML(langFis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            AlertBox.display("Error", "The calculator can not find the required files to launch!");
            throw new FileNotFoundException();
        } catch (IOException e) {
            e.printStackTrace();
            AlertBox.display("Error", "The calculator can not find the required files to launch!");
            throw new FileNotFoundException();
        } finally {
            if (langFis != null) {
                try {
                    langFis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return langFileProperties;
    }

}
