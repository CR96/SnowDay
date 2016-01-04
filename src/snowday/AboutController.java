package snowday;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AboutController {
    
    /*Copyright 2014-2015 Corey Rowe
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
         http:www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.*/
    
    @FXML
    ResourceBundle bundle = ResourceBundle
            .getBundle("bundles.LangBundle", new Locale("en", "EN"));

    public Button btnTwitter;
    public Button btnEmail;
    public Button btnWeb;
    public Button btnGit;

    public Button btnInfo;

    public void setCursorHand() {
        btnTwitter.getScene().setCursor(Cursor.HAND);
    }

    public void setCursorNormal() {
        btnTwitter.getScene().setCursor(Cursor.DEFAULT);
    }

    public void twitter() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(bundle.getString("twitter")));
    }

    public void email() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(bundle.getString("email")));
    }

    public void web() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(bundle.getString("website")));
    }

    public void git() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(bundle.getString("git")));
    }

    public void showLicenseDialog() {
        LicenseDialog.display();
    }
}