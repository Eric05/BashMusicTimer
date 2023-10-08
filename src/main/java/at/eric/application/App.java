package at.eric.application;

import at.eric.gui.MainGui;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
// BUGFIX - bug getList in App 76/100/183
// create playlist folder in only one location
// update without having to restart
// search not only mp3 but array [mp3, wav...]
// Done:
// skip -> find good timespan
// start new song without loading complete gui
// bool for shuffle mode
// css design
// use font

public class App extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private static List<String> settings = new FileOperation("Settings.txt").getSettings();
    private static String VIDEO_PATH = Storage.getValueByKey("playlist", settings);
    private static final boolean isNoShuffle = (Storage.getValueByKey("shuffle", settings).startsWith("1"));
    private static final String resetPlaylistAfterTimespan = (Storage.getValueByKey("resetPlay", settings));
    private static EmbeddedMediaPlayerComponent mediaPlayerComponent = null;
    private static final String appTitle = "Radio " + new File(VIDEO_PATH).getName();
    JButton b_reset = new JButton();

    Font font = MainGui.getCustomFont();
    // register Font to use it HTML
    {
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
    }

    public JLabel l_nextSong = new JLabel("", SwingConstants.CENTER );

    public App() {
        super(appTitle);
        UIManager.put("Label.font", font);
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
        if (list != null) {
            songs = getMp3Files(list);
            pos = Integer.parseInt(list.get(list.size() - 1));
        } else {
            ErrorLogger.writeError("NullExceptionError while reading playlist");
        }
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("error " + e);
        }
        songGui(songs.get(pos));
    }

    private static void songGui(String path) {
        App application = new App();
        application.initialize();
        application.setVisible(true);
        application.setContentPane(path);
        application.loadVideo(path);
    }

    private static List<String> list = getFiles();

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
                   boolean isFileCreated =  f.createNewFile();
                    if (!isFileCreated) {
                        throw new IOException("Unable to create file at specified path. It already exists");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                initFile(VIDEO_PATH + File.separator + "playlist.txt", 0);
            }
        }
        if( !resetPlaylistAfterTimespan.equals("")) {
            long timespan = Long.parseLong(resetPlaylistAfterTimespan);
            long deletion = System.currentTimeMillis() - (timespan * 24 * 60 * 60 * 1000);
            if (f.lastModified() < deletion) {
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
        if(!isNoShuffle) {
            Collections.shuffle(mp3s);
        }
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
    }    private static int pos;

    static {
       if (list != null) {
           pos = Integer.parseInt(list.get(list.size() - 1));
       } else {
           ErrorLogger.writeError("NullExceptionError while reading playlist");
       }
    }

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
        this.setBounds(0, 360, 640, 180);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // to remove border -> this.setUndecorated(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
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
                b_reset.setText(pos + "/" + list.size());
                setContentPane(songs.get(pos));
                printInfo();
            }
        };
        JPanel contentPane = new JPanel();
        b_reset.setText(pos + "/" + list.size());
        b_reset.setBorderPainted(false);
        b_reset.setOpaque(true);
        b_reset.setFont(new Font(font.getName(), font.getStyle(), font.getSize() - 12));
        b_reset.setBackground(new Color(26,26,26));
        b_reset.setForeground(Color.white);
        b_reset.setBounds(2, 117, 120, 20);
        b_reset.addActionListener(this::resetFile);
        b_reset.setToolTipText("Reset Playlist");
        l_nextSong.setForeground(Color.white);
        l_nextSong.setOpaque(true);
        l_nextSong.setBackground(new Color(26,26,26));
        l_nextSong.setBounds(0, 0, 640, 110);
        contentPane.add(b_reset);
        contentPane.add(l_nextSong);
        l_nextSong.setFont(font);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);
        JPanel controlsPane = new JPanel();
        controlsPane.setBackground(new Color(26,26,26));
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
        rewindButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().skipTime(-80000));
        skipButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().skipTime(80000));
        changeFont(contentPane, font);
        printInfo();
        this.setContentPane(contentPane);
        this.setVisible(true);
        this.loadVideo(path);
    }

    private void resetFile(ActionEvent actionEvent) {
        initFile(VIDEO_PATH + File.separator + "playlist.txt", 0);
    }

    private void printInfo() {
        if(songs.size()-2 > pos) {
            try {
                l_nextSong.setText("<html><body style=\\\"padding-left:10px;margin-bottom:20px;\\\"> <b><br>" +
                        setSongTitle(songs.get(pos)) +
                        "</b><br><br>" +
                        "+ " + setSongTitle(songs.get(pos+1)) +
                        "<br>" +
                        "+ " + setSongTitle(songs.get(pos + 2)) +
                        "</body></html>");
            } catch (Exception e) {
                l_nextSong.setText("Shuffling");
            }
        } else {
            l_nextSong.setText("Shuffling");
        }

    }

    public static void changeFont ( Component component, Font font )
    {
        component.setFont ( font );
        if ( component instanceof Container )
        {
            for ( Component child : ( ( Container ) component ).getComponents () )
            {
                changeFont ( child, font );
            }
        }
    }

    public void loadVideo(String path) {
        mediaPlayerComponent.mediaPlayer().media().start(path);
    }

    private static List<String> songs = getMp3Files(list);

}
