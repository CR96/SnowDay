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

package com.gbsnowday.snowday.controller;

import com.gbsnowday.snowday.model.ClosingModel;
import com.gbsnowday.snowday.model.EventModel;
import com.gbsnowday.snowday.model.WeatherModel;
import com.gbsnowday.snowday.network.ClosingsScraper;
import com.gbsnowday.snowday.network.WeatherScraper;
import com.gbsnowday.snowday.ui.RadarDialog;
import com.gbsnowday.snowday.ui.WeatherDialog;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class SnowDayController {

    //Declare scene controls
    public Button btnCalculate;
    public Button btnRadar;
    public MenuItem itemAbout;
    public Label lblDay;
    public Label lblPercent;
    public Label lblPrev;
    public ComboBox<String> lstDays;
    public RadioButton optToday;
    public RadioButton optTomorrow;
    public Label lblError;
    public ImageView imgCalculate;
    public ScrollPane scrClosings;
    public TextArea txtAtherton;
    public TextArea txtBeecher;
    public TextArea txtBendle;
    public TextArea txtBentley;
    public TextArea txtCarman;
    public TextArea txtClio;
    public TextArea txtDavison;
    public TextArea txtDurand;
    public TextArea txtFenton;
    public TextArea txtFlint;
    public TextArea txtFlushing;
    public TextArea txtGB;
    public TextArea txtGBAcademy;
    public TextArea txtGISD;
    public TextArea txtGenesee;
    public TextArea txtGoodrich;
    public TextArea txtHolly;
    public TextArea txtHolyFamily;
    public TextArea txtInfo;
    public TextArea txtKearsley;
    public TextArea txtLKFenton;
    public TextArea txtLapeer;
    public TextArea txtLinden;
    public TextArea txtMontrose;
    public TextArea txtMorris;
    public Label lblNWS;
    public TextArea txtOwosso;
    public TextArea txtSzCreek;
    public TextField txtTier1;
    public TextField txtTier2;
    public TextField txtTier3;
    public TextField txtTier4;
    public Label lblWJRT;
    public TextArea txtWPAcademy;
    public ListView<String> lstWeather;

    private final ResourceBundle arrayBundle = ResourceBundle
            .getBundle("bundle.ArrayBundle", new Locale("en", "EN"));
    private final ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));
    private int days;
    private int dayrun;

    private final ArrayList<Integer> daysarray = new ArrayList<>();

    //Individual components of the calculation
    private int schoolPercent;
    private int weatherPercent;
    private int percent;

    private RotateTransition rt;

    private ClosingsScraper closingsScraper;
    private WeatherScraper weatherScraper;

    @FXML
    void initialize() {
        ToggleGroup group = new ToggleGroup();
        optToday.setToggleGroup(group);
        optTomorrow.setToggleGroup(group);

        lstDays.setItems(FXCollections.observableArrayList(
                arrayBundle.getString("days_array").split(",")));

        EventModel eventModel = new EventModel();

        boolean todayValid = eventModel.isTodayValid();
        boolean tomorrowValid = eventModel.isTomorrowValid();

        if (!todayValid && !tomorrowValid) {
            optToday.setDisable(true);
            optToday.setSelected(false);
            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);
        } else if (!todayValid) {
            optToday.setDisable(true);
            optToday.setSelected(false);
        } else if (!tomorrowValid) {
            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);
        }

        if (eventModel.isEventPresent()) {
            txtInfo.setStyle("-fx-text-fill: blue");
        }

        ArrayList<String> infoList = eventModel.getInfoList();
        //Set the contents of lstInfo
        for (int i = 0; i < infoList.size(); i++) {
            if (i == 0) {
                txtInfo.setText(infoList.get(i));
            } else {
                txtInfo.setText(txtInfo.getText() + "\n" + infoList.get(i));
            }
        }
    }

    public void showRadar() {
        new RadarDialog().display();
    }

    public void showAboutDialog() throws IOException {
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("action_about"));
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/image/icon.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        Pane pane = FXMLLoader.load(getClass().getResource("/view/about.fxml"), bundle);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void openClosingsWeb() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(bundle.getString("closingsweb")));
    }

    public void openWeatherWeb() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(bundle.getString("weatherweb")));
    }

    public void setCursorHand() {
        btnCalculate.getScene().setCursor(Cursor.HAND);
    }

    public void setCursorDefault() {
        btnCalculate.getScene().setCursor(Cursor.DEFAULT);
    }

    public void checkCalculationEnabled() {
        if (lstDays.getSelectionModel().getSelectedIndex() == -1) {
            btnCalculate.setDisable(true);
        } else if (!optToday.isSelected() && !optTomorrow.isSelected()) {
            btnCalculate.setDisable(true);
        } else {
            btnCalculate.setDisable(false);
        }
        special();
    }

    private void special() {
        daysarray.add(lstDays.getSelectionModel().getSelectedIndex());
        int[] specialarray1 = {2, 6, 0, 6, 2, 0, 1, 0};
        int[] specialarray2 = {2, 0, 4, 2, 0, 1};
        if (daysarray.toString().equals(Arrays.toString(specialarray1))) {
            txtInfo.setText(txtInfo.getText() + bundle.getString("special1"));
            txtInfo.setStyle("-fx-text-fill: blue");
        } else if (daysarray.toString().equals(Arrays.toString(specialarray2))) {
            txtInfo.setText(txtInfo.getText() + bundle.getString("special2"));
            txtInfo.setStyle("-fx-text-fill: blue");
        }
    }

    /*
     * This application will predict the possibility of a snow day for Grand Blanc Community Schools.
     * Created by Corey Rowe, February 2014.
     * Factors:
     * Weather warnings from the National Weather Service (includes snowfall, ice, and wind chill)
     * Number of past snow days (more = smaller chance)
     * Schools currently closed (data from WJRT)
     * Schools in higher tiers (4 is highest) will increase the snow day chance.
     * Obviously return 100% if GB is already closed.
     */
    public void Calculate() {
        //Spin the snowflake
        imgCalculate.setVisible(true);

        imgCalculate.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/image/snowflake_blue.png")));

        rt = new RotateTransition(Duration.millis(2000), imgCalculate);
        rt.setByAngle(720);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setAutoReverse(false);

        rt.play();


        //Call a reset to clear any previous data
        Reset();

        //Date setup

        if (optToday.isSelected()) {
            //Set dayrun to 0 (Today)
            dayrun = 0;

        } else if (optTomorrow.isSelected()) {
            //Set dayrun to 1 (Tomorrow)
            dayrun = 1;
        }

        //Have the user input past snow days
        days = lstDays.getSelectionModel().getSelectedIndex();

        closingsScraper = new ClosingsScraper(dayrun, closingModels -> {
            if (closingsScraper.isCancelled()) {
                //Closings scraper has failed.
                txtGB.setText(closingsScraper.getError());

                txtGB.setText(closingsScraper.getGBText()
                        + bundle.getString("CalculateWithoutClosings"));
            } else {
                //Set the school percent.
                schoolPercent = closingsScraper.getSchoolPercent();

                txtGB.setText(closingsScraper.getGBText());

                if (closingsScraper.isGBClosed() || closingsScraper.gbHasMessage()) {
                    txtGB.setStyle("-fx-control-inner-background: orange");
                }

                setSchoolText(txtAtherton, closingModels.get(0));
                setSchoolText(txtBendle, closingModels.get(1));
                setSchoolText(txtBentley, closingModels.get(2));
                setSchoolText(txtCarman, closingModels.get(3));
                setSchoolText(txtFlint, closingModels.get(4));
                setSchoolText(txtGoodrich, closingModels.get(5));
                setSchoolText(txtBeecher, closingModels.get(6));
                setSchoolText(txtClio, closingModels.get(7));
                setSchoolText(txtDavison, closingModels.get(8));
                setSchoolText(txtFenton, closingModels.get(9));
                setSchoolText(txtFlushing, closingModels.get(10));
                setSchoolText(txtGenesee, closingModels.get(11));
                setSchoolText(txtKearsley, closingModels.get(12));
                setSchoolText(txtLKFenton, closingModels.get(13));
                setSchoolText(txtLinden, closingModels.get(14));
                setSchoolText(txtMontrose, closingModels.get(15));
                setSchoolText(txtMorris, closingModels.get(16));
                setSchoolText(txtSzCreek, closingModels.get(17));
                setSchoolText(txtDurand, closingModels.get(18));
                setSchoolText(txtHolly, closingModels.get(19));
                setSchoolText(txtLapeer, closingModels.get(20));
                setSchoolText(txtOwosso, closingModels.get(21));
                setSchoolText(txtGBAcademy, closingModels.get(22));
                setSchoolText(txtGISD, closingModels.get(23));
                setSchoolText(txtHolyFamily, closingModels.get(24));
                setSchoolText(txtWPAcademy, closingModels.get(25));
            }
        });

        closingsScraper.execute();

        weatherScraper = new WeatherScraper(dayrun, weatherModels -> {
            if (weatherScraper.isCancelled()) {
                //Weather scraper has failed.
                lblNWS.setText(weatherScraper.getError()
                        + "\n" + bundle.getString("CalculateWithoutWeather"));
            } else {
                //Set the weather percent
                weatherPercent = weatherScraper.getWeatherPercent();

                Platform.runLater(() -> lblNWS.setText(weatherModels.get(0).getWarningTitle()));

                ArrayList<String> weatherWarningTitles = new ArrayList<>();

                for (WeatherModel weatherModel : weatherModels) {
                    weatherWarningTitles.add(weatherModel.getWarningTitle());
                }

                lstWeather.setItems(FXCollections.observableArrayList(weatherWarningTitles));
                lstWeather.getItems().remove(0);

                lstWeather.setOnMouseClicked(click -> {

                    if (click.getClickCount() == 2) {
                        int i = lstWeather.getSelectionModel().getSelectedIndex() + 1;

                        if (weatherScraper.isWeatherWarningPresent()) {
                            try {
                                new WeatherDialog().display(
                                        weatherModels.get(i).getWarningTitle(),
                                        weatherModels.get(i).getWarningReadableTime(),
                                        weatherModels.get(i).getWarningSummary(),
                                        weatherModels.get(i).getWarningLink());
                            } catch (NullPointerException | IndexOutOfBoundsException e) {
                                new WeatherDialog().display(
                                        null,
                                        null,
                                        bundle.getString("WarningParseError"),
                                        null);
                            }
                        }
                    }
                });
            }
        });

        weatherScraper.execute();

        //Final Percent Calculator
        Thread percentThread = new Thread(new PercentCalculate());
        percentThread.start();

    }

    //TODO: Redesign the closings portion of the GUI so these arrays can be displayed separately (custom list cells).
    private void setSchoolText(TextArea textArea, ClosingModel closingModel) {
        textArea.setText(closingModel.getOrgName()
                + ": " + closingModel.getOrgStatus());
        if (closingModel.isClosed()) {
            textArea.setStyle("-fx-control-inner-background: orange");
        }
    }

    private void clearSchoolText(TextArea textArea, String text) {
        textArea.setText(text);
        textArea.setStyle("-fx-control-inner-background: white");
    }

    private void Reset() {

        lblError.setText("");

        lblPercent.setVisible(false);

        lstWeather.setDisable(true);
        scrClosings.setDisable(true);

        lblPercent.setText("");
        txtGB.setText("");
        txtGB.setVisible(false);

        lstWeather.getItems().clear();

        schoolPercent = 0;
        weatherPercent = 0;
        percent = 0;

        clearSchoolText(txtAtherton, bundle.getString("Atherton"));
        clearSchoolText(txtBendle, bundle.getString("Bendle"));
        clearSchoolText(txtBentley, bundle.getString("Bentley"));
        clearSchoolText(txtCarman, bundle.getString("Carman"));
        clearSchoolText(txtFlint, bundle.getString("Flint"));
        clearSchoolText(txtGoodrich, bundle.getString("Goodrich"));
        clearSchoolText(txtBeecher, bundle.getString("Beecher"));
        clearSchoolText(txtClio, bundle.getString("Clio"));
        clearSchoolText(txtDavison, bundle.getString("Davison"));
        clearSchoolText(txtFenton, bundle.getString("Fenton"));
        clearSchoolText(txtFlushing, bundle.getString("Flushing"));
        clearSchoolText(txtGenesee, bundle.getString("Genesee"));
        clearSchoolText(txtKearsley, bundle.getString("Kearsley"));
        clearSchoolText(txtLKFenton, bundle.getString("LKFenton"));
        clearSchoolText(txtLinden, bundle.getString("Linden"));
        clearSchoolText(txtMontrose, bundle.getString("Montrose"));
        clearSchoolText(txtMorris, bundle.getString("Morris"));
        clearSchoolText(txtSzCreek, bundle.getString("SzCreek"));
        clearSchoolText(txtDurand, bundle.getString("Durand"));
        clearSchoolText(txtHolly, bundle.getString("Holly"));
        clearSchoolText(txtLapeer, bundle.getString("Lapeer"));
        clearSchoolText(txtOwosso, bundle.getString("Owosso"));
        clearSchoolText(txtGBAcademy, bundle.getString("GBAcademy"));
        clearSchoolText(txtGISD, bundle.getString("GISD"));
        clearSchoolText(txtHolyFamily, bundle.getString("HolyFamily"));
        clearSchoolText(txtWPAcademy, bundle.getString("WPAcademy"));

        btnCalculate.setDisable(true);
    }

    private class PercentCalculate implements Runnable {
        @Override
        public void run() {

            //Give the scrapers time to act before displaying the percent

            while (!closingsScraper.isDone() || !weatherScraper.isDone()) {
                try {
                    //Wait for scrapers to finish before continuing
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            //Calculate the total percent.
            //Set the percent to the higher percent.
            if (weatherPercent > schoolPercent) {
                percent = weatherPercent;
            } else if (schoolPercent > weatherPercent) {
                percent = schoolPercent;
            }

            //Reduce the percent chance by three for every snow day entered.
            percent -= (days * 3);

            //No negative percents.
            if (percent < 0) {
                percent = 0;
            }

            //Don't allow a chance above 90%.
            if (percent > 90) {
                percent = 90;
            }

            //Negate the above results for special cases
            if (closingsScraper.isGBClosed()) {
                //WJRTScraper reports Grand Blanc is closed. Override percentage, set to 100%
                percent = 100;
            } else if (closingsScraper.isGBOpen()) {
                //GB is false and the time is during or after school hours. 0% chance.
                percent = 0;
            }

            lblPercent.setVisible(true);
            Platform.runLater(() -> lblPercent.setText("0%"));

            lblPercent.setStyle("-fx-text-fill: red");

            //Animate lblPercent
            if (closingsScraper.isCancelled() && weatherScraper.isCancelled()) {
                //Both scrapers failed. A percentage cannot be determined.
                //Don't set the percent.
                Platform.runLater(() -> lblError.setText(bundle.getString("CalculateError")));
                imgCalculate.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/image/snowflake_red.png")));

                rt.stop();

                FadeTransition red_blink = new FadeTransition(Duration.millis(500), imgCalculate);
                red_blink.setCycleCount(19);
                red_blink.setFromValue(0.0);
                red_blink.setToValue(1.0);
                red_blink.setAutoReverse(true);

                red_blink.play();
                imgCalculate.setRotate(0.0);

                Platform.runLater(() -> lblPercent.setText(""));

                scrClosings.setDisable(true);
                lstWeather.setDisable(true);

            } else if (closingsScraper.isCancelled() || weatherScraper.isCancelled()) {
                //Partial failure
                Platform.runLater(() -> lblError.setText(bundle.getString("NoNetwork")));
                imgCalculate.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/image/snowflake_orange.png")));

                rt.stop();

                FadeTransition orange_blink = new FadeTransition(Duration.millis(500), imgCalculate);
                orange_blink.setCycleCount(19);
                orange_blink.setFromValue(0.0);
                orange_blink.setToValue(1.0);
                orange_blink.setAutoReverse(true);

                orange_blink.play();
                imgCalculate.setRotate(0.0);

                if (!closingsScraper.isCancelled()) {
                    scrClosings.setDisable(false);
                } else if (!weatherScraper.isCancelled()) {
                    lstWeather.setDisable(false);
                }
            } else {
                try {
                    for (int percentscroll = 0; percentscroll <= percent; percentscroll++) {
                        Thread.sleep(20);
                        if (percentscroll >= 0 && percentscroll <= 20) {
                            lblPercent.setStyle("-fx-text-fill: red");
                        }
                        if (percentscroll > 20 && percentscroll <= 60) {
                            lblPercent.setStyle("-fx-text-fill: orange");
                        }
                        if (percentscroll > 60 && percentscroll <= 80) {
                            lblPercent.setStyle("-fx-text-fill: green");
                        }
                        if (percentscroll > 80) {
                            lblPercent.setStyle("-fx-text-fill: blue");
                        }

                        final int finalPercentscroll = percentscroll;
                        Platform.runLater(() -> lblPercent.setText((finalPercentscroll) + "%"));
                    }
                } catch (InterruptedException ignored) {
                }

                lstWeather.setDisable(false);
                scrClosings.setDisable(false);

                imgCalculate.setVisible(false);

                rt.stop();
                imgCalculate.setRotate(0.0);
            }

            btnCalculate.setDisable(false);
            lstWeather.setDisable(false);
            txtGB.setVisible(true);
            scrClosings.setDisable(false);
        }

    }


}
