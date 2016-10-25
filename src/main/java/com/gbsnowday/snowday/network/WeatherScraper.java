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
import java.time.ZoneId;
import java.util.Date;
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

public class WeatherScraper extends SwingWorker<WeatherModel, Void> {
    private ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

    private WeatherModel weatherModel;
    private int dayrun;

    private SimpleDateFormat sdfInput = new SimpleDateFormat
            ("yyyy-MM-dd'T'HH:mm", Locale.US);
    private SimpleDateFormat sdfOutput = new SimpleDateFormat
            ("MMMM dd 'at' h:mm a", Locale.US);

    private AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(WeatherModel weatherModel);
    }

    public WeatherScraper(int i, AsyncResponse delegate) {
        dayrun = i;
        this.delegate = delegate;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    protected WeatherModel doInBackground() throws Exception {
        weatherModel = new WeatherModel();

        try {
            Document weather = Jsoup.connect(
                    bundle.getString("WeatherURL"))
                    .timeout(10000)
                    .get();

            Elements title = weather.select("title");
            Elements summary = weather.select("summary");
            Elements expiretime = weather.select("cap|expires");
            Elements link = weather.select("link");

            if (title != null) {
                for (int i = 0; i < title.size(); i++) {
                    int stringend = title.get(i).text().indexOf("issued");
                    if (stringend != -1) {
                        weatherModel.warningTitles.add(title.get(i).text().substring(0, stringend));
                    } else {
                        weatherModel.warningTitles.add(title.get(i).text());
                    }
                }

                if (!weatherModel.warningTitles.get(1).contains("no active")) {
                    //Weather warnings are present.
                    weatherModel.weatherWarningsPresent = true;
                }
            }
            if (expiretime != null) {
                Date date;
                String readableDate;
                for (int i = 0; i < expiretime.size(); i++) {
                    weatherModel.warningExpireTimes.add(expiretime.get(i).text());
                    date = sdfInput.parse(weatherModel.warningExpireTimes.get(i));
                    readableDate = sdfOutput.format(date);
                    weatherModel.warningReadableTimes.add("Expires " + readableDate);
                }
            }

            if (summary != null) {
                for (int i = 0; i < summary.size(); i++)
                    weatherModel.warningSummaries.add(summary.get(i).text() + "...");
            }

            if (link != null) {
                for (int i = 0; i < link.size(); i++) {
                    weatherModel.warningLinks.add(link.get(i).attr("href"));
                }
            }
        }catch (IOException e) {
            //Connectivity issues
            weatherModel.error = bundle.getString("WeatherConnectionError");
            cancel(true);
        }catch (NullPointerException | IndexOutOfBoundsException | ParseException e) {
            //RSS layout was not recognized.
            weatherModel.error = bundle.getString("WeatherParseError");
            cancel(true);
        }finally{
            parseWeather();
        }
        return weatherModel;
    }

    @Override
    protected void done() {
        delegate.processFinish(weatherModel);
    }

    private void parseWeather() {
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
    }

    /**Check for the presence of weather warnings.
     * Only the highest weather percent is stored (not cumulative).
     * Calculation is affected based on when warning expires.
     * @param warn The string identifying the warning to search for
     * @param weight The value weatherpercent is set to if the warning is found
     */
    private void checkWeatherWarning(String warn, int weight) {
        LocalDate warningDate = null;
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        for (int i = 0; i < weatherModel.warningTitles.size(); i++) {
            if (weatherModel.warningTitles.get(i).contains(warn)) {
                try {
                    Date date = sdfInput.parse(
                            weatherModel.warningExpireTimes.get(i - 1));
                    warningDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                } catch (ParseException e) {
                    //RSS layout was not recognized.
                    weatherModel.error = bundle.getString("WeatherParseError");

                    cancel(true);
                }

                if (warningDate != null) {
                    if ((warningDate.isEqual(today) || warningDate.isAfter(today))
                            && (dayrun == 0)) {
                        weatherModel.weatherpercent = weight;
                    } else if ((warningDate.isEqual(tomorrow) || warningDate.isAfter(tomorrow))
                            && (dayrun == 1)) {
                        weatherModel.weatherpercent = weight;
                    }
                }
            }
        }
    }
}
