package fr.stevenhry.maskinterface.tab.layout;

import fr.stevenhry.maskinterface.tab.GridMaskTab;
import fr.stevenhry.maskinterface.util.JSONMessage;
import fr.stevenhry.maskinterface.util.TimeCalculator;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.akami.mask.core.Mask;
import net.akami.mask.core.MaskImageCalculator;
import net.akami.mask.core.MaskOperatorHandler;
import net.akami.mask.exception.MaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class ImagesCalculatorTab extends GridMaskTab {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImagesCalculatorTab.class);

    //Max settable unknown fields
    //Spaces between last unknown fields line and result line
    private final int unknownFieldsLines = 2, fieldsPerLine = 5, spaces = 4;
    // Already enabled unknown fields characters
    private Map<Label, TextField> variables;
    //private Set<Character> definedUnknowns = new HashSet<>();

    public ImagesCalculatorTab(String tabName) {
        super(tabName, "styleFiles/imagesGridTab.css");
    }

    @Override
    public void loadPane() {
        //Fields initialization
        Label typeLabel = new Label(JSONMessage.getMessage("tabs.images.function"));

        variables = new LinkedHashMap<>();

        TextField function = new TextField();
        Mask exp = new Mask();
        Button update = new Button(JSONMessage.getMessage("tabs.images.update"));

        update.setId("actionButton");
        GridMaskTab.setHalignment(update, HPos.CENTER);
        update.setOnAction(actionEvent -> {
            if (!function.getText().matches("[\\s]+|")) {
                try {
                    exp.reload(function.getText());
                } catch (MaskException e) {
                }
                refurbish();

                if (isNotOutnumberingUnknownsCount(exp)) {
                    activateUnknownEntries(getVariables(exp));
                }
            }
        });

        //Calculation
        actionButton.setOnAction((actionEvent) -> {
            LOGGER.debug("Entered function: \"" + function.getText() + "\"");

            actionButton.setDisable(true);
            timeCalculator = new TimeCalculator();

            if (checkEntries(function, exp, update)) {
                Map<Character, String> values = new HashMap<>();
                variables.entrySet().stream().filter(entry -> !entry.getValue().getText().isEmpty())
                        .forEach(entry -> values.put(entry.getKey().getText().charAt(0), entry.getValue().getText()));

                //Calculation Thread initialization
                Thread calculationThread = new Thread(() -> {
                    timeCalculator.start();
                    try {
                        MaskOperatorHandler handler = new MaskOperatorHandler();
                        handler.begin(exp);
                        handler.compute(MaskImageCalculator.class, exp, values);
                        Platform.runLater(() -> {
                            result.setText(exp.getExpression());
                        });
                    } catch (Exception exception) {
                        resetResultField();
                        errorByTab(JSONMessage.getMessage("tabs.images.error.calculation"), exception);
                    }
                    timeCalculator.stop();
                });

                //Action
                complete(calculationThread, JSONMessage.getMessage("tabs.images.performing"));
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


        for (int i = 0; i < unknownFieldsLines * fieldsPerLine; i++) {
            Label label = new Label();
            label.setId("unknownName");
            label.setDisable(true);
            TextField field = new TextField();
            field.setDisable(true);
            variables.put(label, field);
            this.add(label, i % fieldsPerLine, (i / fieldsPerLine) * 2 + 1);
            this.add(field, i % fieldsPerLine, (i / fieldsPerLine) * 2 + 2);
        }

        refurbish();
    }

    /**
     * @param function     text field where function is entered
     * @param exp          found {@link net.akami.mask.core.Mask} entered in the function
     * @param updateButton button used to update unknown fields activation
     * @return whether all required parameters are set and workable or not
     */
    private boolean checkEntries(TextField function, Mask exp, Button updateButton) {
        if (function.getText().replaceAll("\\s", "").equals("") || exp == null) {
            refurbish();
            errorByTab(JSONMessage.getMessage("tabs.images.error.undefinedExpression"),
                    new IllegalArgumentException("Function is not defined"));
            return false;
        } else {
            if (!isNotOutnumberingUnknownsCount(exp))
                return false;
            try {
                exp.reload(function.getText());
            } catch (MaskException e) {
                return false;
            }

            long enabledFields = variables.keySet().stream().filter(e -> !e.isDisabled()).count();
            long providedUnknowns = variables.values().stream().filter(vals -> !vals.getText().isEmpty()).count();

            List<Character> vars = getVariables(exp);
            if (enabledFields == vars.size()) {
                LOGGER.debug("ENABLED {}, PROVIDED {}", enabledFields, providedUnknowns);
                if (enabledFields == providedUnknowns) {
                    if (vars.stream().anyMatch(c -> !isEntryProvided(c))) {
                        pleaseFillEachField();
                    } else return true;
                } else pleaseFillEachField();
            } else {
                resetResultField();
                actionButton.setDisable(false);
                errorByTab(JSONMessage.getMessage("tabs.images.error.notUpdated"), new IllegalArgumentException());
                updateButton.fire();
            }
        }
        return false;
    }

    private boolean isEntryProvided(char c) {
        boolean hey = variables.keySet().stream().anyMatch(label -> label.getText().charAt(0) == c);
        LOGGER.debug("PROVIDED FOR {} ?: {}", c, hey);
        return hey;
    }

    private List<Character> getVariables(Mask mask) {
        List<Character> vars = new ArrayList<>();
        //Arrays.asList(self.toCharArray()).stream()
        //      .filter(c -> String.valueOf(c).matches("[a-zA-Z]")).distinct().collect(Collectors.toList());
        for (char c : mask.getExpression().toCharArray()) {
            if (vars.contains(c)) continue;
            if (String.valueOf(c).matches("[a-zA-Z]")) {
                vars.add(c);
            }
        }
        return vars;
    }


    /**
     * @param exp Used {@link net.akami.mask.core.Mask}
     * @return whether the MaskExpression unknowns outnumbers the max settable unknowns count or not
     */
    private boolean isNotOutnumberingUnknownsCount(Mask exp) {
        int varCount = getVariables(exp).size();
        LOGGER.debug("Variables count: {}", varCount);
        if (varCount > fieldsPerLine * unknownFieldsLines) {
            errorByTab(JSONMessage.getMessage("tabs.images.error.tooMuchUnknowns").replaceFirst("%d",
                    String.valueOf(fieldsPerLine * unknownFieldsLines)), new IllegalArgumentException("Too much unknowns"));
            return false;
        }
        return true;
    }

    /**
     * Calls {@link GridMaskTab#resetResultField()} then calls {@link #errorByTab(String, Exception)}
     */
    private void pleaseFillEachField() {
        resetResultField();
        errorByTab(JSONMessage.getMessage("tabs.images.error.notFilled"),
                new NullPointerException("Non-recoverable value(s) field(s)"));
    }

    /**
     * Enable necessary unknown fields and titles then disable the other ones.
     *
     * @param vars calculation variables characters
     */
    private void activateUnknownEntries(List<Character> vars) {
        //Enable
        Iterator entries = variables.entrySet().iterator();
        for (int i = 0; i < vars.size(); i++) {
            Map.Entry<Label, TextField> entry = (Map.Entry) entries.next();
            final String text = vars.get(i) + "=";
            Platform.runLater(() -> {
                entry.getKey().setText(text);
                entry.getKey().setDisable(false);
                entry.getValue().setDisable(false);
            });
        }
        //Disable
        while (entries.hasNext()) {
            Map.Entry<Label, TextField> entry = (Map.Entry) entries.next();
            Platform.runLater(() -> {
                entry.getKey().setDisable(true);
                entry.getValue().setDisable(true);
            });
        }
    }

    @Override
    protected void errorByTab(String customMessage, Exception exception) {
        showErrorLabel(customMessage, exception, 1, spaces + 2 + unknownFieldsLines * 2, fieldsPerLine - 2, 1);
    }

    @Override
    public void refurbish() {
        Platform.runLater(() -> {
            this.getChildren().remove(errorLabel);
            actionButton.setDisable(false);
            result.setText("...");
            timeLabel.setText(new SimpleDateFormat("mm:ss:SSS").format(0l));
            for (Map.Entry<Label, TextField> entry : variables.entrySet()) {
                entry.getValue().setText("");
                entry.getKey().setText("");
                entry.getValue().setDisable(true);
                entry.getKey().setDisable(true);
            }
        });
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
