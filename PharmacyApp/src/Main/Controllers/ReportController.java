package Main.Controllers;

import javafx.fxml.FXML;
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
    public void initialize() {
        try {
            WebEngine webEngine = document.getEngine();
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File("PharmacyApp/src/main/resources/Pharmacy_Management_System_Report.docx"));

            HTMLSettings htmlSettings = new HTMLSettings();
            htmlSettings.setWmlPackage(wordMLPackage);

            // Use ByteArrayOutputStream to capture HTML output
            ByteArrayOutputStream base = new ByteArrayOutputStream();
            HTMLExporterXslt exporter = new HTMLExporterXslt();
            exporter.export(htmlSettings, base);

            String htmlContent = base.toString(StandardCharsets.UTF_8);
            webEngine.loadContent(htmlContent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
