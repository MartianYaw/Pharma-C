package Main;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingDialog {
    private final Stage dialog;

    public LoadingDialog(Stage parent, String message) {
        dialog = new Stage();
        dialog.initOwner(parent);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);

        Label messageLabel = new Label(message);
        messageLabel.setFont(new Font("Arial", 14));
        messageLabel.setStyle("-fx-font-weight: bold;");
        messageLabel.setWrapText(true);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        VBox vbox = new VBox(10); // Spacing between label and progress bar
        vbox.getChildren().addAll(messageLabel, progressBar);
        vbox.setAlignment(javafx.geometry.Pos.CENTER); // Center align the content

        BorderPane panel = new BorderPane();
        panel.setStyle("-fx-padding: 20;");
        panel.setCenter(vbox);

        Scene scene = new Scene(panel);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.sizeToScene();
    }

    public void showLoading() {
        Platform.runLater(dialog::show);
    }

    public void hideLoading() {
        Platform.runLater(dialog::hide);
    }
}
