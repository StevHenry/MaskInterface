package fr.stevenhry.maskinterface.util;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JSONMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONMessage.class);
    private static HashMap<String, String> messages = new HashMap<>();

    @SuppressWarnings("unchecked")
    public JSONMessage() throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream("/messages.json");
        addToMessages("", new Gson().fromJson(new InputStreamReader(resourceStream), Map.class));
        resourceStream.close();
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
