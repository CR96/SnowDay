/*
 * Copyright 2014 - 2018 Corey Rowe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gbsnowday.snowday.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class EventModel {

    private final ResourceBundle bundle = ResourceBundle
            .getBundle("bundle.LangBundle", new Locale("en", "EN"));

    private final ArrayList<String> infoList = new ArrayList<>();
    private boolean todayValid = true;
    private boolean tomorrowValid = true;
    private boolean eventPresent;

    //Figure out what tomorrow is
    //Saturday = 6, Sunday = 7
    private final LocalDateTime dt = LocalDateTime.now();
    private final int weekday = dt.getDayOfWeek().getValue();

    /**
     * Make sure the user doesn't try to run the program on the weekend or on specific dates.
     */
    public EventModel() {
        checkDate();

        //Only run checkWeekend() if today or tomorrow is still valid
        if (todayValid || tomorrowValid) {
            checkWeekend();
        }
    }

    /**
     * @return whether today is valid
     **/
    public boolean isTodayValid() {
        return todayValid;
    }

    /**
     * @return whether tomorrow is valid
     **/
    public boolean isTomorrowValid() {
        return tomorrowValid;
    }

    /**
     * @return whether an event is present (affects list entry color)
     **/
    public boolean isEventPresent() {
        return eventPresent;
    }

    /**
     * @return the list to be populated in the main view controller
     **/
    public ArrayList<String> getInfoList() {
        return infoList;
    }


    private void checkDate() {
        //Set the current month, day, and year
        Date date = Date.from(dt.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd yyyy", Locale.US);
        String textdate = currentDate.format(date);

        infoList.add("Current Date: " + textdate);

        /*Check for days school is not in session (such as Winter Break, development days, etc.)
        Uses a mixture of SimpleDateFormat for simple string comparison and JodaTime for more
        complicated arguments*/

        if (dt.getMonthValue() == 6 && dt.getDayOfMonth() > 14) {
            //Summer break (June)
            infoList.add(bundle.getString("Summer"));
            eventPresent = true;
            todayValid = false;
            tomorrowValid = false;
        } else if (dt.getMonthValue() > 6 && dt.getMonthValue() <= 8) {
            //Summer break (July and August)
            infoList.add(bundle.getString("Summer"));
            eventPresent = true;
            todayValid = false;
            tomorrowValid = false;
        } else if (dt.getMonthValue() == 9 && dt.getDayOfMonth() < 3) {
            //Summer break (September)
            infoList.add(bundle.getString("Summer"));
            eventPresent = true;
            todayValid = false;
            tomorrowValid = false;
        } else if (textdate.equals("September 03 2018")) {
            infoList.add(bundle.getString("YearStart"));
            eventPresent = true;
            todayValid = false;
        }
    }

    private void checkWeekend() {
        //Friday is 5
        //Saturday is 6
        //Sunday is 7

        if (weekday == 5) {
            infoList.add(bundle.getString("SaturdayTomorrow"));
            tomorrowValid = false;
            eventPresent = true;
        } else if (weekday == 6) {
            infoList.add(bundle.getString("SaturdayToday"));
            todayValid = false;
            tomorrowValid = false;
            eventPresent = true;
        } else if (weekday == 7) {
            infoList.add(bundle.getString("SundayToday"));
            todayValid = false;
            eventPresent = true;
        }
    }
}