package Jmathcal.GUI;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import Jmathcal.Expression.ExprSyntaxErrorException;
import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariableLabelOccupiedException;
import Jmathcal.Expression.VariablePool;
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

        Scene calScene, gphScene;
        Font tsanger = getFont(new File(launchInfo.getProperty("TsangerYuMo_WO3Path")));

        // Calculator scene
        Button buttonToGphMenu = new Button(langDisplay.getProperty("Calculator_Menu_Graphics"));


        // Formula input text field
        TextField formulaInput = new TextField();
        formulaInput.setPromptText(langDisplay.getProperty("Input_Formula"));
        formulaInput.setPrefSize(900, 40);
        formulaInput.setFont(tsanger);
        formulaInput.setAlignment(Pos.CENTER);
        GridPane.setConstraints(formulaInput, 0, 0);
        
        // number buttons
        GridPane funcGridPane = new GridPane();
        funcGridPane.setPadding(new Insets(20, 20, 20, 20));
        funcGridPane.setVgap(8);
        funcGridPane.setHgap(10);
        Button num7 = getButtonFromFac("7", tsanger, 0, 0, formulaInput);
        Button num8 = getButtonFromFac("8", tsanger, 1, 0, formulaInput);
        Button num9 = getButtonFromFac("9", tsanger, 2, 0, formulaInput);
        Button num4 = getButtonFromFac("4", tsanger, 0, 1, formulaInput);
        Button num5 = getButtonFromFac("5", tsanger, 1, 1, formulaInput);
        Button num6 = getButtonFromFac("6", tsanger, 2, 1, formulaInput);
        Button num1 = getButtonFromFac("1", tsanger, 0, 2, formulaInput);
        Button num2 = getButtonFromFac("2", tsanger, 1, 2, formulaInput);
        Button num3 = getButtonFromFac("3", tsanger, 2, 2, formulaInput);
        Button num0 = getButtonFromFac("0", tsanger, 1, 3, formulaInput);
        funcGridPane.getChildren().addAll(
            num1, num2, num3, num4, num5, num6, num7, num8, num9, num0
        );
        Button sinButton = getButtonFromFac("sin", tsanger, 3, 0, formulaInput);
        Button cosButton = getButtonFromFac("cos", tsanger, 4, 0, formulaInput);
        Button tanButton = getButtonFromFac("tan", tsanger, 5, 0, formulaInput);
        Button arcsinButton = getButtonFromFac("arcsin", tsanger, 3, 1, formulaInput);
        Button arccosButton = getButtonFromFac("arccos", tsanger, 4, 1, formulaInput);
        Button arctanButton = getButtonFromFac("arctan", tsanger, 5, 1, formulaInput);
        Button cotButton = getButtonFromFac("cot", tsanger, 3, 2, formulaInput);
        Button cscButton = getButtonFromFac("csc", tsanger, 4, 2, formulaInput);
        Button secButton = getButtonFromFac("sec", tsanger, 5, 2, formulaInput);
        funcGridPane.getChildren().addAll(
            sinButton, cosButton, tanButton, arcsinButton, arccosButton, arctanButton,
            cotButton, cscButton, secButton
        );
        funcGridPane.setAlignment(Pos.CENTER);

        GridPane.setConstraints(funcGridPane, 0, 1);
        
        // Variable pool
        VariablePool vp = new VariablePool();
        VpPane vpPane = new VpPane(vp, tsanger, langDisplay);
        vpPane.setDisplay();
        GridPane.setConstraints(vpPane, 1, 1);

        // Enter Button
        IOBridge ioBridge = new IOBridge() {

            @Override
            public void outSendMessage(String msg) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public String askForInput(String msg) {
                return AlertBox.askForInput(langDisplay.getProperty("Ask_For_Input_Title"), msg);
            }

            @Override
            public HashMap<String, File> getPropertiesLoc() {
                return propertiesToHashMap(launchInfo);
            }

            private static HashMap<String, File> propertiesToHashMap(Properties properties) {
                HashMap<String, File> reVal = new HashMap<String, File>();
                Set<String> keySet = properties.stringPropertyNames();
                Iterator<String> keySetIterator = keySet.iterator();
                while(keySetIterator.hasNext()) {
                    String currentKey = keySetIterator.next();
                    reVal.put(currentKey, new File(properties.get(currentKey).toString()));
                }
                return reVal;
            }
            
        };

        Button enterButton = new Button(langDisplay.getProperty("Send_Input"));
        enterButton.setFont(tsanger);
        GridPane.setConstraints(enterButton, 1, 0);

        enterButton.setOnAction(action -> {
            String formula = formulaInput.getText();
            try {
                Expressions expr = Expressions.parseFromFlattenExpr(formula, vp, ioBridge);
                MathContext mc = new MathContext(16, RoundingMode.HALF_UP);
                MathContext roundMc = new MathContext(14, RoundingMode.HALF_UP);
                String result = expr.calculate(mc).round(roundMc).toString();
                vpPane.setDisplay();

                System.out.println(result);
            } catch (ArithmeticException error) {
                System.out.println("Math error");
            } catch (ExprSyntaxErrorException error) {
                System.out.println("Syntax error: " + error.getMessage());
            } catch (VariableLabelOccupiedException error) {
                System.out.println("Variable error: " + error.getMessage());
            }
        });       

        // Layout
        GridPane calSceneLayout = new GridPane();
        calSceneLayout.setPadding(new Insets(20, 20, 20, 20));
        calSceneLayout.setVgap(8);
        calSceneLayout.setHgap(10);
        calSceneLayout.setAlignment(Pos.CENTER);

        calSceneLayout.getChildren().addAll(
                formulaInput, enterButton, funcGridPane
        );

        calScene = new Scene(calSceneLayout, 1080, 720);

        // Graphic scene
        Label label2 = new Label("Hello scene2");
        Button buttonToCalMenu = new Button(langDisplay.getProperty("Calculator_Menu_Calculator"));

        StackPane layout2 = new StackPane();
        layout2.getChildren().addAll(label2, buttonToCalMenu);
        gphScene = new Scene(layout2, 1080, 720);

        buttonToGphMenu.setOnAction(e -> {
            primaryStage.setScene(gphScene);
        });

        buttonToCalMenu.setOnAction(e -> {
            primaryStage.setScene(calScene);
        });

        primaryStage.setScene(calScene);
        primaryStage.setTitle(langDisplay.getProperty("Calculator_Window_Title"));
        primaryStage.show();
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

    private static Font getFont(File fontPath) throws FileNotFoundException{
        FileInputStream stream = null;
        Font thisFont = null;
        try {
            stream = new FileInputStream(fontPath);
            thisFont = Font.loadFont(stream, 20);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            AlertBox.display("Error", "The calculator can not find the required files to launch!");
            throw new FileNotFoundException();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } 

        return thisFont;
    }

    private static Button getButtonFromFac(String sym, Font font, int locX, int locY, TextField relatedTF) {
        Button reButton = new Button(String.valueOf(sym));
        reButton.setFont(font);
        reButton.setMinSize(50, 50);
        GridPane.setConstraints(reButton, locX, locY);
        GridPane.setHalignment(reButton, HPos.CENTER);

        reButton.setOnAction(e -> {
            String currentText = relatedTF.getText();
            relatedTF.setText(currentText + sym);
        });

        return reButton;
    }

    private class VpPane extends VBox {
        // TODO
        private VariablePool vp;
        private Font font;
        private Properties langProperties;

        public VpPane(VariablePool vp, Font font, Properties langPro) {
            this.vp = vp;
            this.font = font;
            this.langProperties = langPro;
        }
        public void setDisplay() {
            for (String label : vp.getLabelSet()) {
                HBox variableBox = new HBox();
                Label variableID = new Label(label);
                variableID.setFont(this.getFont());
                variableID.setPrefSize(40, 20);
                TextField variableName = new TextField(vp.getVariable(label).getName());
                TextField variableValue = new TextField();
                variableValue.setPromptText(langProperties.getProperty("Ask_For_Input"));
                variableBox.getChildren().addAll(variableID, variableName, variableValue);
                ScrollPane variableBoxPane = new ScrollPane();
                variableBoxPane.setPrefSize(40, 120);
                variableBoxPane.setContent(variableBox);
                this.getChildren().add(variableBoxPane);
            }
        }
        public void refreshVp() {
        }
        public Font getFont() {
            return (this.font != null) ? this.font : Font.getDefault();
        }
    } 
}
