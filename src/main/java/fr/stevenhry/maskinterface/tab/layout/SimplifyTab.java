package fr.stevenhry.maskinterface.tab.layout;

import fr.stevenhry.maskinterface.tab.GridMaskTab;
import fr.stevenhry.maskinterface.util.JSONMessage;
import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.akami.mask.utils.ReducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class SimplifyTab extends GridMaskTab {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimplifyTab.class);

    public SimplifyTab(String tabName) {
        super(tabName, "styleFiles/gridTabStyle.css");
    }

    @Override
    public void loadPane() {
        //Fields initialization
        Label typeLabel = new Label(JSONMessage.getMessage("tabs.simplifier.function"));
        TextField calculation = new TextField();

        //Calculation
        actionButton.setOnAction((actionEvent) -> {
            LOGGER.debug("Entered text for calculation: \"" + calculation.getText() + "\"");

            actionButton.setDisable(true);
            timeCalculator = new TimeCalculator();

            //Calculation Thread initialization
            Thread calculationThread = new Thread(() -> {
                timeCalculator.start();
                try {
                    if (calculation.getText().matches("[\\s]+|")) {
                        resetResultField();
                        errorByTab(JSONMessage.getMessage("tabs.simplifier.error.undefinedExpression"),
                                new NullPointerException("Expression is not defined"));
                    } else {
                        String calculatedReduction = ReducerFactory.reduce(calculation.getText());
                        Platform.runLater(() -> {
                            result.setText(calculatedReduction);
                        });
                    }
                } catch (Exception exception) {
                    resetResultField();
                    errorByTab(JSONMessage.getMessage("tabs.simplifier.error.calculation"), exception);
                }
                timeCalculator.stop();
            });

            //Action
            refurbishThenComplete(calculationThread, JSONMessage.getMessage("tabs.simplifier.performing"));
        });

        //Configuring tab's layout
        this.addRow(0, typeLabel, calculation, actionButton);
        this.addRow(1, resultLabel, result, timeLabel);
    }

    @Override
    protected void errorByTab(String customMessage, Exception exception) {
        showErrorLabel(customMessage, exception, 1, 2);
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
