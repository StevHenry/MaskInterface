package fr.stevenhry.maskinterface.tabs;

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
        Label typeLabel = new Label("Type your equations:");
        Label resultLabel = new Label("Result:");

        TextArea expressions = new TextArea();

        actionButton.setText("Solve");
        actionButton.setOnAction((actionEvent) -> {
            LOGGER.debug("Entered expressions to solve: \"" + expressions.getText().replace("\n", " | ") + "\"");

            actionButton.setDisable(true);
            timeCalculator = new TimeCalculator();

            Thread calculationThread = new Thread(() -> {
                timeCalculator.start();
                try {
                    if (expressions.getText() == null || expressions.getText().replace(" ", "").equals("")) {
                        resetResultField();
                        errorByTab("You must define your expression(s) before trying to solve!",
                                new NullPointerException("Expression(s) not defined"));
                    } else {
                        Map<Character, String> solvedValues = EquationSolver.solve(EquationSolver.build(
                                expressions.getText().replace(";", "\n").split("\n")));
                        Platform.runLater(() -> {
                            String calculatedValues = String.join("\n", solvedValues.entrySet().stream()
                                    .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.toList()));
                            result.setText(calculatedValues);
                        });
                    }
                } catch (Exception exception) {
                    resetResultField();
                    errorByTab("Unable to solve expressions !", exception);
                }
                timeCalculator.stop();
            });

            refurbishThenComplete(calculationThread, "Solving...");
        });

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
