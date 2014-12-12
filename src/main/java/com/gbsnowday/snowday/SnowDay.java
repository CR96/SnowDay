package com.gbsnowday.snowday;

import java.awt.Color;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
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
         http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.*/
    
    
    /**
     * Creates new form SnowDay
     */
    	
    java.awt.Toolkit toolkit = this.getToolkit();
    java.awt.Image appIcon = toolkit.createImage("./icon.png");
    
    
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

    //True is GB is already open (GB is false, time is during or after school hours)
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

        //Determine the date
        DateTime dt = new DateTime();
        int weekday = dt.getDayOfWeek();
        
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
       
        btnCalculate.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
    }
   
    private class WJRTScraper implements Runnable {
        @Override
        public void run() {
            Document schools = null;
            //Scrape School Closings from WJRT with Jsoup.

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
        }
        protected void onPostExecute(Void result) {
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
                    GBInfo.set(0, "GB" + statusLine[i]);
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
                    closings.set(1, "Atherton" + statusLine[i]);
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
                    closings.set(2, "Bendle" + statusLine[i]);
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
                    closings.set(3, "Bentley" + statusLine[i]);
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
                    closings.set(4, "Carman" + statusLine[i]);
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
                    closings.set(5, "Flint" + statusLine[i]);
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
                    closings.set(6, "Goodrich" + statusLine[i]);
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
                    closings.set(7, "Beecher" + statusLine[i]);
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
                    closings.set(8, "Clio" + statusLine[i]);
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
                    closings.set(9, "Davison" + statusLine[i]);
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
                    closings.set(10, "Fenton" + statusLine[i]);
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
                    closings.set(11, "Flushing" + statusLine[i]);
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
                    closings.set(12, "Genesee" + statusLine[i]);
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
                    closings.set(13, "Kearsley" + statusLine[i]);
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
                    closings.set(14, "LKFenton" + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    LKFenton = true;
                } else {
                    closings.set(14, "LKFenton: OPEN");
                }
            }
            if (!(Linden)) {
                if (orgNameLine[i].contains("Linden") && !orgNameLine[i].contains("Charter")) {
                    closings.set(15, "Linden" + statusLine[i]);
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
                    closings.set(16, "Montrose" + statusLine[i]);
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
                    closings.set(17, "Morris" + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    Morris= true;
                } else {
                    closings.set(17, "Morris: OPEN");
                }
            }
            if (!(SzCreek)) {
                if (orgNameLine[i].contains("Swartz Creek") && !orgNameLine[i].contains("Senior")
                        && !orgNameLine[i].contains("Montessori")) {
                    closings.set(18, "SzCreek" + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier3today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier3tomorrow++;
                    }
                    SzCreek = true;
                } else {
                    closings.set(18, "SzCreek: OPEN");
                }
            }
            if (!(Durand)) {
                if (orgNameLine[i].contains("Durand") && !orgNameLine[i].contains("Senior")) {
                    closings.set(19, "Durand" + statusLine[i]);
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
                    closings.set(20, "Holly" + statusLine[i]);
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
                    closings.set(21, "Lapeer" + statusLine[i]);
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
                    closings.set(22, "Owosso" + statusLine[i]);
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
                    closings.set(23, "GBAcademy" + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    GBAcademy = true;
                } else {
                    closings.set(23, "GBAcademy: OPEN");
                }
            }
            if (!(GISD)) {
                if (orgNameLine[i].contains("Genesee I.S.D.")) {
                    closings.set(24, "GISD" + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    GISD = true;
                } else {
                    closings.set(24, "GISD: OPEN");
                }
            }
            if (!(HolyFamily)) {
                if (orgNameLine[i].contains("Holy Family")) {
                    closings.set(25, "HolyFamily" + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    HolyFamily = true;
                } else {
                    closings.set(25, "HolyFamily: OPEN");
                }
            }
            if (!(WPAcademy)) {
                if (orgNameLine[i].contains("Woodland Park Academy")) {
                    closings.set(26, "WPAcademy" + statusLine[i]);
                    if (statusLine[i].contains("Closed Today") && dayrun == 0) {
                        tier1today++;
                    }else if (statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                        tier1tomorrow++;
                    }
                    WPAcademy = true;
                } else {
                    closings.set(26, "WPAcademy: OPEN");
                }
            }
        }
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


        }

        protected void onPostExecute(Void result) {
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
            
//            progCalculate.setVisible(false);
//            txtPercent.setText("0%");
//
//            //Animate txtPercent
//            if (WJRTFail && NWSFail) {
//                //Both scrapers failed. A percentage cannot be determined.
//                //Don't set the percent.
//                GBInfo.set(0, "Unable to run calculation.");
//                txtPercent.setText("--");
//            } else {
//                try {
//                    for (int i = 0; i < percent; i++) {
//                        Thread.sleep(10);
//                        if (percentscroll >= 0 && percentscroll <= 20) {    
//                            txtPercent.setTextColor(Color.RED); 
//                        } if (percentscroll > 20 && percentscroll <= 60) {       
//                            txtPercent.setTextColor(Color.rgb(255, 165, 0));  
//                        } if (percentscroll > 60 && percentscroll <= 80) {
//                            txtPercent.setTextColor(Color.GREEN); 
//                        } if (percentscroll > 80) {    
//                            txtPercent.setTextColor(Color.BLUE);  
//                        }
//                        
//                        txtPercent.setText((percentscroll) + "%");
//                            
//                        percentscroll++;
//                    }
//                } catch (InterruptedException e) {
//
//                }
//
//            }
        }

        protected void onPostExecute() {

            //Set the content of the information ListView
            if (WJRTFail || NWSFail) {
                //Network communication issues
                GBInfo.add(GBCount, "Network communication issues");
                GBCount++;
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
        lblCalculate = new javax.swing.JLabel();
        optToday = new javax.swing.JRadioButton();
        optTomorrow = new javax.swing.JRadioButton();
        btnCalculate = new javax.swing.JButton();
        lstDays = new javax.swing.JComboBox();
        progressBar = new javax.swing.JProgressBar();
        lblClosings = new javax.swing.JLabel();
        lblWeather = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextArea();
        btnExit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtGBAcademy = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAtherton = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtDurand = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtCarman = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtGB = new javax.swing.JTextArea();
        lblTier4 = new javax.swing.JLabel();
        lblTier3 = new javax.swing.JLabel();
        lblTier2 = new javax.swing.JLabel();
        lblTier1 = new javax.swing.JLabel();
        lblPercent = new javax.swing.JLabel();
        lblWxr = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtGISD = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtHolyFamily = new javax.swing.JTextArea();
        jScrollPane10 = new javax.swing.JScrollPane();
        txtWPAcademy = new javax.swing.JTextArea();
        jScrollPane11 = new javax.swing.JScrollPane();
        txtBendle = new javax.swing.JTextArea();
        jScrollPane12 = new javax.swing.JScrollPane();
        txtFlint = new javax.swing.JTextArea();
        jScrollPane13 = new javax.swing.JScrollPane();
        txtGoodrich = new javax.swing.JTextArea();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtHolly = new javax.swing.JTextArea();
        jScrollPane15 = new javax.swing.JScrollPane();
        txtLapeer = new javax.swing.JTextArea();
        jScrollPane16 = new javax.swing.JScrollPane();
        txtOwosso = new javax.swing.JTextArea();
        jScrollPane17 = new javax.swing.JScrollPane();
        txtBeecher = new javax.swing.JTextArea();
        jScrollPane18 = new javax.swing.JScrollPane();
        txtClio = new javax.swing.JTextArea();
        jScrollPane19 = new javax.swing.JScrollPane();
        txtDavison = new javax.swing.JTextArea();
        jScrollPane20 = new javax.swing.JScrollPane();
        txtFenton = new javax.swing.JTextArea();
        jScrollPane21 = new javax.swing.JScrollPane();
        txtFlushing = new javax.swing.JTextArea();
        jScrollPane22 = new javax.swing.JScrollPane();
        txtGenesee = new javax.swing.JTextArea();
        jScrollPane23 = new javax.swing.JScrollPane();
        txtKearsley = new javax.swing.JTextArea();
        jScrollPane24 = new javax.swing.JScrollPane();
        txtLkFenton = new javax.swing.JTextArea();
        jScrollPane25 = new javax.swing.JScrollPane();
        txtLinden = new javax.swing.JTextArea();
        jScrollPane26 = new javax.swing.JScrollPane();
        txtMontrose = new javax.swing.JTextArea();
        jScrollPane27 = new javax.swing.JScrollPane();
        txtMorris = new javax.swing.JTextArea();
        jScrollPane28 = new javax.swing.JScrollPane();
        txtSzCreek = new javax.swing.JTextArea();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jScrollPane6 = new javax.swing.JScrollPane();
        txtWeather = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Snow Day Calculator");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusCycleRoot(false);
        setIconImage(appIcon);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setName("SnowDayGUI"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDay.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        lblDay.setForeground(new java.awt.Color(204, 0, 0));
        lblDay.setText("1. Choose a Day");
        getContentPane().add(lblDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 269, 31));

        lblPrev.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        lblPrev.setForeground(new java.awt.Color(204, 0, 0));
        lblPrev.setText("2. Input Previous Snow Days");
        getContentPane().add(lblPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 28));

        lblCalculate.setFont(new java.awt.Font("Verdana", 0, 24)); // NOI18N
        lblCalculate.setForeground(new java.awt.Color(204, 0, 0));
        lblCalculate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCalculate.setText("3. Calculate!");
        getContentPane().add(lblCalculate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 290, -1));

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

        btnCalculate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/gbsnowday/snowday/icon.png"))); // NOI18N
        btnCalculate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCalculate.setEnabled(false);
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });
        getContentPane().add(btnCalculate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 288, 279));

        lstDays.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select a Number", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10+" }));
        lstDays.setName("lstDays"); // NOI18N
        lstDays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstDaysActionPerformed(evt);
            }
        });
        getContentPane().add(lstDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 31));
        getContentPane().add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 610, 288, 20));

        lblClosings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/gbsnowday/snowday/WJRT.jpg"))); // NOI18N
        getContentPane().add(lblClosings, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 30, -1, 40));
        lblClosings.getAccessibleContext().setAccessibleName("lblClosings");

        lblWeather.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        lblWeather.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/gbsnowday/snowday/NWS.png"))); // NOI18N
        getContentPane().add(lblWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 160, 150));

        txtInfo.setEditable(false);
        txtInfo.setColumns(20);
        txtInfo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtInfo.setRows(5);
        jScrollPane5.setViewportView(txtInfo);

        getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 510, 288, -1));

        btnExit.setText("Quit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 750, 90, 32));

        txtGBAcademy.setEditable(false);
        txtGBAcademy.setColumns(1);
        txtGBAcademy.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtGBAcademy.setRows(1);
        txtGBAcademy.setText("Grand Blanc Academy:");
        jScrollPane1.setViewportView(txtGBAcademy);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 660, 258, -1));

        txtAtherton.setEditable(false);
        txtAtherton.setColumns(1);
        txtAtherton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtAtherton.setRows(1);
        txtAtherton.setText("Atherton:");
        jScrollPane2.setViewportView(txtAtherton);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 170, 258, -1));

        txtDurand.setEditable(false);
        txtDurand.setColumns(1);
        txtDurand.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtDurand.setRows(1);
        txtDurand.setText("Durand:");
        jScrollPane4.setViewportView(txtDurand);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 550, 258, -1));

        txtCarman.setEditable(false);
        txtCarman.setColumns(1);
        txtCarman.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtCarman.setRows(1);
        txtCarman.setText("Carman-Ainsworth:");
        txtCarman.setCaretPosition(0);
        jScrollPane7.setViewportView(txtCarman);

        getContentPane().add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 120, 258, -1));

        txtGB.setEditable(false);
        txtGB.setColumns(1);
        txtGB.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtGB.setRows(1);
        txtGB.setText("Grand Blanc:");
        txtGB.setCaretPosition(0);
        jScrollPane8.setViewportView(txtGB);

        getContentPane().add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 80, 258, 28));

        lblTier4.setText("Districts near Grand Blanc");
        getContentPane().add(lblTier4, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 150, -1, -1));

        lblTier3.setText("Districts in Genesee County");
        getContentPane().add(lblTier3, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 260, -1, -1));

        lblTier2.setText("Districts in Neighboring Counties");
        getContentPane().add(lblTier2, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 530, -1, -1));

        lblTier1.setText("Academies / Institutions");
        getContentPane().add(lblTier1, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 640, 146, -1));

        lblPercent.setBackground(new java.awt.Color(0, 0, 0));
        lblPercent.setFont(new java.awt.Font("Verdana", 1, 72)); // NOI18N
        lblPercent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(lblPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 640, 288, 126));

        lblWxr.setIcon(new javax.swing.JLabel() {
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
        getContentPane().add(lblWxr, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 190, -1, -1));

        txtGISD.setEditable(false);
        txtGISD.setColumns(1);
        txtGISD.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtGISD.setRows(1);
        txtGISD.setText("Genesee I.S.D.:");
        jScrollPane3.setViewportView(txtGISD);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 680, 258, -1));

        txtHolyFamily.setEditable(false);
        txtHolyFamily.setColumns(1);
        txtHolyFamily.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtHolyFamily.setRows(1);
        txtHolyFamily.setText("Holy Family:");
        jScrollPane9.setViewportView(txtHolyFamily);

        getContentPane().add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 700, 258, -1));

        txtWPAcademy.setEditable(false);
        txtWPAcademy.setColumns(1);
        txtWPAcademy.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtWPAcademy.setRows(1);
        txtWPAcademy.setText("Woodland Park Academy:");
        jScrollPane10.setViewportView(txtWPAcademy);

        getContentPane().add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 720, 258, -1));

        txtBendle.setEditable(false);
        txtBendle.setColumns(1);
        txtBendle.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtBendle.setRows(1);
        txtBendle.setText("Bendle:");
        jScrollPane11.setViewportView(txtBendle);

        getContentPane().add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 190, 258, -1));

        txtFlint.setEditable(false);
        txtFlint.setColumns(1);
        txtFlint.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtFlint.setRows(1);
        txtFlint.setText("Flint:");
        jScrollPane12.setViewportView(txtFlint);

        getContentPane().add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 210, 258, -1));

        txtGoodrich.setEditable(false);
        txtGoodrich.setColumns(1);
        txtGoodrich.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtGoodrich.setRows(1);
        txtGoodrich.setText("Goodrich:");
        jScrollPane13.setViewportView(txtGoodrich);

        getContentPane().add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 230, 258, -1));

        txtHolly.setEditable(false);
        txtHolly.setColumns(1);
        txtHolly.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtHolly.setRows(1);
        txtHolly.setText("Holly:");
        jScrollPane14.setViewportView(txtHolly);

        getContentPane().add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 570, 258, -1));

        txtLapeer.setEditable(false);
        txtLapeer.setColumns(1);
        txtLapeer.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtLapeer.setRows(1);
        txtLapeer.setText("Lapeer:");
        jScrollPane15.setViewportView(txtLapeer);

        getContentPane().add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 590, 258, -1));

        txtOwosso.setEditable(false);
        txtOwosso.setColumns(1);
        txtOwosso.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtOwosso.setRows(1);
        txtOwosso.setText("Owosso:");
        jScrollPane16.setViewportView(txtOwosso);

        getContentPane().add(jScrollPane16, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 610, 258, -1));

        jScrollPane17.setAutoscrolls(true);

        txtBeecher.setEditable(false);
        txtBeecher.setColumns(20);
        txtBeecher.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtBeecher.setText("Beecher:");
        txtBeecher.setAutoscrolls(false);
        jScrollPane17.setViewportView(txtBeecher);

        getContentPane().add(jScrollPane17, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 280, 258, -1));

        jScrollPane18.setAutoscrolls(true);

        txtClio.setEditable(false);
        txtClio.setColumns(20);
        txtClio.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtClio.setText("Clio:");
        txtClio.setAutoscrolls(false);
        jScrollPane18.setViewportView(txtClio);

        getContentPane().add(jScrollPane18, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 300, 258, -1));

        jScrollPane19.setAutoscrolls(true);

        txtDavison.setEditable(false);
        txtDavison.setColumns(20);
        txtDavison.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtDavison.setText("Davison:");
        txtDavison.setAutoscrolls(false);
        jScrollPane19.setViewportView(txtDavison);

        getContentPane().add(jScrollPane19, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 320, 258, -1));

        jScrollPane20.setAutoscrolls(true);

        txtFenton.setEditable(false);
        txtFenton.setColumns(20);
        txtFenton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtFenton.setText("Fenton:");
        txtFenton.setAutoscrolls(false);
        jScrollPane20.setViewportView(txtFenton);

        getContentPane().add(jScrollPane20, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 340, 258, -1));

        jScrollPane21.setAutoscrolls(true);

        txtFlushing.setEditable(false);
        txtFlushing.setColumns(20);
        txtFlushing.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtFlushing.setText("Flushing:");
        txtFlushing.setAutoscrolls(false);
        jScrollPane21.setViewportView(txtFlushing);

        getContentPane().add(jScrollPane21, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 360, 258, -1));

        jScrollPane22.setAutoscrolls(true);

        txtGenesee.setEditable(false);
        txtGenesee.setColumns(20);
        txtGenesee.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtGenesee.setText("Genesee:");
        txtGenesee.setAutoscrolls(false);
        jScrollPane22.setViewportView(txtGenesee);

        getContentPane().add(jScrollPane22, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 380, 258, -1));

        jScrollPane23.setAutoscrolls(true);

        txtKearsley.setEditable(false);
        txtKearsley.setColumns(20);
        txtKearsley.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtKearsley.setText("Kearsley:");
        txtKearsley.setAutoscrolls(false);
        jScrollPane23.setViewportView(txtKearsley);

        getContentPane().add(jScrollPane23, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 400, 258, -1));

        jScrollPane24.setAutoscrolls(true);

        txtLkFenton.setEditable(false);
        txtLkFenton.setColumns(20);
        txtLkFenton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtLkFenton.setText("Lake Fenton:");
        txtLkFenton.setAutoscrolls(false);
        jScrollPane24.setViewportView(txtLkFenton);

        getContentPane().add(jScrollPane24, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 420, 258, -1));

        jScrollPane25.setAutoscrolls(true);

        txtLinden.setEditable(false);
        txtLinden.setColumns(20);
        txtLinden.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtLinden.setText("Linden:");
        txtLinden.setAutoscrolls(false);
        jScrollPane25.setViewportView(txtLinden);

        getContentPane().add(jScrollPane25, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 440, 258, -1));

        jScrollPane26.setAutoscrolls(true);

        txtMontrose.setEditable(false);
        txtMontrose.setColumns(20);
        txtMontrose.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtMontrose.setText("Montrose:");
        txtMontrose.setAutoscrolls(false);
        jScrollPane26.setViewportView(txtMontrose);

        getContentPane().add(jScrollPane26, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 460, 258, -1));

        jScrollPane27.setAutoscrolls(true);

        txtMorris.setEditable(false);
        txtMorris.setColumns(20);
        txtMorris.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtMorris.setText("Mount Morris:");
        txtMorris.setAutoscrolls(false);
        jScrollPane27.setViewportView(txtMorris);

        getContentPane().add(jScrollPane27, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 480, 258, -1));

        jScrollPane28.setAutoscrolls(true);

        txtSzCreek.setEditable(false);
        txtSzCreek.setColumns(20);
        txtSzCreek.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtSzCreek.setText("Swartz Creek:");
        txtSzCreek.setAutoscrolls(false);
        jScrollPane28.setViewportView(txtSzCreek);

        getContentPane().add(jScrollPane28, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 500, 258, -1));
        getContentPane().add(filler1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 330, 20, -1));
        getContentPane().add(filler2, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 430, 20, 10));

        jScrollPane6.setBorder(null);
        jScrollPane6.setForeground(new java.awt.Color(204, 0, 51));

        txtWeather.setEditable(false);
        txtWeather.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        txtWeather.setColumns(20);
        txtWeather.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        txtWeather.setForeground(new java.awt.Color(204, 0, 0));
        txtWeather.setRows(5);
        jScrollPane6.setViewportView(txtWeather);

        getContentPane().add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 20, 370, 150));

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

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate;
    private javax.swing.JButton btnExit;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane28;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel lblCalculate;
    private javax.swing.JLabel lblClosings;
    private javax.swing.JLabel lblDay;
    private javax.swing.JLabel lblPercent;
    private javax.swing.JLabel lblPrev;
    private javax.swing.JLabel lblTier1;
    private javax.swing.JLabel lblTier2;
    private javax.swing.JLabel lblTier3;
    private javax.swing.JLabel lblTier4;
    private javax.swing.JLabel lblWeather;
    private javax.swing.JLabel lblWxr;
    private javax.swing.JComboBox lstDays;
    private javax.swing.JRadioButton optToday;
    private javax.swing.JRadioButton optTomorrow;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextArea txtAtherton;
    private javax.swing.JTextArea txtBeecher;
    private javax.swing.JTextArea txtBendle;
    private javax.swing.JTextArea txtCarman;
    private javax.swing.JTextArea txtClio;
    private javax.swing.JTextArea txtDavison;
    private javax.swing.JTextArea txtDurand;
    private javax.swing.JTextArea txtFenton;
    private javax.swing.JTextArea txtFlint;
    private javax.swing.JTextArea txtFlushing;
    private javax.swing.JTextArea txtGB;
    private javax.swing.JTextArea txtGBAcademy;
    private javax.swing.JTextArea txtGISD;
    private javax.swing.JTextArea txtGenesee;
    private javax.swing.JTextArea txtGoodrich;
    private javax.swing.JTextArea txtHolly;
    private javax.swing.JTextArea txtHolyFamily;
    private javax.swing.JTextArea txtInfo;
    private javax.swing.JTextArea txtKearsley;
    private javax.swing.JTextArea txtLapeer;
    private javax.swing.JTextArea txtLinden;
    private javax.swing.JTextArea txtLkFenton;
    private javax.swing.JTextArea txtMontrose;
    private javax.swing.JTextArea txtMorris;
    private javax.swing.JTextArea txtOwosso;
    private javax.swing.JTextArea txtSzCreek;
    private javax.swing.JTextArea txtWPAcademy;
    private javax.swing.JTextArea txtWeather;
    // End of variables declaration//GEN-END:variables
}

