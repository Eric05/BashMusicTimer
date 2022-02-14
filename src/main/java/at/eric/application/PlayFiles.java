package at.eric.application;

import at.eric.operationSystem.OsCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.swing.JOptionPane.showMessageDialog;

public class PlayFiles extends Thread{
    private String path;
    private int chunkSize;
    private int pos;

    public PlayFiles(String path, int chunkSize) {
        this.path = path;
        this.chunkSize = chunkSize;


    }

    public String getMp3String() {

        var mp3s = getFiles();
        try {
            pos = Integer.parseInt(mp3s.get(mp3s.size() - 1));
        } catch(Exception e) {
            pos = 0;
        }
        Collections.shuffle(mp3s);
        var mp3String = createPlaylistString(mp3s, chunkSize,pos);
        return mp3String;

    }

    private String createPlaylistString(List<String> mp3s, int chunkSize,int pos) {
        StringBuilder sb = new StringBuilder();
        for (int i = pos; i < chunkSize; i++) {
            if ( i == mp3s.size()-1){
                i = 0;
            }
            sb.append(mp3s.get(i)).append(" ");
        }
        return sb.toString();
    }

    private List<String> getFiles() {
        File f = new File(path);
        if (!f.isDirectory()) {
            showMessageDialog(null, "Please enter valid FOLDER as Playlist.");
        } else {
            f = new File(path + File.separator + "playlist.txt");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                initFile(path + File.separator + "playlist.txt", chunkSize);
            }
        }

        try {
            return Files.readAllLines(Path.of(path + File.separator + "playlist.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initFile(String path, int max) {
        var mp3s = getMp3Files();
        mp3s.add(String.valueOf(max));
        try {
            Files.write(Path.of(path), mp3s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getMp3Files() {
        List<String> files = new ArrayList<>();
        try {
            files = Files.list(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".mp3"))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            // Error while reading the directory
        }
        return files;
    }


}