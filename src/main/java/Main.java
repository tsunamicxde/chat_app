import akka.actor.typed.ActorRef;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.Stage;
import akka.actor.typed.ActorSystem;
import java.util.HashMap;
import java.util.Map;
import scala.concurrent.ExecutionContext;

public class Main extends Application {

    static Map<String, ActorRef<User.Command>> members = new HashMap<>();
    static Map<String, TextArea> directArea = new HashMap<>();
    static ActorSystem<User.Command> system;
    static String userName;
    static int seedPort1;
    static int seedPort2;

    public static void setSystem(ActorSystem<User.Command> system) {
        Main.system = system;
    }

    @Override
    public void start(Stage primaryStage) {
        ChatPanel.startChat(new Stage());
        AuthPanel.startAuth(new Stage());
    }

    public static void main(String[] args) {
        String ip = null;
        int port = 0;
        if (args.length != 4)
            throw new IllegalArgumentException("Usage: ./gradlew run --args=\"127.0.0.1 25251 25252 port\"");

        try {
            ip = args[0];
            seedPort1 = Integer.parseInt(args[1]);
            seedPort2 = Integer.parseInt(args[2]);
            port = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        MessageRepository messageRepository = new MessageRepository(ExecutionContext.global());
        StartAkkaCluster.startup(ip, seedPort1, seedPort2, port, messageRepository, ExecutionContext.global());
        launch(args);
    }
}