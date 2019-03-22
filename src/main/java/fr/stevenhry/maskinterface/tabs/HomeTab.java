package fr.stevenhry.maskinterface.tabs;

import fr.stevenhry.maskinterface.MWindow;
import javafx.geometry.HPos;
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
        ImageView img1 = new ImageView(new Image(MWindow.class.getResourceAsStream("/images/icon.png")));
        Label text = new Label();

        GridPane.setHalignment(img1, HPos.CENTER);
        GridPane.setHalignment(text, HPos.CENTER);
        text.setId("homeText");

        //"Their githubs are: https://github.com/lolilolulolilol and https://github.com/Askigh/"
        /*Hyperlink stevenGithub = new Hyperlink("Steven HENRY's github");
        stevenGithub.setOnAction(actionEvent -> { MWindow.openBrowser("https://github.com/lolilolulolilol"); });
        Hyperlink antoineGithub = new Hyperlink("Antoine TRAN's github");
        antoineGithub.setOnAction(actionEvent -> { MWindow.openBrowser("https://github.com/Askigh"); });
        Hyperlink maskGithub = new Hyperlink("Mask repository");
        maskGithub.setOnAction(actionEvent -> { MWindow.openBrowser("https://github.com/Askigh/Mask"); });
        Hyperlink maskInterfaceGithub = new Hyperlink("MaskInterface repository");
        maskInterfaceGithub.setOnAction(actionEvent -> { MWindow.openBrowser("https://github.com/lolilolulolilol/MaskInterface"); });*/

        final String[] introductionText = new String[]{
                /*
                "This software has been developed by Steven HENRY (17 years old french developer)",
                "It is based on the Mask mathematics API created by Antoine Tran (17 years old swiss developer)",
                "They are both part of Starype development team",
                */
                "Mask Interface",
                "Version 1.1 - March 2019",
                "",
                "This software is provided by a pair of students"
        };
        text.setText(String.join("\n", introductionText));
        text.setWrapText(true);
        text.prefHeight(Region.USE_COMPUTED_SIZE);
        text.prefWidth(Region.USE_COMPUTED_SIZE);
        text.textAlignmentProperty().setValue(TextAlignment.CENTER);

        this.addRow(0, img1);
        this.addRow(1, text);
        //this.addRow(2, stevenGithub, antoineGithub, maskGithub, maskInterfaceGithub);
    }

    @Override
    public String getTabName() {
        return tabName.toUpperCase();
    }

    @Override
    public void refurbish() {
    }
}
