package com.gbsnowday.snowday.model;

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

public class WeatherModel {
    private final String warningTitle;
    private String warningExpireTime;
    private String warningReadableTime;
    private String warningSummary;
    private String warningLink;

    public WeatherModel(String warningTitle) {
        this.warningTitle = warningTitle;
    }

    public String getWarningTitle() {
        return warningTitle;
    }

    public String getWarningExpireTime() {
        return warningExpireTime;
    }

    public void setWarningExpireTime(String warningExpireTime) {
        this.warningExpireTime = warningExpireTime;
    }

    public String getWarningReadableTime() {
        return warningReadableTime;
    }

    public void setWarningReadableTime(String warningReadableTime) {
        this.warningReadableTime = warningReadableTime;
    }

    public String getWarningSummary() {
        return warningSummary;
    }

    public void setWarningSummary(String warningSummary) {
        this.warningSummary = warningSummary;
    }

    public String getWarningLink() {
        return warningLink;
    }

    public void setWarningLink(String warningLink) {
        this.warningLink = warningLink;
    }
}