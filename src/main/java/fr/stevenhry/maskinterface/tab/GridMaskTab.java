package fr.stevenhry.maskinterface.tab;

import fr.stevenhry.maskinterface.util.JSONMessage;
import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.geometry.HPos;
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

    //Label where error is shown:
    protected Label errorLabel = new Label();
    //Label where time is shown:
    protected Text timeLabel = new Text(new SimpleDateFormat("mm:ss:SSS").format(0l));
    //Area where result is shown:
    protected TextArea result = new TextArea("...");
    //Label to show where result is displayed
    protected Label resultLabel = new Label(JSONMessage.getMessage("tabs.result"));
    //Button to launch the calculation:
    protected Button actionButton = new Button();
    //TimeCalculator used to calculation calculation:
    protected TimeCalculator timeCalculator = new TimeCalculator();

    public GridMaskTab(String tabName, String cssFileName) {
        this.tabName = tabName;
        this.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource(cssFileName).toExternalForm());

        actionButton.setId("actionButton");
        actionButton.setText(tabName);
        timeLabel.getStyleClass().add("timeLabel");
        GridMaskTab.setHalignment(actionButton, HPos.CENTER);
        GridMaskTab.setHalignment(timeLabel, HPos.CENTER);

        errorLabel.setId("errorLabel");
        errorLabel.setWrapText(true);
        errorLabel.prefHeight(Region.USE_COMPUTED_SIZE);
        errorLabel.prefWidth(Region.USE_COMPUTED_SIZE);

        result.wrapTextProperty().setValue(true);

        loadPane();
    }

    /**
     * @return the {@link org.slf4j.Logger Logger} that is used in the function tab to log errors.
     */
    protected abstract Logger getLogger();

    /**
     * Allows to show the error label with the {@link #showErrorLabel(String, Exception, int, int)} method with the provided sizes arguments.
     * @param customMessage message to show
     * @param exception thrown exception if exists
     */
    protected abstract void errorByTab(String customMessage, Exception exception);


    /**
     * Calls {@link #showErrorLabel(String, Exception, int, int, int, int)} with default column and row spans (=1).
     */
    protected final void showErrorLabel(String customMessage, Exception exception, int column, int row) {
        showErrorLabel(customMessage, exception, column, row, 1, 1);
    }

    /**
     * Displays the error label on the tab
     * @param customMessage message to show
     * @param exception thrown exception if exists
     * @param column column anchor position in the grid
     * @param row row anchor position in the grid
     * @param columnSpan columns label size
     * @param rowSpan rows label size
     */
    protected final void showErrorLabel(String customMessage, Exception exception, int column, int row, int columnSpan, int rowSpan) {
        getLogger().error(customMessage + " (Error: " + exception.getMessage() + ")");
        Platform.runLater(() -> {
            errorLabel.setText("Error:\t" + customMessage + (exception != null &&
                    exception.getMessage() != null ? "\n\t\t(Exception message: " + exception.getMessage() + ")" : ""));
            errorLabel.layoutBoundsProperty();

            if (!this.getChildren().contains(errorLabel)) {
                this.add(errorLabel, column, row, columnSpan, rowSpan);
            }
        });
    }

    /**
     * @return the tab name
     */
    @Override
    public String getTabName() {
        return tabName.toUpperCase();
    }

    /**
     * Resets the result field's text
     */
    protected final void resetResultField() {
        Platform.runLater(() -> {
            result.setText("...");
        });
    }

    /**
     * Calls {@link #refurbish()} then calls {@link #complete(Thread, String)} with provided arguments
     */
    protected final void refurbishThenComplete(Thread calculationThread, String message) {
        refurbish();
        complete(calculationThread, message);
    }

    /**
     * Processes to the calculation and then shows on page
     * @param calculationThread {@link Thread} inside of which the calculation should be done
     * @param message Message in result box when calculation is progress
     */
    protected final void complete(Thread calculationThread, String message) {
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
        }, "").start();
    }
}
