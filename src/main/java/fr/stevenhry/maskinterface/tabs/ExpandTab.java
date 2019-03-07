package fr.stevenhry.maskinterface.tabs;

import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.akami.mask.utils.ReducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class ExpandTab extends GridMaskTab {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExpandTab.class);

    public ExpandTab(String tabName) {
        super(tabName, "gridTabStyle.css");
    }

    @Override
    public void loadPane() {
        Label typeLabel = new Label("Type your expression:");
        Label resultLabel = new Label("Result:");
        TextField calculation = new TextField();

        actionButton.setText("Reduce/Expand");
        actionButton.setOnAction((actionEvent) -> {
            LOGGER.debug("Entered text for calculation: \"" + calculation.getText() + "\"");

            actionButton.setDisable(true);

            Thread calculationThread = new Thread(() -> {
                timeCalculator = new TimeCalculator();
                timeCalculator.start();
                try {
                    if (calculation.getText() == null || calculation.getText().replace(" ", "").equals("")) {
                        Platform.runLater(() -> {
                            result.setText("...");
                        });
                        showErrorLabel("You must define an expression before trying to develop!",
                                new NullPointerException("Expression is not defined"), 1, 2);
                    } else {
                        String calculatedReduction = ReducerFactory.reduce(calculation.getText());
                        Platform.runLater(() -> {
                            result.setText(calculatedReduction);
                        });
                    }
                } catch (Exception exception) {
                    Platform.runLater(() -> {
                        result.setText("...");
                    });
                    showErrorLabel("Unable to reduce expression !", exception, 1, 2);
                }
                timeCalculator.stop();
            });

            refurbishThenComplete(calculationThread, "Expanding/Reducing...");
        });

        this.addRow(0, typeLabel, calculation, actionButton);
        this.addRow(1, resultLabel, result, timeLabel);
    }

    @Override
    public void refurbish() {
        Platform.runLater(() -> {
            actionButton.setDisable(false);
            this.getChildren().remove(errorLabel);
            result.setText("...");
            timeLabel.setText(new SimpleDateFormat("mm:ss:SSS").format(0l));
        });
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
