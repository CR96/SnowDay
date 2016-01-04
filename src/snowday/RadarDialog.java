package snowday;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class RadarDialog {

    static ResourceBundle bundle = ResourceBundle
            .getBundle("bundles.LangBundle", new Locale("en", "EN"));


    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(bundle.getString("radar"));
        window.getIcons().add(new Image(Main.class.getResourceAsStream("icons/icon.png")));
        window.setResizable(false);

        ImageView imgRadar = new ImageView();
        
        imgRadar.setImage(new Image(bundle.getString("radarlink")));

        window.setHeight(600);
        window.setWidth(600);
        
        Pane root = new Pane();
        root.getChildren().addAll(imgRadar);

        Scene scene = new Scene(root);
        window.setScene(scene);
        window.showAndWait();

    }
}