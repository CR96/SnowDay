package com.gbsnowday.snowday.network;

import com.gbsnowday.snowday.model.ClosingsModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.SwingWorker;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/*Copyright 2014-2016 Corey Rowe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

public class ClosingsScraper extends SwingWorker<ClosingsModel, Void> {

    private ResourceBundle arrayBundle = ResourceBundle
            .getBundle("bundle.ArrayBundle", new Locale("en", "EN"));
    private ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

    private ClosingsModel closingsModel;

    private ArrayList<String> orgNames = new ArrayList<>();
    private ArrayList<String> orgStatuses = new ArrayList<>();

    private int dayrun;
    private String weekdaytoday;
    private String weekdaytomorrow;

    private AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(ClosingsModel closingsModel);
    }

    public ClosingsScraper(int i, AsyncResponse delegate) {
        dayrun = i;
        this.delegate = delegate;
    }

    @Override
    protected ClosingsModel doInBackground() throws Exception {
        closingsModel = new ClosingsModel();

        Document schools = null;
        try {
            schools = Jsoup.connect(
                    bundle.getString("ClosingsURL"))
                    .timeout(10000)
                    .get();

            Element table = schools.select("table").last();
            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) { //Skip header row
                Element row = rows.get(i);
                orgNames.add(row.select("td").get(0).text());
                orgStatuses.add(row.select("td").get(1).text());
            }
        }catch (IOException e) {
            //Connectivity issues
            closingsModel.error = bundle.getString("WJRTConnectionError");
            cancel(true);
        }catch (NullPointerException | IndexOutOfBoundsException e) {
            /* This shows in place of the table (as plain text)
            if no schools or institutions are closed. */
            if (schools != null && !schools.text().contains("no closings or delays")) {
                //Webpage layout was not recognized.
                closingsModel.error = bundle.getString("WJRTParseError");
                cancel(true);
            }
        }finally{
            parseClosings();
        }
        return closingsModel;
    }

    @Override
    protected void done() {
        delegate.processFinish(closingsModel);
    }

    private void parseClosings() {

        LocalDateTime today = LocalDateTime.now();

        //Get the day of the week as a string.
        weekdaytoday = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);

        weekdaytomorrow = today.plusDays(1).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);

        //Sanity check - make sure Grand Blanc isn't already closed before predicting
        closingsModel.GB = isClosed(
                arrayBundle.getString("checks_gb").split(","),
                bundle.getString("GB"),
                true,
                -1);

        if (closingsModel.GB) {
            closingsModel.GBText += bundle.getString("SnowDay")  + "\n";
        } else {
            if (dayrun == 0) {
                if (today.getHour() >= 7 && today.getHour() < 16) {
                    //Time is between 7AM and 4PM. School is already in session.
                    closingsModel.GBText += bundle.getString("SchoolOpen") + "\n";
                    closingsModel.GBOpen = true;
                } else if (today.getHour() >= 16) {
                    //Time is after 4PM. School is already out.
                    closingsModel.GBText += bundle.getString("Dismissed") + "\n";
                    closingsModel.GBOpen = true;
                }
            }
        }

        //Check school closings
        String[] tier1schools = arrayBundle.getString("name_t1").split(",");
        String[] tier2schools = arrayBundle.getString("name_t2").split(",");
        String[] tier3schools = arrayBundle.getString("name_t3").split(",");
        String[] tier4schools = arrayBundle.getString("name_t4").split(",");

        //Tier 4
        closingsModel.Atherton = isClosed(
                arrayBundle.getString("checks_atherton").split(","),
                tier4schools[0],
                false,
                4);
        closingsModel.Bendle = isClosed(
                arrayBundle.getString("checks_bendle").split(","),
                tier4schools[1],
                false,
                4);
        closingsModel.Bentley = isClosed(
                arrayBundle.getString("checks_bentley").split(","),
                tier4schools[2],
                false,
                4);
        closingsModel.Carman = isClosed(
                arrayBundle.getString("checks_carman").split(","),
                tier4schools[3],
                false,
                4);
        closingsModel.Flint = isClosed(
                arrayBundle.getString("checks_flint").split(","),
                tier4schools[4],
                false,
                4);
        closingsModel.Goodrich = isClosed(
                arrayBundle.getString("checks_goodrich").split(","),
                tier4schools[5],
                false,
                4);

        //Tier 3
        closingsModel.Beecher = isClosed(
                arrayBundle.getString("checks_beecher").split(","),
                tier3schools[0],
                false,
                3);
        closingsModel.Clio = isClosed(
                arrayBundle.getString("checks_clio").split(","),
                tier3schools[1],
                false,
                3);
        closingsModel.Davison = isClosed(
                arrayBundle.getString("checks_davison").split(","),
                tier3schools[2],
                false,
                3);
        closingsModel.Fenton = isClosed(
                arrayBundle.getString("checks_fenton").split(","),
                tier3schools[3],
                false,
                3);
        closingsModel.Flushing = isClosed(
                arrayBundle.getString("checks_flushing").split(","),
                tier3schools[4],
                false,
                3);
        closingsModel.Genesee = isClosed(
                arrayBundle.getString("checks_genesee").split(","),
                tier3schools[5],
                false,
                3);
        closingsModel.Kearsley = isClosed(
                arrayBundle.getString("checks_kearsley").split(","),
                tier3schools[6],
                false,
                3);
        closingsModel.LKFenton = isClosed(
                arrayBundle.getString("checks_lkfenton").split(","),
                tier3schools[7],
                false,
                3);
        closingsModel.Linden = isClosed(
                arrayBundle.getString("checks_linden").split(","),
                tier3schools[8],
                false,
                3);
        closingsModel. Montrose = isClosed(
                arrayBundle.getString("checks_montrose").split(","),
                tier3schools[9],
                false,
                3);
        closingsModel.Morris = isClosed(
                arrayBundle.getString("checks_morris").split(","),
                tier3schools[10],
                false,
                3);
        closingsModel.SzCreek = isClosed(
                arrayBundle.getString("checks_szcreek").split(","),
                tier3schools[11],
                false,
                3);

        //Tier 2
        closingsModel.Durand = isClosed(
                arrayBundle.getString("checks_durand").split(","),
                tier2schools[0],
                false,
                2);
        closingsModel.Holly = isClosed(
                arrayBundle.getString("checks_holly").split(","),
                tier2schools[1],
                false,
                2);
        closingsModel.Lapeer = isClosed(
                arrayBundle.getString("checks_lapeer").split(","),
                tier2schools[2],
                false,
                2);
        closingsModel.Owosso = isClosed(
                arrayBundle.getString("checks_owosso").split(","),
                tier2schools[3],
                false,
                2);

        //Tier 1
        closingsModel.GBAcademy = isClosed(
                arrayBundle.getString("checks_gbacademy").split(","),
                tier1schools[0],
                false,
                1);
        closingsModel.GISD = isClosed(
                arrayBundle.getString("checks_gisd").split(","),
                tier1schools[1],
                false,
                1);
        closingsModel.HolyFamily = isClosed(
                arrayBundle.getString("checks_holyfamily").split(","),
                tier1schools[2],
                false,
                1);
        closingsModel.WPAcademy = isClosed(
                arrayBundle.getString("checks_wpacademy").split(","),
                tier1schools[3],
                false,
                1);

        //Set the schoolpercent
        if (closingsModel.tier1 > 2) {
            //3+ academies are closed. 20% schoolpercent.
            closingsModel.schoolpercent = 20;
        }
        if (closingsModel.tier2 > 2) {
            //3+ schools in nearby counties are closed. 40% schoolpercent.
            closingsModel.schoolpercent = 40;
        }
        if (closingsModel.tier3 > 2) {
            //3+ schools in Genesee County are closed. 60% schoolpercent.
            closingsModel.schoolpercent = 60;
        }
        if (closingsModel.tier4 > 2) {
            //3+ schools near GB are closed. 80% schoolpercent.
            closingsModel.schoolpercent = 80;
            if (closingsModel.Carman) {
                //Carman is closed along with 2+ close schools. 90% schoolpercent.
                closingsModel.schoolpercent = 90;
            }
        }
    }

    /** Checks if a specified school or organization is closed or has a message.
     * @param checks The array of potential false positives to be checked
     * @param schoolName The name of the school as present in the array populated by {@link ClosingsScraper}
     * @param isGrandBlanc Whether the school is Grand Blanc or another school
     * @param tier The tier the school belongs to (-1 for Grand Blanc)
     * @return The status of the school
     */
    private boolean isClosed(
            String[] checks,
            String schoolName,
            boolean isGrandBlanc,
            int tier) {

        boolean schoolFound = false;
        boolean result = false;

        for (int i = 0; i < orgNames.size(); i++) {
            if (orgNames.get(i).contains(schoolName)) {
                if (!isFalsePositive(checks, orgNames.get(i))) {
                    schoolFound = true;
                    if (isGrandBlanc) {
                        closingsModel.GBMessage = true;
                        closingsModel.GBText += schoolName + ": " + orgStatuses.get(i) + "\n";
                    } else {
                        closingsModel.displayedOrgNames.add(schoolName);
                        closingsModel.displayedOrgStatuses.add(orgStatuses.get(i));
                    }

                    if (orgStatuses.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                            || orgStatuses.get(i).contains("Closed Today") && dayrun == 0
                            || orgStatuses.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                            || orgStatuses.get(i).contains("Closed Tomorrow") && dayrun == 1) {
                        if (isGrandBlanc) {
                            result = true;
                        } else {
                            switch (tier) {
                                case 1:
                                    closingsModel.tier1++;
                                case 2:
                                    closingsModel.tier2++;
                                case 3:
                                    closingsModel.tier3++;
                                case 4:
                                    closingsModel.tier4++;
                                default:
                            }
                            result = true;
                        }
                    }
                }
            }

            if (schoolFound) {break;}
        }

        if (isGrandBlanc && !schoolFound) {
            closingsModel.GBText += (schoolName) + ": " + bundle.getString("Open") + "\n";
        }else if (!schoolFound){
            closingsModel.displayedOrgNames.add(schoolName);
            closingsModel.displayedOrgStatuses.add(bundle.getString("Open"));
        }

        return result;
    }

    private boolean isFalsePositive(String[] checks, String org) {

        for (String check : checks) {
            if (org.contains(check)) {
                return true;
            }
        }
        return false;
    }
}
