package pers.kineticjetice245.jmathcal.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public static String askForInput(String title, String msg, Font font1, Font font2) {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label msgLabel = new Label(msg);
        msgLabel.setFont(font1);
        Button closeButton = new Button("Enter");
        closeButton.setFont(font1);

        TextField textInput = new TextField();
        textInput.setFont(font2);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(msgLabel, textInput, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setMinSize(400, 150);

        String reVal = null;
        class EnterButtonHandler implements EventHandler<ActionEvent> {
            public String value;

            @Override
            public void handle(ActionEvent arg0) {
                value = textInput.getText();
                window.close();
            }

        }
        EnterButtonHandler EBH = new EnterButtonHandler();
        closeButton.setOnAction(EBH);

        Scene scene = new Scene(layout);
        class EnterKeyHandler implements EventHandler<KeyEvent> {
            public String value;

            @Override
            public void handle(KeyEvent key) {
                if (key.getCode() == KeyCode.ENTER) {
                    value = textInput.getText();
                    window.close();
                }
            }

        }
        EnterKeyHandler EKH = new EnterKeyHandler();
        scene.addEventHandler(KeyEvent.KEY_PRESSED, EKH);

        window.setScene(scene);
        window.showAndWait();
        reVal = EBH.value == null ? EKH.value : EBH.value;

        return reVal;
    }
    
    public static void display(String title, String msg) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label msgLabel = new Label(msg);
        Button closeButton = new Button("Close");

        closeButton.setOnAction(e -> {window.close();});

        VBox layout = new VBox(10);
        layout.getChildren().addAll(msgLabel, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setMinSize(200, 100);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
