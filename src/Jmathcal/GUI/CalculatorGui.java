package Jmathcal.GUI;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import Jmathcal.Expression.ExprNumber;
import Jmathcal.Expression.ExprSyntaxErrorException;
import Jmathcal.Expression.Expressions;
import Jmathcal.Expression.VariableLabelOccupiedException;
import Jmathcal.Expression.VariablePool;
import Jmathcal.IOControl.IOBridge;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
        Font tsanger = getFont(new File(launchInfo.getProperty("TsangerYuMo_WO3Path")), 20);
        Font tsanger18 = getFont(new File(launchInfo.getProperty("TsangerYuMo_WO3Path")), 18);
        Font smiley = getFont(new File(launchInfo.getProperty("SmileySansPath")), 25);
        Font smiley18 = getFont(new File(launchInfo.getProperty("SmileySansPath")), 18);
        Font latinMath = getFont(new File(launchInfo.getProperty("latinMathPath")), 25);

        // Formula input text field
        FormulaInputField formulaInput = new FormulaInputField();
        formulaInput.setPromptText(langDisplay.getProperty("Input_Formula"));
        formulaInput.setPrefSize(900, 40);
        formulaInput.setFont(smiley);
        formulaInput.setAlignment(Pos.CENTER);
        formulaInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                formulaInput.setCaretPos(formulaInput.getCaretPosition());
            }
        });
        GridPane.setConstraints(formulaInput, 0, 2, 5, 1);

        // Rounding and Calculating Precision
        GridPane roundSettingPane = new GridPane();
        TextField roundingField = new TextField();
        roundingField.setFont(smiley18);
        roundingField.setPromptText("12");
        roundingField.setMinSize(30, 40);
        roundingField.setMaxWidth(45);
        TextField calField = new TextField();
        calField.setFont(smiley18);
        calField.setPromptText("16");
        calField.setMinSize(30, 40);
        calField.setMaxWidth(45);
        TextField historyField = new TextField();
        historyField.setFont(smiley18);
        historyField.setPromptText("16");
        historyField.setMinSize(30, 40);
        historyField.setMaxWidth(45);
        Label calFieldHelp = new Label(langDisplay.getProperty("Rounding_Setting_Calculation"));
        calFieldHelp.setFont(tsanger18);
        Label roundFieldHelp = new Label(langDisplay.getProperty("Rounding_Setting_Display"));
        roundFieldHelp.setFont(tsanger18);
        Label historyFieldHelp = new Label(langDisplay.getProperty("History_Display"));
        historyFieldHelp.setFont(tsanger18);

        GridPane.setConstraints(calFieldHelp, 0, 0);
        GridPane.setConstraints(roundFieldHelp, 2, 0);
        GridPane.setConstraints(calField, 1, 0);
        GridPane.setConstraints(roundingField, 3, 0);
        GridPane.setConstraints(historyField, 5, 0);
        GridPane.setConstraints(historyFieldHelp, 4, 0);
        roundSettingPane.getChildren().addAll(
            calField, calFieldHelp,
            roundingField, roundFieldHelp,
            historyField, historyFieldHelp);
        GridPane.setConstraints(roundSettingPane, 0, 7, 5, 1);
        roundSettingPane.setAlignment(Pos.CENTER);

        TabPane inputPane = new TabPane();
        inputPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        // input buttons pane 1
        Tab funcGridTab1 = new Tab(langDisplay.getProperty("Calculator_InputTab_Tab1"));
        GridPane funcGridPane1 = new GridPane();
        funcGridPane1.setPadding(new Insets(20, 20, 20, 20));
        funcGridPane1.setVgap(8);
        funcGridPane1.setHgap(10);
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
        Button pointButton = getButtonFromFac(".", tsanger, 0, 3, formulaInput);

        Button backspaceButton = new Button("DEL");
        backspaceButton.setFont(tsanger);
        backspaceButton.setMinSize(50, 50);
        GridPane.setConstraints(backspaceButton, 2, 4);
        GridPane.setHalignment(backspaceButton, HPos.CENTER);
        backspaceButton.setOnAction(e -> {
            int carpetPos = formulaInput.getCaretPos();
            if (!(carpetPos == 0)) {
                formulaInput.deleteCharAt(carpetPos - 1);
            } else {
                formulaInput.deleteCharAt(0);
            }
        });

        Button plusButton = getButtonFromFac("+", tsanger, 3, 0, formulaInput);
        Button minButton = getButtonFromFac("-", tsanger, 3, 1, formulaInput);
        Button mulButton = getButtonFromFac("*", tsanger, 3, 2, formulaInput);
        Button divButton = getButtonFromFac("/", tsanger, 3, 3, formulaInput);
        Button expButton = getButtonFromFac("e^x", (char) 92 + "e^()", tsanger, 2, 3, formulaInput);
        Button expoButton = getButtonFromFac("^", tsanger, 3, 4, formulaInput);
        Button leftPaButton = getButtonFromFac("(", tsanger, 0, 4, formulaInput);
        Button rightPaButton = getButtonFromFac(")", tsanger, 1, 4, formulaInput);
        Button tenExpButton = getButtonFromFac("10^x", "10^()", tsanger, 4, 4, 94, 50, formulaInput);
        funcGridPane1.getChildren().addAll(
                plusButton, minButton, mulButton, divButton, expoButton, leftPaButton, rightPaButton, expButton,
                tenExpButton);

        Button toLeftButton = new Button("<--");
        toLeftButton.setFont(tsanger);
        toLeftButton.setMinSize(94, 50);
        GridPane.setConstraints(toLeftButton, 5, 4);
        GridPane.setHalignment(toLeftButton, HPos.CENTER);
        toLeftButton.setOnAction(e -> {
            int currentPlace = formulaInput.getCaretPos();
            formulaInput.setCaretPosFocused(currentPlace == 0 ? formulaInput.length() : currentPlace - 1);
        });

        Button toRightButton = new Button("-->");
        toRightButton.setFont(tsanger);
        toRightButton.setMinSize(94, 50);
        GridPane.setConstraints(toRightButton, 6, 4);
        GridPane.setHalignment(toRightButton, HPos.CENTER);
        toRightButton.setOnAction(e -> {
            int currentPlace = formulaInput.getCaretPos();
            formulaInput.setCaretPosFocused(currentPlace == formulaInput.length() ? 0 : currentPlace + 1);
        });
        funcGridPane1.getChildren().addAll(toLeftButton, toRightButton);

        funcGridPane1.getChildren().addAll(
                num1, num2, num3, num4, num5, num6, num7, num8, num9, num0, pointButton, backspaceButton);
        Button sinButton = getButtonFromFac("sin", tsanger, 4, 0, 94, 50, formulaInput);
        Button cosButton = getButtonFromFac("cos", tsanger, 5, 0, 94, 50, formulaInput);
        Button tanButton = getButtonFromFac("tan", tsanger, 6, 0, 94, 50, formulaInput);
        Button secButton = getButtonFromFac("csc", tsanger, 4, 1, 94, 50, formulaInput);
        Button cscButton = getButtonFromFac("sec", tsanger, 5, 1, 94, 50, formulaInput);
        Button cotButton = getButtonFromFac("cot", tsanger, 6, 1, 94, 50, formulaInput);
        Button arcsinButton = getButtonFromFac("arcsin", tsanger, 4, 2, 94, 50, formulaInput);
        Button arccosButton = getButtonFromFac("arccos", tsanger, 5, 2, 94, 50, formulaInput);
        Button arctanButton = getButtonFromFac("arctan", tsanger, 6, 2, 94, 50, formulaInput);
        Button arccscButton = getButtonFromFac("arccsc", tsanger, 4, 3, 94, 50, formulaInput);
        Button arcsecButton = getButtonFromFac("arcsec", tsanger, 5, 3, 94, 50, formulaInput);
        Button arccotButton = getButtonFromFac("arccot", tsanger, 6, 3, 94, 50, formulaInput);
        funcGridPane1.getChildren().addAll(
                sinButton, cosButton, tanButton, arcsinButton, arccosButton, arctanButton,
                cotButton, cscButton, secButton, arccscButton, arcsecButton, arccotButton);

        funcGridPane1.setAlignment(Pos.CENTER);
        funcGridTab1.setContent(funcGridPane1);
        inputPane.getTabs().add(funcGridTab1);
        GridPane.setConstraints(inputPane, 0, 4, 1, 1);

        // Input buttons pane 2
        Tab funcGridTab2 = new Tab(langDisplay.getProperty("Calculator_InputTab_Tab2"));
        GridPane funcGridPane2 = new GridPane();
        funcGridPane2.setPadding(new Insets(20, 20, 20, 20));
        funcGridPane2.setVgap(8);
        funcGridPane2.setHgap(10);
        Button sinhButton = getButtonFromFac("sinh", tsanger, 0, 0, 94, 50, formulaInput);
        Button coshButton = getButtonFromFac("cosh", tsanger, 1, 0, 94, 50, formulaInput);
        Button tanhButton = getButtonFromFac("tanh", tsanger, 2, 0, 94, 50, formulaInput);
        Button arsinhButton = getButtonFromFac("arsinh", tsanger, 3, 0, 94, 50, formulaInput);
        Button arcoshButton = getButtonFromFac("arcosh", tsanger, 4, 0, 94, 50, formulaInput);
        Button artanhButton = getButtonFromFac("artanh", tsanger, 5, 0, 94, 50, formulaInput);
        funcGridPane2.getChildren().addAll(
                sinhButton, coshButton, tanhButton, arsinhButton, arcoshButton, artanhButton);
        Button lnButton = getButtonFromFac("ln", tsanger, 0, 1, 94, 50, formulaInput);
        Button logButton = getButtonFromFac("log", "log(, )", tsanger, 1, 1, 94, 50, formulaInput);
        Button sumButton = getButtonFromFac("sum", "sum(n, 0, 1, n+1)", tsanger, 2, 1, 94, 50, formulaInput);
        Button proButton = getButtonFromFac("pro", "pro(n, 1, 2, n+1)", tsanger, 3, 1, 94, 50, formulaInput);
        Button toDegButton = getButtonFromFac("todeg", tsanger, 0, 2, 94, 50, formulaInput);
        Button toGradButton = getButtonFromFac("tograd", tsanger, 1, 2, 94, 50, formulaInput);
        Button degButton = getButtonFromFac("deg", tsanger, 2, 2, 94, 50, formulaInput);
        Button gradButton = getButtonFromFac("grad", tsanger, 3, 2, 94, 50, formulaInput);

        funcGridPane2.getChildren().addAll(
                lnButton, logButton, sumButton, proButton, toDegButton, toGradButton, degButton, gradButton);
        funcGridTab2.setContent(funcGridPane2);
        funcGridPane2.setAlignment(Pos.CENTER);

        inputPane.getTabs().add(funcGridTab2);

        // History
        ScrollPane hScrollPane = new ScrollPane();
        HistoryPane historyPane = new HistoryPane(smiley18, langDisplay, formulaInput);
        historyPane.setPrefSize(900, 100);
        hScrollPane.setContent(historyPane);
        hScrollPane.setPrefSize(900, 100);
        GridPane.setConstraints(hScrollPane, 0, 5, 6, 1);

        // Variable pool
        ScrollPane vScrollPane = new ScrollPane();
        VariablePool vp = new VariablePool();
        VariablePool.Variable ANSvariable = vp.new Variable("[ANS]");
        ANSvariable.setName("Answer");
        ANSvariable.setValue(new ExprNumber("0+0i"));
        VpPane vpPane = new VpPane(vp, smiley18, langDisplay, formulaInput);
        vpPane.setDisplay();
        vScrollPane.setContent(vpPane);
        vScrollPane.setMinSize(350, 300);
        GridPane.setConstraints(vScrollPane, 1, 4, 4, 1);

        // Answer Space
        ScrollPane answerSpace = new ScrollPane();
        Label answerLabel = new Label();
        answerLabel.setText("");
        answerLabel.setFont(smiley);
        answerLabel.setAlignment(Pos.CENTER);
        answerSpace.setMinSize(762, 44);
        answerSpace.setContent(answerLabel);
        GridPane.setConstraints(answerSpace, 0, 3, 2, 1);

        // Enter Button
        Button enterButton = new Button(langDisplay.getProperty("Send_Input"));
        enterButton.setFont(tsanger);
        enterButton.setMinSize(90, 40);
        GridPane.setHalignment(enterButton, HPos.CENTER);
        GridPane.setConstraints(enterButton, 4, 3);
        interface CalInitiator {
            public void initiate();
        }
        CalInitiator calInitiator = new CalInitiator() {
            IOBridge ioBridge = new IOBridge() {
                @Override
                public void outSendMessage(String msg) {
                    AlertBox.display(langDisplay.getProperty("Message_Display_Title"), msg);
                }

                @Override
                public String askForInput(String msg) {
                    return AlertBox.askForInput(langDisplay.getProperty("Ask_For_Input_Title"), msg, tsanger18,
                            smiley18);
                }

                @Override
                public HashMap<String, File> getPropertiesLoc() {
                    return propertiesToHashMap(launchInfo);
                }

                private static HashMap<String, File> propertiesToHashMap(Properties properties) {
                    HashMap<String, File> reVal = new HashMap<String, File>();
                    Set<String> keySet = properties.stringPropertyNames();
                    Iterator<String> keySetIterator = keySet.iterator();
                    while (keySetIterator.hasNext()) {
                        String currentKey = keySetIterator.next();
                        reVal.put(currentKey, new File(properties.get(currentKey).toString()));
                    }
                    return reVal;
                }

            };

            @Override
            public void initiate() {
                String formula = formulaInput.getText();
                String calPreci = calField.getText() == "" ? calField.getPromptText() : calField.getText();
                String ronPreci = roundingField.getText() == "" ? roundingField.getPromptText()
                        : roundingField.getText();
                try {
                    int calPreciInt = Integer.valueOf(calPreci);
                    int ronPreciInt = Integer.valueOf(ronPreci);
                    Expressions expr = Expressions.parseFromFlattenExpr(formula, vp, ioBridge);
                    MathContext mc = new MathContext(calPreciInt, RoundingMode.HALF_UP);
                    MathContext roundMc = new MathContext(ronPreciInt, RoundingMode.HALF_UP);

                    Set<String> ocpVarSet = vpPane.labelVarMap.keySet();
                    Iterator<String> oVSIter = ocpVarSet.iterator();
                    while (oVSIter.hasNext()) {
                        String label = oVSIter.next();
                        Expressions variableExpr = Expressions.parseFromFlattenExpr(
                                vpPane.labelVarMap.get(label).getValueField().getText(), vp, ioBridge);

                        vp.setNameOf(label, vpPane.labelVarMap.get(label).getNameField().getText());
                        vp.setValueOf(label, variableExpr.calculate(mc));
                    }

                    ExprNumber result = expr.calculate(mc).round(roundMc);
                    ANSvariable.setValue(result);
                    String resultStr = result.toString();
                    vpPane.refreshVp();
                    answerLabel.setText(resultStr);
                    historyPane.addBox(formula, result.toAnsString(), historyField);
                } catch (ArithmeticException error) {
                    answerLabel.setText("Math error");
                } catch (ExprSyntaxErrorException error) {
                    answerLabel.setText(
                            "Syntax error: " + (error.getMessage() == null ? "unknown error" : error.getMessage()));
                } catch (VariableLabelOccupiedException error) {
                    answerLabel.setText(
                            "Variable error: " + (error.getMessage() == null ? "unknown error" : error.getMessage()));
                } catch (NoSuchElementException error) {
                    answerLabel.setText(
                            "Parser error: " + (error.getMessage() == null ? "unknown error" : error.getMessage()));
                } catch (NumberFormatException error) {
                    answerLabel.setText(
                            "Number input error: "
                                    + (error.getMessage() == null ? "unknown error" : error.getMessage()));
                } catch (Exception error) {
                    answerLabel.setText("Unknown error: " + error.getMessage());
                }
            }
        };
        enterButton.setOnAction(action -> {
            calInitiator.initiate();
        });

        // AC
        Button allClearButton = new Button("AC");
        allClearButton.setFont(tsanger);
        allClearButton.setOnAction(e -> {
            formulaInput.clear();
        });
        GridPane.setHalignment(allClearButton, HPos.CENTER);
        GridPane.setConstraints(allClearButton, 3, 3);

        // Copy ANS button
        Button copyANSButton = new Button(langDisplay.getProperty("Copy_ANS"));
        copyANSButton.setFont(tsanger);
        copyANSButton.setMinSize(90, 40);
        copyANSButton.setOnAction(e -> {
            StringSelection selection = new StringSelection(ANSvariable.getValue().toAnsString());
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });
        GridPane.setConstraints(copyANSButton, 2, 3);

        // Layout
        GridPane calSceneLayout = new GridPane();
        GridPane.setMargin(calSceneLayout, new Insets(0));
        calSceneLayout.setPadding(new Insets(0));
        calSceneLayout.setVgap(8);
        calSceneLayout.setHgap(10);
        calSceneLayout.setAlignment(Pos.CENTER);
        calSceneLayout.getChildren().addAll(
                formulaInput, enterButton, inputPane, hScrollPane, vScrollPane, answerSpace, copyANSButton,
                allClearButton, roundSettingPane);
     
        // Calculator scene
        Button buttonToGphMenu = new Button(langDisplay.getProperty("Calculator_Menu_Graphics"));
        Button buttonToCalMenu = new Button(langDisplay.getProperty("Calculator_Menu_Calculator"));

        calSceneLayout.setStyle("-fx-background-color: #FFFFFF;");

        // Set up the menu
        VBox mainMenu = new VBox();
        buttonToGphMenu.setStyle("-fx-background-color: transparent;");
        buttonToCalMenu.setStyle("-fx-background-color: transparent;");
        mainMenu.getChildren().addAll(buttonToCalMenu, buttonToGphMenu);
        VBox.setMargin(mainMenu, new Insets(0));
        mainMenu.setPadding(new Insets(0));
        mainMenu.setStyle("-fx-background-color: #dddddd;");

        BorderPane calScenePane = new BorderPane();
        BorderPane.setMargin(calScenePane, new Insets(0));
        calScenePane.setLeft(mainMenu);
        calScenePane.setPadding(new Insets(0));
        calScenePane.setCenter(calSceneLayout);

        calScene = new Scene(calScenePane, 1180, 720);
        calScene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.ENTER) {
                calInitiator.initiate();
                key.consume();
            }
        });

        // Graphic scene
        Label label2 = new Label("Hello scene2");
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

    private Properties getLangFile(File langFilePath) throws FileNotFoundException {
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

    private static Font getFont(File fontPath, int fontSize) throws FileNotFoundException {
        FileInputStream stream = null;
        Font thisFont = null;
        try {
            stream = new FileInputStream(fontPath);
            thisFont = Font.loadFont(stream, fontSize);
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

    private static Button getButtonFromFac(String sym, Font font, int x, int y, FormulaInputField relatedTF) {
        return getButtonFromFac(sym, sym, font, x, y, relatedTF);
    }

    private static Button getButtonFromFac(String sym, String textInput, Font font, int x, int y,
            FormulaInputField relatedTF) {
        return getButtonFromFac(sym, textInput, font, x, y, 66, 50, relatedTF);
    }

    private static Button getButtonFromFac(String sym, Font font, int x, int y, double X, double Y,
            FormulaInputField relatedTF) {
        return getButtonFromFac(sym, sym, font, x, y, X, Y, relatedTF);
    }

    private static Button getButtonFromFac(
            String sym, String textInput, Font font, int x, int y, double X, double Y, FormulaInputField relatedTF) {
        Button reButton = new Button(sym);
        reButton.setFont(font);
        reButton.setMinSize(X, Y);
        GridPane.setConstraints(reButton, x, y);
        GridPane.setHalignment(reButton, HPos.CENTER);

        reButton.setOnAction(e -> {
            int addPosition = relatedTF.getCaretPos();
            relatedTF.addStringAt(addPosition, textInput);
            if (textInput.contains("(")) {
                relatedTF.setCaretPosFocused(addPosition + textInput.indexOf("(") + 1);
            }
        });

        return reButton;
    }

    private class FormulaInputField extends TextField {
        private int caretPos;

        public FormulaInputField() {
            super();
        }

        public int getCaretPos() {
            return caretPos;
        }

        public void addStringAt(int pos, String val) {
            StringBuffer text = new StringBuffer(this.getText());
            text.insert(pos, val);
            this.setText(text.toString());
            this.setCaretPosFocused(pos + val.length());
        }

        public void setCaretPos(int caretPos) {
            this.caretPos = caretPos;
        }

        private void setCaretPosFocused(int caretPos) {
            this.caretPos = caretPos;
            this.requestFocus();
            this.selectPositionCaret(caretPos);
            this.deselect();
        }

        public void deleteCharAt(int pos) {
            String strText = this.getText();
            if (strText == "") {
                return;
            }
            StringBuffer text = new StringBuffer(strText);
            if (pos == 0) {
                text.deleteCharAt(pos);
            } else {
                text.deleteCharAt(pos);
            }
            this.setText(text.toString());
            this.setCaretPosFocused(pos);
        }

        public int length() {
            return this.getText().length();
        }

        @Override
        public void clear() {
            super.clear();
            setCaretPosFocused(0);
        }

    }

    private class VpPane extends VBox {
        private VariablePool vp;
        private Font font;
        private Properties langProperties;
        private HashMap<String, VarBox> labelVarMap = new HashMap<String, VarBox>();
        private FormulaInputField formulaField;

        public VpPane(VariablePool vp, Font font, Properties langPro, FormulaInputField formulaField) {
            this.vp = vp;
            this.font = font;
            this.langProperties = langPro;
            this.formulaField = formulaField;
        }

        public void setDisplay() {
            for (String label : vp.getLabelSet()) {
                createHBox(label);
            }
        }

        public void refreshVp() {
            for (String label : vp.getLabelSet()) {
                Set<String> oLabelSet = labelVarMap.keySet();
                if (oLabelSet.contains(label)) {
                    String name = vp.getVariable(label).getName();
                    TextField variableName = labelVarMap.get(label).getNameField();
                    variableName.setText(name);
                    variableName.setPromptText(langProperties.getProperty("Variable_Name"));

                    String value = vp.getVariable(label).getValue().toAnsString();
                    TextField variableValue = labelVarMap.get(label).getValueField();
                    variableValue.setText(value);
                    variableValue.setPromptText(langProperties.getProperty("Ask_For_Input"));
                    continue;
                }
                createHBox(label);
            }
        }

        public Font getFont() {
            return (this.font != null) ? this.font : Font.getDefault();
        }

        private void createHBox(String label) {
            String name = vp.getVariable(label).getName();
            TextField variableName = new TextField(name);
            variableName.setPromptText(langProperties.getProperty("Variable_Name"));
            variableName.setFont(this.getFont());

            String value = "0+0\\i";
            if (vp.getVariable(label).getValue() != null)
                value = vp.getVariable(label).getValue().toAnsString();
            TextField variableValue = new TextField(value);
            variableValue.setFont(this.getFont());
            variableValue.setPromptText(langProperties.getProperty("Ask_For_Input"));

            Button addVariable = new Button(label);
            // this is to avoid parsing variables like x_(x) as mnemonics
            addVariable.setMnemonicParsing(false);
            addVariable.setFont(this.getFont());
            addVariable.setMinSize(50, 20);
            addVariable.setOnAction(e -> {
                String currentText = formulaField.getText();
                formulaField.addStringAt(formulaField.getCaretPos(), label);
            });

            Button removeButton = new Button("DEL");
            removeButton.setFont(this.getFont());
            if (label == "[ANS]")
                removeButton.setDisable(true);
            removeButton.setOnAction(e -> {
                this.getChildren().remove(labelVarMap.get(label));
                vp.remove(label);
                labelVarMap.remove(label);
            });

            VarBox varBox = new VarBox(addVariable, removeButton, variableValue, variableName);
            labelVarMap.put(label, varBox);

            this.getChildren().add(varBox);
        }

        public class VarBox extends HBox {
            private Button varButton;
            private TextField valueField;
            private TextField nameField;
            private Button removeButton;

            public VarBox(Button varButton, Button removeButton, TextField valueField, TextField nameField) {
                this.varButton = varButton;
                this.nameField = nameField;
                this.valueField = valueField;
                this.removeButton = removeButton;
                this.getChildren().addAll(varButton, nameField, valueField, removeButton);
            }

            public Button getVarButton() {
                return varButton;
            }

            public TextField getNameField() {
                return nameField;
            }

            public TextField getValueField() {
                return this.valueField;
            }

            public Button getRemoveButton() {
                return this.removeButton;
            }
        }
    }

    private class HistoryPane extends VBox {
        private Font font;
        private Properties langProperties;
        private FormulaInputField formulaField;

        public HistoryPane(Font font, Properties langPro, FormulaInputField formulaField) {
            this.font = font;
            this.langProperties = langPro;
            this.formulaField = formulaField;
        }

        public Font getFont() {
            return (this.font != null) ? this.font : Font.getDefault();
        }

        public void addBox(String formula, String result, TextField historyField) {
            HBox histBox = new HBox();
            Label equationLabel = new Label(formula + " = " + result);
            equationLabel.setFont(this.getFont());

            Button addHistory = new Button(langProperties.getProperty("Add_History"));
            addHistory.setMnemonicParsing(false);
            addHistory.setFont(this.getFont());
            addHistory.setMinSize(50, 35);
            addHistory.setOnAction(e -> {
                formulaField.clear();
                formulaField.addStringAt(0, formula.toString());
            });

            Button removeButton = new Button("DEL");
            removeButton.setFont(this.getFont());

            histBox.getChildren().addAll(equationLabel, addHistory, removeButton);
            histBox.setAlignment(Pos.CENTER);

            removeButton.setOnAction(e -> {
                this.getChildren().remove(histBox);
            });

            this.getChildren().add(histBox);
            int maxHistory = 16;
            try {
                maxHistory = Integer
                        .valueOf(historyField.getText() == "" ? historyField.getPromptText() : historyField.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (this.getChildren().size() > maxHistory) {
                this.getChildren().remove(0);
            }

        }
    }

}
