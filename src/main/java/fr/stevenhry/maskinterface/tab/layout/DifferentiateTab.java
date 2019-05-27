package fr.stevenhry.maskinterface.tab.layout;

import fr.stevenhry.maskinterface.tab.GridMaskTab;
import fr.stevenhry.maskinterface.util.JSONMessage;
import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import net.akami.mask.core.Mask;
import net.akami.mask.core.MaskDerivativeCalculator;
import net.akami.mask.core.MaskOperatorHandler;
import net.akami.mask.exception.MaskException;
import net.akami.mask.tree.DerivativeTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class DifferentiateTab extends GridMaskTab {

    private final static Logger LOGGER = LoggerFactory.getLogger(DifferentiateTab.class);

    public DifferentiateTab(String tabName) {
        super(tabName, "styleFiles/differentiateGridTab.css");
    }

    @Override
    public void loadPane() {
        //Fields initialization
        Label typeLabel = new Label(JSONMessage.getMessage("tabs.differentiate.function"));
        Label variableLabel = new Label(JSONMessage.getMessage("tabs.differentiate.variable"));
        TextArea calculation = new TextArea();
        TextField variable = new TextField();

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
                        errorByTab(JSONMessage.getMessage("tabs.differentiate.error.undefinedExpression"),
                                new NullPointerException("Expression is not defined"));
                    } else {
                        if (variable.getText().matches("[a-zA-Z]")) {
                            Mask mask = new Mask(calculation.getText());
                            MaskOperatorHandler handler = new MaskOperatorHandler();
                            handler.begin(mask);
                            handler.compute(MaskDerivativeCalculator.class, mask, variable.getText().charAt(0));
                            Platform.runLater(() -> {
                                result.setText(mask.getExpression());
                            });
                        } else {
                            resetResultField();
                            errorByTab(JSONMessage.getMessage("tabs.differentiate.error.variable"),
                                    new IllegalArgumentException("Bad variable entered"));
                        }
                    }
                } catch (MaskException exception) {
                    resetResultField();
                    errorByTab(JSONMessage.getMessage("tabs.differentiate.error.calculation"), exception);
                }
                timeCalculator.stop();
            });

            //Action
            refurbishThenComplete(calculationThread, JSONMessage.getMessage("tabs.differentiate.performing"));
        });

        //Configuring tab's layout
        this.addRow(0, typeLabel, calculation, actionButton);
        this.addRow(1, variableLabel, variable);
        this.addRow(2, resultLabel, result, timeLabel);
    }

    @Override
    protected void errorByTab(String customMessage, Exception exception) {
        showErrorLabel(customMessage, exception, 1, 3);
    }

    @Override
    public void refurbish() {
        Platform.runLater(() -> {
            actionButton.setDisable(false);
            result.setText("...");
            this.getChildren().remove(errorLabel);
            timeLabel.setText(new SimpleDateFormat("mm:ss:SSS").format(0l));
        });
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
