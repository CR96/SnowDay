package com.gbsnowday.snowday.network;

import com.gbsnowday.snowday.model.ClosingModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
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

public class ClosingsScraper extends SwingWorker<List<ClosingModel>, Void> {

    private final ResourceBundle arrayBundle = ResourceBundle
            .getBundle("bundle.ArrayBundle", new Locale("en", "EN"));
    private final ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

    private List<ClosingModel> closingModels;

    private final List<String> orgNames = new ArrayList<>();
    private final List<String> orgStatuses = new ArrayList<>();

    private final List<String> GBText = new ArrayList<>();

    private final int dayrun;
    private String weekdaytoday;
    private String weekdaytomorrow;

    // Levels of school closings (near vs. far)
    private int tier1 = 0;
    private int tier2 = 0;
    private int tier3 = 0;
    private int tier4 = 0;

    private int schoolPercent;

    private boolean GB; // Check for "Grand Blanc Senior Center", "Grand Blanc Academy",
    // "Grand Blanc Road Montessori", "Grand Blanc Gymnastics Co.", and "Freedom Work Grand Blanc"

    // True if GB is already open (GB = false and time is during or after school hours)
    private boolean GBOpen;

    // Grand Blanc has a message (e.g. "Early Dismissal") but isn't actually closed.
    private boolean GBMessage;

    private String error;

    private final AsyncResponse delegate;

    public ClosingsScraper(int dayrun, AsyncResponse delegate) {
        this.dayrun = dayrun;
        this.delegate = delegate;
    }

    public int getSchoolPercent() {
        return schoolPercent;
    }

    public boolean isGBClosed() {
        return GB;
    }

    public boolean gbHasMessage() {
        return GBMessage;
    }

    public boolean isGBOpen() {
        return GBOpen;
    }

    public String getGBText() {
        StringBuilder result = new StringBuilder();

        for (String aGBText : GBText) {
            result.append(aGBText);
        }

        return result.toString();
    }

    public String getError() {
        return error;
    }

    @Override
    protected List<ClosingModel> doInBackground() {

        closingModels = new ArrayList<>();

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

            parseClosings();
        } catch (IOException e) {
            //Connectivity issues
            error = bundle.getString("WJRTConnectionError");
            cancel(true);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            /* This shows in place of the table (as plain text)
            if no schools or institutions are closed. */
            if (schools != null && !schools.text().contains("no closings or delays")) {
                //Webpage layout was not recognized.
                error = bundle.getString("WJRTParseError");
                cancel(true);
            } else {
                parseClosings();
            }
        }

        return closingModels;
    }

    private void parseClosings() {

        LocalDateTime today = LocalDateTime.now();

        //Get the day of the week as a string.
        weekdaytoday = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);

        weekdaytomorrow = today.plusDays(1).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);

        //Sanity check - make sure Grand Blanc isn't already closed before predicting
        GB = checkClosed(
                arrayBundle.getString("checks_gb").split(","),
                bundle.getString("GB"),
                true,
                -1);

        if (GB) {
            GBText.add(bundle.getString("SnowDay") + "\n");
        } else {
            if (dayrun == 0) {
                if (today.getHour() >= 7 && today.getHour() < 16) {
                    //Time is between 7AM and 4PM. School is already in session.
                    GBText.add(bundle.getString("SchoolOpen") + "\n");
                    GBOpen = true;
                } else if (today.getHour() >= 16) {
                    //Time is after 4PM. School is already out.
                    GBText.add(bundle.getString("Dismissed") + "\n");
                    GBOpen = true;
                }
            }
        }

        //Check school closings
        String[] tier1schools = arrayBundle.getString("name_t1").split(",");
        String[] tier2schools = arrayBundle.getString("name_t2").split(",");
        String[] tier3schools = arrayBundle.getString("name_t3").split(",");
        String[] tier4schools = arrayBundle.getString("name_t4").split(",");

        //Tier 4
        checkClosed(
                arrayBundle.getString("checks_atherton").split(","),
                tier4schools[0],
                false,
                4);
        checkClosed(
                arrayBundle.getString("checks_bendle").split(","),
                tier4schools[1],
                false,
                4);
        checkClosed(
                arrayBundle.getString("checks_bentley").split(","),
                tier4schools[2],
                false,
                4);

        // Special case - Carman-Ainsworth has an additional impact on the calculation
        boolean carman = checkClosed(
                arrayBundle.getString("checks_carman").split(","),
                tier4schools[3],
                false,
                4);

        checkClosed(
                arrayBundle.getString("checks_flint").split(","),
                tier4schools[4],
                false,
                4);
        checkClosed(
                arrayBundle.getString("checks_goodrich").split(","),
                tier4schools[5],
                false,
                4);

        //Tier 3
        checkClosed(
                arrayBundle.getString("checks_beecher").split(","),
                tier3schools[0],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_clio").split(","),
                tier3schools[1],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_davison").split(","),
                tier3schools[2],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_fenton").split(","),
                tier3schools[3],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_flushing").split(","),
                tier3schools[4],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_genesee").split(","),
                tier3schools[5],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_kearsley").split(","),
                tier3schools[6],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_lkfenton").split(","),
                tier3schools[7],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_linden").split(","),
                tier3schools[8],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_montrose").split(","),
                tier3schools[9],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_morris").split(","),
                tier3schools[10],
                false,
                3);
        checkClosed(
                arrayBundle.getString("checks_szcreek").split(","),
                tier3schools[11],
                false,
                3);

        //Tier 2
        checkClosed(
                arrayBundle.getString("checks_durand").split(","),
                tier2schools[0],
                false,
                2);
        checkClosed(
                arrayBundle.getString("checks_holly").split(","),
                tier2schools[1],
                false,
                2);
        checkClosed(
                arrayBundle.getString("checks_lapeer").split(","),
                tier2schools[2],
                false,
                2);
        checkClosed(
                arrayBundle.getString("checks_owosso").split(","),
                tier2schools[3],
                false,
                2);

        //Tier 1
        checkClosed(
                arrayBundle.getString("checks_gbacademy").split(","),
                tier1schools[0],
                false,
                1);
        checkClosed(
                arrayBundle.getString("checks_gisd").split(","),
                tier1schools[1],
                false,
                1);
        checkClosed(
                arrayBundle.getString("checks_holyfamily").split(","),
                tier1schools[2],
                false,
                1);
        checkClosed(
                arrayBundle.getString("checks_wpacademy").split(","),
                tier1schools[3],
                false,
                1);

        //Set the school percent
        if (tier1 > 2) {
            //3+ academies are closed. 20% schoolpercent.
            schoolPercent = 20;
        }
        if (tier2 > 2) {
            //3+ schools in nearby counties are closed. 40% schoolpercent.
            schoolPercent = 40;
        }
        if (tier3 > 2) {
            //3+ schools in Genesee County are closed. 60% schoolpercent.
            schoolPercent = 60;
        }
        if (tier4 > 2) {
            //3+ schools near GB are closed. 80% schoolpercent.
            schoolPercent = 80;
            if (carman) {
                //Carman is closed along with 2+ close schools. 90% schoolpercent.
                schoolPercent = 90;
            }
        }
    }

    /**
     * Checks if a specified school or organization is closed or has a message.
     *
     * @param checks       The array of potential false positives to be checked
     * @param schoolName   The name of the school as present in the array populated by {@link ClosingsScraper}
     * @param isGrandBlanc Whether the school is Grand Blanc or another school
     * @param tier         The tier the school belongs to (-1 for Grand Blanc)
     * @return The status of the school
     */
    private boolean checkClosed(
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
                        GBMessage = true;
                        GBText.add(schoolName + ": " + orgStatuses.get(i) + "\n");
                    } else {
                        closingModels.add(new ClosingModel.ClosingBuilder(schoolName)
                                .setOrgStatus(orgStatuses.get(i))
                                .build()
                        );
                    }

                    if (isClosed(orgStatuses, i, dayrun)) {
                        if (isGrandBlanc) {
                            switch (tier) {
                                case 1:
                                    tier1++;
                                    break;
                                case 2:
                                    tier2++;
                                    break;
                                case 3:
                                    tier3++;
                                    break;
                                case 4:
                                    tier4++;
                                    break;
                                default:
                            }
                            closingModels.get(closingModels.size() - 1).setClosed(true);
                        }
                        result = true;
                    }
                }
            }

            if (schoolFound) {
                break;
            }
        }

        if (isGrandBlanc && !schoolFound) {
            GBText.add((schoolName) + ": " + bundle.getString("Open") + "\n");
        } else if (!schoolFound) {
            closingModels.add(
                    new ClosingModel.ClosingBuilder(schoolName)
                            .setOrgStatus(bundle.getString("Open"))
                            .build());
        }

        return result;
    }

    private boolean isClosed(List<String> orgStatuses, int i, int dayrun) {
        return (orgStatuses.get(i).contains("Closed " + weekdaytoday) && dayrun == 0
                || orgStatuses.get(i).contains("Closed Today") && dayrun == 0
                || orgStatuses.get(i).contains("Closed " + weekdaytomorrow) && dayrun == 1
                || orgStatuses.get(i).contains("Closed Tomorrow") && dayrun == 1);
    }

    private boolean isFalsePositive(String[] checks, String org) {

        for (String check : checks) {
            if (org.contains(check)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void done() {
        delegate.processFinish(closingModels);
    }

    public interface AsyncResponse {
        void processFinish(List<ClosingModel> closingModels);
    }
}
