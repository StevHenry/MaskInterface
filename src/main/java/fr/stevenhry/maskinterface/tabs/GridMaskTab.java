package fr.stevenhry.maskinterface.tabs;

import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;

public abstract class GridMaskTab extends GridPane implements MaskFunctionTab {

    private final String tabName;
    protected Label errorLabel = new Label();
    protected Text timeLabel = new Text(new SimpleDateFormat("mm:ss:SSS").format(0l));
    protected TextArea result = new TextArea("...");
    protected Button actionButton = new Button();
    protected TimeCalculator timeCalculator = new TimeCalculator();

    public GridMaskTab(String tabName, String cssFileName) {
        this.tabName = tabName;
        this.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource(cssFileName).toExternalForm());

        actionButton.setId("actionButton");
        timeLabel.getStyleClass().add("timeLabel");

        errorLabel.setId("errorLabel");
        errorLabel.setWrapText(true);
        errorLabel.prefHeight(Region.USE_COMPUTED_SIZE);
        errorLabel.prefWidth(Region.USE_COMPUTED_SIZE);

        result.wrapTextProperty().setValue(true);

        loadPane();
    }

    protected abstract Logger getLogger();

    @Override
    public String getTabName() {
        return tabName.toUpperCase();
    }

    protected void showErrorLabel(String customMessage, Exception exception, int column, int row) {
        showErrorLabel(customMessage, exception, column, row, 1, 1);
    }

    protected void showErrorLabel(String customMessage, Exception exception, int column, int row, int columnSpan, int rowSpan) {
        getLogger().error(customMessage + " (Error: " + exception.getMessage() + ")");
        Platform.runLater(() -> {
            errorLabel.setText("Error:\t" + customMessage + (exception.getMessage() != null ?  "\n\t\t(Exception message: " + exception.getMessage() + ")" : ""));
            errorLabel.layoutBoundsProperty();
            if (!this.getChildren().contains(errorLabel)) {
                this.add(errorLabel, column, row, columnSpan, rowSpan);
            }
        });
    }

    protected void refurbishThenComplete(Thread calculationThread, String message){
        refurbish();
        complete(calculationThread, message);
    }

    protected void complete(Thread calculationThread, String message){
        Thread labelUpdater = timeCalculator.startProcess(timeLabel, calculationThread);
        Platform.runLater(() -> {
            result.setText(message);
        });


        new Thread(() -> {
            while (labelUpdater.isAlive()) {
                continue;
            }
            Platform.runLater(() -> {
                actionButton.setDisable(false);
            });
        }).start();
    }
}
