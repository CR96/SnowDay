package com.gbsnowday.snowday.ui;

import com.gbsnowday.snowday.main.Main;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LicenseDialog {
    
    /*Copyright 2014-2016 Corey Rowe
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
         http:www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.*/

    private final ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

    public void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(bundle.getString("action_about"));
        window.getIcons().add(new Image(Main.class.getResourceAsStream("/image/icon.png")));
        window.setWidth(625.0);
        window.setHeight(675.0);
        window.setResizable(false);

        ImageView imgIcon = new ImageView();
        imgIcon.setImage(new Image(Main.class.getResourceAsStream("/image/icon.png"), 64.0, 64.0, true, true));

        Label lblTitle = new Label();
        Label lblUpdateTitle = new Label();
        Label lblUpdates = new Label();
        Label lblLicenseTitle = new Label();
        Label lblLicense1 = new Label();
        Hyperlink lblLicenseLink = new Hyperlink();
        Label lblLicense2 = new Label();

        lblTitle.setText(bundle.getString("app_name") + bundle.getString("Version"));
        lblTitle.setGraphic(imgIcon);
        lblTitle.setFont(Font.font("Verdana", 24));
        lblTitle.setWrapText(true);

        lblUpdateTitle.setText(bundle.getString("UpdateTitle"));
        lblUpdateTitle.setFont(Font.font("Verdana", 18));
        lblUpdates.setText(bundle.getString("Updates"));

        lblLicenseTitle.setText(bundle.getString("LicenseTitle"));
        lblLicenseTitle.setFont(Font.font("Verdana", 18));
        lblLicense1.setText(bundle.getString("license_1"));
        lblLicenseLink.setText(bundle.getString("license_link"));
        lblLicense2.setText(bundle.getString("license_2"));

        lblLicenseLink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(bundle.getString("license_link")));
            } catch (IOException | URISyntaxException ignored) {
            }
        });

        VBox root = new VBox();
        root.setPadding(new Insets(16.0));
        root.setSpacing(16.0);
        root.setStyle("-fx-background-color: lightblue");
        root.getChildren().addAll(lblTitle, lblUpdateTitle, lblUpdates, lblLicenseTitle, lblLicense1, lblLicenseLink, lblLicense2);

        Scene scene = new Scene(root);
        window.setScene(scene);
        window.showAndWait();

    }
}