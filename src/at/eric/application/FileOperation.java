package at.eric.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileOperation {
    private final String path;

    public FileOperation(String path) {
        this.path = path;
    }

    public List<String> getSettings() {
        List<String> settings = new ArrayList<>();
        try {
            settings = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void writeSettings(List<String> settings) {
        try {
            Files.write(Path.of(path), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
