package com.gbsnowday.snowday.network;

import com.gbsnowday.snowday.model.WeatherModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import javax.swing.SwingWorker;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

public class WeatherScraper extends SwingWorker<List<WeatherModel>, Void> {
    private ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

    /**
     * This is an array of {@link WeatherModel} objects.
     * A WeatherModel is a custom object containing the information needed to display a single warning.
     */
    private List<WeatherModel> weatherModels;
    private int dayrun;

    private boolean weatherWarningPresent;
    private int weatherPercent;

    private String error;

    // The readable format of warning expiration times as seen by the user
    private DateTimeFormatter outputFormatter = DateTimeFormatter
            .ofPattern("MMMM dd 'at' h:mm a", Locale.US);

    private AsyncResponse delegate = null;

    public int getWeatherPercent() {
        return weatherPercent;
    }

    public boolean isWeatherWarningPresent() {
        return weatherWarningPresent;
    }

    public String getError() {
        return error;
    }

    // This interface serves as a delegate that passes the array of WeatherModel objects
    // to the UI thread after the worker thread finishes running.
    public interface AsyncResponse {
        void processFinish(List<WeatherModel> weatherModels);
    }

    /**
     * Reads and parses weather warnings from the National Weather Service.
     * @param dayrun Whether the calculation is being run for "today" or "tomorrow" (inputted by user)
     * @param delegate The interface implementation used to pass the array of weather warnings
     */
    public WeatherScraper(int dayrun, AsyncResponse delegate) {
        this.dayrun = dayrun;
        this.delegate = delegate;
    }

    /**
     * Retrieve and parse the RSS feed asynchronously.
     * @return the array of {@link WeatherModel} objects
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    protected List<WeatherModel> doInBackground() {
        weatherModels = new ArrayList<>();

        try {
            Document weather = Jsoup.connect(
                    bundle.getString("WeatherURL"))
                    .timeout(10000)
                    .get();

            // Elements present in the RSS feed
            Elements title = weather.select("title");
            Elements summary = weather.select("summary");
            Elements expiretime = weather.select("cap|expires");
            Elements link = weather.select("link");

            if (title != null) {
                // The warning titles in the RSS feed contain the text "issued by NWS at ...".
                // This removes that portion of the warning title if present for a cleaner
                // appearance in the displayed list. The expiration time is shown separately.
                // Example: "Winter Storm Warning" vs. "Winter Storm Warning issued by NWS at ..."
                for (int i = 0; i < title.size(); i++) {
                    int stringend = title.get(i).text().indexOf("issued");
                    if (stringend != -1) {
                        weatherModels.add(new WeatherModel(
                                title.get(i).text().substring(0, stringend)));
                    } else {
                        weatherModels.add(new WeatherModel(title.get(i).text()));
                    }
                }

                if (!title.get(1).text().contains("no active")) {
                    //Weather warnings are present.
                    weatherWarningPresent = true;
                }
            }

            // The first Title element in the feed is the feed title, not a warning title.
            // The text of this element is displayed as a header above any warnings present.
            // The SnowDayController class compensates for this when constructing the list of warnings.
            // Only subsequent array elements contain warning information, hence the use of
            // "i + 1" in the following statements.

            if (expiretime != null) {
                ZonedDateTime time;
                String readableTime;
                for (int i = 0; i < expiretime.size(); i++) {
                    String expireTime = expiretime.get(i).text();
                    time = ZonedDateTime.parse(expireTime);
                    readableTime = outputFormatter.format(time);

                    weatherModels.get(i + 1)
                            .setWarningExpireTime(expireTime);
                    weatherModels.get(i + 1)
                            .setWarningReadableTime("Expires " + readableTime);
                }
            }

            if (summary != null) {
                for (int i = 0; i < summary.size(); i++)
                    weatherModels.get(i + 1)
                            .setWarningSummary(summary.get(i).text() + "...");
            }

            if (link != null) {
                link.remove(0); // Intentionally remove the root-level link tag
                for (int i = 0; i < link.size(); i++) {
                    weatherModels.get(i + 1).
                            setWarningLink(link.get(i).attr("href"));
                }
            }

            // These method calls check for the presence of specific warnings.
            // If one is present, weatherPercent is set to the corresponding value.
            // More severe warnings result in a higher weatherPercent value.

            //Significant Weather Advisory
            checkWeatherWarning(bundle.getString("SigWeather"), 15);

            //Winter Weather Advisory
            checkWeatherWarning(bundle.getString("WinterAdvisory"), 30);

            //Lake Effect Snow Advisory
            checkWeatherWarning(bundle.getString("LakeSnowAdvisory"), 40);

            //Freezing Rain
            checkWeatherWarning(bundle.getString("Rain"), 40);

            //Freezing Drizzle
            checkWeatherWarning(bundle.getString("Drizzle"), 40);

            //Freezing Fog
            checkWeatherWarning(bundle.getString("Fog"), 40);

            //Wind Chill Advisory
            checkWeatherWarning(bundle.getString("WindChillAdvisory"), 40);

            //Ice Storm Warning
            checkWeatherWarning(bundle.getString("IceStorm"), 70);

            //Wind Chill Watch
            checkWeatherWarning(bundle.getString("WindChillWatch"), 70);

            //Wind Chill Warning
            checkWeatherWarning(bundle.getString("WindChillWarn"), 70);

            //Winter Storm Watch
            checkWeatherWarning(bundle.getString("WinterWatch"), 80);

            //Winter Storm Warning
            checkWeatherWarning(bundle.getString("WinterWarn"), 80);

            //Lake Effect Snow Watch
            checkWeatherWarning(bundle.getString("LakeSnowWatch"), 80);

            //Lake Effect Snow Warning
            checkWeatherWarning(bundle.getString("LakeSnowWarn"), 80);

            //Blizzard Watch
            checkWeatherWarning(bundle.getString("BlizzardWatch"), 90);

            //Blizzard Warning
            checkWeatherWarning(bundle.getString("BlizzardWarn"), 90);

        }catch (IOException e) {
            //Connectivity issues
            error = bundle.getString("WeatherConnectionError");
            cancel(true);
        }catch (NullPointerException | IndexOutOfBoundsException | ParseException e) {
            //RSS layout was not recognized.
            error = bundle.getString("WeatherParseError");
            cancel(true);
        }
        return weatherModels;
    }

    /**Check for the presence of weather warnings.
     * Only the highest weather percent is stored (not cumulative).
     * Calculation is affected based on when the warning expires and the day the user selected.
     * @param warn The string identifying the warning to search for
     * @param weight The value weatherPercent is set to if the warning is found
     * @throws ParseException if the RSS.layout is not recognized
     */
    private void checkWeatherWarning(String warn, int weight) throws ParseException {
        ZonedDateTime warningDate;
        ZonedDateTime today = ZonedDateTime.now();
        ZonedDateTime tomorrow = today.plusDays(1).withHour(0); // Midnight tomorrow
        for (int i = 0; i < weatherModels.size(); i++) {
            if (weatherModels.get(i).getWarningTitle().contains(warn)) {
                warningDate = ZonedDateTime.parse(
                        weatherModels.get(i).getWarningExpireTime());
                if (dayrun == 0) {
                    // If the weather warning is present, it has an effect today.
                    weatherPercent = weight;
                } else if ((warningDate.isEqual(tomorrow) || warningDate.isAfter(tomorrow))) {
                    // If the weather warning expires at or after midnight tomorrow, it has an effect.
                    weatherPercent = weight;
                }
            }
        }
    }

    @Override
    protected void done() {
        delegate.processFinish(weatherModels);
    }
}
