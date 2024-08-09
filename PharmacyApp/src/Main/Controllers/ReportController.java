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

/**
 * This JavaFX controller manages the user interface for viewing the pharmacy management system report.
 * It loads a DOCX file and displays it as HTML in a WebView.
 */
public class ReportController {
    @FXML
    private WebView document;
    @FXML
    private ProgressIndicator loadingIndicator;

    /**
     * Initializes the controller, sets up the WebView, and loads the DOCX report as HTML.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the document is being loaded.
     * DOCX to HTML Conversion:
     *   - Loads the DOCX file using docx4j.
     *   - Converts the DOCX content to HTML using HTMLExporterXslt.
     *   - Displays the HTML content in the WebView.
     * Error Handling:
     *   - If an error occurs, displays an error message in the WebView and prints the error message to the console.
     */
    @SuppressWarnings("GrazieInspection")
    @FXML
    public void initialize() {
        loadingIndicator.setVisible(true);
        WebEngine webEngine = document.getEngine();
        try {
            // Load the DOCX file
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File("PharmacyApp/src/main/resources/Pharmacy_Management_System_Report.docx"));

            // Configure HTML settings for the conversion
            HTMLSettings htmlSettings = new HTMLSettings();
            htmlSettings.setWmlPackage(wordMLPackage);

            // Convert DOCX to HTML
            ByteArrayOutputStream base = new ByteArrayOutputStream();
            HTMLExporterXslt exporter = new HTMLExporterXslt();
            exporter.export(htmlSettings, base);

            // Load the HTML content into the WebView
            String htmlContent = base.toString(StandardCharsets.UTF_8);
            webEngine.loadContent(htmlContent);

            // Hide the loading indicator after loading the content
            Platform.runLater(() -> loadingIndicator.setVisible(false));
        } catch (Exception e) {
            // Display an error message if the document fails to load
            webEngine.loadContent("Couldn't load manual");
            System.out.println(e.getMessage());
            Platform.runLater(() -> loadingIndicator.setVisible(false));
        }
    }
}
