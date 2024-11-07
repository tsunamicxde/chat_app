import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class DirectMessageButton implements EventHandler<ActionEvent> {

    private final TextField textField;
    private final String name;

    public DirectMessageButton(TextField textField, String name) {
        this.textField = textField;
        this.name = name;
    }

    @Override
    public void handle(ActionEvent e) {
        String message = textField.getText();

        if (message.isEmpty()) {
            return;
        }
        try {
            Main.members.get(name).tell(new User.PrivateMessage(message, Main.userName, Main.userName));
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            System.out.println("Chat not found");
        }
        Main.system.tell(new User.PrivateMessage(message, name, Main.userName));

        textField.clear();
    }
}