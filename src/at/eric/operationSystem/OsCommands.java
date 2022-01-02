package at.eric.operationSystem;

import at.eric.application.ErrorLogger;
import at.eric.application.FileOperation;
import at.eric.application.Storage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OsCommands {

    private static final String path = getPathToCommands();
    private static final List<String> commands = getOsCommands();

    public static void doCommand(String command, String path) {
        if (command.isBlank()) {
            return;
        }
        String shell = Storage.getValueByKey("shell", commands);
        String com = Storage.getValueByKey(command, commands);
        String res = (shell + " " + com + " " + path).trim();

        try {
            Runtime.getRuntime().exec(res);
        } catch (IOException e) {
            ErrorLogger.writeError("Error when doing command: " + res.toUpperCase(Locale.ROOT));
            e.printStackTrace();
        }
    }

    public static void doCommand(String command) {
        if (command.isBlank()) {
            return;
        }
        String shell = Storage.getValueByKey("shell", commands);
        try {
            Runtime.getRuntime().exec((shell + " " + command).trim());
        } catch (IOException e) {
            ErrorLogger.writeError("Error when doing command: " + command.toUpperCase(Locale.ROOT));
            e.printStackTrace();
        }
    }

    public static List<String> getOsCommands() {
        FileOperation op = new FileOperation(path);
        return op.getSettings();
    }

    public static int getBatteryStatus() {
        int status = 100;
        try {
            if (path.toLowerCase().contains("win")) {
                status = GetCmdValue.getValue(Storage.getValueByKey("battery", commands));
            } else if (path.toLowerCase().contains("mac")) {
                status = GetBashValue.getValue(Storage.getValueByKey("battery", commands));
            } else {
                status = GetBashValue.getValue(Storage.getValueByKey("battery", commands));
            }
        } catch (Exception e){
            ErrorLogger.writeError("Error when getting BatteryStatus");
        }
            return status;
    }

    private static String getPathToCommands() {
        String op = System.getProperty("os.name").toLowerCase();
        if (op.contains("win")) {
            return "Windows.txt";
        } else if (op.contains("mac")) {
            return "Mac.txt";
        } else {
            return "Linux.txt";
        }
    }
}
