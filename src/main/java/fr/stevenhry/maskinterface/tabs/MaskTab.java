package fr.stevenhry.maskinterface.tabs;

import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.List;

public class MaskTab<T extends Pane & MaskFunctionTab> {

    // - EXISTING TABS - \\

    public static final MaskTab HOME = new MaskTab(new HomeTab("Home"));
    public static final MaskTab IMAGES = new MaskTab(new ImagesCalculatorTab("Calculate image"));
    public static final MaskTab REDUCE_EXPAND = new MaskTab(new ExpandTab("Reduce/Expand"));
    public static final MaskTab DIFFERENTIATE = new MaskTab(new DifferentiateTab("Differentiate"));
    public static final MaskTab EQUATIONS_SOLVER = new MaskTab(new EquationSolverTab("Solve equations"));

    public static List<MaskTab> getFunctionTabs(){
        return Arrays.asList(HOME, IMAGES, REDUCE_EXPAND, DIFFERENTIATE, EQUATIONS_SOLVER);
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
