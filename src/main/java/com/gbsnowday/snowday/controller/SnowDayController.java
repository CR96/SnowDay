package com.gbsnowday.snowday.controller;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

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

    ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

    //Declare scene controls
    public Button btnCalculate;
    public Button btnRadar;
    
    public MenuItem itemAbout;
    public Label lblDay;
    public Label lblPercent;
    public Label lblPrev;

    public ComboBox lstDays;
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
    String textdate;
    
    boolean todayValid;
    boolean tomorrowValid;

    int days;
    int dayrun;

    List<String> orgName = new ArrayList<>();
    List<String> status = new ArrayList<>();

    //Declare lists that will be used in ListAdapters
    ArrayList<String> infoList = new ArrayList<>();
    ArrayList<Integer> daysarray = new ArrayList<>();
    
    boolean infoPresent;

    //Figure out what tomorrow is
    //Saturday = 6, Sunday = 7

    LocalDateTime dt = LocalDateTime.now();
    int weekday = dt.getDayOfWeek().getValue();

    String weekdaytoday;
    String weekdaytomorrow;

    String schooltext;

    //Declare lists that will be used in ListAdapters
    List<String> closings = new ArrayList<>();

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
    int tier1today = 0;
    int tier2today = 0;
    int tier3today = 0;
    int tier4today = 0;

    int tier1tomorrow = 0;
    int tier2tomorrow = 0;
    int tier3tomorrow = 0;
    int tier4tomorrow = 0;

    //Every school this program searches for: true = closed, false = open (default)
    boolean GBAcademy;
    boolean GISD;
    boolean HolyFamily;
    boolean WPAcademy;

    boolean Durand; //Check for "Durand Senior Center"
    boolean Holly;  //Check for "Holly Academy"
    boolean Lapeer; //Check for "Lapeer County CMH", "Lapeer Vocational Tech.", "Lapeer Team Work",
    // "Lapeer Senior Center", "Lapeer Co. Education Technology Center", "Lapeer Co. Intermed. Special Ed",
    // "Lapeer Growth and Opportunity, Inc.", "Lapeer District Library", "Lapeer County Offices", "NEMSCA-Lapeer Head Start",
    // "Greater Lapeer Transportation Authority", "Foster Grandparents-Lapeer, Genesee, Shiawassee", "Davenport University-Lapeer",
    // "MSU Extension Service-Lapeer Co.", "Community Connections-Lapeer", and "Chatfield School-Lapeer"
    boolean Owosso; //Check for "Owosso Christian School", "Owosso Senior Center",
    // "Owosso Seventh-day Adventist School", and "Social Security Administration-Owosso"

    boolean Beecher;
    boolean Clio; //Check for "Clio Area Christian School", Clio Area Senior Center",
    // "Clio City Hall", and "Cornerstone Clio"
    boolean Davison; //Check for "Davison Senior Center", "Faith Baptist School-Davison", "Montessori Academy-Davison",
    // and "Ross Medical Education-Davison"
    boolean Fenton; //Check for "Lake Fenton", "Fenton City Hall", "Fenton Academy of Cosmetology",
    // and "Fenton Montessori Academy"
    boolean Flushing; //Check for "Flushing Senior Citizens Center" and "St. Robert-Flushing"
    boolean Genesee; //Check for "Genesee I.S.D.", "Genesee Health System Day Programs", "Genesee Health System",
    // "Genesee Health Plan", "Genesee Academy", "Genesee Area Skill Center", "Genesee Christian School",
    // "Genesee County Free Medical Clinic", "Genesee District Library", "Genesee County Mobile Meal Program",
    // "Genesee STEM Academy", "Genesee Co Circuit Court", "Genesee County Government", "Genesee County Literacy Coalition",
    // "Flint Genesee Job Corps", "Leadership Genesee", "Freedom Work Genesee Co.", "Youth Leadership Genesee",
    // "67th District Court-Genesee Co.", "MSU Extension Service-Genesee Co.",
    // "Genesee Christian-Burton", and "Foster Grandparents-Lapeer, Genesee, Shiawassee"
    boolean Kearsley;
    boolean LKFenton;
    boolean Linden; //Check for "Linden Charter Academy"
    boolean Montrose; //Check for "Montrose Senior Center"
    boolean Morris;  //Check for "Mt Morris Twp Administration" and "St. Mary Church Religious Ed-Mt. Morris"
    boolean SzCreek; //Check for "Swartz Creek Area Senior Center" and "Swartz Creek Montessori"

    boolean Atherton;
    boolean Bendle;
    boolean Bentley;
    boolean Carman; //Check for "Carman-Ainsworth Senior Center"
    boolean Flint; //Thankfully this is listed as "Flint Community Schools" -
    // otherwise there would be a lot of exceptions to check for.
    boolean Goodrich;

    boolean GB; //Check for "Grand Blanc Senior Center", "Grand Blanc Academy", "Grand Blanc Road Montessori",
    // "Grand Blanc Gymnastics Co.", and "Freedom Work Grand Blanc"

    boolean GBOpen; //True if GB is already open (GB = false and time is during or after school hours)

    boolean GBMessage; //Grand Blanc has a message (e.g. "Early Dismissal") but isn't actually closed.

    //Don't try to show weather warning information if no warnings are present
    boolean WeatherWarningsPresent;

    //Scraper status
    boolean WJRTActive = true;
    boolean NWSActive = true;

    /*Used for catching IOExceptions / NullPointerExceptions if there are connectivity issues
    or a webpage is down*/
    boolean WJRTFail;
    boolean NWSFail;

    RotateTransition rt;

    //Threads
    Thread wjrt;
    Thread nws;
    Thread p;

    @FXML
    void initialize() {
        ToggleGroup group = new ToggleGroup();
        optToday.setToggleGroup(group);
        optTomorrow.setToggleGroup(group);

        //Make sure the user doesn't try to run the program on the weekend or on specific dates
        checkDate();

        //Only run checkWeekend() if today or tomorrow is still valid
        if (todayValid || tomorrowValid) {
           checkWeekend();
        }

        //Set the contents of txtInfo
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

    private void checkDate() {

        //These are set to false if the calculation cannot be run on that day
        todayValid = true;
        tomorrowValid = true;

        //Set the current month, day, and year
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        datetoday = sdt.format(calendar.getTime());

        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd yyyy", Locale.US);
        textdate = currentDate.format(calendar.getTime());

        calendar.add(Calendar.DATE, 1);

        datetomorrow = sdt.format(calendar.getTime());

        infoList.add(0, "Current Date: " + textdate);

        /*Check for days school is not in session (such as Winter Break, development days, etc.)
        Uses a mixture of SimpleDateFormat for simple string comparison and JodaTime for more
        complicated arguments*/

        if (dt.getMonthValue() == 6 && dt.getDayOfMonth() > 15) {
            //Summer break (June)
            infoList.add(bundle.getString("Summer"));
            infoPresent=true;
            todayValid = false;
            tomorrowValid = false;
        } else if (dt.getMonthValue() > 6 && dt.getMonthValue() <= 8) {
            //Summer break (July and August)
            infoList.add(bundle.getString("Summer"));
            infoPresent=true;
            todayValid = false;
            tomorrowValid = false;
        } else if (dt.getMonthValue() == 9 && dt.getDayOfMonth() < 7) {
            //Summer break (September)
            infoList.add(bundle.getString("Summer"));
            infoPresent = true;
            todayValid = false;
            tomorrowValid = false;
        }else if (textdate.equals("September 07 2015")) {
            infoList.add(bundle.getString("YearStart"));
            infoPresent = true;
            todayValid = false;
        }else if (textdate.equals("September 25 2015")) {
            infoList.add(bundle.getString("HC"));
            infoPresent = true;
        }else if (textdate.equals("October 20 2015") || textdate.equals("December 08 2015")
                || textdate.equals("February 02 2016") || textdate.equals("May 03 2016")) {
            infoList.add(bundle.getString("LSTomorrow"));
            infoPresent = true;
        } else if (textdate.equals("October 21 2015") || textdate.equals("December 09 2015")
                || textdate.equals("February 03 2016") || textdate.equals("May 04 2016")) {
            infoList.add(bundle.getString("LSToday"));
            infoPresent = true;
        }else if (textdate.equals("November 26 2015")) {
            infoList.add(bundle.getString("Thanksgiving"));
            infoPresent = true;
            todayValid = false;
            tomorrowValid = false;
        }else if (textdate.equals("November 26 2015") || textdate.equals("November 27 2015")) {
            infoList.add(bundle.getString("ThanksgivingRecess"));
            infoPresent = true;
            todayValid = false;
        } else if (textdate.equals("December 22 2015")) {
            infoList.add(bundle.getString("WinterBreakTomorrow"));
            infoPresent = true;
            tomorrowValid = false;
        } else if (textdate.equals("December 23 2015") || textdate.equals("December 24 2015")
                || textdate.equals("December 25 2015") || textdate.equals("December 26 2015") || textdate.equals("December 27 2014")
                || textdate.equals("December 27 2015") || textdate.equals("December 28 2015")
                || textdate.equals("December 29 2015") || textdate.equals("December 30 2015")
                || textdate.equals("December 31 2015") || textdate.equals("January 01 2016")) {
            //Winter Break
            if (textdate.equals("December 25 2015")) {
                infoList.add(bundle.getString("Christmas"));
                infoPresent = true;
            } else if (textdate.equals("January 01 2016")) {
                infoList.add(bundle.getString("NewYear"));
                infoPresent = true;
            }

            infoList.add(bundle.getString("WinterBreak"));
            infoPresent = true;
        } else if (textdate.equals("January 17 2016")) {
            infoList.add(bundle.getString("MLKTomorrow") + bundle.getString("NoSessionTomorrow"));
            infoPresent = true;
            todayValid = false;
            tomorrowValid = false;
        } else if (textdate.equals("January 18 2016")) {
            //MLK Day
            infoList.add(bundle.getString("MLK") + bundle.getString("NoSessionToday"));
            infoPresent = true;
            todayValid = false;
        }else if (textdate.equals("January 24 2016")) {
            infoList.add(bundle.getString("RecordsTomorrow") + bundle.getString("NoSessionTomorrow"));
            infoPresent = true;
            tomorrowValid = false;
        } else if (textdate.equals("January 25 2016")) {
            infoList.add(bundle.getString("Records") + bundle.getString("NoSessionToday"));
            infoPresent = true;
            todayValid = false;
        }else if (textdate.equals("February 11 2016")) {
            infoList.add(bundle.getString("LincolnTomorrow") + bundle.getString("NoSessionTomorrow"));
            infoPresent = true;
            tomorrowValid = false;
        } else if (textdate.equals("February 12 2016")) {
            infoList.add(bundle.getString("Lincoln") + bundle.getString("NoSessionToday"));
            infoPresent = true;
            todayValid = false;
        } else if (textdate.equals("February 14 2016")) {
            infoList.add(bundle.getString("PresidentTomorrow") + bundle.getString("NoSessionTomorrow"));
            infoPresent = true;
            todayValid = false;
            tomorrowValid = false;
        } else if (textdate.equals("February 15 2016")) {
            infoList.add(bundle.getString("President") + bundle.getString("NoSessionToday"));
            infoPresent = true;
            todayValid = false;
        } else if (textdate.equals("November 10 2015") || textdate.equals("March 08 2016")) {
            infoList.add(bundle.getString("HalfDayConferenceMSTomorrow"));
            infoPresent = true;
        }else if (textdate.equals("November 11 2015") || textdate.equals("November 12 2015")
                || textdate.equals("March 09 2016") || textdate.equals("March 10 2016")) {
            infoList.add(bundle.getString("HalfDayConferenceMSTodayTomorrow"));
            infoPresent = true;
        } else if (textdate.equals("November 13 2015") || textdate.equals("March 11 2016")) {
            infoList.add(bundle.getString("HalfDayConferenceMSToday"));
            infoPresent = true;
        } else if (textdate.equals("November 24 2015") || textdate.equals("September 24 2015")
                || textdate.equals("October 08 2015")
                || textdate.equals("March 31 2016")) {
            infoList.add(bundle.getString("HalfDayTomorrow"));
            infoPresent = true;
        }else if (textdate.equals("November 25 2015") || textdate.equals("September 25 2015")
                || textdate.equals("October 09 2015")) {
            if (textdate.equals("November 25 2015")) {
                infoList.add(bundle.getString("ThanksgivingRecessTomorrow"));
                infoPresent = true;
                tomorrowValid = false;
            }

            infoList.add(bundle.getString("HalfDay"));
            infoPresent = true;
        }else if (textdate.equals("March 24 2016")) {
            infoList.add(bundle.getString("GoodFridayTomorrow") + bundle.getString("NoSessionTomorrow"));
            infoPresent = true;
            tomorrowValid = false;
        }else if (textdate.equals("March 25 2016")) {
            infoList.add(bundle.getString("GoodFriday") + bundle.getString("NoSessionToday"));
            infoPresent = true;
            todayValid = false;
        }else if (textdate.equals("March 27 2016")) {
            infoList.add(bundle.getString("Easter"));
            infoPresent = true;
            todayValid = false;
        } else if (textdate.equals("April 01 2016")) {
            infoList.add(bundle.getString("HalfDay"));
            infoList.add(bundle.getString("SpringBreakTomorrow"));
            infoPresent=true;
            tomorrowValid = false;
        } else if (textdate.equals("April 02 2016") || textdate.equals("April 03 2016")
                || textdate.equals("April 04 2016") || textdate.equals("April 05 2016")
                || textdate.equals("April 06 2016") || textdate.equals("April 07 2016")
                || textdate.equals("April 08 2016")) {
            //Spring Break

            infoList.add(bundle.getString("SpringBreak"));
            infoPresent = true;
            todayValid = false;
            tomorrowValid = false;
        } else if (textdate.equals("November 02 2015") || textdate.equals("April 27 2016")) {
            infoList.add(bundle.getString("PDDTomorrow") + bundle.getString("NoSessionTomorrow"));
            infoPresent = true;
            tomorrowValid = false;
        } else if (textdate.equals("November 03 2015") || textdate.equals("April 28 2016")) {
            infoList.add(bundle.getString("PDD") + bundle.getString("NoSessionToday"));
            infoPresent = true;
            todayValid = false;
        } else if (textdate.equals("May 29 2016")) {
            infoList.add(bundle.getString("MemorialDayTomorrow") + bundle.getString("NoSessionTomorrow"));
            infoPresent = true;
            tomorrowValid = false;
        } else if (textdate.equals("May 30 2016")) {
            infoList.add(bundle.getString("MemorialDay") + bundle.getString("NoSessionToday"));
            infoPresent = true;
            todayValid = false;
        } else if (textdate.equals("June 02 2016")) {
            infoList.add(bundle.getString("Senior"));
            infoPresent = true;
            txtInfo.setStyle("-fx-text-fill: red");
        } else if (textdate.equals("June 15 2016")) {
            infoList.add(bundle.getString("YearEnd"));
            infoPresent = true;
            tomorrowValid = false;
        }

        //If items were added...
        if (infoPresent) {
            txtInfo.setStyle("-fx-text-fill: blue");
        }
        
        //Determine if the calculation should be available
        if (!tomorrowValid && !todayValid) {
            optToday.setDisable(true);
            optToday.setSelected(false);

            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);

        } else if (!tomorrowValid) {
            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);

        } else if (!todayValid) {
            optToday.setDisable(true);
            optToday.setSelected(false);

        }
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

    private void checkWeekend() {
        //Friday is 5
        //Saturday is 6
        //Sunday is 7

        if (weekday == 5) {
            infoList.add(bundle.getString("SaturdayTomorrow"));
            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);
            infoPresent = true;
        } else if (weekday == 6) {
            infoList.add(bundle.getString("SaturdayToday"));
            optToday.setDisable(true);
            optToday.setSelected(false);
            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);
            infoPresent = true;
        } else if (weekday == 7) {
            infoList.add(bundle.getString("SundayToday"));
            optToday.setDisable(true);
            optToday.setSelected(false);
            infoPresent = true;
        }
    }

    public void Calculate() {
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

        /**WJRT SCHOOL CLOSINGS SCRAPER**/
        wjrt = new Thread(new WJRTScraper());
        wjrt.start();


        /**NATIONAL WEATHER SERVICE SCRAPER**/
        nws = new Thread(new WeatherScraper());
        nws.start();

        //Final Percent Calculator
        p = new Thread(new PercentCalculate());
        p.start();

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

        tier1today = 0;
        tier2today = 0;
        tier3today = 0;
        tier4today = 0;

        tier1tomorrow = 0;
        tier2tomorrow = 0;
        tier3tomorrow = 0;
        tier4tomorrow = 0;

        weathertoday = 0;
        weathertomorrow = 0;

        WJRTActive = true;
        NWSActive = true;
        WJRTFail = false;
        NWSFail = false;

        GBAcademy = false;
        WPAcademy = false;
        HolyFamily = false;
        GISD = false;
        Durand = false;
        Holly = false;
        Lapeer = false;
        Owosso = false;
        Beecher = false;
        Clio = false;
        Davison = false;
        Fenton = false;
        Flushing = false;
        Genesee = false;
        Kearsley = false;
        LKFenton = false;
        Linden = false;
        Montrose = false;
        Morris = false;
        SzCreek = false;
        Atherton = false;
        Bendle = false;
        Bentley = false;
        Flint = false;
        Goodrich = false;
        Carman = false;
        GB = false;
        
        closings.clear();

        weatherWarn.clear();
        weatherSummary.clear();
        weatherExpire.clear();
        weatherLink.clear();

        //Add the 27 fixed closings values so they can be set out of sequence
        closings.add(0, "");
        closings.add(1, "");
        closings.add(2, "");
        closings.add(3, "");
        closings.add(4, "");
        closings.add(5, "");
        closings.add(6, "");
        closings.add(7, "");
        closings.add(8, "");
        closings.add(9, "");
        closings.add(10, "");
        closings.add(11, "");
        closings.add(12, "");
        closings.add(13, "");
        closings.add(14, "");
        closings.add(15, "");
        closings.add(16, "");
        closings.add(17, "");
        closings.add(18, "");
        closings.add(19, "");
        closings.add(20, "");
        closings.add(21, "");
        closings.add(22, "");
        closings.add(23, "");
        closings.add(24, "");
        closings.add(25, "");
        closings.add(26, "");

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

    private class WJRTScraper implements Runnable {
        @Override
        public void run() {
            Document schools = null;

            /**WJRT SCHOOL CLOSINGS SCRAPER**/
            //Scrape School Closings from WJRT with Jsoup.
            //Run scraper in an Async task.

            //Get the day of the week as a string.
            weekdaytoday = dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);

            //Get tomorrow's weekday as a string.
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

            weekdaytomorrow = tomorrow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);

            try {
                //This is the current listings page.
                schools = Jsoup.connect("http://abc12.com/closings").timeout(10000).get();

                //Attempt to parse input

                Element table = schools.select("table").last();
                Elements rows = table.select("tr");

                for (int i = 1; i < rows.size(); i++) { //Skip header row
                    Element row = rows.get(i);
                    orgName.add(row.select("td").get(0).text());
                    status.add(row.select("td").get(1).text());
                }

            } catch (IOException e) {

                //Connectivity issues
                txtGB.setText(bundle.getString("WJRTConnectionError") + bundle.getString("NoConnection"));
                WJRTFail = true;

            } catch (NullPointerException | IndexOutOfBoundsException e) {

                if (schools != null) {
                    schooltext = schools.text();
                }else{
                    schooltext = "";
                }

                //This shows in place of the table (as plain text) if no schools or institutions are closed.
                if (schooltext.contains("no closings or delays")) {
                    //No schools are closed.
                    WJRTFail = false;

                    //Add a blank entry so checkClosings() still works correctly
                    orgName.add(" ");

                } else {
                    //Webpage layout was not recognized.
                    txtGB.setText(bundle.getString("WJRTParseError") + bundle.getString("ErrorContact"));
                    WJRTFail = true;
                }
            }

            //Only run if WJRTFail is false to avoid NullPointerExceptions
            if (!WJRTFail) {
                //Sanity check - make sure Grand Blanc isn't already closed before predicting
                checkGBClosed();

                //Check school closings
                checkClosings();

            }

            //WJRT scraper has finished.
            WJRTActive = false;
        }
    }


    private void checkGBClosed() {
        //Checking if Grand Blanc is closed.
        for (int i = 0; i < orgName.size(); i++) {
            //If Grand Blanc hasn't been found...
            if (!GB) {
                if (orgName.get(i).contains("Grand Blanc") && !orgName.get(i).contains("Academy")
                        && !orgName.get(i).contains("Freedom") && !orgName.get(i).contains("Offices")
                        && !orgName.get(i).contains("City") && !orgName.get(i).contains("Senior")
                        && !orgName.get(i).contains("Holy") && !orgName.get(i).contains("Montessori")
                        && !orgName.get(i).contains("Gym")) {
                    txtGB.setText(bundle.getString("GB") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0
                            || status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        txtGB.setText(txtGB.getText() + bundle.getString("SnowDay"));
                        //GB is closed.
                        GB = true;
                    }else{
                        //GB at least has a message.
                        GBMessage = true;
                    }
                    break;
                }
            }
        }

        if (!GB) {
            //Grand Blanc is open.
            if (!GBMessage) {
                txtGB.setText(bundle.getString("GB") +  bundle.getString("NotClosed"));
            }

            if (dayrun == 0) {
                if (dt.getHour() >= 7 && dt.getHour() < 16) {
                    //Time is between 7AM and 4PM. School is already in session.
                    txtGB.setText(txtGB.getText() + bundle.getString("SchoolOpen"));
                    GBOpen = true;
                } else if (dt.getHour() >= 16) {
                    //Time is after 4PM. School is already out.
                    txtGB.setText(txtGB.getText() + bundle.getString("Dismissed"));
                    GBOpen = true;
                }
            }
        }
    }

    private void checkClosings() {
        /*General structure:
        ->Checks for the presence of a school name
        -->If the school appears in the list, show its status entry and set its boolean to "true"
        --->If the status entry contains "Closed Today" and the calculation is being run for 'today',
            increase that tier's 'today' count
        --->If the status entry contains "Closed Tomorrow" and the calculation is being run for 'tomorrow',
            increase that tier's 'tomorrow' count*/

        for (int i = 0; i < orgName.size(); i++) {
            if (!(Atherton)) {
                if (orgName.get(i).contains("Atherton")) {
                    closings.set(1, bundle.getString("Atherton") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Atherton = true;
                } else {
                    closings.set(1, bundle.getString("Atherton") + bundle.getString("Open"));
                }
            }
            if (!(Bendle)) {
                if (orgName.get(i).contains("Bendle")) {
                    closings.set(2, bundle.getString("Bendle") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Bendle = true;
                } else {
                    closings.set(2, bundle.getString("Bendle") + bundle.getString("Open"));
                }
            }
            if (!(Bentley)) {
                if (orgName.get(i).contains("Bentley")) {
                    closings.set(3, bundle.getString("Bentley") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Bentley = true;
                } else {
                    closings.set(3, bundle.getString("Bentley") + bundle.getString("Open"));
                }
            }
            if (!(Carman)) {
                if (orgName.get(i).contains("Carman-Ainsworth") && !orgName.get(i).contains("Senior")) {
                    closings.set(4, bundle.getString("Carman") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Carman = true;
                } else {
                    closings.set(4, bundle.getString("Carman") + bundle.getString("Open"));
                }
            }
            if (!(Flint)) {
                if (orgName.get(i).contains("Flint Community Schools")) {
                    closings.set(5, bundle.getString("Flint") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Flint = true;
                } else {
                    closings.set(5, bundle.getString("Flint") + bundle.getString("Open"));
                }
            }
            if (!(Goodrich)) {
                if (orgName.get(i).contains("Goodrich")) {
                    closings.set(6, bundle.getString("Goodrich") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Goodrich = true;
                } else {
                    closings.set(6, bundle.getString("Goodrich") + bundle.getString("Open"));
                }
            }
            if (!(Beecher)) {
                if (orgName.get(i).contains("Beecher")) {
                    closings.set(7, bundle.getString("Beecher") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Beecher = true;
                } else {
                    closings.set(7, bundle.getString("Beecher") + bundle.getString("Open"));
                }
            }
            if (!(Clio)) {
                if (orgName.get(i).contains("Clio") && !orgName.get(i).contains("Christian")
                        && !orgName.get(i).contains("Senior") && !orgName.get(i).contains("City")
                        && !orgName.get(i).contains("Cornerstone")) {
                    closings.set(8, bundle.getString("Clio") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Clio = true;
                } else {
                    closings.set(8, bundle.getString("Clio") + bundle.getString("Open"));
                }
            }
            if (!(Davison)) {
                if (orgName.get(i).contains("Davison") && !orgName.get(i).contains("Senior")
                        && !orgName.get(i).contains("Faith") && !orgName.get(i).contains("Medical")
                        && !orgName.get(i).contains("Montessori")) {
                    closings.set(9, bundle.getString("Davison") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Davison = true;
                } else {
                    closings.set(9, bundle.getString("Davison") + bundle.getString("Open"));
                }
            }
            if (!(Fenton)) {
                if (orgName.get(i).contains("Fenton") && !orgName.get(i).contains("Lake")
                        && !orgName.get(i).contains("City") && !orgName.get(i).contains("Academy")
                        && !orgName.get(i).contains("Montessori")) {
                    closings.set(10, bundle.getString("Fenton") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Fenton = true;
                } else {
                    closings.set(10, bundle.getString("Fenton") + bundle.getString("Open"));
                }
            }
            if (!(Flushing)) {
                if (orgName.get(i).contains("Flushing") && !orgName.get(i).contains("Senior")
                        && !orgName.get(i).contains("Robert")) {
                    closings.set(11, bundle.getString("Flushing") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Flushing = true;
                } else {
                    closings.set(11, bundle.getString("Flushing") + bundle.getString("Open"));
                }
            }
            if (!(Genesee)) {
                if (orgName.get(i).contains("Genesee") && !orgName.get(i).contains("Freedom")
                        && !orgName.get(i).contains("Christian") && !orgName.get(i).contains("Library")
                        && !orgName.get(i).contains("Mobile") && !orgName.get(i).contains("Programs")
                        && !orgName.get(i).contains("Health") && !orgName.get(i).contains("Medical")
                        && !orgName.get(i).contains("Academy") && !orgName.get(i).contains("Skill")
                        && !orgName.get(i).contains("Sys") && !orgName.get(i).contains("STEM")
                        && !orgName.get(i).contains("Court") && !orgName.get(i).contains("County")
                        && !orgName.get(i).contains("Job") && !orgName.get(i).contains("Leadership")
                        && !orgName.get(i).contains("Freedom") && !orgName.get(i).contains("MSU")
                        && !orgName.get(i).contains("I.S.D.") && !orgName.get(i).contains("Foster")) {
                    closings.set(12, bundle.getString("Genesee") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Genesee = true;
                } else {
                    closings.set(12, bundle.getString("Genesee") + bundle.getString("Open"));
                }
            }
            if (!(Kearsley)) {
                if (orgName.get(i).contains("Kearsley")) {
                    closings.set(13, bundle.getString("Kearsley") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Kearsley = true;
                } else {
                    closings.set(13, bundle.getString("Kearsley") + bundle.getString("Open"));
                }
            }
            if (!(LKFenton)) {
                if (orgName.get(i).contains("Lake Fenton")) {
                    closings.set(14, bundle.getString("LKFenton") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    LKFenton = true;
                } else {
                    closings.set(14, bundle.getString("LKFenton") + bundle.getString("Open"));
                }
            }
            if (!(Linden)) {
                if (orgName.get(i).contains("Linden") && !orgName.get(i).contains("Charter")) {
                    closings.set(15, bundle.getString("Linden") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Linden = true;
                } else {
                    closings.set(15, bundle.getString("Linden") + bundle.getString("Open"));
                }
            }
            if (!(Montrose)) {
                if (orgName.get(i).contains("Montrose") && !orgName.get(i).contains("Senior")) {
                    closings.set(16, bundle.getString("Montrose") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    } else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Montrose = true;
                } else {
                    closings.set(16, bundle.getString("Montrose") + bundle.getString("Open"));
                }
            }
            if (!(Morris)) {
                if (orgName.get(i).contains("Mt. Morris") && !orgName.get(i).contains("Administration")
                        && !orgName.get(i).contains("Twp") && !orgName.get(i).contains("Mary")) {
                    closings.set(17, bundle.getString("Morris") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Morris = true;
                } else {
                    closings.set(17, bundle.getString("Morris") + bundle.getString("Open"));
                }
            }
            if (!(SzCreek)) {
                if (orgName.get(i).contains("Swartz Creek") && !orgName.get(i).contains("Senior")
                        && !orgName.get(i).contains("Montessori")) {
                    closings.set(18, bundle.getString("SzCreek") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    SzCreek = true;
                } else {
                    closings.set(18, bundle.getString("SzCreek") + bundle.getString("Open"));
                }
            }
            if (!(Durand)) {
                if (orgName.get(i).contains("Durand") && !orgName.get(i).contains("Senior")) {
                    closings.set(19, bundle.getString("Durand") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Durand = true;
                } else {
                    closings.set(19, bundle.getString("Durand") + bundle.getString("Open"));
                }
            }
            if (!(Holly)) {
                if (orgName.get(i).contains("Holly") && !orgName.get(i).contains("Academy")) {
                    closings.set(20, bundle.getString("Holly") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Holly = true;
                } else {
                    closings.set(20, bundle.getString("Holly") + bundle.getString("Open"));
                }
            }
            if (!(Lapeer)) {
                if (orgName.get(i).contains("Lapeer") && !orgName.get(i).contains("Chatfield")
                        && !orgName.get(i).contains("Greater") && !orgName.get(i).contains("CMH")
                        && !orgName.get(i).contains("Tech") && !orgName.get(i).contains("Team")
                        && !orgName.get(i).contains("Center") && !orgName.get(i).contains("Special")
                        && !orgName.get(i).contains("Growth") && !orgName.get(i).contains("Offices")
                        && !orgName.get(i).contains("Library") && !orgName.get(i).contains("Head")
                        && !orgName.get(i).contains("Senior") && !orgName.get(i).contains("Foster")
                        && !orgName.get(i).contains("Davenport") && !orgName.get(i).contains("MSU")
                        && !orgName.get(i).contains("Paul") && !orgName.get(i).contains("Connections")) {
                    closings.set(21, bundle.getString("Lapeer") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Lapeer = true;
                } else {
                    closings.set(21, bundle.getString("Lapeer") + bundle.getString("Open"));
                }
            }
            if (!(Owosso)) {
                if (orgName.get(i).contains("Owosso") && !orgName.get(i).contains("Christian")
                        && !orgName.get(i).contains("Senior") && !orgName.get(i).contains("Adventist")
                        && !orgName.get(i).contains("Baker") && !orgName.get(i).contains("Paul")
                        && !orgName.get(i).contains("Security")) {
                    closings.set(22, bundle.getString("Owosso") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Owosso = true;
                } else {
                    closings.set(22, bundle.getString("Owosso") + bundle.getString("Open"));
                }
            }
            if (!(GBAcademy)) {
                if (orgName.get(i).contains("Grand Blanc Academy")) {
                    closings.set(23, bundle.getString("GBAcademy") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    GBAcademy = true;
                } else {
                    closings.set(23, bundle.getString("GBAcademy") + bundle.getString("Open"));
                }
            }
            if (!(GISD)) {
                if (orgName.get(i).contains("Genesee I.S.D.")) {
                    closings.set(24, bundle.getString("GISD") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    GISD = true;
                } else {
                    closings.set(24, bundle.getString("GISD") + bundle.getString("Open"));
                }
            }
            if (!(HolyFamily)) {
                if (orgName.get(i).contains("Holy Family")) {
                    closings.set(25, bundle.getString("HolyFamily") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    HolyFamily = true;
                } else {
                    closings.set(25, bundle.getString("HolyFamily") + bundle.getString("Open"));
                }
            }
            if (!(WPAcademy)) {
                if (orgName.get(i).contains("Woodland Park Academy")) {
                    closings.set(26, bundle.getString("WPAcademy") + status.get(i));
                    if (status.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || status.get(i).contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (status.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || status.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    WPAcademy = true;
                } else {
                    closings.set(26, bundle.getString("WPAcademy") + bundle.getString("Open"));
                }
            }
        }
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

            while (WJRTActive || NWSActive) {
                try {
                    //Wait for scrapers to finish before continuing
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            //Set the schoolpercent
            if (tier1today > 2 && dayrun == 0
                    || tier1tomorrow > 2 && dayrun == 1) {
                //3+ academies are closed. 20% schoolpercent.
                schoolpercent = 20;
            }
            if (tier2today > 2 && dayrun == 0
                    || tier2tomorrow > 2 && dayrun == 1) {
                //3+ schools in nearby counties are closed. 40% schoolpercent.
                schoolpercent = 40;
            }
            if (tier3today > 2 && dayrun == 0
                    || tier3tomorrow > 2 && dayrun == 1) {
                //3+ schools in Genesee County are closed. 60% schoolpercent.
                schoolpercent = 60;
            }
            if (tier4today > 2 && dayrun == 0
                    || tier4tomorrow > 2 && dayrun == 1) {
                //3+ schools near GB are closed. 80% schoolpercent.
                schoolpercent = 80;
                if (Carman) {
                    //Carman is closed along with three close schools. 90% schoolpercent.
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
            if (GB) {
                //WJRTScraper reports Grand Blanc is closed. Override percentage, set to 100%
                percent = 100;
            }else if (GBOpen) {
                //GB is false and the time is during or after school hours. 0% chance.
                percent = 0;
            }

            lblPercent.setVisible(true);
            Platform.runLater(() -> lblPercent.setText("0%"));

            lblPercent.setStyle("-fx-text-fill: red");

            //Animate lblPercent
            if (WJRTFail && NWSFail) {
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
                
            } else if (WJRTFail || NWSFail) {
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

                if (!WJRTFail) {
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
                
                //If the school is closed, make it orange.
                if (Atherton) {
                    txtAtherton.setStyle("-fx-control-inner-background: orange");
                }if (Bendle) {
                    txtBendle.setStyle("-fx-control-inner-background: orange");
                }if (Bentley) {
                    txtBentley.setStyle("-fx-control-inner-background: orange");
                }if (Carman) {
                    txtCarman.setStyle("-fx-control-inner-background: orange");
                }if (Flint) {
                    txtFlint.setStyle("-fx-control-inner-background: orange");
                }if (Goodrich) {
                    txtGoodrich.setStyle("-fx-control-inner-background: orange");
                }if (Beecher) {
                    txtBeecher.setStyle("-fx-control-inner-background: orange");
                }if (Clio) {
                    txtClio.setStyle("-fx-control-inner-background: orange");
                }if (Davison) {
                    txtDavison.setStyle("-fx-control-inner-background: orange");
                }if (Fenton) {
                    txtFenton.setStyle("-fx-control-inner-background: orange");
                }if (Flushing) {
                    txtFlushing.setStyle("-fx-control-inner-background: orange");
                }if (Genesee) {
                    txtGenesee.setStyle("-fx-control-inner-background: orange");
                }if (Kearsley) {
                    txtKearsley.setStyle("-fx-control-inner-background: orange");
                }if (LKFenton) {
                    txtLKFenton.setStyle("-fx-control-inner-background: orange");
                }if (Linden) {
                    txtLinden.setStyle("-fx-control-inner-background: orange");
                }if (Montrose) {
                    txtMontrose.setStyle("-fx-control-inner-background: orange");
                }if (Morris) {
                    txtMorris.setStyle("-fx-control-inner-background: orange");
                }if (SzCreek) {
                    txtSzCreek.setStyle("-fx-control-inner-background: orange");
                }if (Durand) {
                    txtDurand.setStyle("-fx-control-inner-background: orange");
                }if (Holly) {
                    txtHolly.setStyle("-fx-control-inner-background: orange");
                }if (Lapeer) {
                    txtLapeer.setStyle("-fx-control-inner-background: orange");
                }if (Owosso) {
                    txtOwosso.setStyle("-fx-control-inner-background: orange");
                }if (GBAcademy) {
                    txtGBAcademy.setStyle("-fx-control-inner-background: orange");
                }if (GISD) {
                    txtGISD.setStyle("-fx-control-inner-background: orange");
                }if (HolyFamily) {
                    txtHolyFamily.setStyle("-fx-control-inner-background: orange");
                }if (WPAcademy) {
                    txtWPAcademy.setStyle("-fx-control-inner-background: orange");
                }

                txtAtherton.setText(closings.get(1));
                txtBendle.setText(closings.get(2));
                txtBentley.setText(closings.get(3));
                txtCarman.setText(closings.get(4));
                txtFlint.setText(closings.get(5));
                txtGoodrich.setText(closings.get(6));
                txtBeecher.setText(closings.get(7));
                txtClio.setText(closings.get(8));
                txtDavison.setText(closings.get(9));
                txtFenton.setText(closings.get(10));
                txtFlushing.setText(closings.get(11));
                txtGenesee.setText(closings.get(12));
                txtKearsley.setText(closings.get(13));
                txtLKFenton.setText(closings.get(14));
                txtLinden.setText(closings.get(15));
                txtMontrose.setText(closings.get(16));
                txtMorris.setText(closings.get(17));
                txtSzCreek.setText(closings.get(18));
                txtDurand.setText(closings.get(19));
                txtHolly.setText(closings.get(20));
                txtLapeer.setText(closings.get(21));
                txtOwosso.setText(closings.get(22));
                txtGBAcademy.setText(closings.get(23));
                txtGISD.setText(closings.get(24));
                txtHolyFamily.setText(closings.get(25));
                txtWPAcademy.setText(closings.get(26));
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
