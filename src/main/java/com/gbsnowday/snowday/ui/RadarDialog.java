package com.gbsnowday.snowday.ui;

import com.gbsnowday.snowday.main.Main;
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
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));


    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(bundle.getString("radar"));
        window.getIcons().add(new Image(Main.class.getResourceAsStream("/image/icon.png")));
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