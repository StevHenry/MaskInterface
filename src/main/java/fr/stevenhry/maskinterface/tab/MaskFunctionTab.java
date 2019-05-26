package fr.stevenhry.maskinterface.tab;

public interface MaskFunctionTab {

    /**
     * Load the pane
     */
    void loadPane();

    /**
     * Refurbishes the pane
     */
    void refurbish();

    /**
     * @return the tab name
     */
    String getTabName();

}
