package fr.stevenhry.maskinterface.tabs;

import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.List;

public class MaskTab<T extends Pane & MaskFunctionTab> {

    public static final MaskTab HOME = new MaskTab(new HomeTab("Home"));
    public static final MaskTab REDUCE_EXPAND = new MaskTab(new ExpandTab("Reduce/Expand"));
    public static final MaskTab EQUATIONS_SOLVER = new MaskTab(new EquationSolverTab("Solve equations"));
    public static final MaskTab IMAGES = new MaskTab(new ImagesCalculatorTab("Calculate images"));

    public static List<MaskTab> getFunctionTabs(){
        return Arrays.asList(HOME, REDUCE_EXPAND, EQUATIONS_SOLVER, IMAGES);
    }

    private static MaskTab current = HOME;
    private final T tabManager;

    private MaskTab(T tabManager) {
        this.tabManager = tabManager;
        tabManager.getStyleClass().add("tab");
    }

    public static MaskTab getCurrentTab() {
        return current;
    }

    public static void setCurrentTab(MaskTab newTab) {
        current = newTab;
    }

    public String getTabName() {
        return tabManager.getTabName();
    }

    public T getTabManager() {
        return tabManager;
    }
}
