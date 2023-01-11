package Jmathcal.GUI;

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
import Jmathcal.Plotter.PlotterPlane;
import Jmathcal.Plotter.PointGroup;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Calculator extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties launchInfo = getLaunchInfo();
        Properties langDisplay = getLangFile(new File(launchInfo.get("langFilePath").toString()));

        Font tsanger = getFont(new File(launchInfo.getProperty("TsangerYuMo_WO3Path")), 20);
        Font tsanger18 = getFont(new File(launchInfo.getProperty("TsangerYuMo_WO3Path")), 18);
        Font smiley = getFont(new File(launchInfo.getProperty("SmileySansPath")), 25);
        Font smiley18 = getFont(new File(launchInfo.getProperty("SmileySansPath")), 18);
        Font smiley16 = getFont(new File(launchInfo.getProperty("SmileySansPath")), 16);
        Font latinMath = getFont(new File(launchInfo.getProperty("latinMathPath")), 25);

        Scene mainScene;
        GridPane calSceneLayout = new GridPane();
        // Rounding and Calculating Precision
        GridPane roundSettingPane = new GridPane();
        TextField roundingField = new TextField();
        roundingField.setFont(smiley18);
        roundingField.setPromptText("12");
        roundingField.setMinSize(30, 20);
        roundingField.setMaxWidth(45);
        TextField calField = new TextField();
        calField.setFont(smiley18);
        calField.setPromptText("16");
        calField.setMinSize(30, 20);
        calField.setMaxWidth(45);
        TextField historyField = new TextField();
        historyField.setFont(smiley18);
        historyField.setPromptText("16");
        historyField.setMinSize(30, 20);
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
        roundSettingPane.setAlignment(Pos.CENTER);
        roundSettingPane.setHgap(2);

        // Formula Input and answer
        GridPane inputAndAnsPane = new GridPane();
        FormulaInputField formulaInput = new FormulaInputField();
        formulaInput.setPromptText(langDisplay.getProperty("Input_Formula"));
        formulaInput.setPrefSize(1080, 40);
        formulaInput.setFont(smiley);
        formulaInput.setAlignment(Pos.CENTER);
        formulaInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                formulaInput.setCaretPos(formulaInput.getCaretPosition());
            }
        });

        // Variable pool
        ScrollPane vScrollPane = new ScrollPane();
        VariablePool vp = new VariablePool();
        VariablePool.Variable ANSvariable = vp.new Variable("[ANS]");
        ANSvariable.setName(langDisplay.getProperty("Variable_Answer"));
        ANSvariable.setValue(new ExprNumber("0+0i"));
        VpPane vpPane = new VpPane(vp, smiley18, langDisplay, formulaInput);
        vpPane.setDisplay();
        vScrollPane.setContent(vpPane);
        vScrollPane.setPrefSize(350, 300);

        // History
        ScrollPane hScrollPane = new ScrollPane();
        HistoryPane historyPane = new HistoryPane(smiley18, langDisplay, formulaInput);
        historyPane.setPrefSize(900, 100);
        hScrollPane.setContent(historyPane);
        GridPane.setConstraints(hScrollPane, 0, 5, 6, 1);

        // Answer Space
        ScrollPane answerSpace = new ScrollPane();
        Label answerLabel = new Label();
        answerLabel.setText("");
        answerLabel.setFont(smiley);
        answerLabel.setAlignment(Pos.CENTER);
        answerSpace.setPrefSize(804, 40);
        answerSpace.setContent(answerLabel);
        // Enter Button
        Button enterButton = new Button(langDisplay.getProperty("Send_Input"));
        enterButton.setFont(tsanger);
        enterButton.setPrefSize(90, 40);
        interface CalInitiator {
            public IOBridge getBridge();

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
            public IOBridge getBridge() {
                return this.ioBridge;
            }

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
                    answerLabel.setText(langDisplay.getProperty("Math_Error"));
                } catch (ExprSyntaxErrorException error) {
                    answerLabel.setText(
                            langDisplay.getProperty("Syntax_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                } catch (VariableLabelOccupiedException error) {
                    answerLabel.setText(
                            langDisplay.getProperty("Variable_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                } catch (NoSuchElementException error) {
                    answerLabel.setText(
                            langDisplay.getProperty("Parser_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                } catch (NumberFormatException error) {
                    answerLabel.setText(
                            langDisplay.getProperty("Number_Input_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                } catch (Exception error) {
                    answerLabel.setText(langDisplay.getProperty("Unknown_Error") + error.getMessage());
                }
            }
        };

        enterButton.setOnAction(action -> {
            calInitiator.initiate();
        });
        // AC
        Button allClearButton = new Button(langDisplay.getProperty("AC"));
        allClearButton.setFont(tsanger);
        allClearButton.setOnAction(e -> {
            formulaInput.clear();
        });
        allClearButton.setPrefSize(90, 40);
        // Copy ANS button
        Button copyANSButton = new Button(langDisplay.getProperty("Copy_ANS"));
        copyANSButton.setFont(tsanger);
        copyANSButton.setPrefSize(90, 40);
        copyANSButton.setOnAction(e -> {
            StringSelection selection = new StringSelection(ANSvariable.getValue().toAnsString());
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });
        GridPane.setConstraints(formulaInput, 0, 0, 4, 1);
        GridPane.setConstraints(answerSpace, 0, 1, 1, 1);
        GridPane.setConstraints(copyANSButton, 1, 1);
        GridPane.setConstraints(allClearButton, 2, 1);
        GridPane.setConstraints(enterButton, 3, 1);
        inputAndAnsPane.setVgap(2);
        inputAndAnsPane.setHgap(2);
        inputAndAnsPane.getChildren().addAll(formulaInput, answerSpace, copyANSButton, allClearButton, enterButton);

        TabPane inputPane = new TabPane();
        inputPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        inputPane.setPrefSize(728, 200);
        Tab funcGridTab1 = new Tab(langDisplay.getProperty("Calculator_InputTab_Tab1"));
        GridPane funcGridPane1 = new GridPane();
        // Input buttons
        funcGridPane1.setVgap(2);
        funcGridPane1.setHgap(2);
        Button num7 = getButtonFromFac("7", tsanger, 5, 0, formulaInput);
        Button num8 = getButtonFromFac("8", tsanger, 6, 0, formulaInput);
        Button num9 = getButtonFromFac("9", tsanger, 7, 0, formulaInput);
        Button num4 = getButtonFromFac("4", tsanger, 5, 1, formulaInput);
        Button num5 = getButtonFromFac("5", tsanger, 6, 1, formulaInput);
        Button num6 = getButtonFromFac("6", tsanger, 7, 1, formulaInput);
        Button num1 = getButtonFromFac("1", tsanger, 5, 2, formulaInput);
        Button num2 = getButtonFromFac("2", tsanger, 6, 2, formulaInput);
        Button num3 = getButtonFromFac("3", tsanger, 7, 2, formulaInput);
        Button num0 = getButtonFromFac("0", tsanger, 6, 3, formulaInput);
        Button pointButton = getButtonFromFac(".", tsanger, 5, 3, formulaInput);
        Button equalButton = new Button("=");
        equalButton.setFont(tsanger);
        equalButton.setPrefSize(66, 50);
        GridPane.setConstraints(equalButton, 7, 4);
        equalButton.setOnAction(e -> {
            calInitiator.initiate();
        });
        Button backspaceButton = new Button("DEL");
        backspaceButton.setFont(tsanger);
        backspaceButton.setMinSize(50, 50);
        GridPane.setConstraints(backspaceButton, 8, 4);
        backspaceButton.setOnAction(e -> {
            int carpetPos = formulaInput.getCaretPos();
            if (!(carpetPos == 0)) {
                formulaInput.deleteCharAt(carpetPos - 1);
            } else {
                formulaInput.deleteCharAt(0);
            }
        });
        Button plusButton = getButtonFromFac("+", tsanger, 8, 0, formulaInput);
        Button minButton = getButtonFromFac("-", tsanger, 8, 1, formulaInput);
        Button mulButton = getButtonFromFac("*", tsanger, 8, 2, formulaInput);
        Button divButton = getButtonFromFac("/", tsanger, 8, 3, formulaInput);
        Button expoButton = getButtonFromFac("^", tsanger, 7, 3, formulaInput);
        Button leftPaButton = getButtonFromFac("(", tsanger, 5, 4, formulaInput);
        Button rightPaButton = getButtonFromFac(")", tsanger, 6, 4, formulaInput);
        Button tenExpButton = getButtonFromFac("10^x", "10^()", tsanger, 2, 4, 94, 50, formulaInput);
        Button toLeftButton = new Button("<--");
        toLeftButton.setFont(tsanger);
        toLeftButton.setMinSize(94, 50);
        GridPane.setConstraints(toLeftButton, 3, 4);
        GridPane.setHalignment(toLeftButton, HPos.CENTER);
        toLeftButton.setOnAction(e -> {
            int currentPlace = formulaInput.getCaretPos();
            formulaInput.setCaretPosFocused(currentPlace == 0 ? formulaInput.length() : currentPlace - 1);
        });
        Button toRightButton = new Button("-->");
        toRightButton.setFont(tsanger);
        toRightButton.setMinSize(94, 50);
        GridPane.setConstraints(toRightButton, 4, 4);
        GridPane.setHalignment(toRightButton, HPos.CENTER);
        toRightButton.setOnAction(e -> {
            int currentPlace = formulaInput.getCaretPos();
            formulaInput.setCaretPosFocused(currentPlace == formulaInput.length() ? 0 : currentPlace + 1);
        });
        Button sinButton = getButtonFromFac("sin", tsanger, 2, 0, 94, 50, formulaInput);
        Button cosButton = getButtonFromFac("cos", tsanger, 3, 0, 94, 50, formulaInput);
        Button tanButton = getButtonFromFac("tan", tsanger, 4, 0, 94, 50, formulaInput);
        Button secButton = getButtonFromFac("csc", tsanger, 2, 1, 94, 50, formulaInput);
        Button cscButton = getButtonFromFac("sec", tsanger, 3, 1, 94, 50, formulaInput);
        Button cotButton = getButtonFromFac("cot", tsanger, 4, 1, 94, 50, formulaInput);
        Button arcsinButton = getButtonFromFac("arcsin", tsanger, 2, 2, 94, 50, formulaInput);
        Button arccosButton = getButtonFromFac("arccos", tsanger, 3, 2, 94, 50, formulaInput);
        Button arctanButton = getButtonFromFac("arctan", tsanger, 4, 2, 94, 50, formulaInput);
        Button arccscButton = getButtonFromFac("arccsc", tsanger, 2, 3, 94, 50, formulaInput);
        Button arcsecButton = getButtonFromFac("arcsec", tsanger, 3, 3, 94, 50, formulaInput);
        Button arccotButton = getButtonFromFac("arccot", tsanger, 4, 3, 94, 50, formulaInput);
        Button sqrtButton = getButtonFromFac("sqrt", "sqrt", tsanger, 1, 0, formulaInput);
        Button cbrtButton = getButtonFromFac("cbrt", "()^(1/3)", tsanger, 1, 1, formulaInput);
        Button expButton = getButtonFromFac("e^x", (char) 92 + "e^()", tsanger, 1, 2, formulaInput);
        Button piButton = getButtonFromFac("pi", (char) 92 + "pi", tsanger, 1, 3, formulaInput);
        Button iButton = getButtonFromFac("i", (char) 92 + "i", tsanger, 1, 4, formulaInput);
        Button absButton = getButtonFromFac("abs", "abs", tsanger, 0, 0, 94, 50, formulaInput);
        Button sgnButton = getButtonFromFac("sgn", "sgn", tsanger, 0, 1, 94, 50, formulaInput);
        Button facButton = getButtonFromFac("x!", "pro(n, 1, x, n)", tsanger, 0, 2, 94, 50, formulaInput);
        Button degButton = getButtonFromFac("deg", tsanger, 0, 3, 94, 50, formulaInput);
        Button toDegButton = getButtonFromFac("todeg", tsanger, 0, 4, 94, 50, formulaInput);

        funcGridPane1.getChildren().addAll(
                equalButton, backspaceButton,
                num1, num2, num3, num4, num5, num6, num7, num8, num9, num0, pointButton,
                plusButton, minButton, mulButton, divButton, expoButton,
                leftPaButton, rightPaButton, toLeftButton, toRightButton, tenExpButton,
                expButton, sqrtButton, cbrtButton, piButton, iButton,
                sinButton, cosButton, tanButton, arcsinButton, arccosButton, arctanButton,
                cotButton, cscButton, secButton, arccscButton, arcsecButton, arccotButton,
                absButton, sgnButton, facButton, degButton, toDegButton);
        funcGridPane1.setAlignment(Pos.CENTER_LEFT);
        funcGridTab1.setContent(funcGridPane1);
        inputPane.getTabs().add(funcGridTab1);

        // Input buttons pane 2
        Tab funcGridTab2 = new Tab(langDisplay.getProperty("Calculator_InputTab_Tab2"));
        GridPane funcGridPane2 = new GridPane();
        funcGridPane2.setVgap(2);
        funcGridPane2.setHgap(2);
        Button sinhButton = getButtonFromFac("sinh", tsanger, 0, 0, 94, 50, formulaInput);
        Button coshButton = getButtonFromFac("cosh", tsanger, 1, 0, 94, 50, formulaInput);
        Button tanhButton = getButtonFromFac("tanh", tsanger, 2, 0, 94, 50, formulaInput);
        Button arsinhButton = getButtonFromFac("arsinh", tsanger, 0, 1, 94, 50, formulaInput);
        Button arcoshButton = getButtonFromFac("arcosh", tsanger, 1, 1, 94, 50, formulaInput);
        Button artanhButton = getButtonFromFac("artanh", tsanger, 2, 1, 94, 50, formulaInput);

        Button lnButton = getButtonFromFac("ln", tsanger, 0, 2, 94, 50, formulaInput);
        Button log10Button = getButtonFromFac("log", "log(10, )", tsanger, 1, 2, 94, 50, formulaInput);
        Button logButton = getButtonFromFac("log", "log(, )", tsanger, 2, 2, 94, 50, formulaInput);
        Button sumButton = getButtonFromFac("sum", "sum(n, 0, 1, n+1)", tsanger, 0, 3, 94, 50, formulaInput);
        Button proButton = getButtonFromFac("pro", "pro(n, 1, 2, n+1)", tsanger, 1, 3, 94, 50, formulaInput);
        Button ranButton = getButtonFromFac("Ran", "ran(, )", tsanger, 2, 3, 94, 50, formulaInput);

        Button polrButton = getButtonFromFac("PolR", "polr(, )", tsanger, 0, 4, 94, 50, formulaInput);
        Button poltButton = getButtonFromFac("PolT", "polt(, )", tsanger, 1, 4, 94, 50, formulaInput);
        Button ranNumButton = getButtonFromFac("Ran#", (char) 92 + "ran", tsanger, 2, 4, 94, 50, formulaInput);
        Button quadPosButton = getButtonFromFac("(-b+sqrt(b^2-4ac))/(2a)", tsanger, 3, 1, 324, 50, formulaInput);
        Button quadNegButton = getButtonFromFac("(-b-sqrt(b^2-4ac))/(2a)", tsanger, 3, 2, 324, 50, formulaInput);
        Button cosR1Button = getButtonFromFac("sqrt(a^2+b^2-2abcos[theta])", tsanger, 3, 1, 324, 50, formulaInput);
        Button cosR2gButton = getButtonFromFac("arccos(a^2+b^2)/(2ab)", tsanger, 3, 2, 324, 50, formulaInput);
        GridPane.setConstraints(quadPosButton, 3, 1, 4, 1);
        GridPane.setConstraints(quadNegButton, 3, 2, 4, 1);
        GridPane.setConstraints(cosR1Button, 3, 3, 4, 1);
        GridPane.setConstraints(cosR2gButton, 3, 4, 4, 1);
        Button toGradButton = getButtonFromFac("tograd", tsanger, 3, 0, 94, 50, formulaInput);
        Button gradButton = getButtonFromFac("grad", tsanger, 4, 0, 94, 50, formulaInput);
        Button gButton = getButtonFromFac("g", "\\g", tsanger, 5, 0, 81, 50, formulaInput);
        Button GButton = getButtonFromFac("G", "\\G", tsanger, 6, 0, 81, 50, formulaInput);
        Button NaButton = getButtonFromFac("Na", "\\Na", tsanger, 8, 0, 81, 50, formulaInput);
        GridPane.setConstraints(GButton, 6, 0, 2, 1);
        Button phiButton = getButtonFromFac("phi", "\\phi", tsanger, 7, 1, 113, 50, formulaInput);
        Button leftSPaButton = getButtonFromFac("[", tsanger, 7, 1, 113, 50, formulaInput);
        Button commaButton = getButtonFromFac(",", tsanger, 7, 1, 113, 50, formulaInput);
        Button rightSPaButton = getButtonFromFac("]", tsanger, 7, 1, 113, 50, formulaInput);
        GridPane.setConstraints(phiButton, 7, 1, 2, 1);
        GridPane.setConstraints(leftSPaButton, 7, 2, 2, 1);
        GridPane.setConstraints(commaButton, 7, 3, 2, 1);
        GridPane.setConstraints(rightSPaButton, 7, 4, 2, 1);

        funcGridPane2.getChildren().addAll(
                sinhButton, coshButton, tanhButton, arsinhButton, arcoshButton, artanhButton,
                lnButton, logButton, sumButton, proButton, log10Button, ranButton,
                polrButton, poltButton, ranNumButton,
                quadPosButton, quadNegButton, cosR1Button, cosR2gButton,
                toGradButton, gradButton, gButton, GButton, NaButton,
                phiButton, leftSPaButton, commaButton, rightSPaButton);
        funcGridTab2.setContent(funcGridPane2);
        funcGridPane2.setAlignment(Pos.CENTER_LEFT);
        inputPane.getTabs().add(funcGridTab2);

        GridPane.setConstraints(inputAndAnsPane, 0, 2, 2, 1);
        GridPane.setConstraints(inputPane, 1, 3, 1, 1);
        GridPane.setConstraints(vScrollPane, 0, 3, 1, 1);
        GridPane.setConstraints(hScrollPane, 0, 0, 2, 1);
        GridPane.setConstraints(roundSettingPane, 0, 5, 2, 1);
        calSceneLayout.setHgap(2);
        calSceneLayout.setVgap(2);
        calSceneLayout.setPadding(new Insets(0, 5, 5, 5));
        calSceneLayout.getChildren().addAll(inputAndAnsPane, inputPane, vScrollPane, hScrollPane, roundSettingPane);
        calSceneLayout.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.ENTER) {
                calInitiator.initiate();
                key.consume();
            }
        });

        VBox graphPane = new VBox();
        HBox planeSetting = new HBox();
        TextField lengthXField = new TextField();
        lengthXField.setFont(smiley16);
        lengthXField.setPromptText("10");
        lengthXField.setMinSize(30, 20);
        lengthXField.setMaxWidth(45);
        TextField lengthYField = new TextField();
        lengthYField.setFont(smiley16);
        lengthYField.setPromptText("10");
        lengthYField.setMinSize(30, 20);
        lengthYField.setMaxWidth(45);
        TextField oriXField = new TextField();
        oriXField.setFont(smiley16);
        oriXField.setPromptText("-5");
        oriXField.setMinSize(30, 20);
        oriXField.setMaxWidth(45);
        TextField oriYField = new TextField();
        oriYField.setFont(smiley16);
        oriYField.setPromptText("-5");
        oriYField.setMinSize(30, 20);
        oriYField.setMaxWidth(45);
        TextField planeSizeXField = new TextField();
        planeSizeXField.setFont(smiley16);
        planeSizeXField.setPromptText("1080");
        planeSizeXField.setMinSize(50, 20);
        planeSizeXField.setMaxWidth(45);
        TextField planeSizeYField = new TextField();
        planeSizeYField.setFont(smiley16);
        planeSizeYField.setPromptText("720");
        planeSizeYField.setMinSize(50, 20);
        planeSizeYField.setMaxWidth(45);
        TextField resolutionXField = new TextField();
        resolutionXField.setFont(smiley16);
        resolutionXField.setPromptText("100");
        resolutionXField.setMinSize(30, 20);
        resolutionXField.setMaxWidth(45);
        TextField resolutionYField = new TextField();
        resolutionYField.setFont(smiley16);
        resolutionYField.setPromptText("100");
        resolutionYField.setMinSize(30, 20);
        resolutionYField.setMaxWidth(45);
        TextField depthField = new TextField();
        depthField.setFont(smiley16);
        depthField.setPromptText("4");
        depthField.setMinSize(30, 20);
        depthField.setMaxWidth(45);
        Label lengthHelpLabel = new Label(langDisplay.getProperty("Plane_Length_Setting"));
        lengthHelpLabel.setFont(tsanger18);
        Label oriHelpLabel = new Label(langDisplay.getProperty("Plane_Origin_Setting"));
        oriHelpLabel.setFont(tsanger18);
        Label resolutionHelpLabel = new Label(langDisplay.getProperty("Plane_Resolution_Setting"));
        resolutionHelpLabel.setFont(tsanger18);
        Label depthHelpLabel = new Label(langDisplay.getProperty("Plane_Depth_Setting"));
        depthHelpLabel.setFont(tsanger18);
        Label planeSizeHelpLabel = new Label(langDisplay.getProperty("Plane_Size_Setting"));
        planeSizeHelpLabel.setFont(tsanger18);
        planeSetting.getChildren().addAll(
                lengthHelpLabel, lengthXField, lengthYField,
                oriHelpLabel, oriXField, oriYField,
                resolutionHelpLabel, resolutionXField, resolutionYField,
                planeSizeHelpLabel, planeSizeXField, planeSizeYField,
                depthHelpLabel, depthField);
        planeSetting.setAlignment(Pos.CENTER);
        planeSetting.setSpacing(2);

        // graph Pane
        boolean[] gridSetting = { true, true, true };
        ScrollPane graphSCPane = new ScrollPane();
        FuncPane funcPane = new FuncPane(smiley18, smiley, langDisplay);
        graphPane.getChildren().add(funcPane);
        graphPane.getChildren().add(planeSetting);
        graphPane.setAlignment(Pos.CENTER);
        graphSCPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        graphSCPane.setContent(graphPane);
        interface PlanePlotter {
            void draw();
        }
        PlanePlotter plotter = new PlanePlotter() {
            @Override
            public void draw() {
                VariablePool varPool = new VariablePool();
                String lenXStr = lengthXField.getText();
                String lenYStr = lengthYField.getText();
                String oriXStr = oriXField.getText();
                String oriYStr = oriYField.getText();
                String planeSizeXStr = planeSizeXField.getText();
                String planeSizeYStr = planeSizeYField.getText();
                String resolutionXStr = resolutionXField.getText();
                String resolutionYStr = resolutionYField.getText();
                String depthStr = depthField.getText();
                double[] lenSetting = new double[2];
                double[] oriSetting = new double[2];
                int[] planeSetting = new int[2];
                int[] resolutionSetting = new int[2];
                int depth;

                try {
                    lenSetting[0] = Double.valueOf(lenXStr);
                } catch (NumberFormatException e) {
                    lenSetting[0] = Double.valueOf(lengthXField.getPromptText());
                }
                try {
                    lenSetting[1] = Double.valueOf(lenYStr);
                } catch (NumberFormatException e) {
                    lenSetting[1] = Double.valueOf(lengthYField.getPromptText());
                }
                try {
                    oriSetting[0] = Double.valueOf(oriXStr);
                } catch (NumberFormatException e) {
                    oriSetting[0] = Double.valueOf(oriXField.getPromptText());
                }
                try {
                    oriSetting[1] = Double.valueOf(oriYStr);
                } catch (NumberFormatException e) {
                    oriSetting[1] = Double.valueOf(oriYField.getPromptText());
                }
                try {
                    planeSetting[0] = Integer.valueOf(planeSizeXStr);
                } catch (NumberFormatException e) {
                    planeSetting[0] = Integer.valueOf(planeSizeXField.getPromptText());
                }
                try {
                    planeSetting[1] = Integer.valueOf(planeSizeYStr);
                } catch (NumberFormatException e) {
                    planeSetting[1] = Integer.valueOf(planeSizeYField.getPromptText());
                }
                try {
                    resolutionSetting[0] = Integer.valueOf(resolutionXStr);
                } catch (NumberFormatException e) {
                    resolutionSetting[0] = Integer.valueOf(resolutionXField.getPromptText());
                }
                try {
                    resolutionSetting[1] = Integer.valueOf(resolutionYStr);
                } catch (NumberFormatException e) {
                    resolutionSetting[1] = Integer.valueOf(resolutionYField.getPromptText());
                }
                try {
                    depth = Integer.valueOf(depthStr);
                } catch (NumberFormatException e) {
                    depth = Integer.valueOf(depthField.getPromptText());
                }

                PlotterPlane plotterPlane = new PlotterPlane(lenSetting, oriSetting, planeSetting, resolutionSetting,
                        depth);
                PointGroup pg = new PointGroup();
                funcPane.pg = pg;
                funcPane.setPlane(plotterPlane, gridSetting);

                for (Node i : funcPane.getChildren()) {
                    if (i instanceof FuncPane.FuncBox) {
                        FuncPane.FuncBox func = (FuncPane.FuncBox) i;
                        String funcStr = func.getText();
                        String leftStr, rightStr;
                        try {
                            leftStr = funcStr.substring(0, funcStr.indexOf("="));
                            rightStr = funcStr.substring(funcStr.indexOf("=") + 1, funcStr.length());
                        } catch (Exception e) {
                            func.addMes(langDisplay.getProperty("Syntax_Error_Graph"));
                            continue;
                        }

                        try {
                            Expressions leftExpr = Expressions.parseFromFlattenExpr(leftStr, varPool,
                                    calInitiator.getBridge());
                            Expressions rightExpr = Expressions.parseFromFlattenExpr(rightStr, varPool,
                                    calInitiator.getBridge());
                            Expressions expr = Expressions.subtractExpr(leftExpr, rightExpr);
                            Color lineColor = Color.web("#000000");
                            try {
                                lineColor = Color.web(func.getColorInput());
                            } catch (Exception e) {
                                func.addMes(langDisplay.getProperty("Color_Error"));
                            }
                            funcPane.addFunc(expr, lineColor);
                        } catch (ArithmeticException error) {
                            func.addMes(langDisplay.getProperty("Math_Error"));
                        } catch (ExprSyntaxErrorException error) {
                            func.addMes(langDisplay.getProperty("Syntax_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                        } catch (VariableLabelOccupiedException error) {
                            func.addMes(langDisplay.getProperty("Variable_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                        } catch (NoSuchElementException error) {
                            func.addMes(langDisplay.getProperty("Parser_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                        } catch (NumberFormatException error) {
                            func.addMes(langDisplay.getProperty("Number_Input_Error")
                                    + (error.getMessage() == null ? langDisplay.getProperty("Unknown_Error")
                                            : error.getMessage()));
                        } catch (Exception error) {
                            func.addMes(langDisplay.getProperty("Unknown_Error") + error.getMessage());
                        }
                    }
                }
                Stage window = new Stage();
                window.setTitle(langDisplay.getProperty("Calculator_Graphics"));
                VBox layout = new VBox();
                Scene sc = new Scene(layout, planeSetting[0], planeSetting[1]);
                layout.getChildren().add(pg.getPlane());
                window.setScene(sc);
                window.show();
            }
        };
        funcPane.getEnterButton().setOnAction(e -> {
            plotter.draw();
        });
        graphPane.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.ENTER) {
                plotter.draw();
                key.consume();
            }
        });

        VBox menuBox = new VBox();
        Button buttonToCalMenu = new Button();
        buttonToCalMenu.setPrefSize(90, 90);
        Image calculatorIma = new Image(launchInfo.getProperty("calculator_menu"));
        ImageView calculatorImaView = new ImageView(calculatorIma);
        calculatorImaView.setFitWidth(65);
        calculatorImaView.setPreserveRatio(true);
        buttonToCalMenu.setGraphic(calculatorImaView);

        Button buttonToGphMenu = new Button();
        buttonToGphMenu.setPrefSize(90, 90);
        Image graphIma = new Image(launchInfo.getProperty("graphic_menu"));
        ImageView graphImaView = new ImageView(graphIma);
        graphImaView.setFitHeight(60);
        graphImaView.setPreserveRatio(true);
        buttonToGphMenu.setGraphic(graphImaView);

        Button buttonToBaseN = new Button();
        buttonToBaseN.setPrefSize(90, 90);
        menuBox.getChildren().addAll(buttonToCalMenu, buttonToGphMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(calSceneLayout);
        borderPane.setLeft(menuBox);

        buttonToBaseN.setOnAction(e -> {
            borderPane.setCenter(graphSCPane);
        });
        buttonToGphMenu.setOnAction(e -> {
            borderPane.setCenter(graphSCPane);
        });
        buttonToCalMenu.setOnAction(e -> {
            borderPane.setCenter(calSceneLayout);
        });
        mainScene = new Scene(borderPane, 1180, 720);
        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle(langDisplay.getProperty("Calculator_Window_Title"));
        primaryStage.show();
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
                        .valueOf(historyField.getText().equals("") ? historyField.getPromptText() : historyField.getText());
            } catch (Exception e) {
                e.printStackTrace();
                maxHistory = Integer.valueOf(historyField.getPromptText());
            }
            while (this.getChildren().size() > maxHistory) {
                this.getChildren().remove(0);
            }

        }
    }

    private class FuncPane extends VBox {
        private PointGroup pg;
        private Font font;
        private Font buttonFont;
        private Properties langProperties;
        private Button enterButton;

        public FuncPane(Font buttonFont, Font font, Properties langProperties) {
            this.font = font;
            this.buttonFont = buttonFont;
            this.setAlignment(Pos.TOP_CENTER);
            this.setPadding(new Insets(5, 0, 0, 5));
            HBox box = new HBox();
            this.enterButton = new Button(langProperties.getProperty("Draw"));
            enterButton.setFont(buttonFont);
            enterButton.setPrefSize(400, 40);
            Button addNewFuncBox = new Button("+");
            addNewFuncBox.setFont(buttonFont);
            addNewFuncBox.setPrefSize(665, 40);
            addNewFuncBox.setOnAction(e -> {
                this.addFuncBox();
            });
            box.getChildren().addAll(addNewFuncBox, enterButton);
            this.getChildren().add(box);
            this.langProperties = langProperties;
        }

        public Button getEnterButton() {
            return this.enterButton;
        }

        public void setPlane(PlotterPlane plane, boolean[] gridSetting) {
            pg.setUpGrid(plane, gridSetting[0], gridSetting[1], gridSetting[2]);
        }

        public void addFuncBox() {
            Button delButton = new Button("DEL");
            delButton.setPrefSize(50, 40);
            delButton.setFont(buttonFont);
            FuncBox fb = new FuncBox(font, delButton, langProperties);
            this.getChildren().add(fb);
            delButton.setOnAction(e -> {
                this.getChildren().remove(fb);
            });
        }

        public void addFunc(Expressions expr, Color color) {
            pg.addFunc(expr, color);
        }

        public FuncPane getOuter() {
            return this;
        }

        private class FuncBox extends VBox {
            private TextField input;
            private TextField colorInput;
            private Label status;

            public FuncBox(Font font, Button delButton, Properties langProperties) {

                this.setAlignment(Pos.CENTER);
                HBox box = new HBox();
                this.colorInput = new TextField();
                colorInput.setPromptText(langProperties.getProperty("Input_Color"));
                colorInput.setPrefSize(90, 48);
                colorInput.setFont(getOuter().buttonFont);
                colorInput.setAlignment(Pos.CENTER);
                this.input = new TextField();
                input.setPromptText(langProperties.getProperty("Input_Formula"));
                input.setMinSize(975, 40);
                input.setFont(font);
                input.setAlignment(Pos.CENTER);
                box.getChildren().add(input);
                box.getChildren().add(colorInput);

                ScrollPane pane = new ScrollPane();
                Label answerLabel = new Label();
                answerLabel.setText("");
                answerLabel.setFont(font);
                answerLabel.setAlignment(Pos.CENTER);
                answerLabel.setMaxSize(1020, 40);
                pane.setContent(answerLabel);
                this.status = answerLabel;

                HBox box1 = new HBox();
                box1.setAlignment(Pos.CENTER);
                box1.getChildren().add(delButton);
                box1.getChildren().add(answerLabel);

                this.getChildren().add(box);
                this.getChildren().add(box1);
            }

            public String getText() {
                return this.input.getText();
            }

            public void addMes(String msg) {
                this.status.setText(msg);
            }

            public String getColorInput() {
                return this.colorInput.getText().equals("") ? "#000000" : this.colorInput.getText();
            }
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
            variableName.setPrefSize(75, 20);

            String value = "0+0\\i";
            if (vp.getVariable(label).getValue() != null)
                value = vp.getVariable(label).getValue().toAnsString();
            TextField variableValue = new TextField(value);
            variableValue.setPromptText(langProperties.getProperty("Ask_For_Input"));
            variableValue.setFont(this.getFont());
            variableValue.setPrefSize(143, 20);

            Button addVariable = new Button(label);
            // this is to avoid parsing variables like x_(x) as mnemonics
            addVariable.setMnemonicParsing(false);
            addVariable.setFont(this.getFont());
            addVariable.setPrefSize(75, 20);
            addVariable.setOnAction(e -> {
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
            varBox.setSpacing(2);
            labelVarMap.put(label, varBox);

            this.getChildren().add(varBox);
        }

        public class VarBox extends HBox {
            private TextField valueField;
            private TextField nameField;

            public VarBox(Button varButton, Button removeButton, TextField valueField, TextField nameField) {
                this.nameField = nameField;
                this.valueField = valueField;
                this.getChildren().addAll(varButton, nameField, valueField, removeButton);
            }

            public TextField getNameField() {
                return nameField;
            }

            public TextField getValueField() {
                return this.valueField;
            }
        }
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

    private static Button getButtonFromFac(String sym, Font font, int x, int y, FormulaInputField relatedTF) {
        return getButtonFromFac(sym, sym, font, x, y, relatedTF);
    }

    private static Button getButtonFromFac(String sym, String textInput, Font font, int x, int y,
            FormulaInputField relatedTF) {
        return getButtonFromFac(sym, textInput, font, x, y, 66.4, 50, relatedTF);
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
                if (textInput.contains("x")) {
                    int xPos = addPosition + textInput.indexOf("x");
                    relatedTF.setCaretPosFocused(xPos + 1);
                    relatedTF.selectRange(xPos, xPos + 1);
                }
                if (textInput == "log(10,)") {
                    relatedTF.setCaretPosFocused(addPosition + textInput.indexOf(",") + 1);
                }
            }
        });

        return reButton;
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

}