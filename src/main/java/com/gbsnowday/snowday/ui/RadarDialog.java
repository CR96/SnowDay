/*
 * Copyright 2014 - 2018 Corey Rowe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private final ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));


    public void display() {
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