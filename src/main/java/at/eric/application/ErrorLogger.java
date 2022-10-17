package at.eric.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {

    private final static String PATH = "Log.txt";

    public ErrorLogger() {

    }

    public static void writeError(String text) {
        FileWriter fw = null;

        try {
            String currentWorkingDir = FileOperation.getCurrentWorkingDir();
            fw = new FileWriter(currentWorkingDir + File.separator + PATH, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(getFormattedDate() + " " + text);
            bw.newLine();
            bw.close();
        } catch (IOException ignored) {

        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    System.err.println("error closing writer for file " + PATH);
                }
            }
        }
    }

    private static String getFormattedDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return date.format(formatter);
    }
}
