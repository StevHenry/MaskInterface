package fr.stevenhry.maskinterface.tabs;

import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.akami.mask.exception.MaskException;
import net.akami.mask.math.MaskExpression;
import net.akami.mask.operation.MaskOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ImagesCalculatorTab extends GridMaskTab {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExpandTab.class);

    private final int unknownFieldsLines = 2, fieldsPerLine = 5, spaces = 4;
    //Map<character, fieldPosition>
    private Set<Character> positionedUnknowns = new HashSet<>();
    private Label[] unknownsNames;
    private TextField[] unknownsValues;

    public ImagesCalculatorTab(String tabName) {
        super(tabName, "imagesGridTab.css");
    }

    @Override
    public void loadPane() {
        Label typeLabel = new Label("Function:");
        Label resultLabel = new Label("Result:");
        unknownsNames = new Label[unknownFieldsLines * fieldsPerLine];
        unknownsValues = new TextField[unknownFieldsLines * fieldsPerLine];

        TextField function = new TextField();

        MaskExpression exp = new MaskExpression();

        Button update = new Button("Update");
        update.setId("actionButton");

        update.setOnAction(actionEvent -> {
            if (function.getText() != null && !function.getText().replace(" ", "").equals("")) {
                try {
                    exp.reload(function.getText());
                } catch (MaskException e) {
                }
                refurbish();

                char[] unknowns = exp.getVariables();
                if(!checkUnknowsCount(exp)){ return; }
                activateEntries(unknowns);
            }
        });

        actionButton.setText("Calculate image");
        actionButton.setOnAction((actionEvent) -> {
            LOGGER.debug("Entered function: \"" + function.getText() + "\"");

            actionButton.setDisable(true);

            if(checkEntries(function, exp)){
                String[] values = new String[exp.getVariablesAmount()];
                long positionedCharacters = Arrays.stream(unknownsValues).filter(e -> !e.isDisabled()
                        && e.getText() != null && !e.getText().replace(" ", "").equals("")).count();

                for (int i = 0; i < positionedCharacters; i++) {
                    values[i] = unknownsValues[i].getText();
                }

                Thread calculationThread = new Thread(() -> {
                    timeCalculator = new TimeCalculator();
                    timeCalculator.start();
                    try {
                        float myResult = MaskOperator.begin().imageFor(exp, MaskExpression.TEMP, true, values).asFloat();
                        Platform.runLater(() -> {
                            result.setText(String.valueOf(myResult));
                        });
                    } catch (Exception exception) {
                        Platform.runLater(() -> { result.setText("..."); });
                        showErrorLabel("Unable to calculate image!", exception, 1,
                                2 + unknownFieldsLines * 2 + spaces, fieldsPerLine - 2, 1);
                    }
                    timeCalculator.stop();
                });

                complete(calculationThread, "Calculating image...");
            }
        });
        this.addRow(0, typeLabel);
        this.add(function, 1, 0, fieldsPerLine - 3, 1);
        this.add(update, fieldsPerLine - 2, 0);
        this.add(actionButton, fieldsPerLine - 1, 0);
        for (int i = 0; i < unknownFieldsLines * 2; i++) {
            this.addRow(i);
        }

        for (int i = 0; i < spaces; i++) {
            this.addRow(1 + unknownFieldsLines * 2 + i);
        }

        this.addRow(spaces + unknownFieldsLines * 2, resultLabel);
        this.add(result, 1, spaces + unknownFieldsLines * 2, fieldsPerLine - 2, 1);
        this.add(timeLabel, fieldsPerLine - 1, spaces + unknownFieldsLines * 2);


        for (int i = 0; i < unknownFieldsLines; i++) {
            for (int k = 0; k < fieldsPerLine; k++) {
                Label label = new Label();
                label.setId("unknownName");
                unknownsNames[i * fieldsPerLine + k] = label;
                TextField field = new TextField();
                unknownsValues[i * fieldsPerLine + k] = field;

                this.add(label, k, i * 2 + 1);
                this.add(field, k, i * 2 + 2);
            }
        }
        refurbish();
    }

    private boolean checkEntries(TextField function, MaskExpression exp){
        if (function.getText() == null || function.getText().replace(" ", "").equals("")
                || exp == null) {
            refurbish();
            customError("You must define an expression before trying to calculate an image !",
                    new IllegalArgumentException("Function is not defined"));
            return false;
        } else {
            if(!checkUnknowsCount(exp)){ return false; }

            try { exp.reload(function.getText()); } catch (MaskException e) { return false;}

            long enabledFields = Arrays.stream(unknownsValues).filter(e -> !e.isDisabled()).count();
            long positionedCharacters = Arrays.stream(unknownsValues).filter(e -> !e.isDisabled()
                    && e.getText() != null && !e.getText().replace(" ", "").equals("")).count();

            for (TextField field : unknownsValues) {
                if (!field.isDisabled() && field.getText() != null && !field.getText().replace(" ", "").equals("")) {
                    if (!field.getText().matches("[\\d.]+")) {
                        Platform.runLater(() -> {result.setText("...");});
                        timeCalculator.stop();
                        customError("Please insert numbers only !", new IllegalArgumentException("Bad argument format detected"));
                        return false;
                    }
                }
            }

            if (enabledFields == exp.getVariablesAmount()) {
                if (enabledFields == positionedCharacters) {
                    for (char c : exp.getVariables()) {
                        if (!positionedUnknowns.contains(c)) {
                            pleaseFillEach();
                            timeCalculator.stop();
                            return false;
                        }
                    }
                    return true;
                } else {
                    pleaseFillEach();
                    timeCalculator.stop();
                }
            } else {
                Platform.runLater(() -> { result.setText("..."); });
                timeCalculator.stop();
                customError("You must click update button before trying to calculate!\n"
                        + "\t\tAutomatically updated !", new IllegalArgumentException());
            }
        }
        return false;
    }

    private void customError(String customMessage, Exception e){
        showErrorLabel(customMessage, e, 1, spaces + 2 + unknownFieldsLines * 2, fieldsPerLine - 2, 1);
    }

    /**
     * @return whether it can show or not
     */
    private boolean checkUnknowsCount(MaskExpression exp){
        if(exp.getVariablesAmount() > fieldsPerLine*unknownFieldsLines){
            timeCalculator.stop();
            showErrorLabel("You can't define a function with more than " +
                            (fieldsPerLine * unknownFieldsLines) + " unknowns in your function!",
                    new IllegalArgumentException("Too much unknowns"), 1, spaces + 2 + unknownFieldsLines * 2,
                    fieldsPerLine - 2, 1);
            return false;
        }
        return true;
    }

    private void pleaseFillEach() {
        Platform.runLater(() -> {result.setText("...");});
        showErrorLabel("You must fill each unknown value!",
                new NullPointerException("Ungettable value(s) field(s)"), 1, spaces + 1 + unknownFieldsLines * 2,
                fieldsPerLine - 2, 1);
    }

    private void activateEntries(char[] variables) {
        positionedUnknowns.clear();
        Platform.runLater(() -> {
            for (int i = 0; i < variables.length; i++) {
                unknownsNames[i].setDisable(false);
                unknownsNames[i].setText(variables[i] + "=");
                unknownsValues[i].setDisable(false);
                positionedUnknowns.add(variables[i]);
            }

            for (int i = variables.length; i < fieldsPerLine * unknownFieldsLines; i++) {
                unknownsNames[i].setDisable(true);
                unknownsValues[i].setDisable(true);
            }
        });
    }

    @Override
    public void refurbish() {
        Platform.runLater(() -> {
            this.getChildren().remove(errorLabel);
            actionButton.setDisable(false);
            result.setText("...");
            timeLabel.setText(new SimpleDateFormat("mm:ss:SSS").format(0l));
            Arrays.asList(unknownsNames).forEach(label -> {
                label.setText("");
                label.setDisable(true);
            });
            Arrays.asList(unknownsValues).forEach(field -> {
                field.setText("");
                field.setDisable(true);
            });
        });
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
