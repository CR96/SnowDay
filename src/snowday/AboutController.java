package snowday;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {

    public Button btnTwitter;
    public Button btnEmail;
    public Button btnWeb;
    public Button btnGit;

    public Button btnClose;

    public void setCursorHand() {
        btnClose.getScene().setCursor(Cursor.HAND);
    }

    public void setCursorNormal() {
        btnClose.getScene().setCursor(Cursor.DEFAULT);
    }

    public void twitter() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://twitter.com/gbsnowday"));
    }

    public void email() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("mailto:gbsnowday@gmail.com"));
    }

    public void web() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://gbsnowday.weebly.com"));
    }

    public void git() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/CR96/SnowDay"));
    }

    public void close() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}