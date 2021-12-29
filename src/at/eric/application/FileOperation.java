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
    private static final String currentWorkingDir = System.getProperty("user.dir");

    public FileOperation(String path) {
        this.path = path;
    }


    public static String getCurrentWorkingDir(){
        return currentWorkingDir;
    }


    public List<String> getSettings() {
        List<String> settings = new ArrayList<>();
        try {
            settings = Files.readAllLines(Path.of(currentWorkingDir + File.separator + path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void writeSettings(List<String> settings) {
        try {
            Files.write(Path.of(currentWorkingDir + File.separator + path), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
