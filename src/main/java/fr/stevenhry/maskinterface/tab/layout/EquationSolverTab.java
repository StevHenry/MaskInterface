package fr.stevenhry.maskinterface.tab.layout;

import fr.stevenhry.maskinterface.tab.GridMaskTab;
import fr.stevenhry.maskinterface.util.JSONMessage;
import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import net.akami.mask.structure.EquationSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;

public class EquationSolverTab extends GridMaskTab {

    private final static Logger LOGGER = LoggerFactory.getLogger(EquationSolverTab.class);

    public EquationSolverTab(String tabName) {
        super(tabName, "styleFiles/gridTabStyle.css");
    }

    @Override
    public void loadPane() {
        //Fields initialization
        Label typeLabel = new Label(JSONMessage.getMessage("tabs.equations.equation"));
        TextArea expressions = new TextArea();

        //Calculation
        actionButton.setText(JSONMessage.getMessage("tabs.equations.actionButton"));
        actionButton.setOnAction((actionEvent) -> {
            LOGGER.debug("Entered expression(s) to solve: \"" + expressions.getText().replace("\n", " | ") + "\"");

            actionButton.setDisable(true);
            timeCalculator = new TimeCalculator();

            //Calculation Thread initialization
            Thread calculationThread = new Thread(() -> {
                timeCalculator.start();
                try {
                    if (expressions.getText().matches("[\\s]+|")) {
                        resetResultField();
                        errorByTab(JSONMessage.getMessage("tabs.equations.error.undefinedExpression"),
                                new NullPointerException("Expression(s) not defined"));
                    } else {
                        Map<Character, String> solvedValues = EquationSolver.solve(EquationSolver.build(
                                expressions.getText().replace(";", "\n").split("\n")));
                        Platform.runLater(() -> {
                            String calculatedValues = String.join("\n", solvedValues.entrySet().stream()
                                    .map(entry -> entry.getKey() + " = " + entry.getValue()).collect(Collectors.toList()));
                            result.setText(calculatedValues);
                        });
                    }
                } catch (Exception exception) {
                    resetResultField();
                    errorByTab(JSONMessage.getMessage("tabs.equations.error.calculation"), exception);
                }
                timeCalculator.stop();
            });

            //Action
            refurbishThenComplete(calculationThread, JSONMessage.getMessage("tabs.equations.performing"));
        });

        //Configuring tab's layout
        this.addRow(0, typeLabel, expressions, actionButton);
        this.addRow(1, resultLabel, result, timeLabel);

    }

    @Override
    protected void errorByTab(String customMessage, Exception exception) {
        showErrorLabel(customMessage, exception, 1, 2);
    }

    @Override
    public void refurbish() {
        Platform.runLater(() -> {
            this.getChildren().remove(errorLabel);
            actionButton.setDisable(false);
            result.setText("...");
            timeLabel.setText(new SimpleDateFormat("mm:ss:SSS").format(0l));
        });
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

}
