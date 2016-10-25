package com.gbsnowday.snowday.model;

import java.util.ArrayList;

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
    public ArrayList<String> warningTitles = new ArrayList<>();
    public ArrayList<String> warningExpireTimes = new ArrayList<>();
    public ArrayList<String> warningReadableTimes = new ArrayList<>();
    public ArrayList<String> warningSummaries = new ArrayList<>();
    public ArrayList<String> warningLinks = new ArrayList<>();

    public int weatherpercent;

    public boolean weatherWarningsPresent;

    public String error;
}