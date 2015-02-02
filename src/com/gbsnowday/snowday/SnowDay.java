package com.gbsnowday.snowday;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicProgressBarUI;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("serial")
public class SnowDay extends javax.swing.JFrame {
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
    
    
    /**
     * Creates new form SnowDay
     */
    	
    Toolkit toolkit = this.getToolkit();
    java.awt.Image appIcon = toolkit.createImage("./src/com/gbsnowday/snowday/icon.png");
    
    ImageIcon appIconImage = new ImageIcon("./src/com/gbsnowday/snowday/icon.png");
    
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
    List<String> infoList = new ArrayList<>();
    List<Integer> daysarray = new ArrayList<>();
    int infoCount = 1;

    //Figure out what tomorrow is
    //Saturday = 6, Sunday = 7

    DateTime dt = new DateTime();
    int weekday = dt.getDayOfWeek();

    String orgName;
    String status;
    String schooltext;
    String weathertext;

    String[] orgNameLine;
    String[] statusLine;

    //Declare lists that will be used in ListAdapters
    List<String> GBInfo = new ArrayList<>();
    List<String> closings = new ArrayList<>();
    List<String> wjrtInfo = new ArrayList<>();
    List<String> weather = new ArrayList<>();
    List<String> nwsInfo = new ArrayList<>();

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
        
    public SnowDay() {
        initComponents();
        ButtonGroup group = new ButtonGroup();
        group.add(optToday);
        group.add(optTomorrow);
        pack();
        
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
            infoList.add(infoCount, "Tomorrow is President's Day. School will not be not in session.");
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
            optToday.setEnabled(false);
            optToday.setSelected(false);
            optToday.setForeground(Color.GRAY);
            optTomorrow.setEnabled(false);
            optTomorrow.setSelected(false);
            optTomorrow.setForeground(Color.GRAY);
        } else if (!tomorrowValid) {
            optTomorrow.setEnabled(false);
            optTomorrow.setSelected(false);
            optTomorrow.setForeground(Color.GRAY);
        } else if (!todayValid) {
            optToday.setEnabled(false);
            optToday.setSelected(false);
            optToday.setForeground(Color.GRAY);
        }
    }
    
    private void special() {
        daysarray.add(dayscount, lstDays.getSelectedIndex());
        dayscount++;
        int[] specialarray1 = {3, 7, 1, 7, 3, 1, 2, 1};
        int[] specialarray2 = {3, 3, 1, 5, 3, 1, 2, 2};
        if (daysarray.toString().equals(Arrays.toString(specialarray1))) {
            txtInfo.setText(txtInfo.getText() + "\n\nSilence will fall when the question is asked.");
            txtInfo.setForeground(Color.blue);
        }else if (daysarray.toString().equals(Arrays.toString(specialarray2)) || date.equals("April 22 2015")) {
            txtInfo.setText(txtInfo.getText() + "\n\nThe Doctor\n22/04/2011\n5:02pm\nLake Silencio, Utah");
            txtInfo.setForeground(Color.blue);
        }
    }
    
    private void checkWeekend() {
        //Friday is 5
        //Saturday is 6
        //Sunday is 7

        if (weekday == 5) {
            infoList.add(infoCount, "Tomorrow is Saturday.");
            optTomorrow.setEnabled(false);
            optTomorrow.setSelected(false);
            optTomorrow.setForeground(Color.GRAY);
            infoCount++;
        } else if (weekday == 6) {
            infoList.add(infoCount, "Today is Saturday. Try again tomorrow!");
            optToday.setEnabled(false);
            optToday.setSelected(false);
            optToday.setForeground(Color.GRAY);
            optTomorrow.setEnabled(false);
            optTomorrow.setSelected(false);
            optTomorrow.setForeground(Color.GRAY);
            infoCount++;
        } else if (weekday == 7) {
            infoList.add(infoCount, "Today is Sunday.");
            optToday.setEnabled(false);
            optToday.setSelected(false);
            optToday.setForeground(Color.GRAY);
            infoCount++;
        }
    }
    


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.htmll 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SnowDay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new SnowDay().setVisible(true);  
        });
        
    }

    /**
     *
     * @throws IOException
     * @throws InterruptedException
     */
 
    private void Calculate() throws InterruptedException {
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
        days = lstDays.getSelectedIndex() - 1;
        
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
        txtAtherton.setBackground(new Color(240, 240, 240));
        txtAtherton.setForeground(Color.BLACK);
        
        txtBendle.setText("Bendle:");
        txtBendle.setBackground(new Color(240, 240, 240));
        txtBendle.setForeground(Color.BLACK);
        
        txtBentley.setText("Bentley:");
        txtBentley.setBackground(new Color(240, 240, 240));
        txtBentley.setForeground(Color.BLACK);
        
        txtCarman.setText("Carman-Ainsworth:");
        txtCarman.setBackground(new Color(240, 240, 240));
        txtCarman.setForeground(Color.BLACK);
        
        txtFlint.setText("Flint:");
        txtFlint.setBackground(new Color(240, 240, 240));
        txtFlint.setForeground(Color.BLACK);
        
        txtGoodrich.setText("Goodrich:");
        txtGoodrich.setBackground(new Color(240, 240, 240));
        txtGoodrich.setForeground(Color.BLACK);
        
        txtBeecher.setText("Beecher:");
        txtBeecher.setBackground(new Color(240, 240, 240));
        txtBeecher.setForeground(Color.BLACK);
        
        txtClio.setText("Clio:");
        txtClio.setBackground(new Color(240, 240, 240));
        txtClio.setForeground(Color.BLACK);
        
        txtDavison.setText("Davison:");
        txtDavison.setBackground(new Color(240, 240, 240));
        txtDavison.setForeground(Color.BLACK);  

        txtFenton.setText("Fenton:");
        txtFenton.setBackground(new Color(240, 240, 240));
        txtFenton.setForeground(Color.BLACK);
        
        txtFlushing.setText("Flushing:");
        txtFlushing.setBackground(new Color(240, 240, 240));
        txtFlushing.setForeground(Color.BLACK);
        
        txtGenesee.setText("Genesee:");
        txtGenesee.setBackground(new Color(240, 240, 240));
        txtGenesee.setForeground(Color.BLACK);

        txtKearsley.setText("Kearsley:");
        txtKearsley.setBackground(new Color(240, 240, 240));
        txtKearsley.setForeground(Color.BLACK);
        
        txtLKFenton.setText("Lake Fenton:");
        txtLKFenton.setBackground(new Color(240, 240, 240));
        txtLKFenton.setForeground(Color.BLACK);
        
        txtLinden.setText("Linden:");
        txtLinden.setBackground(new Color(240, 240, 240));
        txtLinden.setForeground(Color.BLACK);

        txtMontrose.setText("Montrose:");
        txtMontrose.setBackground(new Color(240, 240, 240));
        txtMontrose.setForeground(Color.BLACK);
        
        txtMorris.setText("Mount Morris:");
        txtMorris.setBackground(new Color(240, 240, 240));
        txtMorris.setForeground(Color.BLACK);
        
        txtSzCreek.setText("Swartz Creek:");
        txtSzCreek.setBackground(new Color(240, 240, 240));
        txtSzCreek.setForeground(Color.BLACK);

        txtDurand.setText("Durand:");
        txtDurand.setBackground(new Color(240, 240, 240));
        txtDurand.setForeground(Color.BLACK);

        txtHolly.setText("Holly:");
        txtHolly.setBackground(new Color(240, 240, 240));
        txtHolly.setForeground(Color.BLACK);
        
        txtLapeer.setText("Lapeer:");
        txtLapeer.setBackground(new Color(240, 240, 240));
        txtLapeer.setForeground(Color.BLACK);
        
        txtOwosso.setText("Owosso:");
        txtOwosso.setBackground(new Color(240, 240, 240));
        txtOwosso.setForeground(Color.BLACK);
        
        txtGBAcademy.setText("Grand Blanc Academy:");
        txtGBAcademy.setBackground(new Color(240, 240, 240));
        txtGBAcademy.setForeground(Color.BLACK);     
        
        txtGISD.setText("Genesee I.S.D.:");
        txtGISD.setBackground(new Color(240, 240, 240));
        txtGISD.setForeground(Color.BLACK);
        
        txtHolyFamily.setText("Holy Family:");
        txtHolyFamily.setBackground(new Color(240, 240, 240));
        txtHolyFamily.setForeground(Color.BLACK);     

        txtWPAcademy.setText("Woodland Park Academy:");
        txtWPAcademy.setBackground(new Color(240, 240, 240));
        txtWPAcademy.setForeground(Color.BLACK); 	

        btnCalculate.setEnabled(false);
        
        progCalculate.setStringPainted(true);
        progCalculate.setForeground(new Color(51,153,255));
        progCalculate.setString("");
        progCalculate.setValue(0);
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
                schools = Jsoup.connect("http://ftpcontent2.worldnow.com/wjrt/school/closings.htm").get();
                //Attempt to parse input
                schools.select("td[bgcolor]").stream().map((row) -> {
                    //Reading closings - name of institution and status
                    orgName = orgName + "\n" + (row.select("font.orgname").first().text());
                    return row;
                }).forEach((row) -> {
                    status = status + "\n" + (row.select("font.status").first().text());
                });
                
                //20% complete
                progCalculate.setValue(20);
                
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
        progCalculate.setValue(40);
    }

    
    private class WeatherScraper implements Runnable {
        @Override
        public void run() {
            /**NATIONAL WEATHER SERVICE SCRAPER**/
            //Change the percentage based on current storm/wind/temperature warnings.

            Document weatherdoc;

            //Live html
            try {
                weatherdoc = Jsoup.connect("http://forecast.weather.gov/afm/PointClick.php?lat=42.92580&lon=-83.61870").get();
                //"Searching for elements in class 'warn'
                Elements weatherWarn = weatherdoc.getElementsByClass("warn");
                //Saving elements to searchable string weathertext
                weathertext = weatherWarn.toString();
                
                //60% complete
                progCalculate.setValue(60);
                
                if (weathertext.equals("")) {
                    //weathertext is empty.
                    //Searching for element 'hazards_content'
                    //This element should always be present even if no hazards are present.
                    Element weatherNull = weatherdoc.getElementById("hazards_content");

                    if (weatherNull.toString().contains("No Hazards in Effect")) {
                        //Webpage parsed correctly: no hazards present.
                        weather.set(0, "No applicable weather warnings.");
                        NWSFail = false;
                    }
                } else {
                    //Hazards found. Use the data
                    getWeather();
                }
            } catch (IOException e) {
                //Connectivity issues
                nwsInfo.add(nwsCount, "Could not connect to National Weather Service.");
                nwsInfo.add(nwsCount + 1, "Check your internet connection.");
                nwsCount += 2;
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

        if (weathertext.contains("Significant Weather Advisory")) {
            //Significant Weather Advisory - 15% weatherpercent
            SigWeather = true;
            weathertoday = 15;
        }
        if (weathertext.contains("Winter Weather Advisory")) {
            //Winter Weather Advisory - 30% weatherpercent
            WinterAdvisory = true;
            weathertoday = 30;
        }
        if (weathertext.contains("Lake-Effect Snow Advisory")) {
            //Lake Effect Snow Advisory - 40% weatherpercent
            LakeSnowAdvisory = true;
            weathertoday = 40;
        }
        if (weathertext.contains("Freezing Rain Advisory")) {
            //Freezing Rain - 40% weatherpercent
            Rain = true;
            weathertoday = 40;
        }
        if (weathertext.contains("Freezing Drizzle Advisory")) {
            //Freezing Drizzle - 40% weatherpercent
            Drizzle = true;
            weathertoday = 40;
        }
        if (weathertext.contains("Freezing Fog Advisory")) {
            //Freezing Fog - 40% weatherpercent
            Fog = true;
            weathertoday = 40;
        }
        if (weathertext.contains("Wind Chill Advisory")) {
            //Wind Chill Advisory - 40% weatherpercent
            WindChillAdvisory = true;
            weathertoday = 40;
        }
        if (weathertext.contains("Ice Storm Warning")) {
            //Ice Storm Warning - 70% weatherpercent
            IceStorm = true;
            weathertoday = 70;
        }
        if (weathertext.contains("Wind Chill Watch")) {
            //Wind Chill Watch - 70% weatherpercent
            WindChillWatch = true;
            weathertomorrow = 70;
        }
        if (weathertext.contains("Wind Chill Warning")) {
            //Wind Chill Warning - 70% weatherpercent
            WindChillWarn = true;
            weathertoday = 70;
        }
        if (weathertext.contains("Winter Storm Watch")) {
            //Winter Storm Watch - 80% weatherpercent
            WinterWatch = true;
            weathertomorrow = 80;
        }
        if (weathertext.contains("Winter Storm Warning")) {
            //Winter Storm Warning - 80% weatherpercent
            WinterWarn = true;
            weathertoday = 80;
        }
        if (weathertext.contains("Lake-Effect Snow Watch")) {
            //Lake Effect Snow Watch - 80% weatherpercent
            LakeSnowWatch = true;
            weathertomorrow = 80;
        }
        if (weathertext.contains("Lake-Effect Snow Warning")) {
            //Lake Effect Snow Warning - 80% weatherpercent
            LakeSnowWarn = true;
            weathertoday = 80;
        }
        if (weathertext.contains("Blizzard Watch")) {
            //Blizzard Watch - 90% weatherpercent
            BlizzardWatch = true;
            weathertomorrow = 90;
        }
        if (weathertext.contains("Blizzard Warning")) {
            //Blizzard Warning - 90% weatherpercent
            BlizzardWarn = true;
            weathertoday = 90;
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
        progCalculate.setValue(80);
    }

    private class PercentCalculate implements Runnable {
        @Override
        public void run() {
            
            //Give the scrapers time to act before displaying the percent

            while (WJRTActive || NWSActive) {
                try {
                    //Wait for scrapers to finish before continuing
                    Thread.sleep(100);
                } catch (InterruptedException e) {
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
            
            progCalculate.setValue(100);
            lblPercent.setText("0%");
            lblPercent.setForeground(Color.RED);

            //Animate lblPercent
            if (WJRTFail && NWSFail) {
                //Both scrapers failed. A percentage cannot be determined.
                //Don't set the percent.
                progCalculate.setForeground(Color.RED);
                progCalculate.setString("Unable to run calculation.");
                progCalculate.setUI(new BasicProgressBarUI() {
                    @Override 
                    protected Color getSelectionBackground() { return Color.white; }
                  });
                lblPercent.setText("--");
            } else if (WJRTFail || NWSFail) {
                //Partial failure
                progCalculate.setForeground(Color.orange);
                progCalculate.setString("Network communication issues");
                progCalculate.setUI(new BasicProgressBarUI() {
                    @Override 
                    protected Color getSelectionForeground() { return Color.red; }
                  });
            }else{
                try {
                    for (int percentscroll = 0; percentscroll <= percent; percentscroll++) {
                        Thread.sleep(10);
                        if (percentscroll >= 0 && percentscroll <= 20) {    
                            lblPercent.setForeground(Color.RED); 
                        } if (percentscroll > 20 && percentscroll <= 60) {       
                            lblPercent.setForeground(Color.ORANGE);  
                        } if (percentscroll > 60 && percentscroll <= 80) {
                            lblPercent.setForeground(Color.BLUE); 
                        } if (percentscroll > 80) {    
                            lblPercent.setForeground(Color.BLUE);  
                        }
                        
                        lblPercent.setText((percentscroll) + "%");                        
                    }
                } catch (InterruptedException e) {

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
                    txtAtherton.setBackground(new Color(255, 136, 0));
                    txtAtherton.setForeground(Color.WHITE);
                }if (Bendle) {
                    txtBendle.setBackground(new Color(255, 136, 0));
                    txtBendle.setForeground(Color.WHITE);
                }if (Bentley) {
                    txtBentley.setBackground(new Color(255, 136, 0));
                    txtBentley.setForeground(Color.WHITE);
                }if (Carman) {
                    txtCarman.setBackground(new Color(255, 136, 0));
                    txtCarman.setForeground(Color.WHITE);
                }if (Flint) {
                    txtFlint.setBackground(new Color(255, 136, 0));
                    txtFlint.setForeground(Color.WHITE);
                }if (Goodrich) {
                    txtGoodrich.setBackground(new Color(255, 136, 0));
                    txtGoodrich.setForeground(Color.WHITE);
                }if (Beecher) {
                    txtBeecher.setBackground(new Color(255, 136, 0));
                    txtBeecher.setForeground(Color.WHITE);
                }if (Clio) {
                    txtClio.setBackground(new Color(255, 136, 0));
                    txtClio.setForeground(Color.WHITE);
                }if (Davison) {
                    txtDavison.setBackground(new Color(255, 136, 0));
                    txtDavison.setForeground(Color.WHITE);  
                }if (Fenton) {
                    txtFenton.setBackground(new Color(255, 136, 0));
                    txtFenton.setForeground(Color.WHITE);
                }if (Flushing) {
                    txtFlushing.setBackground(new Color(255, 136, 0));
                    txtFlushing.setForeground(Color.WHITE);
                }if (Genesee) {
                    txtGenesee.setBackground(new Color(255, 136, 0));
                    txtGenesee.setForeground(Color.WHITE);
                }if (Kearsley) {
                    txtKearsley.setBackground(new Color(255, 136, 0));
                    txtKearsley.setForeground(Color.WHITE);
                }if (LKFenton) {
                    txtLKFenton.setBackground(new Color(255, 136, 0));
                    txtLKFenton.setForeground(Color.WHITE);
                }if (Linden) {
                    txtLinden.setBackground(new Color(255, 136, 0));
                    txtLinden.setForeground(Color.WHITE);
                }if (Montrose) {
                    txtMontrose.setBackground(new Color(255, 136, 0));
                    txtMontrose.setForeground(Color.WHITE);
                }if (Morris) {
                    txtMorris.setBackground(new Color(255, 136, 0));
                    txtMorris.setForeground(Color.WHITE);
                }if (SzCreek) {
                    txtSzCreek.setBackground(new Color(255, 136, 0));
                    txtSzCreek.setForeground(Color.WHITE);
                }if (Durand) {
                    txtDurand.setBackground(new Color(255, 136, 0));
                    txtDurand.setForeground(Color.WHITE);
                }if (Holly) {
                    txtHolly.setBackground(new Color(255, 136, 0));
                    txtHolly.setForeground(Color.WHITE);
                }if (Lapeer) {
                    txtLapeer.setBackground(new Color(255, 136, 0));
                    txtLapeer.setForeground(Color.WHITE);
                }if (Owosso) {
                    txtOwosso.setBackground(new Color(255, 136, 0));
                    txtOwosso.setForeground(Color.WHITE);
                }if (GBAcademy) {
                    txtGBAcademy.setBackground(new Color(255, 136, 0));
                    txtGBAcademy.setForeground(Color.WHITE);     
                }if (GISD) {
                    txtGISD.setBackground(new Color(255, 136, 0));
                    txtGISD.setForeground(Color.WHITE);
                }if (HolyFamily) {
                    txtHolyFamily.setBackground(new Color(255, 136, 0));
                    txtHolyFamily.setForeground(Color.WHITE);     
                }if (WPAcademy) {
                    txtWPAcademy.setBackground(new Color(255, 136, 0));
                    txtWPAcademy.setForeground(Color.WHITE); 
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
            btnCalculate.setEnabled(true);
        }
}
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
  // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        
        lblDay = new javax.swing.JLabel();
        lblPrev = new javax.swing.JLabel();
        optToday = new javax.swing.JRadioButton();
        optTomorrow = new javax.swing.JRadioButton();
        btnCalculate = new javax.swing.JButton();
        lstDays = new javax.swing.JComboBox();
        progCalculate = new javax.swing.JProgressBar();
        lblPercent = new javax.swing.JLabel();
        txtWJRT = new javax.swing.JTextField();
        lblRadar = new javax.swing.JLabel();
        fillerVertical = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        fillerHorizontal = new javax.swing.Box.Filler(new java.awt.Dimension(500, 0), new java.awt.Dimension(500, 0), new java.awt.Dimension(500, 32767));
        scrGB = new javax.swing.JScrollPane();
        txtGB = new javax.swing.JTextArea();
        scrInfo = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextArea();
        txtPercent = new javax.swing.JTextField();
        txtNWS = new javax.swing.JTextField();
        scrWeather = new javax.swing.JScrollPane();
        txtWeather = new javax.swing.JTextArea();
        scrClosings = new javax.swing.JScrollPane();
        pnlClosings = new javax.swing.JPanel();
        txtTier4 = new javax.swing.JTextField();
        txtAtherton = new javax.swing.JTextField();
        txtBendle = new javax.swing.JTextField();
        txtBentley = new javax.swing.JTextField();
        txtCarman = new javax.swing.JTextField();
        txtFlint = new javax.swing.JTextField();
        txtGoodrich = new javax.swing.JTextField();
        txtTier3 = new javax.swing.JTextField();
        txtBeecher = new javax.swing.JTextField();
        txtClio = new javax.swing.JTextField();
        txtDavison = new javax.swing.JTextField();
        txtFenton = new javax.swing.JTextField();
        txtFlushing = new javax.swing.JTextField();
        txtGenesee = new javax.swing.JTextField();
        txtKearsley = new javax.swing.JTextField();
        txtLKFenton = new javax.swing.JTextField();
        txtLinden = new javax.swing.JTextField();
        txtMontrose = new javax.swing.JTextField();
        txtMorris = new javax.swing.JTextField();
        txtSzCreek = new javax.swing.JTextField();
        txtTier2 = new javax.swing.JTextField();
        txtDurand = new javax.swing.JTextField();
        txtHolly = new javax.swing.JTextField();
        txtLapeer = new javax.swing.JTextField();
        txtOwosso = new javax.swing.JTextField();
        txtTier1 = new javax.swing.JTextField();
        txtGBAcademy = new javax.swing.JTextField();
        txtGISD = new javax.swing.JTextField();
        txtHolyFamily = new javax.swing.JTextField();
        txtWPAcademy = new javax.swing.JTextField();
        menu = new javax.swing.JMenuBar();
        menuAbout = new javax.swing.JMenu();
        itemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Snow Day Calculator");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusCycleRoot(false);
        setIconImage(appIcon);
        setIconImages(null);
        setLocationByPlatform(true);
        setName("SnowDay"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDay.setFont(lblDay.getFont());
        lblDay.setText("Run calculation for:");
        getContentPane().add(lblDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 269, 31));

        lblPrev.setFont(lblPrev.getFont());
        lblPrev.setText("How many snow days have occurred?");
        getContentPane().add(lblPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, 28));

        optToday.setText("Today");
        optToday.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optTodayActionPerformed(evt);
            }
        });
        getContentPane().add(optToday, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        optTomorrow.setText("Tomorrow");
        optTomorrow.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optTomorrowActionPerformed(evt);
            }
        });
        getContentPane().add(optTomorrow, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, 20));

        btnCalculate.setText("CALCULATE");
        btnCalculate.setBorder(null);
        btnCalculate.setEnabled(false);
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });
        getContentPane().add(btnCalculate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 290, 40));

        lstDays.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10 or more" }));
        lstDays.setName("lstDays"); // NOI18N
        lstDays.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstDaysActionPerformed(evt);
            }
        });
        getContentPane().add(lstDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, 31));
        getContentPane().add(progCalculate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 290, 20));

        lblPercent.setBackground(new java.awt.Color(0, 0, 0));
        lblPercent.setFont(new java.awt.Font("Verdana", 1, 72)); // NOI18N
        lblPercent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(lblPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 290, 110));

        txtWJRT.setEditable(false);
        txtWJRT.setBackground(new java.awt.Color(204, 0, 0));
        txtWJRT.setForeground(new java.awt.Color(255, 255, 255));
        txtWJRT.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtWJRT.setText("ABC 12 School Closings");
        txtWJRT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtWJRT.setToolTipText("Click to open ABC 12 closings");
        getContentPane().add(txtWJRT, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, 330, -1));

        lblRadar.setIcon(new javax.swing.JLabel() {
            @Override
            public javax.swing.Icon getIcon() {
                try {
                    return new javax.swing.ImageIcon(
                        new java.net.URL("http://radar.weather.gov/Conus/Loop/centgrtlakes_loop.gif")
                    );
                } catch (java.net.MalformedURLException e) {
                }
                return null;
            }
        }.getIcon());
        lblRadar.setToolTipText("Great Lakes Sector Loop");
        getContentPane().add(lblRadar, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 210, 410, 300));
        getContentPane().add(fillerVertical, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 10, 10, 500));
        getContentPane().add(fillerHorizontal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, 1010, 10));

        scrGB.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtGB.setEditable(false);
        txtGB.setBackground(java.awt.SystemColor.control);
        txtGB.setColumns(20);
        txtGB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtGB.setRows(5);
        txtGB.setWrapStyleWord(true);
        scrGB.setViewportView(txtGB);

        getContentPane().add(scrGB, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 330, 60));

        txtInfo.setEditable(false);
        txtInfo.setBackground(java.awt.SystemColor.control);
        txtInfo.setColumns(20);
        txtInfo.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtInfo.setLineWrap(true);
        txtInfo.setRows(5);
        txtInfo.setWrapStyleWord(true);
        scrInfo.setViewportView(txtInfo);

        getContentPane().add(scrInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 290, 100));

        txtPercent.setEditable(false);
        txtPercent.setBackground(new java.awt.Color(204, 204, 204));
        txtPercent.setForeground(new java.awt.Color(255, 255, 255));
        txtPercent.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPercent.setText("Percent");
        txtPercent.setToolTipText("");
        getContentPane().add(txtPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 250, 270, -1));

        txtNWS.setEditable(false);
        txtNWS.setBackground(new java.awt.Color(0, 102, 255));
        txtNWS.setForeground(new java.awt.Color(255, 255, 255));
        txtNWS.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNWS.setText("National Weather Service Warnings");
        txtNWS.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtNWS.setToolTipText("Click to open weather");
        getContentPane().add(txtNWS, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 410, -1));

        txtWeather.setEditable(false);
        txtWeather.setBackground(java.awt.SystemColor.control);
        txtWeather.setColumns(20);
        txtWeather.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtWeather.setRows(5);
        txtWeather.setWrapStyleWord(true);
        scrWeather.setViewportView(txtWeather);

        getContentPane().add(scrWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, 410, 160));

        pnlClosings.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTier4.setEditable(false);
        txtTier4.setBackground(new java.awt.Color(0, 61, 103));
        txtTier4.setForeground(new java.awt.Color(255, 255, 255));
        txtTier4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTier4.setText("Districts near Grand Blanc");
        pnlClosings.add(txtTier4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 310, -1));

        txtAtherton.setEditable(false);
        txtAtherton.setText("Atherton:");
        pnlClosings.add(txtAtherton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 310, -1));

        txtBendle.setEditable(false);
        txtBendle.setText("Bendle:");
        pnlClosings.add(txtBendle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 310, -1));

        txtBentley.setEditable(false);
        txtBentley.setText("Bentley:");
        pnlClosings.add(txtBentley, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 310, -1));

        txtCarman.setEditable(false);
        txtCarman.setText("Carman-Ainsworth:");
        pnlClosings.add(txtCarman, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 310, -1));

        txtFlint.setEditable(false);
        txtFlint.setText("Flint:");
        pnlClosings.add(txtFlint, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 310, -1));

        txtGoodrich.setEditable(false);
        txtGoodrich.setText("Goodrich:");
        pnlClosings.add(txtGoodrich, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 310, -1));

        txtTier3.setEditable(false);
        txtTier3.setBackground(new java.awt.Color(0, 61, 103));
        txtTier3.setForeground(new java.awt.Color(255, 255, 255));
        txtTier3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTier3.setText("Districts in Genesee County");
        pnlClosings.add(txtTier3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 310, -1));

        txtBeecher.setEditable(false);
        txtBeecher.setText("Beecher:");
        pnlClosings.add(txtBeecher, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 310, -1));

        txtClio.setEditable(false);
        txtClio.setText("Clio:");
        pnlClosings.add(txtClio, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 310, -1));

        txtDavison.setEditable(false);
        txtDavison.setText("Davison:");
        pnlClosings.add(txtDavison, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 310, -1));

        txtFenton.setEditable(false);
        txtFenton.setText("Fenton:");
        pnlClosings.add(txtFenton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 310, -1));

        txtFlushing.setEditable(false);
        txtFlushing.setText("Flushing:");
        pnlClosings.add(txtFlushing, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 240, 310, -1));

        txtGenesee.setEditable(false);
        txtGenesee.setText("Genesee:");
        pnlClosings.add(txtGenesee, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 310, -1));

        txtKearsley.setEditable(false);
        txtKearsley.setText("Kearsley:");
        pnlClosings.add(txtKearsley, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 310, -1));

        txtLKFenton.setEditable(false);
        txtLKFenton.setText("Lake Fenton:");
        pnlClosings.add(txtLKFenton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 310, -1));

        txtLinden.setEditable(false);
        txtLinden.setText("Linden:");
        pnlClosings.add(txtLinden, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 320, 310, -1));

        txtMontrose.setEditable(false);
        txtMontrose.setText("Montrose:");
        pnlClosings.add(txtMontrose, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 340, 310, -1));

        txtMorris.setEditable(false);
        txtMorris.setText("Mount Morris:");
        pnlClosings.add(txtMorris, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 310, -1));

        txtSzCreek.setEditable(false);
        txtSzCreek.setText("Swartz Creek:");
        pnlClosings.add(txtSzCreek, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 310, -1));

        txtTier2.setEditable(false);
        txtTier2.setBackground(new java.awt.Color(0, 61, 103));
        txtTier2.setForeground(new java.awt.Color(255, 255, 255));
        txtTier2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTier2.setText("Districts in Neighboring Counties");
        pnlClosings.add(txtTier2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 400, 310, -1));

        txtDurand.setEditable(false);
        txtDurand.setText("Durand:");
        pnlClosings.add(txtDurand, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 310, -1));

        txtHolly.setEditable(false);
        txtHolly.setText("Holly:");
        pnlClosings.add(txtHolly, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 310, -1));

        txtLapeer.setEditable(false);
        txtLapeer.setText("Lapeer:");
        pnlClosings.add(txtLapeer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 460, 310, -1));

        txtOwosso.setEditable(false);
        txtOwosso.setText("Owosso:");
        pnlClosings.add(txtOwosso, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 480, 310, -1));

        txtTier1.setEditable(false);
        txtTier1.setBackground(new java.awt.Color(0, 61, 103));
        txtTier1.setForeground(new java.awt.Color(255, 255, 255));
        txtTier1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTier1.setText("Academies / Institutions");
        pnlClosings.add(txtTier1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 500, 310, -1));

        txtGBAcademy.setEditable(false);
        txtGBAcademy.setText("Grand Blanc Academy:");
        pnlClosings.add(txtGBAcademy, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 520, 310, -1));

        txtGISD.setEditable(false);
        txtGISD.setText("Genesee I.S.D.:");
        pnlClosings.add(txtGISD, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 540, 310, -1));

        txtHolyFamily.setEditable(false);
        txtHolyFamily.setText("Holy Family:");
        pnlClosings.add(txtHolyFamily, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 310, -1));

        txtWPAcademy.setEditable(false);
        txtWPAcademy.setText("Woodland Park Academy:");
        pnlClosings.add(txtWPAcademy, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 580, 310, -1));

        scrClosings.setViewportView(pnlClosings);

        getContentPane().add(scrClosings, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 110, 330, 400));

        menuAbout.setText("Menu");

        itemAbout.setText("About");
        itemAbout.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAboutActionPerformed(evt);
            }
        });
        menuAbout.add(itemAbout);

        menu.add(menuAbout);

        setJMenuBar(menu);

        
        //Start listeners
        closingslistener(txtWJRT);
        weatherlistener(txtNWS);
        
        pack();
    }// </editor-fold>                        

    private void closingslistener(JTextField closings) {
        closings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.abc12.com/category/213603/school-closings"));
                } catch (URISyntaxException | IOException ex) {
                }
            }
        });
    }
    
    private void weatherlistener(JTextField nws) {
        nws.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://forecast.weather.gov/afm/PointClick.php?lat=42.9275&lon=-83.6299"));
                } catch (URISyntaxException | IOException ex) {
                }
            }
        });
    }
    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        btnCalculate.setEnabled(false);
        try {
            //Start the calculation
            Calculate();
        } catch (InterruptedException ex) {
            Logger.getLogger(SnowDay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCalculateActionPerformed

    private void optTomorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optTomorrowActionPerformed
        if (lstDays.getSelectedIndex() !=0) {
            btnCalculate.setEnabled(true);
        }    }//GEN-LAST:event_optTomorrowActionPerformed

    private void optTodayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optTodayActionPerformed
        if (lstDays.getSelectedIndex() != 0) {
            btnCalculate.setEnabled(true);
        }    }//GEN-LAST:event_optTodayActionPerformed

    private void lstDaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lstDaysActionPerformed
        if (lstDays.getSelectedIndex() == 0) {
            btnCalculate.setEnabled(false);
        }else if (!optToday.isSelected() && !optTomorrow.isSelected()) {
            btnCalculate.setEnabled(false);
        }else{
            btnCalculate.setEnabled(true);
        }
        special();
    }//GEN-LAST:event_lstDaysActionPerformed

    private void itemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAboutActionPerformed
        /* Create and display the form */
        new About(this, true).setVisible(true);
    }//GEN-LAST:event_itemAboutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate;
    private javax.swing.Box.Filler fillerHorizontal;
    private javax.swing.Box.Filler fillerVertical;
    private javax.swing.JMenuItem itemAbout;
    private javax.swing.JLabel lblDay;
    private javax.swing.JLabel lblPercent;
    private javax.swing.JLabel lblPrev;
    private javax.swing.JLabel lblRadar;
    private javax.swing.JComboBox lstDays;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JRadioButton optToday;
    private javax.swing.JRadioButton optTomorrow;
    private javax.swing.JPanel pnlClosings;
    private javax.swing.JProgressBar progCalculate;
    private javax.swing.JScrollPane scrClosings;
    private javax.swing.JScrollPane scrGB;
    private javax.swing.JScrollPane scrInfo;
    private javax.swing.JScrollPane scrWeather;
    private javax.swing.JTextField txtAtherton;
    private javax.swing.JTextField txtBeecher;
    private javax.swing.JTextField txtBendle;
    private javax.swing.JTextField txtBentley;
    private javax.swing.JTextField txtCarman;
    private javax.swing.JTextField txtClio;
    private javax.swing.JTextField txtDavison;
    private javax.swing.JTextField txtDurand;
    private javax.swing.JTextField txtFenton;
    private javax.swing.JTextField txtFlint;
    private javax.swing.JTextField txtFlushing;
    private javax.swing.JTextArea txtGB;
    private javax.swing.JTextField txtGBAcademy;
    private javax.swing.JTextField txtGISD;
    private javax.swing.JTextField txtGenesee;
    private javax.swing.JTextField txtGoodrich;
    private javax.swing.JTextField txtHolly;
    private javax.swing.JTextField txtHolyFamily;
    private javax.swing.JTextArea txtInfo;
    private javax.swing.JTextField txtKearsley;
    private javax.swing.JTextField txtLKFenton;
    private javax.swing.JTextField txtLapeer;
    private javax.swing.JTextField txtLinden;
    private javax.swing.JTextField txtMontrose;
    private javax.swing.JTextField txtMorris;
    private javax.swing.JTextField txtNWS;
    private javax.swing.JTextField txtOwosso;
    private javax.swing.JTextField txtPercent;
    private javax.swing.JTextField txtSzCreek;
    private javax.swing.JTextField txtTier1;
    private javax.swing.JTextField txtTier2;
    private javax.swing.JTextField txtTier3;
    private javax.swing.JTextField txtTier4;
    private javax.swing.JTextField txtWJRT;
    private javax.swing.JTextField txtWPAcademy;
    private javax.swing.JTextArea txtWeather;
    // End of variables declaration//GEN-END:variables
}

