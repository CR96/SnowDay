package snowday;


import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class SnowDayController {
    /*Copyright 2014 Corey Rowe
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
         http:www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.*/

    //Declare scene controls
    public Button btnCalculate;
    public MenuItem itemAbout;
    public Label lblDay;
    public Label lblPercent;
    public Label lblPrev;

    public ComboBox lstDays;
    public RadioButton optToday;
    public RadioButton optTomorrow;

    public Label lblError;
    public ProgressBar progCalculate;

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
    public TextArea txtWeather;

    //Declare variables
    String date;

    int dayscount = 0;
    boolean todayValid;
    boolean tomorrowValid;
    boolean reminder;
    boolean bobcats;

    int days;
    int dayrun;

    //Declare lists that will be used in ListAdapters
    ArrayList<String> infoList = new ArrayList<>();
    ArrayList<Integer> daysarray = new ArrayList<>();
    int infoCount = 1;

    //Figure out what tomorrow is
    //Saturday = 6, Sunday = 7

    DateTime dt = new DateTime();
    int weekday = dt.getDayOfWeek();

    String orgName;
    String status;
    String schooltext;

    String[] orgNameLine;
    String[] statusLine;
    String[] weatherwarn;

    //Declare lists that will be used in ListAdapters
    ArrayList<String> GBInfo = new ArrayList<>();
    ArrayList<String> closings = new ArrayList<>();
    ArrayList<String> wjrtInfo = new ArrayList<>();
    ArrayList<String> weather = new ArrayList<>();
    ArrayList<String> nwsInfo = new ArrayList<>();

    int GBCount = 1;
    int weatherCount = 0;
    int wjrtCount = 0;
    int nwsCount = 0;

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
    boolean Lapeer; //Check for "Chatfield School-Lapeer", "Greater Lapeer Transit Authority",
    // "Lapeer CMH Day Programs", "Lapeer Co. Ed-Tech Center", "Lapeer County Ofices", "
    // Lapeer District Library", "Lapeer Senior Center", and "St. Paul Lutheran-Lapeer"
    boolean Owosso; //Check for "Owosso Senior Center", "Baker College-Owosso", "Owosso Social Security Office",
    // and "St. Paul Catholic-Owosso"

    boolean Beecher;
    boolean Clio; //Check for "Clio Area Senior Center", "Clio City Hall", and "Cornerstone Clio"
    boolean Davison; //Check for "Davison Senior Center", "Faith Baptist School-Davison", "Montessori Academy-Davison",
    // and "Ross Medical Education-Davison"
    boolean Fenton; //Check for "Lake Fenton", "Fenton City Hall", and "Fenton Montessori Academy"
    boolean Flushing; //Check for "Flushing Senior Citizens Center" and "St. Robert-Flushing"
    boolean Genesee; //Check for "Freedom Work-Genesee Co.", "Genesee Christian-Burton", and "Genesee District Library",
    // "Genesee Co. Mobile Meals", "Genesee Hlth Sys Day Programs", "Genesee Stem Academy", and "Genesee I.S.D."
    boolean Kearsley;
    boolean LKFenton;
    boolean Linden; //Check for "Linden Charter Academy"
    boolean Montrose; //Check for "Montrose Senior Center"
    boolean Morris;  //Check for "Mt Morris Twp Administration" and "St. Mary's-Mt. Morris"
    boolean SzCreek; //Check for "Swartz Creek Area Senior Ctr." and "Swartz Creek Montessori"

    boolean Atherton;
    boolean Bendle;
    boolean Bentley;
    boolean Carman; //Check for "Carman-Ainsworth Senior Ctr."
    boolean Flint; //Thankfully this is listed as "Flint Community Schools" -
    // otherwise there would be 25 exceptions to check for.
    boolean Goodrich;

    boolean GB; //Check for "Freedom Work-Grand Blanc", "Grand Blanc Academy", "Grand Blanc City Offices",
    // "Grand Blanc Senior Center", and "Holy Family-Grand Blanc"

    //True when Grand Blanc is already open (GB will be false - only checked
    //during or after school hours)
    boolean GBOpen;

    //Every weather warning this program searches for
    boolean SigWeather;
    boolean WinterAdvisory;
    boolean WinterWatch;
    boolean LakeSnowAdvisory;
    boolean LakeSnowWatch;
    boolean Rain;
    boolean Drizzle;
    boolean Fog;
    boolean WindChillAdvisory;
    boolean WindChillWatch;
    boolean BlizzardWatch;
    boolean WinterWarn;
    boolean LakeSnowWarn;
    boolean IceStorm;
    boolean WindChillWarn;
    boolean BlizzardWarn;

    //Scraper status
    boolean WJRTActive = true;
    boolean NWSActive = true;

    /*Used for catching IOExceptions / NullPointerExceptions if there are connectivity issues
    or a webpage is down*/
    boolean WJRTFail;
    boolean NWSFail;

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

    public void showAboutDialog() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("About");
        stage.getIcons().add(new javafx.scene.image.Image(Main.class.getResourceAsStream("icons/icon.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        Pane pane = FXMLLoader.load(getClass().getResource("about.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    public void openClosingsWeb() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("http://www.abc12.com/closings"));
    }

    public void openWeatherWeb() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("http://forecast.weather.gov/afm/PointClick.php?lat=42.9275&lon=-83.6299"));
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
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd yyyy");
        date = currentDate.format(calendar.getTime());

        infoList.add(0, "Current Date: " + date);

        /*Check for days school is not in session (such as Winter Break, development days, etc.)
        Uses a mixture of SimpleDateFormat for simple string comparison and JodaTime for more
        complicated arguments*/

        if (date.equals("December 09 2014") || date.equals("February 03 2015")
                || date.equals("May 05 2015")) {
            infoList.add(infoCount, "REMINDER: Tomorrow is a Late Start.");
            infoCount++;
        } else if (date.equals("December 10 2014") || date.equals("February 04 2015")
                || date.equals("May 06 2015")) {
            infoList.add(infoCount, "REMINDER: Today is a Late Start.");
            infoCount++;
        } else if (date.equals("December 21 2014")) {
            infoList.add(infoCount, "Winter Break begins tomorrow.");
            infoCount++;
            tomorrowValid = false;
        } else if (date.equals("December 22 2014") || date.equals("December 23 2014")
                || date.equals("December 24 2014") || date.equals("December 25 2014")
                || date.equals("December 26 2014") || date.equals("December 27 2014")
                || date.equals("December 28 2014") || date.equals("December 29 2014")
                || date.equals("December 30 2014") || date.equals("December 31 2014")
                || date.equals("January 01 2015") || date.equals("January 02 2015")) {
            //Winter Break
            if (date.equals("December 25 2014")) {
                infoList.add(infoCount, "Merry Christmas!");
                infoCount++;
            } else if (date.equals("January 01 2015")) {
                infoList.add(infoCount, "Happy New Year!");
                infoCount++;
            }

            infoList.add(infoCount, "Enjoy your winter break!");
            infoCount++;
            todayValid = false;
            tomorrowValid = false;
        } else if (date.equals("January 18 2015")) {
            infoList.add(infoCount, "Tomorrow is Martin Luther King Day. School will not be in session.");
            infoCount++;
            todayValid = false;
            tomorrowValid = false;
        } else if (date.equals("January 19 2015")) {
            //MLK Day
            infoList.add(infoCount, "Happy Martin Luther King Day! School is not in session.");
            infoCount += 2;
            infoCount++;
            todayValid = false;
            //Special case: teacher records day is the following day
            tomorrowValid = false;
        } else if (date.equals("January 20 2015")) {
            infoList.add(infoCount, "Today is Teacher Records Day. School is not in session.");
            infoCount++;
            todayValid = false;
        } else if (date.equals("February 15 2015")) {
            infoList.add(infoCount, "Tomorrow is President's Day. School will not be in session.");
            infoCount++;
            todayValid = false;
            tomorrowValid = false;
        } else if (date.equals("February 16 2015")) {
            infoList.add(infoCount, "Happy President's Day! School is not in session.");
            infoCount++;
            todayValid = false;
        } else if (date.equals("March 10 2015")) {
            infoList.add(infoCount, "REMINDER: Tomorrow is a Half Day for elementary and middle school students.");
            infoCount++;
        }else if (date.equals("March 11 2015") || date.equals("March 12 2015")) {
            infoList.add(infoCount, "REMINDER: Today and Tomorrow are Half Days for elementary and middle school students.");
            infoCount++;
        } else if (date.equals("March 13 2015")) {
            infoList.add(infoCount, "REMINDER: Today is a Half Day for elementary and middle school students.");
            infoCount++;
        } else if (date.equals("April 01 2015")) {
            infoList.add(infoCount, "REMINDER: Tomorrow is a Half Day.");
            infoCount++;
        } else if (date.equals("April 02 2015")) {
            infoList.add(infoCount, "REMINDER: Today is a Half Day.");
            infoList.add(infoCount, "Spring Break begins tomorrow.");
            infoCount++;
            tomorrowValid = false;
        } else if (date.equals("April 03 2015") || date.equals("April 04 2015")
                || date.equals("April 05 2015") || date.equals("April 06 2015")
                || date.equals("April 07 2015") || date.equals("April 08 2015")
                || date.equals("April 09 2015") || date.equals("April 10 2015")) {
            //Spring Break
            if (date.equals("April 05 2015")) {
                infoList.add(infoCount, "Happy Easter!");
                infoCount++;
            }

            infoList.add(infoCount, "Enjoy your Spring Break!");
            infoCount++;
            todayValid = false;
            tomorrowValid = false;
        } else if (date.equals("April 22 2015")) {
            infoList.add(infoCount, "Tomorrow is Staff Professional Development Day. School will not be in session.");
            infoCount++;
            tomorrowValid = false;
        } else if (date.equals("April 23 2015")) {
            infoList.add(infoCount, "Today is Staff Professional Development Day. School is not in session.");
            infoCount++;
            todayValid = false;
        } else if (date.equals("April 24 2015")) {
            infoList.add(infoCount, "Tomorrow is Memorial Day. School will not be in session.");
            infoCount++;
            tomorrowValid = false;
        } else if (date.equals("April 25 2015")) {
            infoList.add(infoCount, "Happy Memorial Day! School is not in session.");
            infoCount++;
            todayValid = false;
        } else if (date.equals("May 19 2015")) {
            infoList.add(infoCount, "Congratulations Senior Class of 2015!");
            infoCount++;
            bobcats = true;
        } else if (date.equals("June 11 2015")) {
            infoList.add(infoCount, "Today is the last day of school!");
            infoCount++;
            tomorrowValid = false;
        } else if (dt.getMonthOfYear() == 6 && dt.getDayOfMonth() > 11) {
            //Summer break (June)
            infoList.add(infoCount, "Enjoy your Summer!");
            infoCount++;
            todayValid = false;
            tomorrowValid = false;
        } else if (dt.getMonthOfYear() > 6 && dt.getMonthOfYear() < 8) {
            //Summer break (July and August)
            infoList.add(infoCount, "Enjoy your Summer!");
            infoCount++;
            todayValid = false;
            tomorrowValid = false;
        } else if (dt.getMonthOfYear() == 9) {
            /*Summmer break (September)
            Conditions for the first day of the 2015-16 year
            to be determined*/
        }

        //If items were added...
        reminder = infoCount > 1;


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
        daysarray.add(dayscount, lstDays.getSelectionModel().getSelectedIndex());
        dayscount++;
        int[] specialarray1 = {2, 6, 0, 6, 2, 0, 1, 0};
        int[] specialarray2 = {2, 0, 4, 2, 0, 1};
        if (daysarray.toString().equals(Arrays.toString(specialarray1))) {
            txtInfo.setText(txtInfo.getText() + "\n\nSilence will fall when the question is asked.");
            txtInfo.setStyle("-fx-text-fill: blue");
        }else if (daysarray.toString().equals(Arrays.toString(specialarray2))) {
            txtInfo.setText(txtInfo.getText() + "\n\nThe Doctor\n22/04/2011\n5:02pm\nLake Silencio, Utah");
            txtInfo.setStyle("-fx-text-fill: blue");
        }
    }

    private void checkWeekend() {
        //Friday is 5
        //Saturday is 6
        //Sunday is 7

        if (weekday == 5) {
            infoList.add(infoCount, "Tomorrow is Saturday.");
            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);
            infoCount++;
        } else if (weekday == 6) {
            infoList.add(infoCount, "Today is Saturday. Try again tomorrow!");
            optToday.setDisable(true);
            optToday.setSelected(false);
            optTomorrow.setDisable(true);
            optTomorrow.setSelected(false);
            infoCount++;
        } else if (weekday == 7) {
            infoList.add(infoCount, "Today is Sunday.");
            optToday.setDisable(true);
            optToday.setSelected(false);
            infoCount++;
        }
    }

    public void Calculate() throws InterruptedException {
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

        lblPercent.setVisible(false);

        txtWeather.setDisable(true);
        txtGB.setDisable(true);
        scrClosings.setDisable(true);

        lblPercent.setText("");
        txtGB.setText("");
        txtWeather.setText("");

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

        SigWeather = false;
        WinterAdvisory = false;
        WinterWatch = false;
        LakeSnowAdvisory = false;
        LakeSnowWatch = false;
        Rain = false;
        Drizzle = false;
        Fog = false;
        WindChillAdvisory = false;
        WindChillWatch = false;
        BlizzardWatch = false;
        WinterWarn = false;
        LakeSnowWarn = false;
        IceStorm = false;
        WindChillWarn = false;
        BlizzardWarn = false;

        closings.clear();
        wjrtInfo.clear();
        weather.clear();
        nwsInfo.clear();
        GBInfo.clear();

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

        //Add the first GBInfo value so it can be set out of sequence
        GBInfo.add(0, "");

        //Add the first weather value so it can be set out of sequence
        weather.add(0, "");

        wjrtCount = 0;
        weatherCount = 0;
        nwsCount = 0;
        GBCount = 1;

        txtAtherton.setText("Atherton:");
        txtAtherton.setStyle("-fx-control-inner-background: white");

        txtBendle.setText("Bendle:");
        txtBendle.setStyle("-fx-control-inner-background: white");

        txtBentley.setText("Bentley:");
        txtBentley.setStyle("-fx-control-inner-background: white");

        txtCarman.setText("Carman-Ainsworth:");
        txtCarman.setStyle("-fx-control-inner-background: white");

        txtFlint.setText("Flint:");
        txtFlint.setStyle("-fx-control-inner-background: white");

        txtGoodrich.setText("Goodrich:");
        txtGoodrich.setStyle("-fx-control-inner-background: white");

        txtBeecher.setText("Beecher:");
        txtBeecher.setStyle("-fx-control-inner-background: white");

        txtClio.setText("Clio:");
        txtClio.setStyle("-fx-control-inner-background: white");

        txtDavison.setText("Davison:");
        txtDavison.setStyle("-fx-control-inner-background: white");

        txtFenton.setText("Fenton:");
        txtFenton.setStyle("-fx-control-inner-background: white");

        txtFlushing.setText("Flushing:");
        txtFlushing.setStyle("-fx-control-inner-background: white");

        txtGenesee.setText("Genesee:");
        txtGenesee.setStyle("-fx-control-inner-background: white");

        txtKearsley.setText("Kearsley:");
        txtKearsley.setStyle("-fx-control-inner-background: white");

        txtLKFenton.setText("Lake Fenton:");
        txtLKFenton.setStyle("-fx-control-inner-background: white");

        txtLinden.setText("Linden:");
        txtLinden.setStyle("-fx-control-inner-background: white");

        txtMontrose.setText("Montrose:");
        txtMontrose.setStyle("-fx-control-inner-background: white");

        txtMorris.setText("Mount Morris:");
        txtMorris.setStyle("-fx-control-inner-background: white");

        txtSzCreek.setText("Swartz Creek:");
        txtSzCreek.setStyle("-fx-control-inner-background: white");

        txtDurand.setText("Durand:");
        txtDurand.setStyle("-fx-control-inner-background: white");

        txtHolly.setText("Holly:");
        txtHolly.setStyle("-fx-control-inner-background: white");

        txtLapeer.setText("Lapeer:");
        txtLapeer.setStyle("-fx-control-inner-background: white");

        txtOwosso.setText("Owosso:");
        txtOwosso.setStyle("-fx-control-inner-background: white");

        txtGBAcademy.setText("Grand Blanc Academy:");
        txtGBAcademy.setStyle("-fx-control-inner-background: white");

        txtGISD.setText("Genesee I.S.D.:");
        txtGISD.setStyle("-fx-control-inner-background: white");

        txtHolyFamily.setText("Holy Family:");
        txtHolyFamily.setStyle("-fx-control-inner-background: white");

        txtWPAcademy.setText("Woodland Park Academy:");
        txtWPAcademy.setStyle("-fx-control-inner-background: white");

        btnCalculate.setDisable(true);

        //progCalculate.setString("");
        progCalculate.setProgress(0);
    }

    private class WJRTScraper implements Runnable {
        @Override
        public void run() {
            Document schools;

            /**WJRT SCHOOL CLOSINGS SCRAPER**/
            //Scrape School Closings from WJRT with Jsoup.
            //Run scraper in an Async task.

            //This is the current listings page.

            try {
                schools = Jsoup.connect("http://gray.ftp.clickability.com/wjrt/school/closings.htm").get();
                //Attempt to parse input
                schools.select("td[bgcolor]").stream().map((row) -> {
                    //Reading closings - name of institution and status
                    orgName = orgName + "\n" + (row.select("font.orgname").first().text());
                    return row;
                }).forEach((row) -> status = status + "\n" + (row.select("font.status").first().text()));

                //20% complete
                progCalculate.setProgress(20);



                //Checking for null pointers not caught by NullPointerException
                if (orgName == null || status == null) {
                    //orgName or status is null.
                    schooltext = schools.text();
                    //This shows in place of the table (as plain text) if no schools or institutions are closed.
                    if (schooltext.contains("no active records")) {
                        //No schools are closed.
                        wjrtInfo.add(wjrtCount, "No schools are closed.");
                        wjrtCount++;
                        WJRTFail = false;
                    } else {
                        //Webpage layout was not recognized.
                        wjrtInfo.add(wjrtCount, "Unable to read WJRT closings.");
                        wjrtInfo.add(wjrtCount + 1, "If this error persists please contact the developer.");
                        wjrtCount += 2;
                        WJRTFail = true;

                    }

                    //orgName and status have no content.
                    //Set dummy content so the scraper doesn't fail with a NullPointerException.
                    orgName = "DummyLine1\nDummyLine2\nDummyLine3";
                    status = "DummyLine1\nDummyLine2\nDummyLine3";

                }

            } catch (IOException e) {
                //Connectivity issues
                wjrtInfo.add(wjrtCount, "Could not connect to ABC 12.");
                wjrtInfo.add(wjrtCount + 1, "Check your internet connection.");
                wjrtCount += 2;
                WJRTFail = true;

            } catch (NullPointerException e) {
                //Webpage layout was not recognized.
                wjrtInfo.add(wjrtCount, "Unable to read WJRT closings.");
                wjrtInfo.add(wjrtCount + 1, "If this error persists please contact the developer.");
                wjrtCount += 2;
                WJRTFail = true;
            }

            //Only run if WJRTFail is false to avoid NullPointerExceptions
            if (!WJRTFail) {
                //Splitting orgName and status strings by line break.
                //Saving to orgNameLine and statusLine.
                //This will create string arrays that can be parsed by for loops.
                orgNameLine = orgName.split("\n");
                statusLine = status.split("\n");


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
        //Checking if GB is closed.
        for (int i = 1; i < orgNameLine.length; i++) {
            //If GB hasn't been found...
            if (!GB) {
                if (orgNameLine[i].contains("Grand Blanc") && !orgNameLine[i].contains("Academy")
                        && !orgNameLine[i].contains("Freedom") && !orgNameLine[i].contains("Offices")
                        && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("Holy")) {
                    GBInfo.set(0, "Grand Blanc: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0
                            || statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        GBInfo.add(GBCount, "Enjoy your Snow Day!");
                        GBCount++;
                        //GB Found
                        GB = true;
                    }
                    break;
                }
            }
        }

        if (!GB) {
            //If GB is still false, GB is open
            if (dayrun == 0) {
                GBInfo.set(0, "Grand Blanc: Open Today");
            }else if (dayrun == 1) {
                GBInfo.set(0, "Grand Blanc: Open Tomorrow");
            }
            if (dt.getHourOfDay() >= 7 && dt.getHourOfDay() < 16 && dayrun == 0) {
                //Time is between 7AM and 4PM. School is already in session.
                GBInfo.add(GBCount, "School is already in session.");
                GBCount++;
                GBOpen = true;
            } else if (dt.getHourOfDay() >= 16 && dayrun == 0) {
                //Time is after 4PM. School is already out.
                GBInfo.add(GBCount, "School has already been dismissed.");
                GBCount++;
                GBOpen = true;
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

        for (int i = 1; i < orgNameLine.length; i++) {
            if (!(Atherton)) {
                if (orgNameLine[i].contains("Atherton")) {
                    closings.set(1, "Atherton: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Atherton = true;
                } else {
                    closings.set(1, "Atherton: OPEN");
                }
            }
            if (!(Bendle)) {
                if (orgNameLine[i].contains("Bendle")) {
                    closings.set(2, "Bendle: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Bendle = true;
                } else {
                    closings.set(2, "Bendle: OPEN");
                }
            }
            if (!(Bentley)) {
                if (orgNameLine[i].contains("Bentley")) {
                    closings.set(3, "Bentley: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Bentley = true;
                } else {
                    closings.set(3, "Bentley: OPEN");
                }
            }
            if (!(Carman)) {
                if (orgNameLine[i].contains("Carman-Ainsworth") && !orgNameLine[i].contains("Senior")) {
                    closings.set(4, "Carman-Ainsworth: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Carman = true;
                } else {
                    closings.set(4, "Carman: OPEN");
                }
            }
            if (!(Flint)) {
                if (orgNameLine[i].contains("Flint Community Schools")) {
                    closings.set(5, "Flint: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Flint = true;
                } else {
                    closings.set(5, "Flint: OPEN");
                }
            }
            if (!(Goodrich)) {
                if (orgNameLine[i].contains("Goodrich")) {
                    closings.set(6, "Goodrich: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier4today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier4tomorrow++;
                    }
                    Goodrich = true;
                } else {
                    closings.set(6, "Goodrich: OPEN");
                }
            }
            if (!(Beecher)) {
                if (orgNameLine[i].contains("Beecher")) {
                    closings.set(7, "Beecher: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Beecher = true;
                } else {
                    closings.set(7, "Beecher: OPEN");
                }
            }
            if (!(Clio)) {
                if (orgNameLine[i].contains("Clio") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Cornerstone")) {
                    closings.set(8, "Clio: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Clio = true;
                } else {
                    closings.set(8, "Clio: OPEN");
                }
            }
            if (!(Davison)) {
                if (orgNameLine[i].contains("Davison") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("Faith") && !orgNameLine[i].contains("Medical")
                        && !orgNameLine[i].contains("Montessori")) {
                    closings.set(9, "Davison: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Davison = true;
                } else {
                    closings.set(9, "Davison: OPEN");
                }
            }
            if (!(Fenton)) {
                if (orgNameLine[i].contains("Fenton") && !orgNameLine[i].contains("Lake")
                        && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Montessori")) {
                    closings.set(10, "Fenton: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Fenton = true;
                } else {
                    closings.set(10, "Fenton: OPEN");
                }
            }
            if (!(Flushing)) {
                if (orgNameLine[i].contains("Flushing") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("Robert")) {
                    closings.set(11, "Flushing: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Flushing = true;
                } else {
                    closings.set(11, "Flushing: OPEN");
                }
            }
            if (!(Genesee)) {
                if (orgNameLine[i].contains("Genesee") && !orgNameLine[i].contains("Freedom")
                        && !orgNameLine[i].contains("Christian") && !orgNameLine[i].contains("Library")
                        && !orgNameLine[i].contains("Mobile") && !orgNameLine[i].contains("Programs")
                        && !orgNameLine[i].contains("Hlth") && !orgNameLine[i].contains("Sys")
                        && !orgNameLine[i].contains("Stem") && !orgNameLine[i].contains("I.S.D.")) {
                    closings.set(12, "Genesee: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Genesee = true;
                } else {
                    closings.set(12, "Genesee: OPEN");
                }
            }
            if (!(Kearsley)) {
                if (orgNameLine[i].contains("Kearsley")) {
                    closings.set(13, "Kearsley: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Kearsley = true;
                } else {
                    closings.set(13, "Kearsley: OPEN");
                }
            }
            if (!(LKFenton)) {
                if (orgNameLine[i].contains("Lake Fenton")) {
                    closings.set(14, "Lake Fenton: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    LKFenton = true;
                } else {
                    closings.set(14, "Lake Fenton: OPEN");
                }
            }
            if (!(Linden)) {
                if (orgNameLine[i].contains("Linden") && !orgNameLine[i].contains("Charter")) {
                    closings.set(15, "Linden: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Linden = true;
                } else {
                    closings.set(15, "Linden: OPEN");
                }
            }
            if (!(Montrose)) {
                if (orgNameLine[i].contains("Montrose") && !orgNameLine[i].contains("Senior")) {
                    closings.set(16, "Montrose: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    } else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Montrose = true;
                } else {
                    closings.set(16, "Montrose: OPEN");
                }
            }
            if (!(Morris)) {
                if (orgNameLine[i].contains("Mt. Morris") && !orgNameLine[i].contains("Administration")
                        && !orgNameLine[i].contains("Twp") && !orgNameLine[i].contains("Mary")) {
                    closings.set(17, "Mount Morris: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Morris= true;
                } else {
                    closings.set(17, "Mount Morris: OPEN");
                }
            }
            if (!(SzCreek)) {
                if (orgNameLine[i].contains("Swartz Creek") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("Montessori")) {
                    closings.set(18, "Swartz Creek: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    SzCreek = true;
                } else {
                    closings.set(18, "Swartz Creek: OPEN");
                }
            }
            if (!(Durand)) {
                if (orgNameLine[i].contains("Durand") && !orgNameLine[i].contains("Senior")) {
                    closings.set(19, "Durand: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Durand = true;
                } else {
                    closings.set(19, "Durand: OPEN");
                }
            }
            if (!(Holly)) {
                if (orgNameLine[i].contains("Holly") && !orgNameLine[i].contains("Academy")) {
                    closings.set(20, "Holly: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Holly = true;
                } else {
                    closings.set(20, "Holly: OPEN");
                }
            }
            if (!(Lapeer)) {
                if (orgNameLine[i].contains("Lapeer") && !orgNameLine[i].contains("Chatfield")
                        && !orgNameLine[i].contains("Transit") && !orgNameLine[i].contains("CMH")
                        && !orgNameLine[i].contains("Tech") && !orgNameLine[i].contains("Offices")
                        && !orgNameLine[i].contains("Library") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("Paul")) {
                    closings.set(21, "Lapeer: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Lapeer = true;
                } else {
                    closings.set(21, "Lapeer: OPEN");
                }
            }
            if (!(Owosso)) {
                if (orgNameLine[i].contains("Owosso") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("Baker") && !orgNameLine[i].contains("Paul")
                        && !orgNameLine[i].contains("Security")) {
                    closings.set(22, "Owosso: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier2today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier2tomorrow++;
                    }
                    Owosso = true;
                } else {
                    closings.set(22, "Owosso: OPEN");
                }
            }
            if (!(GBAcademy)) {
                if (orgNameLine[i].contains("Grand Blanc Academy")) {
                    closings.set(23, "Grand Blanc Academy: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    GBAcademy = true;
                } else {
                    closings.set(23, "Grand Blanc Academy: OPEN");
                }
            }
            if (!(GISD)) {
                if (orgNameLine[i].contains("Genesee I.S.D.")) {
                    closings.set(24, "Genesee I.S.D.: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    GISD = true;
                } else {
                    closings.set(24, "Genesee I.S.D.: OPEN");
                }
            }
            if (!(HolyFamily)) {
                if (orgNameLine[i].contains("Holy Family")) {
                    closings.set(25, "Holy Family: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    HolyFamily = true;
                } else {
                    closings.set(25, "Holy Family: OPEN");
                }
            }
            if (!(WPAcademy)) {
                if (orgNameLine[i].contains("Woodland Park Academy")) {
                    closings.set(26, "Woodland Park Academy: " + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    WPAcademy = true;
                } else {
                    closings.set(26, "Woodland Park Academy: OPEN");
                }
            }
        }

        //40% complete
        progCalculate.setProgress(40);
    }


    private class WeatherScraper implements Runnable {
        @Override
        public void run() {
            /**NATIONAL WEATHER SERVICE SCRAPER**/
            //Change the percentage based on current storm/wind/temperature warnings.

            Document weatherdoc;

            //Live html
            try {
                weatherdoc = Jsoup.connect("http://alerts.weather.gov/cap/wwaatmget.php?x=MIZ061&y=0").get();
                //Saving to searchable string array weatherwarn
                weatherwarn=weatherdoc.toString().split("<title>");

                //60% complete
                progCalculate.setProgress(60);

                getWeather();
            }catch (IOException e) {
                //Connectivity issues
                nwsInfo.add(nwsCount, "Could not connect to National Weather Service. Check your internet connection.");
                nwsCount++;
                NWSFail = true;
            } catch (NullPointerException e) {
                //Webpage layout not recognized.
                nwsInfo.add(nwsCount, "Unable to read weather information.");
                nwsInfo.add(nwsCount + 1, "If this error persists please contact the developer.");
                nwsCount += 2;
                NWSFail = true;
            }

            //Weather scraper has finished.
            NWSActive = false;
        }
    }

    private void getWeather() {
        /*Only the highest weatherpercent is stored (not cumulative).
        Watches affect tomorrow's calculation.
        Advisories and Warnings affect today's calculation.*/
        for (int i = 1; i < weatherwarn.length; i++) {
            if (weatherwarn[i].contains("Significant Weather Advisory")) {
                //Significant Weather Advisory - 15% weatherpercent
                SigWeather = true;
                weathertoday = 15;
            }
            if (weatherwarn[i].contains("Winter Weather Advisory")) {
                //Winter Weather Advisory - 30% weatherpercent
                WinterAdvisory = true;
                weathertoday = 30;
            }
            if (weatherwarn[i].contains("Lake-Effect Snow Advisory")) {
                //Lake Effect Snow Advisory - 40% weatherpercent
                LakeSnowAdvisory = true;
                weathertoday = 40;
            }
            if (weatherwarn[i].contains("Freezing Rain Advisory")) {
                //Freezing Rain - 40% weatherpercent
                Rain = true;
                weathertoday = 40;
            }
            if (weatherwarn[i].contains("Freezing Drizzle Advisory")) {
                //Freezing Drizzle - 40% weatherpercent
                Drizzle = true;
                weathertoday = 40;
            }
            if (weatherwarn[i].contains("Freezing Fog Advisory")) {
                //Freezing Fog - 40% weatherpercent
                Fog = true;
                weathertoday = 40;
            }
            if (weatherwarn[i].contains("Wind Chill Advisory")) {
                //Wind Chill Advisory - 40% weatherpercent
                WindChillAdvisory = true;
                weathertoday = 40;
            }
            if (weatherwarn[i].contains("Ice Storm Warning")) {
                //Ice Storm Warning - 70% weatherpercent
                IceStorm = true;
                weathertoday = 70;
            }
            if (weatherwarn[i].contains("Wind Chill Watch")) {
                //Wind Chill Watch - 70% weatherpercent
                WindChillWatch = true;
                weathertomorrow = 70;
            }
            if (weatherwarn[i].contains("Wind Chill Warning")) {
                //Wind Chill Warning - 70% weatherpercent
                WindChillWarn = true;
                weathertoday = 70;
            }
            if (weatherwarn[i].contains("Winter Storm Watch")) {
                //Winter Storm Watch - 80% weatherpercent
                WinterWatch = true;
                weathertomorrow = 80;
            }
            if (weatherwarn[i].contains("Winter Storm Warning")) {
                //Winter Storm Warning - 80% weatherpercent
                WinterWarn = true;
                weathertoday = 80;
            }
            if (weatherwarn[i].contains("Lake-Effect Snow Watch")) {
                //Lake Effect Snow Watch - 80% weatherpercent
                LakeSnowWatch = true;
                weathertomorrow = 80;
            }
            if (weatherwarn[i].contains("Lake-Effect Snow Warning")) {
                //Lake Effect Snow Warning - 80% weatherpercent
                LakeSnowWarn = true;
                weathertoday = 80;
            }
            if (weatherwarn[i].contains("Blizzard Watch")) {
                //Blizzard Watch - 90% weatherpercent
                BlizzardWatch = true;
                weathertomorrow = 90;
            }
            if (weatherwarn[i].contains("Blizzard Warning")) {
                //Blizzard Warning - 90% weatherpercent
                BlizzardWarn = true;
                weathertoday = 90;
            }
        }

        //If none of the above warnings are present
        if (weathertoday == 0 && weathertomorrow == 0) {
            weather.set(0, "No applicable weather warnings.");
        }

        //Set entries in the list in order of decreasing category (warn -> watch -> advisory)
        if (BlizzardWarn) {
            weather.add(weatherCount, "Blizzard Warning");
            weatherCount++;
        }if (LakeSnowWarn) {
            weather.add(weatherCount, "Lake-Effect Snow Warning");
            weatherCount++;
        }if (WinterWarn) {
            weather.add(weatherCount, "Winter Storm Warning");
            weatherCount++;
        }if (WindChillWarn) {
            weather.add(weatherCount, "Wind Chill Warning");
            weatherCount++;
        }if (IceStorm) {
            weather.add(weatherCount, "Ice Storm Warning");
            weatherCount++;
        }if (BlizzardWatch){
            weather.add(weatherCount, "Blizzard Watch");
            weatherCount++;
        }if (LakeSnowWatch) {
            weather.add(weatherCount, "Lake-Effect Snow Watch");
            weatherCount++;
        }if (WinterWatch) {
            weather.add(weatherCount, "Winter Storm Watch");
            weatherCount++;
        }if (WindChillWatch) {
            weather.add(weatherCount, "Wind Chill Watch");
            weatherCount++;
        }if (LakeSnowAdvisory) {
            weather.add(weatherCount, "Lake-Effect Snow Advisory");
            weatherCount++;
        }if (WinterAdvisory) {
            weather.add(weatherCount, "Winter Weather Advisory");
            weatherCount++;
        }if (WindChillAdvisory) {
            weather.add(weatherCount, "Wind Chill Advisory");
            weatherCount++;
        }if (Rain) {
            weather.add(weatherCount, "Freezing Rain Advisory");
            weatherCount++;
        }if (Drizzle) {
            weather.add(weatherCount, "Freezing Drizzle Advisory");
            weatherCount++;
        }if (Fog) {
            weather.add(weatherCount, "Freezing Fog Advisory");
            weatherCount++;
        }if (SigWeather) {
            weather.add(weatherCount, "Significant Weather Advisory");
            weatherCount++;
        }

        //80% complete
        progCalculate.setProgress(80);
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

            progCalculate.setProgress(100);

            lblPercent.setVisible(true);
            Platform.runLater(() -> lblPercent.setText("0%"));

            lblPercent.setStyle("-fx-text-fill: red");

            //Animate lblPercent
            if (WJRTFail && NWSFail) {
                //Both scrapers failed. A percentage cannot be determined.
                //Don't set the percent.
                progCalculate.setStyle("-fx-text-fill: red");
                Platform.runLater(() -> lblError.setText("Unable to run calculation."));
                progCalculate.setStyle("-fx-accent: red");
                Platform.runLater(() -> lblPercent.setText("--"));
            } else if (WJRTFail || NWSFail) {
                //Partial failure
                Platform.runLater(() -> lblError.setText("Network communication issues"));
                progCalculate.setStyle("-fx-accent: orange");
            }else{
                try {
                    for (int percentscroll = 0; percentscroll <= percent; percentscroll++) {
                        Thread.sleep(10);
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

                        txtWeather.setDisable(false);
                        txtGB.setDisable(false);
                        scrClosings.setDisable(false);
                    }
                } catch (InterruptedException ignored) {
                }

            }

            //Set the content of txtGB
            for (int i = 0; i < GBInfo.size(); i++) {
                if (i == 0) {
                    txtGB.setText(GBInfo.get(i));
                }else{
                    txtGB.setText(txtGB.getText() + "\n" + GBInfo.get(i));
                }
            }

            //Red is 204 0 0
            //Blue is 0 153 204
            if (!WJRTFail) {
                //WJRT has not failed.
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

            }else{
                //WJRT has failed.
                for (int i = 0; i < wjrtInfo.size(); i++) {
                    if (i == 0) {
                        txtGB.setText(wjrtInfo.get(i));
                    }else{
                        txtGB.setText(txtGB.getText() + "\n" + wjrtInfo.get(i));
                    }
                }
            }

            if (!NWSFail) {
                //NWS has not failed.
                for (int i = 0; i < weather.size(); i++) {
                    if (i == 0) {
                        txtWeather.setText(weather.get(i));
                    }else{
                        txtWeather.setText(txtWeather.getText() + "\n" + weather.get(i));
                    }
                }
            }else{
                //NWS has failed.
                for (int i = 0; i < nwsInfo.size(); i++) {
                    if (i == 0) {
                        txtWeather.setText(nwsInfo.get(i));
                    }else{
                        txtWeather.setText(txtWeather.getText() + "\n" + nwsInfo.get(i));
                    }
                }
            }

            btnCalculate.setDisable(false);
            txtWeather.setDisable(false);
            txtGB.setDisable(false);
            scrClosings.setDisable(false);
        }

    }


}
