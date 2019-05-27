package fr.stevenhry.maskinterface.tab;

import fr.stevenhry.maskinterface.tab.layout.*;
import fr.stevenhry.maskinterface.util.JSONMessage;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.List;

public class MaskTab<T extends Pane & MaskFunctionTab> {

    // - EXISTING TABS - \\
    public static final MaskTab HOME = new MaskTab<>(new HomeTab(JSONMessage.getMessage("tabs.home.name")));
    public static final MaskTab IMAGES = new MaskTab<>(new ImagesCalculatorTab(JSONMessage.getMessage("tabs.images.name")));
    public static final MaskTab SIMPLIFY = new MaskTab<>(new SimplifyTab(JSONMessage.getMessage("tabs.simplifier.name")));
    public static final MaskTab DIFFERENTIATE = new MaskTab<>(new DifferentiateTab(JSONMessage.getMessage("tabs.differentiate.name")));
    //public static final MaskTab EQUATIONS_SOLVER = new MaskTab<>(new EquationSolverTab(JSONMessage.getMessage("tabs.equations.name")));

    public static List<MaskTab> getFunctionTabs(){
        return Arrays.asList(HOME, IMAGES, SIMPLIFY, DIFFERENTIATE);
    }

    private static MaskTab current = HOME;
    private final T tabManager;

    private MaskTab(T tabManager) {
        this.tabManager = tabManager;
        tabManager.getStyleClass().add("tab");
    }

    /**
     * @return the currently selected tab
     */
    public static MaskTab getCurrentTab() {
        return current;
    }

    /**
     * Sets the currently selected tab
     */
    public static void setCurrentTab(MaskTab newTab) {
        current = newTab;
    }

    /**
     * @return {@link MaskFunctionTab#getTabName()}
     */
    public String getTabName() {
        return tabManager.getTabName();
    }

    /**
     * @return the manager instance of the tab
     */
    public T getTabManager() {
        return tabManager;
    }
}
