package snowday;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("serial")
public class SnowDayGUI extends javax.swing.JFrame {
    /**
     * Creates new form SnowDayGUI
     */
    	
    java.awt.Toolkit toolkit = this.getToolkit();
    java.awt.Image appIcon = toolkit.createImage("./icon.png");
    
    //Debug strings
    public String mainThread = "(Main) ";
    public String Reset = "(Reset) ";
    public String checkWeekend = "(checkWeekend) ";
    public String checkTime = "(checkTime) ";
    public String WJRTScraper = "(WJRTScraper) ";
    public String checkClosingsToday = "(checkClosingsToday) ";
    public String checkClosingsTomorrow = "(checkClosingsTomorrow) ";
    public String checkGBClosed = "(checkGBClosed) ";
    public String weatherScraper = "(WeatherScraper) ";
    public String getWeather = "(getWeather) ";
    public String percentCalculate = "(percentCalculate) ";
    
    
    public String orgName;
    public String status;
    public String hazardName;
    public String weathertext;
    public String weathercheck;
    public boolean nullWeather = true;
    public String[] orgNameLine;
    public String[] statusLine;
    public int days;
    
    int schoolpercent = 0;
    int weatherpercent = 0;
    int percent;
    int percentscroll;
    int dayrun = 0;
    int tier1 = 0;
    int tier2 = 0;
    int tier3 = 0;
    int tier4 = 0;
    int tier5 = 0;

    boolean schoolNull;
    String schooltext;

    boolean GBAcademy;
    boolean HolyFamily;
    boolean WPAcademy;
    boolean GISD;
    boolean Durand; //Check for "Durand Senior Center"
    boolean Holly;  //Check for "Holly Academy"
    boolean Lapeer; //Check for "Chatfield School-Lapeer", "Greater Lapeer Transit Authority", "Lapeer CMH Day Programs",
    //"Lapeer Co. Ed-Tech Center", "Lapeer County Ofices", "Lapeer District Library", "Lapeer Senior Center", and "St. Paul Lutheran-Lapeer"
    boolean Owosso; //Check for "Owosso Senior Center", "Baker College-Owosso", and "St. Paul Catholic-Owosso"
    boolean Beecher;
    boolean Clio; //Check for "Clio Area Senior Center", "Clio City Hall", and "Cornerstone Clio"
    boolean Davison; //Check for "Davison Senior Center", "Faith Baptist School-Davison", and "Montessori Academy-Davison"
    boolean Fenton; //Check for "Lake Fenton", "Fenton City Hall", and "Fenton Montessori Academy"
    boolean Flushing; //Check for "Flushing Senior Citizens Center" and "St. Robert-Flushing"
    boolean Genesee; //Check for "Freedom Work-Genesee Co.", "Genesee Christian-Burton", "Genesee Co. Mobile Meals", "Genesee Hlth Sys Day Programs", "Genesee Stem Academy", and "Genesee I.S.D."
    boolean Kearsley;
    boolean LKFenton;
    boolean Linden; //Check for "Linden Charter Academy"
    boolean Montrose; //Check for "Montrose Senior Center"
    boolean Morris;  //Check for "Mt Morris Twp Administration" and "St. Mary's-Mt. Morris"
    boolean SzCreek; //Check for "Swartz Creek Area Senior Ctr." and "Swartz Creek Montessori"
    boolean Atherton;
    boolean Bendle;
    boolean Bentley;
    boolean Flint; //Thankfully this is listed as "Flint Community Schools" - otherwise there would be 25 exceptions to check for.
    boolean Goodrich;
    boolean Carman; //Check for "Carman-Ainsworth Senior Ctr."
    boolean GB; //Check for "Freedom Work-Grand Blanc", "Grand Blanc Academy", "Grand Blanc City Offices", "Grand Blanc Senior Center", and "Holy Family-Grand Blanc", 
    
    public Thread wjrt;
    public Thread nws;
    public Thread p;
    
    //Figure out what tomorrow is
    //Saturday = 0, Sunday = 1
    String today;
    String tomorrow;
    Date date;
    Format formatter;
    Calendar calendar = Calendar.getInstance();
    int weekday = calendar.get(Calendar.DAY_OF_WEEK);
    int month = calendar.get(Calendar.MONTH);
        
    public SnowDayGUI() {
        System.out.println(mainThread + "We're live!");
        System.out.println(mainThread + "Creating SnowDayGUI");
        initComponents();
        ButtonGroup group = new ButtonGroup();
        group.add(optToday);
        group.add(optTomorrow);
        lblPercent.setVisible(false);
        pack();
        
        //Make sure the user doesn't try to run the program on the weekend or during school hours
        //checkWeekend();
        checkTime(); 
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
            java.util.logging.Logger.getLogger(SnowDayGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SnowDayGUI().setVisible(true);
            }
        });
    }

    /**
     *
     * @throws IOException
     * @throws InterruptedException
     */
 
    private void SnowDayCalculate() throws InterruptedException {
        /**
         *This program will predict the possibility of a snow day for Grand Blanc High School.
         *Created by Corey Rowe, AP Computer Science A, 1st Hour, February 2014 - Current.
         *Factors:
         *Predicted snowfall and time of arrival (not yet implemented)
         *Predicted ice accumulation (not yet implemented)
         *Predicted wind chill (below -20F?) (not yet implemented)
         *Number of snow days accrued (more = smaller chance) (implemented)
         *Schools currently closed (data from WJRT) (implemented)
         **Schools in higher tiers (5 is highest) will increase the snow day chance.
         **Obviously return 100% if GB is already closed.
         */
        System.out.println(mainThread + "Calculation has started");
        //Call a reset to clear any previous data
        Reset();
        //Date setup
        System.out.println(mainThread + "Checking selected day");
        if (optToday.isSelected()) {
            System.out.println(mainThread + "optToday checked");
            dayrun = 0;
        }else if (optTomorrow.isSelected()) {
            System.out.println(mainThread + "optTomorrow checked");
            dayrun = 1;
        }

        System.out.println(mainThread + "Determining date");
        date = calendar.getTime();
        formatter = new SimpleDateFormat("MMM dd yyyy");
        today = formatter.format(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        formatter = new SimpleDateFormat("MMM dd yyyy");
        tomorrow = formatter.format(date);
        
        //Set calculation to today or tomorrow
        if (dayrun == 0) {
           txtInfo.setText(txtInfo.getText() + "Running calculation for " + today + "...");
        }else if (dayrun == 1) {
           txtInfo.setText(txtInfo.getText() + "Running calculation for " + tomorrow + "...");
        }
        
        //Have the user input past snow days
        days = lstDays.getSelectedIndex() - 1;
        System.out.println(mainThread + "User says " + days + " snow days have occurred.");
        
        /**WJRT SCHOOL CLOSINGS SCRAPER**/
        wjrt = new Thread(new WJRTScraper());
        wjrt.start();
        
        
        //Next Test: Weather!

        /**NATIONAL WEATHER SERVICE SCRAPER**/
        nws = new Thread(new WeatherScraper());
        nws.start();
        
        //Final Percent Calculator
        p = new Thread(new PercentCalculate());
        p.start();
        
        //Set the calendar back a day if it was set forward initially
        if (dayrun == 1) {
            calendar.add(Calendar.DATE, -1);
        }
    }
    
	
    private void Reset() {
        System.out.println(Reset + "Resetting SnowDay variables");
        
        today = "";
        tomorrow = "";
        schoolpercent = 0;
        weatherpercent = 0;
        percent = 0;
        tier1 = 0;
        tier2 = 0;
        tier3 = 0;
        tier4 = 0;
        tier5 = 0;
       
        schoolNull = false;
       
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
        txtInfo.setText("");
        txtWeather.setText("");
        nullWeather = true;
        txtGBAcademy.setText("");
        txtGBAcademy.setBackground(Color.WHITE);
        txtGISD.setText("");
        txtGISD.setBackground(Color.WHITE);
        txtHolyFamily.setText("");
        txtHolyFamily.setBackground(Color.WHITE);
        txtWPAcademy.setText("");
        txtWPAcademy.setBackground(Color.WHITE);
        txtDurand.setText("");
        txtDurand.setBackground(Color.WHITE);
        txtBeecher.setText("");
        txtBeecher.setBackground(Color.WHITE);
        txtClio.setText("");
        txtClio.setBackground(Color.WHITE);
        txtDavison.setText("");
        txtDavison.setBackground(Color.WHITE);
        txtFenton.setText("");
        txtFenton.setBackground(Color.WHITE);
        txtFlushing.setText("");
        txtFlushing.setBackground(Color.WHITE);
        txtGenesee.setText("");
        txtGenesee.setBackground(Color.WHITE);
        txtKearsley.setText("");
        txtKearsley.setBackground(Color.WHITE);
        txtLkFenton.setText("");
        txtLkFenton.setBackground(Color.WHITE);
        txtLinden.setText("");
        txtLinden.setBackground(Color.WHITE);
        txtMontrose.setText("");
        txtMontrose.setBackground(Color.WHITE);
        txtMorris.setText("");
        txtMorris.setBackground(Color.WHITE);
        txtSzCreek.setText("");
        txtSzCreek.setBackground(Color.WHITE);
        txtAtherton.setText("");
        txtAtherton.setBackground(Color.WHITE);
        txtDurand.setText("");
        txtDurand.setBackground(Color.WHITE);
        txtHolly.setText("");
        txtHolly.setBackground(Color.WHITE);
        txtLapeer.setText("");
        txtLapeer.setBackground(Color.WHITE);
        txtOwosso.setText("");
        txtOwosso.setBackground(Color.WHITE);
        txtBendle.setText("");
        txtBendle.setBackground(Color.WHITE);
        txtFlint.setText("");
        txtFlint.setBackground(Color.WHITE);
        txtGoodrich.setText("");
        txtGoodrich.setBackground(Color.WHITE);
        txtCarman.setText("");
        txtCarman.setBackground(Color.WHITE);
        txtGB.setText("");
        txtGB.setBackground(Color.WHITE);
        btnCalculate.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
    }
    
    private void checkWeekend() {
        System.out.println(checkWeekend + "Checking the Weekend...");
        //Friday is 6
        //Saturday is 7
        //Sunday is 1
        
        if (weekday == 6) {
            System.out.println(checkWeekend + "Today is Friday (6).");
            txtInfo.setText("Tomorrow is Saturday. \nEnjoy the weekend!");
            optTomorrow.setEnabled(false);
       }else if (weekday == 7) {
           System.out.println(checkWeekend + "Today is Saturday (7).");
            txtInfo.setText("Today is Saturday. \nEnjoy the Weekend!\nPress Quit to exit.");
            txtGB.setText("Grand Blanc: Weekend");
            txtGB.setBackground(Color.YELLOW);
            optToday.setEnabled(false);
            optTomorrow.setEnabled(false);
            lstDays.setEnabled(false);
        }else if (weekday == 1) {
            System.out.println(checkWeekend + "Today is Sunday (1).");
            txtInfo.setText("Today is Sunday. \nEnjoy the Weekend!");
            optToday.setEnabled(false);
        }
    }
    
    private void checkTime() {
        System.out.println(checkTime + "Checking the time...");
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 7 && calendar.get(Calendar.HOUR_OF_DAY)<16 && weekday!=7 && weekday!=1) {
            System.out.println(checkTime + "Time is between 7AM and 4PM.");
            System.out.println(checkTime + "The school's already open.");
            optToday.setEnabled(false);
            txtGB.setText("Grand Blanc: OPEN");
            txtInfo.setText("The school's already open.");
            dayrun = 1;
        }else if (calendar.get(Calendar.HOUR_OF_DAY) >=16 && weekday!=7 && weekday!=1) {
            System.out.println(checkTime + "Time is after 4PM");
            System.out.println(checkTime + "School's already out for today!");
            optToday.setEnabled(false);
            txtGB.setText("Grand Blanc: Dismissed");
            txtGB.setBackground(Color.YELLOW);
            txtInfo.setText("School's already out for today!");
            dayrun = 1;
        }
    }
   

    private class WJRTScraper implements Runnable {
        @Override
        public void run() {
        //Scrape School Closings from WJRT with Jsoup.
        System.out.println(WJRTScraper + "Starting WJRT Scraper");
        Document schools = null;
        
        //The following is a rigged archive from January 5th - every school referenced by this program was closed the following day.
        System.out.println(WJRTScraper + "Reading from Closings.htm...");
        File input = new File("./Closings.htm");
            try {
                schools = Jsoup.parse(input, "UTF-8", "");
                System.out.println(WJRTScraper + "Read successful");
            } catch (IOException ex) {
                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(WJRTScraper + "Couldn't read the file.");
                txtInfo.setText(txtInfo.getText() + "\nCould not retrieve school closings. \nAre you connected to the internet?");
            }
        
        //This is a second rigged archive from December 23rd - Swartz Creek and Kearsley were closed on the day for reference.
//        File input = new File("./ClosingsToday.htm");
//            try {
//                schools = Jsoup.parse(input, "UTF-8", "");
//            } catch (IOException ex) {
//                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//                txtInfo.setText(txtInfo.getText() + "\nCould not retrieve school closings. \nAre you connected to the internet?");
//            }
            
        //This third document tests for false triggers, e.g. "Owosso" shouldn't show as "closed" if only "Owosso Senior Center" is closed.
        //This document will not trigger any closings if the code is working properly.
//        File input = new File("./Trials.htm");
//            try {
//                schools = Jsoup.parse(input, "UTF-8", "");
//            } catch (IOException ex) {
//                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//                txtInfo.setText(txtInfo.getText() + "\nCould not retrieve school closings. \nAre you connected to the internet?");
//            }
            
        //Fourth html archive - every school except GB, Durand, Owosso, and Holy Family is closed (shouldn't trigger 100%)
//        File input = new File("./GBNotClosed.htm");
//            try {
//                schools = Jsoup.parse(input, "UTF-8", "");
//            } catch (IOException ex) {
//                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//                txtInfo.setText(txtInfo.getText() + "\nCould not retrieve school closings. \nAre you connected to the internet?");
//            }
            
        //This is a blank example (no active records) - check how the program runs when nullpointerexception is thrown
//        File input = new File("./Weather.htm");
//            try {
//                schools = Jsoup.parse(input, "UTF-8", "");
//            } catch (IOException ex) {
//                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//                txtInfo.setText(txtInfo.getText() + "\nCould not retrieve school closings. \nAre you connected to the internet?");
//            }
            
        //This is the current listings page.
//           try {
//              schools = Jsoup.connect("http://ftpcontent2.worldnow.com/wjrt/school/closings.htm").get();
//           } catch (IOException ex) {
//               Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//               txtInfo.setText(txtInfo.getText() + "\nCould not retrieve school closings. \nAre you connected to the internet?");
//           }
        
        for (Element row : schools.select("td[bgcolor]")) {
            System.out.println(WJRTScraper + "the for loop is working");
            orgName = orgName + "\n" + (row.select("font.orgname").first().text());
            status = status + "\n" + (row.select("font.status").first().text());
        }
        System.out.println(WJRTScraper + "Loop exited.");
            
        System.out.println(WJRTScraper + "Checking for null pointers...");
        if (orgName == null || status == null) {
            System.out.println(WJRTScraper + "orgName or status is null.");
            schooltext = schools.text();
            //This shows in place of the table (as plain text) if no schools or institutions are closed.
            if (schooltext.contains("no active records")) {
                System.out.println(WJRTScraper + "No schools are closed.");
                txtInfo.setText(txtInfo.getText() + "\nDoesn't look like *any* schools are closed.");
            }else{
                System.out.println(WJRTScraper + "Webpage layout was not recognized.");
                txtInfo.setText(txtInfo.getText() + "\nUnable to parse WJRT listings. \nIf this error persists contact the developer.");
            }
            
            orgName = "DummyLine1\nDummyLine2\nDummyLine3";
            status = "DummyLine1\nDummyLine2\nDummyLine3";
        }
        
        System.out.println(WJRTScraper + "Splitting orgName and status strings by line break.");
        System.out.println(WJRTScraper + "Saving to orgNameLine and statusLine.");
        System.out.println(WJRTScraper + "This will create string arrays that can be parsed by for loops.");
        
        orgNameLine = orgName.split("\n");
        statusLine = status.split("\n");
        
        //The first test: School Closings!
        //Decide whether to check for today's closings or tomorrow's closings.
        if (dayrun == 0) {
            System.out.println(WJRTScraper + "Will check closings for today.");
            checkClosingsToday();
        }else if (dayrun == 1) {
            System.out.println(WJRTScraper + "Will check closings for tomorrow.");
            checkClosingsTomorrow();
        }

        //Sanity Check - make sure GB isn't actually closed before predicting
        System.out.println(WJRTScraper + "Checking to see if Grand Blanc is already closed.");
        checkGBClosed();
        
        System.out.println(WJRTScraper + "WJRTScraper finished.");
    }
    }
    

    private void checkClosingsToday() {
         for (int i = 1; i < orgNameLine.length; i++) {
            if (!(GBAcademy)) {
                if (orgNameLine[i].contains("Grand Blanc Academy")&& statusLine[i].contains("Closed Today")) {
                txtGBAcademy.setText("Grand Blanc Academy: CLOSED");
                txtGBAcademy.setBackground(Color.YELLOW);
                System.out.println(checkClosingsToday + "Closures Detected Correctly");
                tier1++;
                GBAcademy = true;
            }else{
                txtGBAcademy.setText("Grand Blanc Academy: OPEN");
            }
            }
            if (!(GISD)) {
            if (orgNameLine[i].contains("Genesee I.S.D.")&& statusLine[i].contains("Closed Today")) {
                txtGISD.setText("Genesee I.S.D.: CLOSED");
                txtGISD.setBackground(Color.YELLOW);
                tier1++;
                GISD = true;
            }else{
                txtGISD.setText("Genesee I.S.D.: OPEN");
            }
            }
            if (!(HolyFamily)) {
            if (orgNameLine[i].contains("Holy Family")&& statusLine[i].contains("Closed Today")) {
                txtHolyFamily.setText("Holy Family: CLOSED");
                txtHolyFamily.setBackground(Color.YELLOW);
                tier1++;
                HolyFamily = true;
            }else{
                txtHolyFamily.setText("Holy Family: OPEN");
            }
            }
            if (!(WPAcademy)) {
            if (orgNameLine[i].contains("Woodland Park Academy")&& statusLine[i].contains("Closed Today")) {
                txtWPAcademy.setText("Woodland Park Academy: CLOSED");
                txtWPAcademy.setBackground(Color.YELLOW);
                tier1++;
                WPAcademy = true;
            }else{
                txtWPAcademy.setText("Woodland Park Academy: OPEN");            
            }
            }
            if (!(Durand)) {
            if (orgNameLine[i].contains("Durand") && !orgNameLine[i].contains("Senior") && statusLine[i].contains("Closed Today")) {
                txtDurand.setText("Durand: CLOSED");
                txtDurand.setBackground(Color.YELLOW);
                tier2++;
                Durand = true;
            }else{
                txtDurand.setText("Durand: OPEN");
            }
            }
            if (!(Holly)) {
            if (orgNameLine[i].contains("Holly") && !orgNameLine[i].contains("Academy") && statusLine[i].contains("Closed Today")) {
                txtHolly.setText("Holly: CLOSED");
                txtHolly.setBackground(Color.YELLOW);
                tier2++;
                Holly = true;
            }else{
                txtHolly.setText("Holly: OPEN");
            }
            }
            if (!(Lapeer)) {
            if (orgNameLine[i].contains("Lapeer") && !orgNameLine[i].contains("Chatfield") && !orgNameLine[i].contains("Transit") && !orgNameLine[i].contains("CMH") && !orgNameLine[i].contains("Tech") && !orgNameLine[i].contains("Offices") && !orgNameLine[i].contains("Library") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Paul") && statusLine[i].contains("Closed Today")) {
                txtLapeer.setText("Lapeer: CLOSED");
                txtLapeer.setBackground(Color.YELLOW);
                tier2++;
                Lapeer = true;
              }else{
                txtLapeer.setText("Lapeer: OPEN");
            }
            }
            if (!(Owosso)) {
            if (orgNameLine[i].contains("Owosso") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Baker") && !orgNameLine[i].contains("Paul") && statusLine[i].contains("Closed Today")) {
                txtOwosso.setText("Owosso: CLOSED");
                txtOwosso.setBackground(Color.YELLOW);
                tier2++;
                Owosso = true;
            }else{
                txtOwosso.setText("Owosso: OPEN");
            }
            }
            if (!(Beecher)) {
            if (orgNameLine[i].contains("Beecher") && statusLine[i].contains("Closed Today")) {
                txtBeecher.setText("Beecher: CLOSED");
                txtBeecher.setBackground(Color.YELLOW);
                tier2++;
                Beecher = true;
            }else{
                txtBeecher.setText("Beecher: OPEN");
            }
            }
            if (!(Clio)) {
            if (orgNameLine[i].contains("Clio") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Cornerstone") && statusLine[i].contains("Closed Today")) {
                txtClio.setText("Clio: CLOSED");
                txtClio.setBackground(Color.YELLOW);
                tier3++;
                Clio = true;
            }else{
                txtClio.setText("Clio: OPEN");
            }
            }
            if (!(Davison)) {
            if (orgNameLine[i].contains("Davison") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Faith") && !orgNameLine[i].contains("Montessori") && statusLine[i].contains("Closed Today")) {
                txtDavison.setText("Davison: CLOSED");
                txtDavison.setBackground(Color.YELLOW);
                tier3++;
                Davison = true;
            }else{
                txtDavison.setText("Davison: OPEN");
            }
            }
            if (!(Fenton)) {
            if (orgNameLine[i].contains("Fenton") && !orgNameLine[i].contains("Lake") && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Montessori") && statusLine[i].contains("Closed Today")) {
                txtFenton.setText("Fenton: CLOSED");
                txtFenton.setBackground(Color.YELLOW);
                tier3++;
                Fenton = true;
            }else{
                txtFenton.setText("Fenton: OPEN");
            }
            }
            if (!(Flushing)) {
            if (orgNameLine[i].contains("Flushing") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Robert") && statusLine[i].contains("Closed Today")) {
                txtFlushing.setText("Flushing: CLOSED");
                txtFlushing.setBackground(Color.YELLOW);
                tier3++;
                Flushing = true;
            }else{
                txtFlushing.setText("Flushing: OPEN");
            } 
            }
            if (!(Genesee)) {
            if (orgNameLine[i].contains("Genesee") && !orgNameLine[i].contains("Freedom") && !orgNameLine[i].contains("Christian") && !orgNameLine[i].contains("Mobile") && !orgNameLine[i].contains("Programs") && !orgNameLine[i].contains("Hlth") && !orgNameLine[i].contains("Sys") && !orgNameLine[i].contains("Stem") && !orgNameLine[i].contains("I.S.D.") && statusLine[i].contains("Closed Today")) {
                txtGenesee.setText("Genesee: CLOSED");
                txtGenesee.setBackground(Color.YELLOW);
                tier3++;
                Genesee = true;
            }else{
                txtGenesee.setText("Genesee: OPEN");
            }
            }
            if (!(Kearsley)) {
            if (orgNameLine[i].contains("Kearsley") && statusLine[i].contains("Closed Today")) {
                txtKearsley.setText("Kearsley: CLOSED");
                txtKearsley.setBackground(Color.YELLOW);
                tier3++;
                Kearsley = true;
            }else{
                txtKearsley.setText("Kearsley: OPEN");
            } 
            }
            if (!(LKFenton)) {
            if (orgNameLine[i].contains("Lake Fenton") && statusLine[i].contains("Closed Today")) {
                txtLkFenton.setText("Lake Fenton: CLOSED");
                txtLkFenton.setBackground(Color.YELLOW);
                tier3++;
                LKFenton = true;
            }else{
                txtLkFenton.setText("Lake Fenton: OPEN");
            } 
            }
            if (!(Linden)) {
            if (orgNameLine[i].contains("Linden") && !orgNameLine[i].contains("Charter") && statusLine[i].contains("Closed Today")) {
                txtLinden.setText("Linden: CLOSED");
                txtLinden.setBackground(Color.YELLOW);
                tier3++;
                Linden = true;
            }else{
                txtLinden.setText("Linden: OPEN");
            } 
            }
            if (!(Montrose)) {
            if (orgNameLine[i].contains("Montrose") && !orgNameLine[i].contains("Senior") && statusLine[i].contains("Closed Today")) {
                txtMontrose.setText("Montrose: CLOSED");
                txtMontrose.setBackground(Color.YELLOW);
                tier3++;
                Montrose = true;
            }else{
                txtMontrose.setText("Montrose: OPEN");
            } 
            }
            if (!(Morris)) {
            if (orgNameLine[i].contains("Mt. Morris") && !orgNameLine[i].contains("Administration") && !orgNameLine[i].contains("Twp") && !orgNameLine[i].contains("Mary") && statusLine[i].contains("Closed Today")) {
                txtMorris.setText("Mount Morris: CLOSED");
                txtMorris.setBackground(Color.YELLOW);
                tier3++;
                Morris = true;
            }else{
                txtMorris.setText("Mount Morris: OPEN");
            } 
            }
            if (!(SzCreek)) {
            if (orgNameLine[i].contains("Swartz Creek") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Montessori") && statusLine[i].contains("Closed Today")) {
                txtSzCreek.setText("Swartz Creek: CLOSED");
                txtSzCreek.setBackground(Color.YELLOW);
                tier3++;
                SzCreek = true;
            }else{
                txtSzCreek.setText("Swartz Creek: OPEN");
            }
            }
            if (!(Atherton)) {
            if (orgNameLine[i].contains("Atherton") && statusLine[i].contains("Closed Today")) {
                txtAtherton.setText("Atherton: CLOSED");
                txtAtherton.setBackground(Color.YELLOW);
                tier4++;
                Atherton = true;
            }else{
                txtAtherton.setText("Atherton: OPEN");
            }
            }
            if (!(Bendle)) {
            if (orgNameLine[i].contains("Bendle") && statusLine[i].contains("Closed Today")) {
                txtBendle.setText("Bendle: CLOSED");
                txtBendle.setBackground(Color.YELLOW);
                tier4++;
                Bendle = true;
            }else{
                txtBendle.setText("Bendle: OPEN");
            }
            }
            if (!(Flint)) {
            if (orgNameLine[i].contains("Flint Community Schools") && statusLine[i].contains("Closed Today")) {
                txtFlint.setText("Flint: CLOSED");
                txtFlint.setBackground(Color.YELLOW);
                tier4++;
                Flint = true;
            }else{
                txtFlint.setText("Flint: OPEN");
            }
            }
            if (!(Goodrich)) {
            if (orgNameLine[i].contains("Goodrich") && statusLine[i].contains("Closed Today")) {
                txtGoodrich.setText("Goodrich: CLOSED");
                txtGoodrich.setBackground(Color.YELLOW);
                tier4++;
                Goodrich = true;
            }else{
                txtGoodrich.setText("Goodrich: OPEN");
            }
            }
            if (!(Carman)) {
            if (orgNameLine[i].contains("Carman-Ainsworth") && !orgNameLine[i].contains("Senior") && statusLine[i].contains("Closed Today")) {
                txtCarman.setText("Carman-Ainsworth: CLOSED");
                txtCarman.setBackground(Color.ORANGE);
                tier5++;
                Carman = true;
            }else{
                txtCarman.setText("Carman-Ainsworth: OPEN");
            }
            }
         }
    }

    private void checkClosingsTomorrow() {
         for (int i = 1; i < orgNameLine.length; i++) {
            if (!(GBAcademy)) {
                if (orgNameLine[i].contains("Grand Blanc Academy")&& statusLine[i].contains("Closed Tomorrow")) {
                txtGBAcademy.setText("Grand Blanc Academy: CLOSED");
                txtGBAcademy.setBackground(Color.YELLOW);
                System.out.println(checkClosingsTomorrow + "Closures Detected Correctly");
                tier1++;
                GBAcademy = true;
            }else{
                txtGBAcademy.setText("Grand Blanc Academy: OPEN");
            }
            }
            if (!(GISD)) {
            if (orgNameLine[i].contains("Genesee I.S.D.")&& statusLine[i].contains("Closed Tomorrow")) {
                txtGISD.setText("Genesee I.S.D.: CLOSED");
                txtGISD.setBackground(Color.YELLOW);
                tier1++;
                GISD = true;
            }else{
                txtGISD.setText("Genesee I.S.D.: OPEN");
            }
            }
            if (!(HolyFamily)) {
            if (orgNameLine[i].contains("Holy Family")&& statusLine[i].contains("Closed Tomorrow")) {
                txtHolyFamily.setText("Holy Family: CLOSED");
                txtHolyFamily.setBackground(Color.YELLOW);
                tier1++;
                HolyFamily = true;
            }else{
                txtHolyFamily.setText("Holy Family: OPEN");
            }
            }
            if (!(WPAcademy)) {
            if (orgNameLine[i].contains("Woodland Park Academy")&& statusLine[i].contains("Closed Tomorrow")) {
                txtWPAcademy.setText("Woodland Park Academy: CLOSED");
                txtWPAcademy.setBackground(Color.YELLOW);
                tier1++;
                WPAcademy = true;
            }else{
                txtWPAcademy.setText("Woodland Park Academy: OPEN");            
            }
            }
            if (!(Durand)) {
            if (orgNameLine[i].contains("Durand") && !orgNameLine[i].contains("Senior") && statusLine[i].contains("Closed Tomorrow")) {
                txtDurand.setText("Durand: CLOSED");
                txtDurand.setBackground(Color.YELLOW);
                tier2++;
                Durand = true;
            }else{
                txtDurand.setText("Durand: OPEN");
            }
            }
            if (!(Holly)) {
            if (orgNameLine[i].contains("Holly") && !orgNameLine[i].contains("Academy") && statusLine[i].contains("Closed Tomorrow")) {
                txtHolly.setText("Holly: CLOSED");
                txtHolly.setBackground(Color.YELLOW);
                tier2++;
                Holly = true;
            }else{
                txtHolly.setText("Holly: OPEN");
            }
            }
            if (!(Lapeer)) {
            if (orgNameLine[i].contains("Lapeer") && !orgNameLine[i].contains("Chatfield") && !orgNameLine[i].contains("Transit") && !orgNameLine[i].contains("CMH") && !orgNameLine[i].contains("Tech") && !orgNameLine[i].contains("Offices") && !orgNameLine[i].contains("Library") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Paul") && statusLine[i].contains("Closed Tomorrow")) {
                txtLapeer.setText("Lapeer: CLOSED");
                txtLapeer.setBackground(Color.YELLOW);
                tier2++;
                Lapeer = true;
              }else{
                txtLapeer.setText("Lapeer: OPEN");
            }
            }
            if (!(Owosso)) {
            if (orgNameLine[i].contains("Owosso") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Baker") && !orgNameLine[i].contains("Paul") && statusLine[i].contains("Closed Tomorrow")) {
                txtOwosso.setText("Owosso: CLOSED");
                txtOwosso.setBackground(Color.YELLOW);
                tier2++;
                Owosso = true;
            }else{
                txtOwosso.setText("Owosso: OPEN");
            }
            }
            if (!(Beecher)) {
            if (orgNameLine[i].contains("Beecher") && statusLine[i].contains("Closed Tomorrow")) {
                txtBeecher.setText("Beecher: CLOSED");
                txtBeecher.setBackground(Color.YELLOW);
                tier2++;
                Beecher = true;
            }else{
                txtBeecher.setText("Beecher: OPEN");
            }
            }
            if (!(Clio)) {
            if (orgNameLine[i].contains("Clio") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Cornerstone") && statusLine[i].contains("Closed Tomorrow")) {
                txtClio.setText("Clio: CLOSED");
                txtClio.setBackground(Color.YELLOW);
                tier3++;
                Clio = true;
            }else{
                txtClio.setText("Clio: OPEN");
            }
            }
            if (!(Davison)) {
            if (orgNameLine[i].contains("Davison") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Faith") && !orgNameLine[i].contains("Montessori") && statusLine[i].contains("Closed Tomorrow")) {
                txtDavison.setText("Davison: CLOSED");
                txtDavison.setBackground(Color.YELLOW);
                tier3++;
                Davison = true;
            }else{
                txtDavison.setText("Davison: OPEN");
            }
            }
            if (!(Fenton)) {
            if (orgNameLine[i].contains("Fenton") && !orgNameLine[i].contains("Lake") && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Montessori") && statusLine[i].contains("Closed Tomorrow")) {
                txtFenton.setText("Fenton: CLOSED");
                txtFenton.setBackground(Color.YELLOW);
                tier3++;
                Fenton = true;
            }else{
                txtFenton.setText("Fenton: OPEN");
            }
            }
            if (!(Flushing)) {
            if (orgNameLine[i].contains("Flushing") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Robert") && statusLine[i].contains("Closed Tomorrow")) {
                txtFlushing.setText("Flushing: CLOSED");
                txtFlushing.setBackground(Color.YELLOW);
                tier3++;
                Flushing = true;
            }else{
                txtFlushing.setText("Flushing: OPEN");
            } 
            }
            if (!(Genesee)) {
            if (orgNameLine[i].contains("Genesee") && !orgNameLine[i].contains("Freedom") && !orgNameLine[i].contains("Christian") && !orgNameLine[i].contains("Mobile") && !orgNameLine[i].contains("Programs") && !orgNameLine[i].contains("Hlth") && !orgNameLine[i].contains("Sys") && !orgNameLine[i].contains("Stem") && !orgNameLine[i].contains("I.S.D.") && statusLine[i].contains("Closed Tomorrow")) {
                txtGenesee.setText("Genesee: CLOSED");
                txtGenesee.setBackground(Color.YELLOW);
                tier3++;
                Genesee = true;
            }else{
                txtGenesee.setText("Genesee: OPEN");
            }
            }
            if (!(Kearsley)) {
            if (orgNameLine[i].contains("Kearsley") && statusLine[i].contains("Closed Tomorrow")) {
                txtKearsley.setText("Kearsley: CLOSED");
                txtKearsley.setBackground(Color.YELLOW);
                tier3++;
                Kearsley = true;
            }else{
                txtKearsley.setText("Kearsley: OPEN");
            } 
            }
            if (!(LKFenton)) {
            if (orgNameLine[i].contains("Lake Fenton") && statusLine[i].contains("Closed Tomorrow")) {
                txtLkFenton.setText("Lake Fenton: CLOSED");
                txtLkFenton.setBackground(Color.YELLOW);
                tier3++;
                LKFenton = true;
            }else{
                txtLkFenton.setText("Lake Fenton: OPEN");
            } 
            }
            if (!(Linden)) {
            if (orgNameLine[i].contains("Linden") && !orgNameLine[i].contains("Charter") && statusLine[i].contains("Closed Tomorrow")) {
                txtLinden.setText("Linden: CLOSED");
                txtLinden.setBackground(Color.YELLOW);
                tier3++;
                Linden = true;
            }else{
                txtLinden.setText("Linden: OPEN");
            } 
            }
            if (!(Montrose)) {
            if (orgNameLine[i].contains("Montrose") && !orgNameLine[i].contains("Senior") && statusLine[i].contains("Closed Tomorrow")) {
                txtMontrose.setText("Montrose: CLOSED");
                txtMontrose.setBackground(Color.YELLOW);
                tier3++;
                Montrose = true;
            }else{
                txtMontrose.setText("Montrose: OPEN");
            } 
            }
            if (!(Morris)) {
            if (orgNameLine[i].contains("Mt. Morris") && !orgNameLine[i].contains("Administration") && !orgNameLine[i].contains("Twp") && !orgNameLine[i].contains("Mary") && statusLine[i].contains("Closed Tomorrow")) {
                txtMorris.setText("Mount Morris: CLOSED");
                txtMorris.setBackground(Color.YELLOW);
                tier3++;
                Morris = true;
            }else{
                txtMorris.setText("Mount Morris: OPEN");
            } 
            }
            if (!(SzCreek)) {
            if (orgNameLine[i].contains("Swartz Creek") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Montessori") && statusLine[i].contains("Closed Tomorrow")) {
                txtSzCreek.setText("Swartz Creek: CLOSED");
                txtSzCreek.setBackground(Color.YELLOW);
                tier3++;
                SzCreek = true;
            }else{
                txtSzCreek.setText("Swartz Creek: OPEN");
            }
            }
            if (!(Atherton)) {
            if (orgNameLine[i].contains("Atherton") && statusLine[i].contains("Closed Tomorrow")) {
                txtAtherton.setText("Atherton: CLOSED");
                txtAtherton.setBackground(Color.YELLOW);
                tier4++;
                Atherton = true;
            }else{
                txtAtherton.setText("Atherton: OPEN");
            }
            }
            if (!(Bendle)) {
            if (orgNameLine[i].contains("Bendle") && statusLine[i].contains("Closed Tomorrow")) {
                txtBendle.setText("Bendle: CLOSED");
                txtBendle.setBackground(Color.YELLOW);
                tier4++;
                Bendle = true;
            }else{
                txtBendle.setText("Bendle: OPEN");
            }
            }
            if (!(Flint)) {
            if (orgNameLine[i].contains("Flint Community Schools") && statusLine[i].contains("Closed Tomorrow")) {
                txtFlint.setText("Flint: CLOSED");
                txtFlint.setBackground(Color.YELLOW);
                tier4++;
                Flint = true;
            }else{
                txtFlint.setText("Flint: OPEN");
            }
            }
            if (!(Goodrich)) {
            if (orgNameLine[i].contains("Goodrich") && statusLine[i].contains("Closed Tomorrow")) {
                txtGoodrich.setText("Goodrich: CLOSED");
                txtGoodrich.setBackground(Color.YELLOW);
                tier4++;
                Goodrich = true;
            }else{
                txtGoodrich.setText("Goodrich: OPEN");
            }
            }
            if (!(Carman)) {
            if (orgNameLine[i].contains("Carman-Ainsworth") && !orgNameLine[i].contains("Senior") && statusLine[i].contains("Closed Tomorrow")) {
                txtCarman.setText("Carman-Ainsworth: CLOSED");
                txtCarman.setBackground(Color.ORANGE);
                tier5++;
                Carman = true;
            }else{
                txtCarman.setText("Carman-Ainsworth: OPEN");
            }
            }
        }
    }
    
    private void checkGBClosed() {
        System.out.println(checkGBClosed + "Checking if GB is closed.");
        for (int i = 1; i < orgNameLine.length; i++) {
            System.out.println(checkGBClosed + "We're in the loop.");
            if (!GB) {
                System.out.println(checkGBClosed + "GB is false.");
                if (orgNameLine[i].contains("Grand Blanc") && !orgNameLine[i].contains("Academy") && !orgNameLine[i].contains("Freedom") && !orgNameLine[i].contains("Offices") && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Holy") && statusLine[i].contains("Closed Today") && dayrun == 0) {
                    txtInfo.setText(txtInfo.getText() + "\nGrand Blanc is Closed Today! \nEnjoy your Snow Day!");
                    txtGB.setText("Grand Blanc: CLOSED");
                    txtGB.setBackground(Color.RED);
                    percent = 100;
                    GB = true;
                    System.out.println(checkGBClosed + "GB Found (today)!");
                    break;
                }else if (orgNameLine[i].contains("Grand Blanc")&& !orgNameLine[i].contains("Academy") && !orgNameLine[i].contains("Freedom") && !orgNameLine[i].contains("Offices") && !orgNameLine[i].contains("City") && !orgNameLine[i].contains("Senior") && !orgNameLine[i].contains("Holy") && statusLine[i].contains("Closed Tomorrow") && dayrun == 1) {
                    txtInfo.setText(txtInfo.getText() + "\nGrand Blanc is Closed Tomorrow! \nEnjoy your Snow Day!");
                    txtGB.setText("Grand Blanc: CLOSED");
                    txtGB.setBackground(Color.RED);
                    percent = 100;
                    GB = true;
                    System.out.println(checkGBClosed + "GB Found (tomorrow)!");
                    break;
                }else{
                    System.out.println(checkGBClosed + "Didn't find GB yet");
                    txtGB.setText("Grand Blanc: OPEN");
                    txtGB.setBackground(Color.WHITE);
                    GB = false;
                }
            }
        }
         System.out.println(checkGBClosed + "Loop exited.");
    }
    
    private class WeatherScraper implements Runnable {
        @Override
        public void run() {
        System.out.println(weatherScraper + "Weather scraper started.");
        //txtWeather.setText(txtWeather.getText() + "Retrieving Weather from NWS Detroit/Pontiac...");
        //Change the percentage based on current storm/wind/temperature warnings.
        Document weatherdoc = null;
        //Live html
//            try {
//                weatherdoc = Jsoup.connect("http://forecast.weather.gov/afm/PointClick.php?lat=42.92580&lon=-83.61870").get();
//            } catch (IOException ex) {
//                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//                txtInfo.setText(txtInfo.getText() + "\nCould not retrieve weather information. \nAre you connected to the internet?");
//            }
        
        //Document with multiple preset conditions
        System.out.println(weatherScraper + "Accessing Weather.htm...");
        File weatherinput = new File("./Weather.htm");
        try {
            weatherdoc = Jsoup.parse(weatherinput, "UTF-8", "");
            System.out.println(weatherScraper + "Successfully parsed file.");
        } catch (IOException ex) {
            System.out.println(weatherScraper + "Couldn't read the file.");
            Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //Document with no warnings
        //File weatherinput = new File("./WeatherTest.htm");
//        try {
//            weatherdoc = Jsoup.parse(weatherinput, "UTF-8", "");
//        } catch (IOException ex) {
//            Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //NullPointerException test
//        File weatherinput = new File("./Blank.htm");
//        try {
//            weatherdoc = Jsoup.parse(weatherinput, "UTF-8", "");
//        } catch (IOException ex) {
//            Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //String weatherWarn = null;
        System.out.println(weatherScraper + "Searching for elements in class 'warn'");
        Elements weatherWarn = weatherdoc.getElementsByClass("warn");
        System.out.println(weatherScraper + "Saving elements to searchable string weathertext");
        weathertext = weatherWarn.toString();
        
        if (weathertext.equals("")) {
            System.out.println(weatherScraper + "weathertext is empty.");
            try {
                System.out.println(weatherScraper + "Searching for element 'hazards_content'.");
                System.out.println(weatherScraper + "This element should always be present even if no hazards are present.");
                Element weatherNull = weatherdoc.getElementById("hazards_content");
                weathercheck = weatherNull.toString();
                if (weathercheck.contains("No Hazards in Effect")) {
                    System.out.println(weatherScraper + "Webpage parsed correctly: no hazards present.");
                    txtWeather.setText("No applicable weather warnings.");
                }
            }catch (NullPointerException e) {
                System.out.println(weatherScraper + "Something has changed in the webpage and can't be parsed.");
                txtWeather.setText("Unable to obtain weather. \nIf this error persists please contact the developer.");
            }
        }else{
            System.out.println(weatherScraper + "Hazards found.");
            //Use the data
            getWeather();
            System.out.println(weatherScraper + "WeatherScraper finished.");
        }
    }
    }
    
    private void getWeather() {
        System.out.println(getWeather + "Running getWeather()");
        System.out.println(getWeather + "Only the highest weatherpercent is stored (not cumulative)");
        if (weathertext.contains("Hazardous Weather Outlook")) {
            System.out.println(getWeather + "Hazardous Weather Outlook - no effect on percent (too vague)");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Hazardous Weather Outlook is in effect.");
            
            }else{
            txtWeather.setText("A Hazardous Weather Outlook is in effect.");
            nullWeather = false;
            }
            weatherpercent = 0;
        }
        if (weathertext.contains("Significant Weather Advisory")) {
            System.out.println(getWeather + "Significant Weather Advisory - 15% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Significant Weather Advisory is in effect.");
            }else{
            txtWeather.setText("A Significant Weather Advisory is in effect.");
            nullWeather = false;
            }
            weatherpercent = 15;
        }
        if (weathertext.contains("Winter Weather Advisory")) {
            System.out.println(getWeather + "Winter Weather Advisory - 30% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Winter Weather Advisory is in effect.");
            }else{
            txtWeather.setText("A Winter Weather Advisory is in effect.");
            nullWeather = false;
            }
            weatherpercent = 30;
        }
        if (weathertext.contains("Winter Storm Watch")) {
            System.out.println(getWeather + "Winter Storm Watch - 40% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Winter Storm Watch is in effect.");
            }else{
            txtWeather.setText("A Winter Storm Watch is in effect.");
            nullWeather = false;
            }
            weatherpercent = 40;
        }
        if (weathertext.contains("Lake-Effect Snow Advisory") || weathertext.contains("Lake-Effect Snow Watch")) {
            System.out.println(getWeather + "Lake Effect Snow Advisory / Watch - 40% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Lake-Effect Snow Advisory / Watch is in effect.");
            }else{
            txtWeather.setText("A Lake-Effect Snow Advisory / Watch is in effect.");
            nullWeather = false;
            }
            weatherpercent = 40;
        }
        if (weathertext.contains("Freezing Rain Advisory") || weathertext.contains("Freezing Drizzle Advisory") || weathertext.contains("Freezing Fog Advisory")) {
            System.out.println(getWeather + "Freezing Rain - 40% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Freezing Rain / Drizzle / Fog Advisory is in effect.");
            }else{
            txtWeather.setText("A Freezing Rain / Drizzle / Fog Advisory is in effect.");
            nullWeather = false;
            }
            weatherpercent = 40;
        }
        if (weathertext.contains("Wind Chill Advisory")) {
            System.out.println(getWeather + "Wind Chill Advisory - 40% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Wind Chill Advisory is in effect.");
            }else{
            txtWeather.setText("A Wind Chill Advisory is in effect.");
            nullWeather = false;
            }
            weatherpercent = 40;
        }
        
        if (weathertext.contains("Wind Chill Watch")) {
            System.out.println(getWeather + "Wind Chill Watch - 40% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Wind Chill Watch is in effect.");
            }else{
            txtWeather.setText("A Wind Chill Watch is in effect.");
            nullWeather = false;
            }
            weatherpercent = 40;
        }
        if (weathertext.contains("Blizzard Watch")) {
            System.out.println(getWeather + "Blizzard Watch - 40% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Blizzard Watch is in effect.");
            }else{
            txtWeather.setText("A Blizzard Watch is in effect.");
            }
            nullWeather = false;
            weatherpercent = 40;
        }
        if (weathertext.contains("Winter Storm Warning")) {
            System.out.println(getWeather + "Winter Storm Warning - 60% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Winter Storm Warning is in effect.");
            }else{
            txtWeather.setText("A Winter Storm Warning is in effect.");
            nullWeather = false;
            }
            weatherpercent = 60;
        }
        if (weathertext.contains("Lake-Effect Snow Warning")) {
            System.out.println(getWeather + "Lake Effect Snow Warning - 70% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Lake-Effect Snow Warning is in effect.");
            }else{
            txtWeather.setText("A Lake-Effect Snow Warning is in effect.");
            nullWeather = false;
            }
            weatherpercent = 70;
        }
        if (weathertext.contains("Ice Storm Warning")) {
            System.out.println(getWeather + "Ice Storm Warning - 70% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nAn Ice Storm Warning is in effect.");
            }else{
            txtWeather.setText("An Ice Storm Warning is in effect.");
            nullWeather = false;
            }
            weatherpercent = 70;
        }
        if (weathertext.contains("Wind Chill Warning")) {
            System.out.println(getWeather + "Wind Chill Warning - 75% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Wind Chill Warning is in effect.");
            }else{
            txtWeather.setText("A Wind Chill Warning is in effect.");
            nullWeather = false;
            }
            weatherpercent = 75;
        }
        if (weathertext.contains("Blizzard Warning")) {
            System.out.println(getWeather + "Blizzard Warning - 75% weatherpercent");
            if (!nullWeather) {
            txtWeather.setText(txtWeather.getText() + "\nA Blizzard Warning is in effect.");
            }else{
            txtWeather.setText("A Blizzard Warning is in effect.");
            nullWeather = false;
            }
            weatherpercent = 75;    
        } 
    }

    private class PercentCalculate implements Runnable {
        @Override
        public void run(){
            System.out.println(percentCalculate + "Starting PercentCalculate");
            
            //Sleep for 1000 ms - if the while loop is run *too* soon a scraper might not have
            //a chance to start before being considered 'done'
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, e);
            }
            
            //Give the scrapers time to act before displaying the percent
            while (wjrt.isAlive() || nws.isAlive()){
                try {
                    System.out.println(percentCalculate + "Waiting for scrapers to finish...");
                    Thread.sleep(100);
                }catch (InterruptedException ex) {               
                     Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            
            System.out.println(percentCalculate + "Both scrapers have finished.");
        
            if (tier5==1) {
                System.out.println(percentCalculate + "Carman-Ainsworth was closed. We'll close. 90% schoolpercent");
                schoolpercent+=90;
            }else if (tier4!=0) {
                System.out.println(percentCalculate + "Schools near GB were closed. 80% schoolpercent");
                schoolpercent+=80;
            }else if (tier3!=0) {
                System.out.println(percentCalculate + "Schools in Genesee County were closed. 60% schoolpercent");
                schoolpercent+=60;
            }else if (tier2!=0) {
                System.out.println(percentCalculate + "Schools in nearby counties were closed. 40% schoolpercent");
                schoolpercent+=40;
            }else if (tier1!=0) {
                System.out.println(percentCalculate + "Academies were closed. 20% schoolpercent");
                schoolpercent+=20;
            }

            //Calculate the total percent.
            
            if (weatherpercent > schoolpercent) {
                percent = weatherpercent;
            }else if (schoolpercent > weatherpercent) {
                percent = schoolpercent;
            }

            System.out.println(percentCalculate + "schoolpercent: " + schoolpercent);
            System.out.println(percentCalculate + "weatherpercent: " + weatherpercent);
            System.out.println(percentCalculate + "Final percent is " + percent);

            //Reduce the percent chance by three for every snow day entered.
            System.out.println(percentCalculate + "Percent will be reduced by 3% for every snow day that has already occurred.");
            percent-=(days*3);
            //No negative percents.
            if (percent < 0) {
                System.out.println(percentCalculate + "Percent became negative. Setting to 0%.");
                percent = 0;
            }

            //Don't allow a chance above 90%.
            if (percent > 90) {
                System.out.println(percentCalculate + "Percent (somehow) was above 90%. Capping at 90%.");
                percent = 90;
            }

            //Negate the above results for special cases
            if (GB) {
                System.out.println(percentCalculate + "WJRTScraper reports Grand Blanc is closed. Overriding percentage, setting to 100%");
                percent = 100;
            }

            lblPercent.setText("0%");
            lblPercent.setVisible(true);
            
            //Animate lblPercent
            percentscroll = 0;
            
            System.out.println(percentCalculate + "Enjoy this cool little animation.");
            
            //We're done!
            progressBar.setValue(100);
            progressBar.setIndeterminate(false);
            
            //Animate txtPercent
            
            try {
                for (int i = 0; i < percent + 1; i++) {
                    Thread.sleep(10);
                    if (percentscroll >= 0 && percentscroll <= 20) {
                        lblPercent.setForeground(Color.RED);
                    }else if (percentscroll > 20 && percentscroll <=60) {
                        lblPercent.setForeground(Color.ORANGE);
                    }else if (percentscroll > 60 && percentscroll <=80) {
                        lblPercent.setForeground(Color.GREEN);
                    }else if (percentscroll >80) {
                        lblPercent.setForeground(Color.BLUE);
                    }
                    lblPercent.setText((percentscroll) + "%");
                    percentscroll++;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(percentCalculate + "Program Completed. We made it!");
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

        btnCalculate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/snowday/icon.png"))); // NOI18N
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

        lblClosings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/snowday/WJRT.jpg"))); // NOI18N
        getContentPane().add(lblClosings, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 30, -1, 40));
        lblClosings.getAccessibleContext().setAccessibleName("lblClosings");

        lblWeather.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        lblWeather.setIcon(new javax.swing.ImageIcon(getClass().getResource("/snowday/NWS.png"))); // NOI18N
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

        getContentPane().add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 370, 150));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        System.out.println(mainThread + "btnCalculate clicked");
        try {
            SnowDayCalculate();
        } catch (InterruptedException ex) {
            Logger.getLogger(SnowDayGUI.class.getName()).log(Level.SEVERE, null, ex);
            txtInfo.setText(txtInfo.getText() + "\nThe calculation was interrupted.");
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
        if (optToday.isSelected() || optTomorrow.isSelected()) {
            if (lstDays.getSelectedIndex() == 0) {
            btnCalculate.setEnabled(false);
        }else{
            btnCalculate.setEnabled(true);
        }
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

