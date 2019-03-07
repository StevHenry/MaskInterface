package fr.stevenhry.maskinterface.tabs;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class HomeTab extends StackPane implements MaskFunctionTab {

    private final String tabName;

    public HomeTab(String tabName) {
        this.tabName = tabName;
        this.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource("home.css").toExternalForm());
        loadPane();
    }

    @Override
    public void loadPane() {

        Text tabNameText = new Text(tabName);
        tabNameText.getStyleClass().add("homeText");

        this.getChildren().add(tabNameText);
    }

    @Override
    public String getTabName() {
        return tabName.toUpperCase();
    }

    @Override
    public void refurbish() {

    }
}
