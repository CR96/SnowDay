package com.gbsnowday.snowday.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;

public class WeatherDialog {

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

    private ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));


    public void display(String title, String summary, String link) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(bundle.getString("weather"));
        window.getIcons().add(new Image(WeatherDialog.class.getResourceAsStream("/image/icon.png")));
        window.setWidth(720.0);
        window.setHeight(400.0);
        window.setResizable(false);

        ImageView imgWeather = new ImageView();
        imgWeather.setImage(new Image(WeatherDialog.class.getResourceAsStream("/image/weather.png")));

        Label lblTitle = new Label();
        Label lblSummary = new Label();

        lblTitle.setText(title);
        lblTitle.setGraphic(imgWeather);
        lblTitle.setFont(Font.font ("Verdana", 20));
        lblTitle.setWrapText(true);
        lblSummary.setText(summary);
        lblSummary.setWrapText(true);

        Button infoButton = new Button(bundle.getString("action_info"));

        if (link == null) {
            infoButton.setDisable(true);
        }

        infoButton.setOnAction(e -> {
            try {
                if (link != null) {
                    Desktop.getDesktop().browse(new URI(link));
                }
            } catch (IOException | URISyntaxException e1) {
                infoButton.setText(bundle.getString("LinkParseError"));
                infoButton.setDisable(true);
            }
        });

        HBox titlebox = new HBox(16);
        titlebox.getChildren().addAll(lblTitle);
        titlebox.setAlignment(Pos.CENTER);

        HBox summarybox = new HBox(16);
        summarybox.getChildren().add(lblSummary);
        summarybox.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(16);
        buttons.getChildren().add(infoButton);
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: lightblue");
        root.setTop(titlebox);
        root.setCenter(summarybox);
        root.setBottom(buttons);


        Scene scene = new Scene(root);
        window.setScene(scene);
        window.showAndWait();

    }
}