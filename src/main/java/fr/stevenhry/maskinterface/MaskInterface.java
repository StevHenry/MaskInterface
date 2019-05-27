package fr.stevenhry.maskinterface;

import fr.stevenhry.maskinterface.util.JSONMessage;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class MaskInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaskInterface.class);

    /**
     * Init method
     */
    public static void main(String... args) {
        try {
            new JSONMessage();
            createFont("/fonts/Roboto-Regular.ttf");
            createFont("/fonts/Bellerose.ttf");
        } catch (FontFormatException | IOException ex) {
            LOGGER.error(ex.getMessage());
            return;
        }

        Application.launch(MWindow.class, new String[]{});
    }

    private static void createFont(String path) throws IOException, FontFormatException {
        InputStream resourceStream = MaskInterface.class.getResourceAsStream(path);
        Font.createFont(Font.TRUETYPE_FONT, resourceStream);
        resourceStream.close();
    }
}