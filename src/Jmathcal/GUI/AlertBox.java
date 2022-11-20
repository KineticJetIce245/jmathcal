package Jmathcal.GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox extends Application {

    public static String askForInput(String title, String msg) {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label msgLabel = new Label(msg);
        Button closeButton = new Button("Enter");

        TextField textInput = new TextField();


        VBox layout = new VBox(10);
        layout.getChildren().addAll(msgLabel, textInput, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setMinSize(200, 100);

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
        window.setScene(scene);
        window.showAndWait();
        reVal = EBH.value;

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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        String input = askForInput("hi", "hi");
        System.out.println(input);
    }
}
