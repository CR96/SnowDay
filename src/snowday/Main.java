package snowday;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    ResourceBundle bundle = ResourceBundle.getBundle("bundles.LangBundle", new Locale("en", "EN"));
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("snowday.fxml"), bundle);
        primaryStage.setTitle(bundle.getString("app_name"));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icons/icon.png")));
        primaryStage.setScene(new Scene(root, 1200, 720));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        //Temporary workaround to fix a hang with Windows 10 / Intel processors.
        System.setProperty("glass.accessible.force", "false");
        launch(args);
    }
}
