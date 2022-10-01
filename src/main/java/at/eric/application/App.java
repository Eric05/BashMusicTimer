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

//todo:
// use font
// create playlist folder in only one location
// start new song without loading complete gui
// update without having to restart
// search not only mp3 but array [mp3, wav...]

public class App extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private static List<String> settings = new FileOperation("Settings.txt").getSettings();
    private static String VIDEO_PATH = Storage.getValueByKey("playlist", settings);
    private static EmbeddedMediaPlayerComponent mediaPlayerComponent = null;
    public JLabel l_nextSong = new JLabel("");

    public App(String title) {
        super(title);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown Hook is running !");
            try {
                var list = Files.readAllLines(Path.of(VIDEO_PATH + File.separator + "playlist.txt"));
                int actual = Integer.parseInt(list.get(list.size() - 1));
                if (actual != pos + 1) {
                    writeFile(VIDEO_PATH + File.separator + "playlist.txt");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
            }


    public static void main() {
        settings = new FileOperation("Settings.txt").getSettings();
        VIDEO_PATH = Storage.getValueByKey("playlist", settings);
        list = getFiles();
        songs = getMp3Files(list);
        pos = Integer.parseInt(list.get(list.size() - 1));
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
        application.setContentPane(path);
        application.loadVideo(path);
    }

    private static void playSong(String path) {
        mediaPlayerComponent.mediaPlayer().media().startPaused(path);
        mediaPlayerComponent.mediaPlayer().controls().play();
    }    private static List<String> list = getFiles();

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
                initFile(VIDEO_PATH + File.separator + "playlist.txt", 0);
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
        songs = getMp3Files(mp3s);
        mp3s.add(String.valueOf(max));
        try {
            Files.write(Path.of(path), mp3s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String path) {
        if (pos >= songs.size() - 1) {
            initFile(VIDEO_PATH + File.separator + "playlist.txt", 0);
        } else {
            songs.add(String.valueOf(pos + 1));
            try {
                Files.write(Path.of(path), songs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    private static int pos = Integer.parseInt(list.get(list.size() - 1));

    public static String setSongTitle(String song) {
        String clean = song.replaceFirst("[.][^.]+$", "");
        File path = new File(clean);
        return path.getName();
    }

    public static void setPos(int pos) {
        pos++;
        if (pos >= songs.size()) {
            System.out.println("End of playlist. Shuffling");
            pos = 0;
            initFile(VIDEO_PATH + File.separator + "playlist.txt", pos);
        }
        App.pos = pos;
    }

    public void initialize() {
        this.setBounds(0, 360, 640, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // to remove border -> this.setUndecorated(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
       l_nextSong.setBounds(10, 370, 80, 800);
       add(l_nextSong);

    }

    public void setContentPane(String path) {
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                super.playing(mediaPlayer);
                System.out.println("Media Playback started.");
            }
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                super.playing(mediaPlayer);
                System.out.println("Media Playback finished.");
                setPos(pos);
                setContentPane(songs.get(pos));
                l_nextSong.setText(songs.get(pos+1));
            }
        };
        this.setTitle(setSongTitle(path));
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        l_nextSong.setForeground(Color.blue);
        l_nextSong.setBackground(Color.yellow);
        contentPane.add(l_nextSong);
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
        skipButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().skipTime(180000));
        printInfo();
        this.setContentPane(contentPane);
        this.setVisible(true);
        this.loadVideo(path);
    }

    private void printInfo() {
        l_nextSong.setText("<html><body>Playing:" + setSongTitle(songs.get(pos)) + "<br>Next:" + setSongTitle(songs.get(pos +1)) +"</body></html>");
    }

    public void loadVideo(String path) {
        mediaPlayerComponent.mediaPlayer().media().start(path);
    }


    private static List<String> songs = getMp3Files(list);


}
