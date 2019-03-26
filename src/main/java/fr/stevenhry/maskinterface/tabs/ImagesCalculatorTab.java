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

    private final static Logger LOGGER = LoggerFactory.getLogger(ImagesCalculatorTab.class);

    //Max settable unknown fields
    //Spaces between last unknown fields line and result line
    private final int unknownFieldsLines = 2, fieldsPerLine = 5, spaces = 4;

    // Already enabled unknown fields characters
    private Set<Character> positionedUnknowns = new HashSet<>();
    // Unknown fields titles
    private Label[] unknownsNames;
    // Unknown fields values
    private TextField[] unknownsValues;

    public ImagesCalculatorTab(String tabName) {
        super(tabName, "styleFiles/imagesGridTab.css");
    }

    @Override
    public void loadPane() {
        //Fields initialization
        Label typeLabel = new Label("Function:");
        Label resultLabel = new Label("Result:");
        unknownsNames = new Label[unknownFieldsLines * fieldsPerLine];
        unknownsValues = new TextField[unknownFieldsLines * fieldsPerLine];
        TextField function = new TextField();
        MaskExpression exp = new MaskExpression();
        Button update = new Button("Update");

        update.setId("actionButton");
        update.setOnAction(actionEvent -> {
            if (!function.getText().matches("[\\s]+|")) {
                try { exp.reload(function.getText()); } catch (MaskException e) { }
                refurbish();

                if(isNotOutnumberingUnknownsCount(exp)){
                    activateUnknownEntries(exp.getVariables());
                }
            }
        });

        //Calculation
        actionButton.setOnAction((actionEvent) -> {
            LOGGER.debug("Entered function: \"" + function.getText() + "\"");

            actionButton.setDisable(true);
            timeCalculator = new TimeCalculator();

            if(checkEntries(function, exp, update)){
                String[] values = new String[exp.getVariablesAmount()];
                long providedUnknowns = Arrays.stream(unknownsValues).filter(vals -> !vals.isDisabled()
                        && !vals.getText().replaceAll("[\\s]+", "").equals("")).count();

                for (int i = 0; i < providedUnknowns; i++) {
                    values[i] = unknownsValues[i].getText();
                }

                //Calculation Thread initialization
                Thread calculationThread = new Thread(() -> {
                    timeCalculator.start();
                    try {
                        String myResult = MaskOperator.begin().imageFor(exp, MaskExpression.TEMP, false, values).asExpression();
                        Platform.runLater(() -> {
                            result.setText(myResult);
                        });
                    } catch (Exception exception) {
                        resetResultField();
                        errorByTab("Unable to calculate image!", exception);
                    }
                    timeCalculator.stop();
                });

                //Action
                complete(calculationThread, "Calculating image...");
            }
        });

        //Configuring tab's layout
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

    /**
     * @return whether all required parameters are set and workable or not
     * @param function text field where function is entered
     * @param exp found {@link net.akami.mask.math.MaskExpression} entered in the function
     * @param updateButton button used to update unknown fields activation
     */
    private boolean checkEntries(TextField function, MaskExpression exp, Button updateButton){
        if (function.getText().replaceAll("\\s", "").equals("") || exp == null) {
            refurbish();
            errorByTab("You must define an expression before trying to calculate an image !",
                    new IllegalArgumentException("Function is not defined"));
            return false;
        } else {
            if(!isNotOutnumberingUnknownsCount(exp)){ return false; }

            try { exp.reload(function.getText()); } catch (MaskException e) { return false;}

            long enabledFields = Arrays.stream(unknownsValues).filter(e -> !e.isDisabled()).count();
            long providedUnknowns = Arrays.stream(unknownsValues).filter(vals -> !vals.isDisabled()
                    && !vals.getText().replaceAll("[\\s]+", "").equals("")).count();

            /*for (TextField field : unknownsValues) {
                if (!field.isDisabled() && field.getText() != null && !field.getText().replace(" ", "").equals("")) {
                    if (!field.getText().matches("[\\d.]+")) {
                        resetResultField();
                        errorByTab("Please insert numbers only !", new IllegalArgumentException("Bad argument format detected"));
                        return false;
                    }
                }
            }*/

            if (enabledFields == exp.getVariablesAmount()) {
                if (enabledFields == providedUnknowns) {
                    for (char c : exp.getVariables()) {
                        if (!positionedUnknowns.contains(c)) {
                            pleaseFillEachField();
                            return false;
                        }
                    }
                    return true;
                } else {
                    pleaseFillEachField();
                }
            } else {
                resetResultField();
                errorByTab("You must click update button before trying to calculate!\n"
                        + "\t\tAutomatically updated !", new IllegalArgumentException());
                updateButton.fire();
            }
        }
        return false;
    }

    /**
     * @return whether the MaskExpression unknowns outnumbers the max settable unknowns count or not
     * @param exp Used {@link net.akami.mask.math.MaskExpression}
     */
    private boolean isNotOutnumberingUnknownsCount(MaskExpression exp){
        LOGGER.debug("Variables count: " + exp.getVariablesAmount());
        if(exp.getVariablesAmount() > fieldsPerLine*unknownFieldsLines){
            showErrorLabel("You can't define a function with more than " +
                            (fieldsPerLine * unknownFieldsLines) + " unknowns in your function!",
                    new IllegalArgumentException("Too much unknowns"), 1, spaces + 2 + unknownFieldsLines * 2,
                    fieldsPerLine - 2, 1);
            return false;
        }
        return true;
    }

    /**
     * Calls {@link GridMaskTab#resetResultField()} then calls {@link #errorByTab(String, Exception)}
     */
    private void pleaseFillEachField() {
        resetResultField();
        errorByTab("You must fill each unknown value!",
                new NullPointerException("Non-recoverable value(s) field(s)"));
    }

    /**
     * Enable necessary unknown fields and titles then disable the other ones.
     * @param variables calculation variables
     */
    private void activateUnknownEntries(char[] variables) {
        positionedUnknowns.clear();
        Platform.runLater(() -> {
            //Enable
            for (int i = 0; i < variables.length; i++) {
                unknownsNames[i].setDisable(false);
                unknownsNames[i].setText(variables[i] + "=");
                unknownsValues[i].setDisable(false);
                positionedUnknowns.add(variables[i]);
            }

            //Disable
            for (int i = variables.length; i < fieldsPerLine * unknownFieldsLines; i++) {
                unknownsNames[i].setDisable(true);
                unknownsValues[i].setDisable(true);
            }
        });
    }

    @Override
    protected void errorByTab(String customMessage, Exception exception){
        showErrorLabel(customMessage, exception, 1, spaces + 2 + unknownFieldsLines * 2, fieldsPerLine - 2, 1);
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
