package Main.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.convert.out.html.HTMLExporterXslt;
import org.docx4j.convert.out.HTMLSettings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class ReportController {
    @FXML
    private WebView document;
    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {
        loadingIndicator.setVisible(true);
        WebEngine webEngine = document.getEngine();
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File("PharmacyApp/src/main/resources/Pharmacy_Management_System_Report.docx"));

            HTMLSettings htmlSettings = new HTMLSettings();
            htmlSettings.setWmlPackage(wordMLPackage);

            ByteArrayOutputStream base = new ByteArrayOutputStream();
            HTMLExporterXslt exporter = new HTMLExporterXslt();
            exporter.export(htmlSettings, base);

            String htmlContent = base.toString(StandardCharsets.UTF_8);
            webEngine.loadContent(htmlContent);
            Platform.runLater(()-> loadingIndicator.setVisible(false));
        } catch (Exception e) {
            webEngine.loadContent("Couldn't load manual");
            System.out.println(e.getMessage());
            Platform.runLater(()-> loadingIndicator.setVisible(false));
        }
    }
}
