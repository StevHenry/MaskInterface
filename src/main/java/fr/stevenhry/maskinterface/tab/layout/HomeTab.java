package fr.stevenhry.maskinterface.tab.layout;

import fr.stevenhry.maskinterface.MWindow;
import fr.stevenhry.maskinterface.MaskInterface;
import fr.stevenhry.maskinterface.tab.MaskFunctionTab;
import fr.stevenhry.maskinterface.util.JSONMessage;
import javafx.geometry.HPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;

public class HomeTab extends GridPane implements MaskFunctionTab {

    private final String tabName;

    public HomeTab(String tabName) {
        this.tabName = tabName;
        this.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource("styleFiles/home.css").toExternalForm());
        loadPane();
    }

    @Override
    public void loadPane() {
        ImageView img1 = new ImageView(new Image(MaskInterface.class.getResourceAsStream("/images/MI_icon.png")));
        img1.setFitHeight(250);
        img1.setFitWidth(250);
        Label text = new Label();

        GridPane.setHalignment(img1, HPos.CENTER);
        GridPane.setHalignment(text, HPos.CENTER);
        text.setId("homeText");

        Hyperlink stevenGithub = createHyperlink("tabs.home.StevenGithubName", "tabs.home.StevenGithubLink");
        Hyperlink antoineGithub = createHyperlink("tabs.home.AntoineGithubName","tabs.home.AntoineGithubLink");
        Hyperlink maskRepository = createHyperlink("tabs.home.MaskRepositoryName","tabs.home.MaskRepositoryLink");
        Hyperlink maskInterfaceRepository = createHyperlink("tabs.home.MaskInterfaceRepositoryName",
                "tabs.home.MaskInterfaceRepositoryLink");

        text.setText(JSONMessage.getMessage("tabs.home.softwareDescription"));
        text.setWrapText(true);
        text.prefHeight(Region.USE_COMPUTED_SIZE);
        text.prefWidth(Region.USE_COMPUTED_SIZE);
        text.textAlignmentProperty().setValue(TextAlignment.CENTER);

        this.add(img1, 0, 1, 4, 1);
        this.add(text, 0, 2, 4, 1);
        this.addRow(3, stevenGithub, antoineGithub, maskRepository, maskInterfaceRepository);
    }

    private Hyperlink createHyperlink(String textPath, String urlPath){
        Hyperlink hyperlink = new Hyperlink(JSONMessage.getMessage(textPath));
        hyperlink.setOnAction(actionEvent -> { MWindow.openURL(JSONMessage.getMessage(urlPath)); });
        return hyperlink;
    }

    @Override
    public String getTabName() {
        return tabName.toUpperCase();
    }

    @Override
    public void refurbish() {
    }
}
