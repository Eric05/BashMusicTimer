package at.eric.application;

import java.util.List;
import java.util.Locale;

public class Storage {

    public static String getValueByKey(String key, List<String> values) {
        String val = "";

        for (var s : values) {
            if (s.startsWith(key)) {
                try {
                    val = s.split("[,|;]")[1].trim();
                } catch (Exception ignored) {
                    ErrorLogger.writeError("Error when splitting " + key.toUpperCase(Locale.ROOT));
                }
            }
        }
        return val;
    }
}
