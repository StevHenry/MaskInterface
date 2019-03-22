package fr.stevenhry.maskinterface.tabs;

import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
        Label typeLabel = new Label("Type your expression:");
        Label resultLabel = new Label("Result:");
        Label variableLabel = new Label("Variable:");
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
                        errorByTab("You must define an expression before trying to differentiate!",
                                new NullPointerException("Expression is not defined"));
                    } else {
                        if (variable.getText().matches("[a-zA-Z]")) {
                            DerivativeTree tree = new DerivativeTree(calculation.getText(), variable.getText().charAt(0));
                            String derivedResult = tree.merge();
                            Platform.runLater(() -> {
                                result.setText(derivedResult);
                            });
                        } else {
                            resetResultField();
                            errorByTab("Please enter a valid variable ! (Single character only)",
                                    new IllegalArgumentException("Bad variable entered"));
                        }
                    }
                } catch (Exception exception) {
                    resetResultField();
                    errorByTab("Unable to differentiate expression !", exception);
                }
                timeCalculator.stop();
            });

            //Action
            refurbishThenComplete(calculationThread, "Differentiating...");
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
