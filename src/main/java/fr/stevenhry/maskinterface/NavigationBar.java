package fr.stevenhry.maskinterface;

import fr.stevenhry.maskinterface.tabs.MaskTab;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;

public class NavigationBar extends TilePane {

    public NavigationBar(MWindow window) {
        this.getStyleClass().addAll("pane", "flow-tile");

        MaskTab.getFunctionTabs().forEach(tab -> {
            Button button = new Button(tab.getTabName());
            button.setOnAction(actionEvent -> {
                MaskTab.setCurrentTab(tab);
                window.setCenterPane(tab.getTabManager());
            });
            getChildren().add(button);
        });

        this.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource("styleFiles/navBar.css").toExternalForm());
    }
}
