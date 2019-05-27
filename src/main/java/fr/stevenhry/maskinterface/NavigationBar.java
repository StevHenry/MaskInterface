package fr.stevenhry.maskinterface;

import fr.stevenhry.maskinterface.tab.MaskTab;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class NavigationBar extends GridPane {

    public NavigationBar(MWindow window) {
        this.getStyleClass().add("pane");
        this.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource("styleFiles/navBar.css").toExternalForm());

        for(int current = 0; current < MaskTab.getFunctionTabs().size(); current++){
            MaskTab tab = MaskTab.getFunctionTabs().get(current);
            Button paneSelectButton = new Button(tab.getTabName());

            paneSelectButton.setOnAction(actionEvent -> {
                MaskTab.setCurrentTab(tab);
                window.setCenterPane(tab.getTabManager());
            });

            this.add(paneSelectButton, current, 0);
        }
    }
}
