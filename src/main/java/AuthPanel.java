import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AuthPanel {

    public static void startAuth(Stage primaryStage) {
        primaryStage.setTitle("Auth");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome to the Chat");
        scenetitle.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        scenetitle.setFill(Color.NAVY);
        GridPane.setHalignment(scenetitle, HPos.CENTER);
        grid.add(scenetitle, 0, 0, 2, 1);

        Label labelUserName = new Label("Username:");
        labelUserName.setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
        labelUserName.setTextFill(Color.DIMGRAY);
        grid.add(labelUserName, 0, 1);

        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter your username");
        userTextField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(userTextField, 1, 1);

        Button btn = new Button("Sign In");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 3);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String userName = userTextField.getText();

                if (!userName.equals("")) {
                    if(Main.members.containsKey(userName)) {
                        actiontarget.setFill(Color.FIREBRICK);
                        actiontarget.setText("This username already exists: " + userName);
                    } else {
                        Main.userName = userName;
                        Main.system.tell(new User.Join(Main.userName));
                        primaryStage.close();
                    }
                } else {
                    actiontarget.setText("Username must not be empty");
                }
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}