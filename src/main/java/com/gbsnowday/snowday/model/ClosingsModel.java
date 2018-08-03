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

public class ClosingsModel {
    public ArrayList<String> displayedOrgNames = new ArrayList<>();
    public ArrayList<String> displayedOrgStatuses = new ArrayList<>();

    public String GBText = "";

    //Levels of school closings (near vs. far)
    public int tier1 = 0;
    public int tier2 = 0;
    public int tier3 = 0;
    public int tier4 = 0;

    public int schoolPercent;

    //Every school this program searches for: true = closed, false = open (default)
    public boolean GBAcademy;
    public boolean GISD;
    public boolean HolyFamily;
    public boolean WPAcademy;

    public boolean Durand; //Check for "Durand Senior Center"
    public boolean Holly;  //Check for "Holly Academy"
    public boolean Lapeer; //Check for "Lapeer County CMH", "Lapeer Vocational Tech.", "Lapeer Team Work",
    // "Lapeer Senior Center", "Lapeer Co. Education Technology Center", "Lapeer Co. Intermed. Special Ed",
    // "Lapeer Growth and Opportunity, Inc.", "Lapeer District Library", "Lapeer County Offices", "NEMSCA-Lapeer Head Start",
    // "Greater Lapeer Transportation Authority", "Foster Grandparents-Lapeer, Genesee, Shiawassee", "Davenport University-Lapeer",
    // "MSU Extension Service-Lapeer Co.", "Community Connections-Lapeer", and "Chatfield School-Lapeer"
    public boolean Owosso; //Check for "Owosso Christian School", "Owosso Senior Center",
    // "Owosso Seventh-day Adventist School", and "Social Security Administration-Owosso"

    public boolean Beecher;
    public boolean Clio; //Check for "Clio Area Christian School", Clio Area Senior Center",
    // "Clio City Hall", and "Cornerstone Clio"
    public boolean Davison; //Check for "Davison Senior Center", "Faith Baptist School-Davison", "Montessori Academy-Davison",
    // and "Ross Medical Education-Davison"
    public boolean Fenton; //Check for "Lake Fenton", "Fenton City Hall", "Fenton Academy of Cosmetology",
    // and "Fenton Montessori Academy"
    public boolean Flushing; //Check for "Flushing Senior Citizens Center" and "St. Robert-Flushing"
    public boolean Genesee; //Check for "Genesee I.S.D.", "Genesee Health System Day Programs", "Genesee Health System",
    // "Genesee Health Plan", "Genesee Academy", "Genesee Area Skill Center", "Genesee Christian School",
    // "Genesee County Free Medical Clinic", "Genesee District Library", "Genesee County Mobile Meal Program",
    // "Genesee STEM Academy", "Genesee Co Circuit Court", "Genesee County Government", "Genesee County Literacy Coalition",
    // "Flint Genesee Job Corps", "Leadership Genesee", "Freedom Work Genesee Co.", "Youth Leadership Genesee",
    // "67th District Court-Genesee Co.", "MSU Extension Service-Genesee Co.",
    // "Genesee Christian-Burton", and "Foster Grandparents-Lapeer, Genesee, Shiawassee"
    public boolean Kearsley;
    public boolean LKFenton;
    public boolean Linden; //Check for "Linden Charter Academy"
    public boolean Montrose; //Check for "Montrose Senior Center"
    public boolean Morris;  //Check for "Mt Morris Twp Administration" and "St. Mary Church Religious Ed-Mt. Morris"
    public boolean SzCreek; //Check for "Swartz Creek Area Senior Center" and "Swartz Creek Montessori"

    public boolean Atherton;
    public boolean Bendle;
    public boolean Bentley;
    public boolean Carman; //Check for "Carman-Ainsworth Senior Center"
    public boolean Flint; //Thankfully this is listed as "Flint Community Schools" -
    // otherwise there would be a lot of exceptions to check for.
    public boolean Goodrich;

    public boolean GB; //Check for "Grand Blanc Senior Center", "Grand Blanc Academy", "Grand Blanc Road Montessori",
    // "Grand Blanc Gymnastics Co.", and "Freedom Work Grand Blanc"

    public boolean GBOpen; //True if GB is already open (GB = false and time is during or after school hours)

    public boolean GBMessage; //Grand Blanc has a message (e.g. "Early Dismissal") but isn't actually closed.

    public String error;
}
