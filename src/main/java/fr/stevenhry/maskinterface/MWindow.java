package fr.stevenhry.maskinterface;

import fr.stevenhry.maskinterface.tabs.MaskTab;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MWindow extends Application {

    //private static final Logger LOGGER = LoggerFactory.getLogger(MWindow.class);
    private BorderPane borderPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Mask Interface");
        primaryStage.setMaximized(false);
        primaryStage.setResizable(false);

        borderPane = new BorderPane();
        NavigationBar navigationBar = new NavigationBar(this);

        Scene scene = new Scene(borderPane, MaskInterface.WINDOW_WIDTH, MaskInterface.WINDOW_HEIGHT);

        borderPane.setTop(navigationBar);
        setCenterPane(MaskTab.getCurrentTab().getTabManager());

        scene.setRoot(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setCenterPane(Pane pane){
        borderPane.setCenter(pane);
    }
}
