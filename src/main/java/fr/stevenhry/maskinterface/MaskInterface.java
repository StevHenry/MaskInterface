package fr.stevenhry.maskinterface;

import javafx.application.Application;
import javafx.scene.text.Font;

public class MaskInterface {

    public static final int WINDOW_HEIGHT = 600;
    public static final int WINDOW_WIDTH = 1000;


    public static void main(String... args) {
        Font.loadFont(ClassLoader.getSystemResourceAsStream("/fonts/Bellerose.ttf"), 10);
        Application.launch(MWindow.class, new String[]{});
    }
}