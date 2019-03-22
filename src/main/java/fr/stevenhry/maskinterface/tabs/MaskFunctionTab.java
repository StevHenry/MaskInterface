package fr.stevenhry.maskinterface.tabs;

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
