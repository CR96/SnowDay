package com.gbsnowday.snowday.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    private final ResourceBundle bundle = ResourceBundle.getBundle("bundle.LangBundle", new Locale("en", "EN"));

    public static void main(String[] args) {
        //Temporary workaround to fix a hang with Windows 10 / Intel processors.
        System.setProperty("glass.accessible.force", "false");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/snowday.fxml"), bundle);
        primaryStage.setTitle(bundle.getString("app_name"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
        primaryStage.setScene(new Scene(root, 1200, 720));
        primaryStage.setResizable(false);
        primaryStage.show();

        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            splash.close();
        }


    }
}
