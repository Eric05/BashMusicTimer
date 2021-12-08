package at.eric.operationSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetCmdValue {

   public static int getValue(String command){

        ProcessBuilder pb = new ProcessBuilder(  "cmd.exe", "/c",command);
        pb.redirectErrorStream(true);
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
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
