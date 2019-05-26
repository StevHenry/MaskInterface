package fr.stevenhry.maskinterface;

import fr.stevenhry.maskinterface.util.JSONMessage;
import javafx.application.Application;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class MaskInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaskInterface.class);
    public static final int WINDOW_HEIGHT = 675;
    public static final int WINDOW_WIDTH = 1000;

    /**
     * Init method
     */
    public static void main(String... args) {
        try {
            new JSONMessage();
        } catch (FileNotFoundException | URISyntaxException e) {
            LOGGER.error("Messages configuration file not found ! ");
            return;
        }
        Font.loadFont(ClassLoader.getSystemResourceAsStream("/fonts/Bellerose.ttf"), 10);
        Application.launch(MWindow.class, new String[]{});

    }
}