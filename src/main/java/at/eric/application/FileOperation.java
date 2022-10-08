package at.eric.application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileOperation {
    private static String currentWorkingDir;
    private final String path;

    public FileOperation(String path) {
        this.path = path;
    }


    public static String getCurrentWorkingDir() {
      if (currentWorkingDir == null) {
            try {
                final var pathToJar = Path.of(FileOperation.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation().toURI());
                currentWorkingDir = pathToJar.getParent().toAbsolutePath().toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return currentWorkingDir;
       //return System.getProperty("user.dir");
    }
//TODO uncomment for production

    public List<String> getSettings() {
        List<String> settings = new ArrayList<>();
        try {
            settings = Files.readAllLines(Path.of(getCurrentWorkingDir() + File.separator + path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void writeSettings(List<String> settings) {
        try {
            Files.write(Path.of(getCurrentWorkingDir() + File.separator + path), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
