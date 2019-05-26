package fr.stevenhry.maskinterface.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import fr.stevenhry.maskinterface.MaskInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class JSONMessage {

    private static HashMap<String, String> messages = new HashMap<>();

    public JSONMessage() throws URISyntaxException, FileNotFoundException {
        File file = new File(MaskInterface.class.getResource("/messages.json").toURI());
        JsonReader reader = new JsonReader(new FileReader(file.getPath()));
        addToMessages("", new Gson().fromJson(reader, Map.class));
    }

    public static String getMessage(String path) {
        return messages.get(path);
    }

    private void addToMessages(String start, Map<String, Object> elements) {
        for (Map.Entry entry : elements.entrySet()) {
            String path = (start != "" ? start + "." : "") + entry.getKey();
            if (entry.getValue() instanceof String) {
                messages.put(path, (String) entry.getValue());
            } else {
                addToMessages(path, (Map) entry.getValue());
            }
        }
    }
}
