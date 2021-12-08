package at.eric.operationSystem;

import at.eric.application.ErrorLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetBashValue {
    public static int getValue(String command){

        ProcessBuilder pb = new ProcessBuilder(  "bash", "-c",command);
        pb.redirectErrorStream(true);
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            ErrorLogger.writeError("Error when getting Battery Status in Bash/Cmd");
            return 100;
        }
        BufferedReader stdin = null;
        if (p != null) {
            stdin = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
        }
        StringBuilder commandOutput = new StringBuilder();
        String line = "";
        while (true) {
            try {
                if (stdin != null && (line = stdin.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            commandOutput.append(line);
        }

        String numberOnly= String.valueOf(commandOutput).replaceAll("[^0-9]", "");
        return Integer.parseInt(numberOnly);

    }
}
