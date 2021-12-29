package at.eric.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileOperation {
    private final String path;
    private final String userDirectory = Paths.get("").toAbsolutePath().toString();

    public FileOperation(String path) {
        this.path = path;
    }

    public List<String> getSettings() {
        List<String> settings = new ArrayList<>();
        try {
            settings = Files.readAllLines(Path.of(userDirectory + File.separator + path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void writeSettings(List<String> settings) {
        try {
            Files.write(Path.of(userDirectory + File.separator + path), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
