package com.gbsnowday.snowday.controller;

import com.gbsnowday.snowday.model.ClosingsModel;
import com.gbsnowday.snowday.model.EventModel;
import com.gbsnowday.snowday.network.ClosingsScraper;
import com.gbsnowday.snowday.ui.RadarDialog;
import com.gbsnowday.snowday.ui.WeatherDialog;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SnowDayController {
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

    private ResourceBundle arrayBundle = ResourceBundle
            .getBundle("bundle.ArrayBundle", new Locale("en", "EN"));
    private ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

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

    //Declare variables
    String datetoday;
    String datetomorrow;

    int days;
    int dayrun;

    ArrayList<Integer> daysarray = new ArrayList<>();

    List<String> weatherWarn = new ArrayList<>();
    List<String> weatherSummary = new ArrayList<>();
    List<String> weatherExpire = new ArrayList<>();
    List<String> weatherLink = new ArrayList<>();

    //Individual components of the calculation
    int schoolpercent;
    int weathertoday;
    int weathertomorrow;
    int weatherpercent;
    int percent;

    //Levels of school closings (near vs. far)
    private int tier1 = 0;
    private int tier2 = 0;
    private int tier3 = 0;
    private int tier4 = 0;

    //Don't try to show weather warning information if no warnings are present
    boolean WeatherWarningsPresent;

    //Scraper status
    boolean NWSActive = true;

    /*Used for catching IOExceptions / NullPointerExceptions if there are connectivity issues
    or a webpage is down*/
    boolean NWSFail;

    RotateTransition rt;

    ClosingsScraper closingsScraper;
    Thread nws;
    Thread p;

    ClosingsModel mClosingsModel;

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
        if (eventModel.isBobcats()) {
            txtInfo.setStyle("-fx-text-fill: red");
        }

        ArrayList<String> infoList = eventModel.getInfoList();
        //Set the contents of lstInfo
        for (int i = 0; i < infoList.size(); i++) {
            if (i == 0) {
                txtInfo.setText(infoList.get(i));
            }else{
                txtInfo.setText(txtInfo.getText() + "\n" + infoList.get(i));
            }
        }
    }

    public void showRadar() {
        RadarDialog.display();
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
        }else if (!optToday.isSelected() && !optTomorrow.isSelected()) {
            btnCalculate.setDisable(true);
        }else{
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
        }else if (daysarray.toString().equals(Arrays.toString(specialarray2))) {
            txtInfo.setText(txtInfo.getText() + bundle.getString("special2"));
            txtInfo.setStyle("-fx-text-fill: blue");
        }
    }

    public void Calculate() throws ExecutionException, InterruptedException {
        /**
         * This application will predict the possibility of a snow day for Grand Blanc Community Schools.
         * Created by Corey Rowe, February 2014.
         * Factors:
         * Weather warnings from the National Weather Service (includes snowfall, ice, and wind chill)
         * Number of past snow days (more = smaller chance)
         * Schools currently closed (data from WJRT)
         * Schools in higher tiers (4 is highest) will increase the snow day chance.
         * Obviously return 100% if GB is already closed.
         */

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

        closingsScraper = new ClosingsScraper(dayrun, closingsModel -> {
            mClosingsModel = closingsModel;
            if (closingsScraper.isCancelled()) {
                //Closings scraper has failed.
                txtGB.setText(closingsModel.error);
            } else {
                //Set the school percent.
                schoolpercent = closingsModel.schoolpercent;

                txtGB.setText(closingsModel.GBText);
                if (closingsModel.GB) {
                    txtGB.setStyle("-fx-control-inner-background: orange");
                }

                setSchoolText(txtAtherton, 0, closingsModel.Atherton);
                setSchoolText(txtBendle, 1, closingsModel.Bendle);
                setSchoolText(txtBentley, 2, closingsModel.Bentley);
                setSchoolText(txtCarman, 3, closingsModel.Carman);
                setSchoolText(txtFlint, 4, closingsModel.Flint);
                setSchoolText(txtGoodrich, 5, closingsModel.Goodrich);
                setSchoolText(txtBeecher, 6, closingsModel.Beecher);
                setSchoolText(txtClio, 7, closingsModel.Clio);
                setSchoolText(txtDavison, 8, closingsModel.Davison);
                setSchoolText(txtFenton, 9, closingsModel.Fenton);
                setSchoolText(txtFlushing, 10, closingsModel.Flushing);
                setSchoolText(txtGenesee, 11, closingsModel.Genesee);
                setSchoolText(txtKearsley, 12, closingsModel.Kearsley);
                setSchoolText(txtLKFenton, 13, closingsModel.LKFenton);
                setSchoolText(txtLinden, 14, closingsModel.Linden);
                setSchoolText(txtMontrose, 15, closingsModel.Montrose);
                setSchoolText(txtMorris, 16, closingsModel.Morris);
                setSchoolText(txtSzCreek, 17, closingsModel.SzCreek);
                setSchoolText(txtDurand, 18, closingsModel.Durand);
                setSchoolText(txtHolly, 19, closingsModel.Holly);
                setSchoolText(txtLapeer, 20, closingsModel.Lapeer);
                setSchoolText(txtOwosso, 21, closingsModel.Owosso);
                setSchoolText(txtGBAcademy, 22, closingsModel.GBAcademy);
                setSchoolText(txtGISD, 23, closingsModel.GISD);
                setSchoolText(txtHolyFamily, 24, closingsModel.HolyFamily);
                setSchoolText(txtWPAcademy, 25, closingsModel.WPAcademy);
            }
        });

        closingsScraper.execute();

        /**NATIONAL WEATHER SERVICE SCRAPER**/
        nws = new Thread(new WeatherScraper());
        nws.start();

        //Final Percent Calculator
        p = new Thread(new PercentCalculate());
        p.start();

    }

    //TODO: Redesign the closings portion of the GUI so these arrays can be displayed separately (custom list cells).
    private void setSchoolText(TextArea textArea, int position, boolean closed) {
        textArea.setText(mClosingsModel.displayedOrgNames.get(position)
                + ": " + mClosingsModel.displayedOrgStatuses.get(position));
        if (closed) {
            textArea.setStyle("-fx-control-inner-background: orange");
        }
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

        schoolpercent = 0;
        weatherpercent = 0;
        percent = 0;

        tier1 = 0;
        tier2 = 0;
        tier3 = 0;
        tier4 = 0;

        weathertoday = 0;
        weathertomorrow = 0;

        NWSActive = true;
        NWSFail = false;

        weatherWarn.clear();
        weatherSummary.clear();
        weatherExpire.clear();
        weatherLink.clear();

        txtAtherton.setText("Atherton:");
        txtAtherton.setStyle("-fx-control-inner-background: white");

        txtBendle.setText(bundle.getString("Bendle"));
        txtBendle.setStyle("-fx-control-inner-background: white");

        txtBentley.setText(bundle.getString("Bentley"));
        txtBentley.setStyle("-fx-control-inner-background: white");

        txtCarman.setText(bundle.getString("Carman"));
        txtCarman.setStyle("-fx-control-inner-background: white");

        txtFlint.setText(bundle.getString("Flint"));
        txtFlint.setStyle("-fx-control-inner-background: white");

        txtGoodrich.setText(bundle.getString("Goodrich"));
        txtGoodrich.setStyle("-fx-control-inner-background: white");

        txtBeecher.setText(bundle.getString("Beecher"));
        txtBeecher.setStyle("-fx-control-inner-background: white");

        txtClio.setText(bundle.getString("Clio"));
        txtClio.setStyle("-fx-control-inner-background: white");

        txtDavison.setText(bundle.getString("Davison"));
        txtDavison.setStyle("-fx-control-inner-background: white");

        txtFenton.setText(bundle.getString("Fenton"));
        txtFenton.setStyle("-fx-control-inner-background: white");

        txtFlushing.setText(bundle.getString("Flushing"));
        txtFlushing.setStyle("-fx-control-inner-background: white");

        txtGenesee.setText(bundle.getString("Genesee"));
        txtGenesee.setStyle("-fx-control-inner-background: white");

        txtKearsley.setText(bundle.getString("Kearsley"));
        txtKearsley.setStyle("-fx-control-inner-background: white");

        txtLKFenton.setText(bundle.getString("LKFenton"));
        txtLKFenton.setStyle("-fx-control-inner-background: white");

        txtLinden.setText(bundle.getString("Linden"));
        txtLinden.setStyle("-fx-control-inner-background: white");

        txtMontrose.setText(bundle.getString("Montrose"));
        txtMontrose.setStyle("-fx-control-inner-background: white");

        txtMorris.setText(bundle.getString("Morris"));
        txtMorris.setStyle("-fx-control-inner-background: white");

        txtSzCreek.setText(bundle.getString("SzCreek"));
        txtSzCreek.setStyle("-fx-control-inner-background: white");

        txtDurand.setText(bundle.getString("Durand"));
        txtDurand.setStyle("-fx-control-inner-background: white");

        txtHolly.setText(bundle.getString("Holly"));
        txtHolly.setStyle("-fx-control-inner-background: white");

        txtLapeer.setText(bundle.getString("Lapeer"));
        txtLapeer.setStyle("-fx-control-inner-background: white");

        txtOwosso.setText(bundle.getString("Owosso"));
        txtOwosso.setStyle("-fx-control-inner-background: white");

        txtGBAcademy.setText(bundle.getString("GBAcademy"));
        txtGBAcademy.setStyle("-fx-control-inner-background: white");

        txtGISD.setText(bundle.getString("GISD"));
        txtGISD.setStyle("-fx-control-inner-background: white");

        txtHolyFamily.setText(bundle.getString("HolyFamily"));
        txtHolyFamily.setStyle("-fx-control-inner-background: white");

        txtWPAcademy.setText(bundle.getString("WPAcademy"));
        txtWPAcademy.setStyle("-fx-control-inner-background: white");

        btnCalculate.setDisable(true);
    }

    private class WeatherScraper implements Runnable {
        @SuppressWarnings("ForLoopReplaceableByForEach")
        @Override
        public void run() {
            /**NATIONAL WEATHER SERVICE SCRAPER**/
            //Change the percentage based on current storm/wind/temperature warnings.

            Document weatherdoc;

            //Live html
            try {
                weatherdoc = Jsoup.connect("http://alerts.weather.gov/cap/wwaatmget.php?x=MIZ061&y=0").timeout(10000).get();
                Elements title = weatherdoc.select("title");
                Elements summary = weatherdoc.select("summary");
                Elements expiretime = weatherdoc.select("cap|expires");
                Elements link = weatherdoc.select("link");


                if (title != null) {
                    for (int i = 0; i < title.size(); i++) {
                        weatherWarn.add(title.get(i).text().replace(" by NWS", ""));
                    }

                    if (!weatherWarn.get(1).contains("no active")) {
                        //Weather warnings are present.
                        WeatherWarningsPresent = true;
                    }
                }
                if (expiretime != null) {
                    for (int i = 0; i < expiretime.size(); i++) {
                        weatherExpire.add(expiretime.get(i).text());
                    }
                }

                if (summary != null) {
                    for (int i = 0; i < summary.size(); i++) {
                        weatherSummary.add(summary.get(i).text() + "...");
                    }
                }

                if (weatherLink != null) {
                    for (int i = 0; i < link.size(); i++) {
                        weatherLink.add(link.get(i).attr("href"));
                    }
                }

                getWeather();

            }catch (IOException e) {
                //Connectivity issues
                weatherWarn.add(bundle.getString("WeatherConnectionError") + bundle.getString("NoConnection"));
                NWSFail = true;

            } catch (NullPointerException | IndexOutOfBoundsException e) {
                //Webpage layout not recognized.
                weatherWarn.clear();
                weatherWarn.add(bundle.getString("WeatherParseError") + bundle.getString("ErrorContact"));
                NWSFail = true;

            }

            //Weather scraper has finished.
            NWSActive = false;
        }
    }

    private void getWeather() {
        /*Only the highest weatherpercent is stored (not cumulative).
        Calculation is affected based on when warning expires.*/
        for (int i = 0; i < weatherWarn.size(); i++) {
            if (weatherWarn.get(i).contains("Significant Weather Advisory")) {
                //Significant Weather Advisory - 15% weatherpercent
                checkWarningTime(i, 15);
            }
            if (weatherWarn.get(i).contains("Winter Weather Advisory")) {
                //Winter Weather Advisory - 30% weatherpercent
                checkWarningTime(i, 30);
            }
            if (weatherWarn.get(i).contains("Lake-Effect Snow Advisory")) {
                //Lake Effect Snow Advisory - 40% weatherpercent
                checkWarningTime(i, 40);
            }
            if (weatherWarn.get(i).contains("Freezing Rain Advisory")) {
                //Freezing Rain - 40% weatherpercent
                checkWarningTime(i, 40);
            }
            if (weatherWarn.get(i).contains("Freezing Drizzle Advisory")) {
                //Freezing Drizzle - 40% weatherpercent
                checkWarningTime(i, 40);
            }
            if (weatherWarn.get(i).contains("Freezing Fog Advisory")) {
                //Freezing Fog - 40% weatherpercent
                checkWarningTime(i, 40);
            }
            if (weatherWarn.get(i).contains("Wind Chill Advisory")) {
                //Wind Chill Advisory - 40% weatherpercent
                checkWarningTime(i, 40);
            }
            if (weatherWarn.get(i).contains("Ice Storm Warning")) {
                //Ice Storm Warning - 70% weatherpercent
                checkWarningTime(i, 70);
            }
            if (weatherWarn.get(i).contains("Wind Chill Watch")) {
                //Wind Chill Watch - 70% weatherpercent
                checkWarningTime(i, 70);
            }
            if (weatherWarn.get(i).contains("Wind Chill Warning")) {
                //Wind Chill Warning - 70% weatherpercent
                checkWarningTime(i, 70);
            }
            if (weatherWarn.get(i).contains("Winter Storm Watch")) {
                //Winter Storm Watch - 80% weatherpercent
                checkWarningTime(i, 80);
            }
            if (weatherWarn.get(i).contains("Winter Storm Warning")) {
                //Winter Storm Warning - 80% weatherpercent
                checkWarningTime(i, 80);
            }
            if (weatherWarn.get(i).contains("Lake-Effect Snow Watch")) {
                //Lake Effect Snow Watch - 80% weatherpercent
                checkWarningTime(i, 80);
            }
            if (weatherWarn.get(i).contains("Lake-Effect Snow Warning")) {
                //Lake Effect Snow Warning - 80% weatherpercent
                checkWarningTime(i, 80);
            }
            if (weatherWarn.get(i).contains("Blizzard Watch")) {
                //Blizzard Watch - 90% weatherpercent
                checkWarningTime(i, 90);
            }
            if (weatherWarn.get(i).contains("Blizzard Warning")) {
                //Blizzard Warning - 90% weatherpercent
                checkWarningTime(i, 90);
            }
        }
    }

    private void checkWarningTime(int i, int w) {
        
        if (weatherExpire.get(i - 1).substring(0, 10).equals(datetoday)) {
            weathertoday = w;
        }

        if (weatherExpire.get(i - 1).substring(0, 10).equals(datetomorrow)) {
            weathertoday = w;
            weathertomorrow = w;
        }
    }

    private class PercentCalculate implements Runnable{
        @Override
        public void run(){

            //Give the scrapers time to act before displaying the percent

            while (!closingsScraper.isDone() || NWSActive) {
                try {
                    //Wait for scrapers to finish before continuing
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            //Set the schoolpercent
            if (tier1 > 2) {
                //3+ academies are closed. 20% schoolpercent.
                schoolpercent = 20;
            }
            if (tier2 > 2) {
                //3+ schools in nearby counties are closed. 40% schoolpercent.
                schoolpercent = 40;
            }
            if (tier3 > 2) {
                //3+ schools in Genesee County are closed. 60% schoolpercent.
                schoolpercent = 60;
            }
            if (tier4 > 2) {
                //3+ schools near GB are closed. 80% schoolpercent.
                schoolpercent = 80;
                if (mClosingsModel.Carman) {
                    //Carman is closed along with 2+ close schools. 90% schoolpercent.
                    schoolpercent = 90;
                }
            }

            //Set the weatherpercent
            if (dayrun == 0) {
                weatherpercent = weathertoday;
            }else if (dayrun == 1) {
                weatherpercent = weathertomorrow;
            }

            //Calculate the total percent.
            //Set the percent to the higher percent.
            if (weatherpercent > schoolpercent) {
                percent = weatherpercent;
            } else if (schoolpercent > weatherpercent) {
                percent = schoolpercent;
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
            if (mClosingsModel.GB) {
                //WJRTScraper reports Grand Blanc is closed. Override percentage, set to 100%
                percent = 100;
            }else if (mClosingsModel.GBOpen) {
                //GB is false and the time is during or after school hours. 0% chance.
                percent = 0;
            }

            lblPercent.setVisible(true);
            Platform.runLater(() -> lblPercent.setText("0%"));

            lblPercent.setStyle("-fx-text-fill: red");

            //Animate lblPercent
            if (closingsScraper.isCancelled() && NWSFail) {
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
                
            } else if (closingsScraper.isCancelled() || NWSFail) {
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
                }else if (!NWSFail) {
                    lstWeather.setDisable(false);
                }
            }else{
                try {
                    for (int percentscroll = 0; percentscroll <= percent; percentscroll++) {
                        Thread.sleep(20);
                        if (percentscroll >= 0 && percentscroll <= 20) {
                            lblPercent.setStyle("-fx-text-fill: red");
                        } if (percentscroll > 20 && percentscroll <= 60) {
                            lblPercent.setStyle("-fx-text-fill: orange");
                        } if (percentscroll > 60 && percentscroll <= 80) {
                            lblPercent.setStyle("-fx-text-fill: green");
                        } if (percentscroll > 80) {
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

            //Remove blank entries
            for (int i = 0; i < weatherWarn.size(); i++) {
                if (weatherWarn.get(i).equals("")) {
                    weatherWarn.remove(i);
                }
            }

            lstWeather.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @SuppressWarnings({"UnnecessaryLocalVariable", "unchecked"})
                @Override
                public ListCell<String> call(ListView<String> list) {
                    final ListCell cell = new ListCell() {
                        private Text text;

                        @Override
                        public void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                text = new Text(item.toString());
                                text.setWrappingWidth(lstWeather.getPrefWidth());
                                Platform.runLater(() -> setGraphic(text));
                            }
                        }
                    };
                    return cell;
                }
            });

            Platform.runLater(() -> {
                lblNWS.setText(weatherWarn.get(0));
                lstWeather.setItems(FXCollections.observableArrayList(weatherWarn));
                lstWeather.getItems().remove(0);
            });

            lstWeather.setOnMouseClicked(click -> {

                if (click.getClickCount() == 2) {
                    int i = lstWeather.getSelectionModel().getSelectedIndex();

                    if (WeatherWarningsPresent) {
                        try {
                            WeatherDialog.display(weatherWarn.get(i + 1), weatherSummary.get(i), weatherLink.get(i + 1));
                        }catch (NullPointerException | IndexOutOfBoundsException e) {
                            WeatherDialog.display(null, bundle.getString("WarningParseError"), null);
                        }
                    }
                }
            });

            btnCalculate.setDisable(false);
            lstWeather.setDisable(false);
            txtGB.setVisible(true);
            scrClosings.setDisable(false);
        }

    }


}
