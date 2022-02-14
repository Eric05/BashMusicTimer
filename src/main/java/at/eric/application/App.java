package at.eric.application;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.swing.JOptionPane.showMessageDialog;

public class App extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String VIDEO_PATH = "C:\\Music\\Test2";
    private static EmbeddedMediaPlayerComponent mediaPlayerComponent = null;
    public App(String title) {
        super(title);
        mediaPlayerComponent = getMediaPlayerComponent();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown Hook is running !");
            writeFile(VIDEO_PATH + File.separator + "playlist.txt", pos);
        }));
    }    private static final List<String> list = getFiles();

    private static EmbeddedMediaPlayerComponent getMediaPlayerComponent() {
        return new EmbeddedMediaPlayerComponent() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                super.playing(mediaPlayer);
                System.out.println("Media Playback started." + songs.get(pos));
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                super.playing(mediaPlayer);
                System.out.println("Media Playback finished.");
                setPos(pos);

                songGui(songs.get(pos));
            }
        };
    }    private static int pos = Integer.parseInt(list.get(list.size() - 1));

    public static void main() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        }
        songGui(songs.get(pos));
    }

    private static void songGui(String path) {
        App application = new App(setSongTitle(songs.get(pos)));
        application.initialize();
        application.setVisible(true);
        application.loadVideo(path);
        playSong(path);
    }

    private static void playSong(String path) {
        mediaPlayerComponent.mediaPlayer().media().startPaused(path);
        mediaPlayerComponent.mediaPlayer().controls().play();
    }    private static final List<String> songs = getMp3Files(list);

    private static List<String> getMp3Files(List<String> list) {
        List<String> files;
        files = list.stream()
                .filter(path -> path.endsWith(".mp3"))
                .collect(Collectors.toList());
        return files;
    }

    private static List<String> getMp3Files() {
        List<String> res = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(VIDEO_PATH))) {
            res = walk.map(Path::toString)
                    .filter(f -> f.endsWith(".mp3")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static List<String> getFiles() {
        File f = new File(VIDEO_PATH);
        if (!f.isDirectory()) {
            showMessageDialog(null, "Please enter valid FOLDER as Playlist.");
        } else {
            f = new File(VIDEO_PATH + File.separator + "playlist.txt");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                initFile(VIDEO_PATH + File.separator + "playlist.txt", pos);
            }
        }
        try {
            return Files.readAllLines(Path.of(VIDEO_PATH + File.separator + "playlist.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void initFile(String path, int max) {
        var mp3s = getMp3Files();
        Collections.shuffle(mp3s);
        mp3s.add(String.valueOf(max));
        try {
            Files.write(Path.of(path), mp3s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(String path, int max) {
        songs.add(String.valueOf(max));
        try {
            Files.write(Path.of(path), songs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String setSongTitle(String song) {
        String clean = song.replaceFirst("[.][^.]+$", "");
        File path = new File(clean);
        return path.getName();
    }

    public static void setPos(int pos) {
        pos++;
        if (pos >= songs.size() - 1) {
            System.out.println("shuffling");
            pos = 0;
            initFile(VIDEO_PATH, pos);
        }
        App.pos = pos;
    }

    public void initialize() {
        this.setBounds(100, 100, 400, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);
        JPanel controlsPane = new JPanel();
        JButton playButton = new JButton("Play");
        controlsPane.add(playButton);
        JButton pauseButton = new JButton("Pause");
        controlsPane.add(pauseButton);
        JButton rewindButton = new JButton("Rewind");
        controlsPane.add(rewindButton);
        JButton skipButton = new JButton("Skip");
        controlsPane.add(skipButton);
        contentPane.add(controlsPane, BorderLayout.SOUTH);
        playButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().play());
        pauseButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().pause());
        rewindButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().skipTime(-14000));
        skipButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().skipTime(4000));
        this.setContentPane(contentPane);
        this.setVisible(true);
    }

    public void loadVideo(String path) {
        mediaPlayerComponent.mediaPlayer().media().startPaused(path);
    }
}