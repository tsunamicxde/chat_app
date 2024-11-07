import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class SendMessageButton implements EventHandler<ActionEvent> {

    private final TextField textField;

    public SendMessageButton(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void handle(ActionEvent e) {
        String message = textField.getText();

        if (message.isEmpty() || Main.userName == null) {
            return;
        }
        if (message.startsWith("PM")) {
            String[] a = message.split(": ");
            if (a.length == 2) {
                String namePM = a[1];
                if (Main.members.containsKey(namePM)) {
                    Main.members.get(namePM).tell(new User.JoinPrivateChat(Main.userName));
                    Main.system.tell(new User.JoinPrivateChat(namePM));
                }
            }
        } else {
            Main.system.tell(new User.Message(message, Main.userName));
        }
        textField.clear();
    }
}