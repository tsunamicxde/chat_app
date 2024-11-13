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

import javafx.application.Platform;
import scala.collection.immutable.Seq;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.jdk.javaapi.CollectionConverters;

import java.util.List;

public class ChatPanel {

    static TextArea txtAreaDisplay;

    public static void startChat(Stage primaryStage, MessageRepository messageRepository) {
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F0F4F8; -fx-background-color: #F0F4F8;");

        txtAreaDisplay = new TextArea();
        txtAreaDisplay.setEditable(false);
        txtAreaDisplay.setWrapText(true);
        txtAreaDisplay.setStyle("-fx-control-inner-background: #F9FAFB; -fx-text-fill: #333333; -fx-font-family: 'Arial'; -fx-font-size: 14;");
        scrollPane.setContent(txtAreaDisplay);

        TextField txtInput = new TextField();
        txtInput.setPromptText("New message");
        txtInput.setTooltip(new Tooltip("Write your message."));
        txtInput.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14;");

        Button btnSend = new Button("Send");
        btnSend.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btnSend.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;");
        btnSend.setOnAction(new SendMessageButton(txtInput));

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(txtInput, btnSend);
        hBox.setHgrow(txtInput, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().addAll(scrollPane, hBox);
        vBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(vBox, 450, 500);
        primaryStage.setTitle("Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static void loadMessages(MessageRepository messageRepository) {
        Future<Seq<ChatMessage>> messagesFuture = messageRepository.getMessages();

        messagesFuture.onComplete(messages -> {
            if (messages.isSuccess()) {
                Platform.runLater(() -> {
                    List<ChatMessage> chatMessages = CollectionConverters.asJava(messages.get());
                    for (ChatMessage message : chatMessages) {
                        txtAreaDisplay.appendText("[" + message.username() + "]: " + message.message() + "\n");
                    }
                });
            } else {
                Throwable throwable = messages.failed().get();
                System.err.println("Failed to load messages: " + throwable.getMessage());
            }
            return null;
        }, ExecutionContext.global());
    }
}
