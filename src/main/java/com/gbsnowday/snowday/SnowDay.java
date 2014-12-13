package com.gbsnowday.snowday;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
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
    java.awt.Image appIcon = toolkit.createImage("./icon.png");
    
    ImageIcon appIconImage = new ImageIcon("./icon.png");
    
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
    List<String> infoList = new ArrayList<String>();
    List<Integer> daysarray = new ArrayList<Integer>();
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
    List<String> GBInfo = new ArrayList<String>();
    List<String> closings = new ArrayList<String>();
    List<String> wjrtInfo = new ArrayList<String>();
    List<String> weather = new ArrayList<String>();
    List<String> nwsInfo = new ArrayList<String>();

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

    //For the ending animation
    int percentscroll;

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
    boolean Owosso; //Check for "Owosso Senior Center", "Baker College-Owosso", and "St. Paul Catholic-Owosso"

    boolean Beecher;
    boolean Clio; //Check for "Clio Area Senior Center", "Clio City Hall", and "Cornerstone Clio"
    boolean Davison; //Check for "Davison Senior Center", "Faith Baptist School-Davison", and "Montessori Academy-Davison"
    boolean Fenton; //Check for "Lake Fenton", "Fenton City Hall", and "Fenton Montessori Academy"
    boolean Flushing; //Check for "Flushing Senior Citizens Center" and "St. Robert-Flushing"
    boolean Genesee; //Check for "Freedom Work-Genesee Co.", "Genesee Christian-Burton",
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
        lblPercent.setVisible(false);
        pack();
        
        //Make sure the user doesn't try to run the program on the weekend or on specific dates
        checkDate();
        
        //Only run checkWeekend() if today or tomorrow is still valid
        if (todayValid || tomorrowValid) {
            checkWeekend();
        }
        
        //Set the contents of lstInfo
        DefaultComboBoxModel infoModel = new DefaultComboBoxModel(infoList.toArray());
        lstInfo.setModel(infoModel);
        
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SnowDay().setVisible(true);
            }
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
       
        lblPercent.setText("");		
        lblPercent.setVisible(false);

        btnCalculate.setEnabled(false);
        lstClosings.removeAll();
        lstWeather.removeAll();
        
        progCalculate.setValue(0);
    }
   
    private class WJRTScraper implements Runnable {
        @Override
        public void run() {
            Document schools = null;

            /**WJRT SCHOOL CLOSINGS SCRAPER**/
            //Scrape School Closings from WJRT with Jsoup.
            //Run scraper in an Async task.

            //This is the current listings page.

            try {
                schools = Jsoup.connect("http://ftpcontent2.worldnow.com/wjrt/school/closings.htm").get();
                //Attempt to parse input
                for (Element row : schools.select("td[bgcolor]")) {
                    //Reading closings - name of institution and status
                    orgName = orgName + "\n" + (row.select("font.orgname").first().text());
                    status = status + "\n" + (row.select("font.status").first().text());
                }
                
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
                wjrtInfo.add(wjrtCount, "Could not connect to ABC 12. Check your internet connection.");
                wjrtCount++;
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
                    closings.set(4, "Carman: " + statusLine[i]);
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
                        && !orgNameLine[i].contains("Faith") && !orgNameLine[i].contains("Montessori")) {
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
                        && !orgNameLine[i].contains("Christian") && !orgNameLine[i].contains("Mobile")
                        && !orgNameLine[i].contains("Programs") && !orgNameLine[i].contains("Hlth")
                        && !orgNameLine[i].contains("Sys") && !orgNameLine[i].contains("Stem")
                        && !orgNameLine[i].contains("I.S.D.")) {
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
                        && !orgNameLine[i].contains("Baker") && !orgNameLine[i].contains("Paul")) {
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
        public void run() {
            /**NATIONAL WEATHER SERVICE SCRAPER**/
            //Change the percentage based on current storm/wind/temperature warnings.

            Document weatherdoc = null;

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
                nwsInfo.add(nwsCount, "Could not connect to National Weather Service."
                        + " Check your internet connection.");
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

            percentscroll = 0;
            
            progCalculate.setValue(100);
            lblPercent.setVisible(true);
            lblPercent.setText("0%");
            lblPercent.setForeground(Color.RED);

            //Animate lblPercent
            if (WJRTFail && NWSFail) {
                //Both scrapers failed. A percentage cannot be determined.
                //Don't set the percent.
                GBInfo.set(0, "Unable to run calculation.");
                lblPercent.setText("--");
            } else {
                try {
                    for (int i = 0; i < percent; i++) {
                        Thread.sleep(10);
                        if (percentscroll >= 0 && percentscroll <= 20) {    
                            lblPercent.setForeground(Color.RED); 
                        } if (percentscroll > 20 && percentscroll <= 60) {       
                            lblPercent.setForeground(Color.ORANGE);  
                        } if (percentscroll > 60 && percentscroll <= 80) {
                            lblPercent.setForeground(Color.GREEN); 
                        } if (percentscroll > 80) {    
                            lblPercent.setForeground(Color.BLUE);  
                        }
                        
                        lblPercent.setText((percentscroll) + "%");
                            
                        percentscroll++;
                    }
                } catch (InterruptedException e) {

                }

            }     
            if (WJRTFail || NWSFail) {
                //Network communication issues
                GBInfo.add(GBCount, "Network communication issues");
                GBCount++;
            }
            
            //Set the content of lstGB
            DefaultComboBoxModel modelGB = new DefaultComboBoxModel(GBInfo.toArray());
            lstGB.setModel(modelGB);
            
            if (!WJRTFail) {
                //WJRT has not failed.
                DefaultComboBoxModel modelClosings = new DefaultComboBoxModel();
                
                modelClosings.addElement("Districts near Grand Blanc");
                for (int i = 1; i < closings.size(); i++) {
                    modelClosings.addElement(closings.get(i));
                    if (i == 6) {
                        modelClosings.addElement("Districts in Genesee County");
                        modelClosings.getElementAt(6);
                    } if (i == 18) {
                        modelClosings.addElement("Districts in Neighboring Counties");
                    } if (i == 22) {
                        modelClosings.addElement("Academies and Institutions");
                    }
                }
                
                lstClosings.setModel(modelClosings);
                
                //Change the colors
                lstClosings.getCellRenderer();
            }else{
                //WJRT has failed.
                DefaultComboBoxModel modelWJRT = new DefaultComboBoxModel(wjrtInfo.toArray());
                lstClosings.setModel(modelWJRT);
            }      
            
            if (!NWSFail) {
                //NWS has not failed.
                DefaultComboBoxModel modelWeather = new DefaultComboBoxModel(weather.toArray());
                lstWeather.setModel(modelWeather);
            }else{
                //NWS has failed.
                DefaultComboBoxModel modelNWS = new DefaultComboBoxModel(nwsInfo.toArray());
                lstWeather.setModel(modelNWS);
            }
    }
}
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblDay = new javax.swing.JLabel();
        lblPrev = new javax.swing.JLabel();
        optToday = new javax.swing.JRadioButton();
        optTomorrow = new javax.swing.JRadioButton();
        btnCalculate = new javax.swing.JButton();
        lstDays = new javax.swing.JComboBox();
        progCalculate = new javax.swing.JProgressBar();
        lblPercent = new javax.swing.JLabel();
        scrInfo = new javax.swing.JScrollPane();
        lstInfo = new javax.swing.JList();
        scrGB = new javax.swing.JScrollPane();
        lstGB = new javax.swing.JList();
        scrClosings = new javax.swing.JScrollPane();
        lstClosings = new javax.swing.JList();
        scrWeather = new javax.swing.JScrollPane();
        lstWeather = new javax.swing.JList();
        txtWeather = new javax.swing.JTextField();
        txtClosings = new javax.swing.JTextField();
        txtPercent = new javax.swing.JTextField();
        lblRadar = new javax.swing.JLabel();
        fillerVertical = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        fillerHorizontal = new javax.swing.Box.Filler(new java.awt.Dimension(500, 0), new java.awt.Dimension(500, 0), new java.awt.Dimension(500, 32767));
        menu = new javax.swing.JMenuBar();
        menuAbout = new javax.swing.JMenu();
        itemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Snow Day Calculator");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusCycleRoot(false);
        setIconImage(appIcon);
        setLocationByPlatform(true);
        setMaximumSize(null);
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optTodayActionPerformed(evt);
            }
        });
        getContentPane().add(optToday, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        optTomorrow.setText("Tomorrow");
        optTomorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optTomorrowActionPerformed(evt);
            }
        });
        getContentPane().add(optTomorrow, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, 20));

        btnCalculate.setText("CALCULATE");
        btnCalculate.setBorder(null);
        btnCalculate.setEnabled(false);
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });
        getContentPane().add(btnCalculate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 290, 40));

        lstDays.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10 or more" }));
        lstDays.setName("lstDays"); // NOI18N
        lstDays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstDaysActionPerformed(evt);
            }
        });
        getContentPane().add(lstDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, 31));
        getContentPane().add(progCalculate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 290, 20));

        lblPercent.setBackground(new java.awt.Color(0, 0, 0));
        lblPercent.setFont(new java.awt.Font("Verdana", 1, 72)); // NOI18N
        lblPercent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(lblPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 240, 270, 160));

        lstInfo.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstInfo.setToolTipText("");
        lstInfo.setSelectionBackground(new java.awt.Color(255, 255, 255));
        lstInfo.setSelectionForeground(new java.awt.Color(0, 0, 0));
        scrInfo.setViewportView(lstInfo);

        getContentPane().add(scrInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 290, 220));

        lstGB.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstGB.setSelectionBackground(new java.awt.Color(255, 255, 255));
        lstGB.setSelectionForeground(new java.awt.Color(0, 0, 0));
        scrGB.setViewportView(lstGB);

        getContentPane().add(scrGB, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 410, 270, 100));

        lstClosings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstClosings.setDragEnabled(true);
        lstClosings.setSelectionBackground(new java.awt.Color(255, 255, 255));
        lstClosings.setSelectionForeground(new java.awt.Color(0, 0, 0));
        scrClosings.setViewportView(lstClosings);

        getContentPane().add(scrClosings, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 270, 160));

        lstWeather.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstWeather.setSelectionBackground(new java.awt.Color(255, 255, 255));
        lstWeather.setSelectionForeground(new java.awt.Color(0, 0, 0));
        scrWeather.setViewportView(lstWeather);

        getContentPane().add(scrWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, 410, 160));

        txtWeather.setEditable(false);
        txtWeather.setBackground(new java.awt.Color(0, 102, 255));
        txtWeather.setForeground(new java.awt.Color(255, 255, 255));
        txtWeather.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtWeather.setText("National Weather Service Warnings");
        getContentPane().add(txtWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 410, -1));

        txtClosings.setEditable(false);
        txtClosings.setBackground(new java.awt.Color(255, 0, 0));
        txtClosings.setForeground(new java.awt.Color(255, 255, 255));
        txtClosings.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtClosings.setText("ABC 12 School Closings");
        txtClosings.setToolTipText("");
        getContentPane().add(txtClosings, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, 270, -1));

        txtPercent.setEditable(false);
        txtPercent.setBackground(new java.awt.Color(204, 204, 204));
        txtPercent.setForeground(new java.awt.Color(255, 255, 255));
        txtPercent.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPercent.setText("Percent");
        txtPercent.setToolTipText("");
        getContentPane().add(txtPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 210, 270, -1));

        lblRadar.setIcon(new javax.swing.JLabel() {
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
        getContentPane().add(fillerVertical, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 10, 10, 500));
        getContentPane().add(fillerHorizontal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, 1010, 10));

        menuAbout.setText("Menu");

        itemAbout.setText("About");
        itemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAboutActionPerformed(evt);
            }
        });
        menuAbout.add(itemAbout);

        menu.add(menuAbout);

        setJMenuBar(menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    }//GEN-LAST:event_lstDaysActionPerformed

    private void itemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAboutActionPerformed
        /* Create and display the form */
        new About().setVisible(true);
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
    private javax.swing.JList lstClosings;
    private javax.swing.JComboBox lstDays;
    private javax.swing.JList lstGB;
    private javax.swing.JList lstInfo;
    private javax.swing.JList lstWeather;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JRadioButton optToday;
    private javax.swing.JRadioButton optTomorrow;
    private javax.swing.JProgressBar progCalculate;
    private javax.swing.JScrollPane scrClosings;
    private javax.swing.JScrollPane scrGB;
    private javax.swing.JScrollPane scrInfo;
    private javax.swing.JScrollPane scrWeather;
    private javax.swing.JTextField txtClosings;
    private javax.swing.JTextField txtPercent;
    private javax.swing.JTextField txtWeather;
    // End of variables declaration//GEN-END:variables
}
