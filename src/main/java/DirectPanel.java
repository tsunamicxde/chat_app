import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import scala.collection.immutable.Seq;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.jdk.javaapi.CollectionConverters;

import java.util.List;
import java.util.stream.Collectors;

public class DirectPanel {

    public static void startPrivateChat(Stage primaryStage, String name) {
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F5F5F5; -fx-background-color: #F5F5F5;");

        TextArea txtAreaDisplay = new TextArea();
        Main.directArea.put(name, txtAreaDisplay);
        txtAreaDisplay.setEditable(false);
        txtAreaDisplay.setWrapText(true);
        txtAreaDisplay.setStyle("-fx-control-inner-background: #FFFFFF; -fx-text-fill: #2E2E2E; -fx-font-family: 'Arial'; -fx-font-size: 14;");
        scrollPane.setContent(txtAreaDisplay);

        TextField txtInput = new TextField();
        txtInput.setPromptText("New message");
        txtInput.setTooltip(new Tooltip("Write your message."));
        txtInput.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14; -fx-padding: 5;");

        Button btnSend = new Button("Send");
        btnSend.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btnSend.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-padding: 5 15;");
        btnSend.setOnAction(new DirectMessageButton(txtInput, name));

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(txtInput, btnSend);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setHgrow(txtInput, Priority.ALWAYS);

        vBox.getChildren().addAll(scrollPane, hBox);
        vBox.setVgrow(scrollPane, Priority.ALWAYS);


        Scene scene = new Scene(vBox, 450, 500);
        primaryStage.setTitle("Direct");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadDirectMessages(name, txtAreaDisplay);
    }

    private static void loadDirectMessages(String recipient, TextArea txtAreaDisplay) {
        Future<Seq<DirectMessage>> messagesFuture = Main.messageRepository.getDirectMessages();

        messagesFuture.onComplete(result -> {
            if (result.isSuccess()) {
                List<DirectMessage> messages = CollectionConverters.asJava(result.get());
                String filteredMessages = messages.stream()
                        .filter(msg -> msg.recipient().equals(recipient) || msg.sender().equals(recipient))
                        .map(msg -> "[Direct]: [" + msg.sender() + "]: " + msg.message())
                        .collect(Collectors.joining("\n"));

                Platform.runLater(() -> txtAreaDisplay.setText(filteredMessages));
                Platform.runLater(() -> txtAreaDisplay.appendText("\n"));
            } else {
                System.out.println("Failed to load private messages: " + result.failed().get());
            }
            return null;
        }, ExecutionContext.global());
    }
}