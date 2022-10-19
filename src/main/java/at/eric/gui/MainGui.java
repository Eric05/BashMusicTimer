package at.eric.gui;

import at.eric.application.FileDialog;
import at.eric.application.*;
import at.eric.operationSystem.OsCommands;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static javax.swing.JOptionPane.showMessageDialog;

public class MainGui extends JFrame {

    public static String playlist;
    private final List<String> commands = OsCommands.getOsCommands();   
    Font font = getCustomFont();
    JLabel l_playlist, l_timer, l_inc;
    JTextField tf_playlist, tf_timer;
    JButton b_playlist, b_timer, b_settings, b_browse, b_stop, b_start;
    private List<String> settings = new FileOperation("Settings.txt").getSettings();
    private int time;
    private String root;
    private int increment;
    private int warning;
    private boolean isScreenOff = false;
    private int delay = 1;


    public MainGui() {
        super("Music Player");

        UIManager.put("Label.font", font);
        if (!Storage.getValueByKey("batteryLimit", settings).isBlank() &&
                parseInt(Storage.getValueByKey("batteryLimit", settings)) > 0) {
            checkBattery();
        }
        if (!Storage.getValueByKey("bluetooth", commands).isEmpty()) {
            connectBluetooth();
        }

        if (!Storage.getValueByKey("delay", commands).isBlank()) {
            delay = Integer.parseInt(Storage.getValueByKey("delay", commands));
        }

        Thread t1 = new Thread(() -> {
            Sleep.delaySeconds(delay);
            createGui();
            init();
            timer();
        });

        Thread t2 = new Thread(this::screenOff);
        Thread t3 = new Thread(this::setMode);
        t3.start();
        t2.start();
        t1.start();

    }

    public static void changeFont(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                changeFont(child, font);
            }
        }
    }

    public void createGui() {
        String currentWorkingDir = FileOperation.getCurrentWorkingDir();
        var pathToPicture = new File(currentWorkingDir + File.separator + "mic.jpg");
        if (pathToPicture.exists()) {
            try {
                final Image backgroundImage = javax.imageio.ImageIO.read(pathToPicture);
                setContentPane(new JPanel(new BorderLayout()) {
                    @Override
                    public void paintComponent(Graphics g) {
                        g.drawImage(backgroundImage, 0, 0, null);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            getContentPane().setBackground(new Color(26, 26, 26));
        }

        l_playlist = new JLabel("PLAYLIST:");
        l_playlist.setBounds(10, 30, 100, 20);

        tf_playlist = new JTextField();
        tf_playlist.setBounds(120, 28, 250, 25);
        tf_playlist.setBackground(Color.darkGray);
        tf_playlist.setForeground(Color.white);
        tf_playlist.setFont(new Font(font.getFontName(), Font.PLAIN, 16));
        tf_playlist.setFont(font);
        l_playlist.setForeground(Color.green);

        b_browse = new JButton(" ... ");
        b_browse.setBounds(370, 28, 100, 25);
        b_browse.addActionListener(this::changePlaylist);
        b_browse.setToolTipText("Change Playlist temporarily");

        l_timer = new JLabel("SLEEP IN:");
        l_timer.setBounds(10, 60, 100, 25);
        l_timer.setForeground(Color.white);

        b_stop = new JButton("x");
        b_stop.setBounds(370, 60, 50, 25);
        b_stop.addActionListener(this::stopTimer);
        b_start = new JButton(">");
        b_start.setBounds(420, 60, 50, 25);
        b_start.addActionListener(this::startTimer);
        b_start.setToolTipText("Start Timer");
        b_stop.setToolTipText("Stop Timer");

        tf_timer = new JTextField();
        tf_timer.setBounds(120, 60, 250, 25);
        tf_timer.setBackground(Color.darkGray);
        tf_timer.setForeground(Color.white);
        tf_timer.setFont(font);

        l_inc = new JLabel("...");
        l_inc.setBounds(10, 90, 200, 20);
        l_inc.setForeground(Color.white);

        b_playlist = new JButton("Update");
        b_playlist.setBounds(350, 260, 120, 30);
        b_playlist.addActionListener(this::update);

        b_settings = new JButton("Change");
        b_settings.setBounds(230, 260, 120, 30);
        b_settings.addActionListener(this::changeSettings);

        b_timer = new JButton("Increase");
        b_timer.setBounds(10, 260, 120, 30);
        b_timer.addActionListener(this::increaseTimer);

        add(l_playlist);
        add(tf_playlist);
        add(b_browse);
        add(l_inc);
        add(b_playlist);
        add(b_timer);
        add(b_stop);
        add(b_start);
        add(b_settings);
        add(l_timer);
        add(tf_timer);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getRootPane().setDefaultButton(b_timer);
        setSize(640, 360);
        changeFont(this, font);
        setLayout(null);
        setVisible(true);
    }

    private void update(ActionEvent actionEvent) {
        settings = new FileOperation("Settings.txt").getSettings();
        Thread t = new Thread(this::setMode);
        Thread t2 = new Thread(this::screenOff);
        OsCommands.doCommand("mediaOff", "");
        Sleep.delaySeconds(delay);
        init();
        t.start();
        Sleep.delaySeconds(delay);
        this.toFront();
        this.requestFocus();
        t2.start();
    }

    public void init() {
        playlist = Storage.getValueByKey("playlist", settings);
        setPlaylist(playlist);
        time = parseInt(Storage.getValueByKey("timer", settings));
        if (time < 3) {
            time = -1;
        }
        setTime(time);
        increment = parseInt(Storage.getValueByKey("increment", settings));
        setIncrement(increment);
        root = Storage.getValueByKey("root", settings);
        warning = parseInt(Storage.getValueByKey("warning", settings));
    }

    private void connectBluetooth() {
        if (!Storage.getValueByKey("bluetooth", settings).isEmpty()) {
            OsCommands.doCommand("bluetooth", "");
        }
    }

    private void checkBattery() {
        if (!Storage.getValueByKey("batteryLimit", settings).isEmpty()) {
            int actual = OsCommands.getBatteryStatus();
            if (actual < Integer.parseInt(Storage.getValueByKey("batteryLimit", settings))) {
                acoustic(2);
                showMessageDialog(null, "Low Battery: " + actual);
            }
        }
    }

    private void setMode() {

        String mode = Storage.getValueByKey("mode", settings);
        if (mode.startsWith("off")) {
            return;
        }
        if (mode.startsWith("browse")) {
            OsCommands.doCommand(Storage.getValueByKey("browser", settings));
        } else if (mode.startsWith("shuffle")) {
            App.main();
        } else {
            String path = Storage.getValueByKey("playlist", settings);
            File f = new File(path);
            if (!f.exists()) {
                showMessageDialog(null, "Please enter valid Path to playlist");
            } else {
                OsCommands.doCommand("mediaOn", path);
            }
        }
    }

    public void timer() {

        while (parseInt(tf_timer.getText()) >= 0) {
            if (parseInt(tf_timer.getText()) == 0) {
                OsCommands.doCommand(Storage.getValueByKey("shutdown", commands));
            }
            if (parseInt(tf_timer.getText()) == warning) {
                Thread t1 = new Thread(this::warning);
                t1.start();
            }
            if (parseInt(tf_timer.getText()) == 1) {
                String mode = Storage.getValueByKey("mode", settings);
                if (mode.startsWith("shuffle")) {
                    App.writeFile(playlist + File.separator + "playlist.txt");
                }
            }
            Sleep.delayMinutes(1);
            int time = parseInt(tf_timer.getText());
            time--;
            setTime(time);
        }
    }

    private void screenOff() {
        Thread t = new Thread(() -> {
            int screenOffDelay = parseInt(Storage.getValueByKey("screenOffDelay", settings));
            Sleep.delaySeconds(screenOffDelay);
            if (screenOffDelay > 0) {
                OsCommands.doCommand("screenOff", "");
                isScreenOff = true;
            }
        });
        t.start();
    }

    private void screenOn() {
        if (isScreenOff) {
            Thread t = new Thread(() -> OsCommands.doCommand("screenOn", ""));
            t.start();
        }
    }

    public void stopTimer(ActionEvent e) {
        setTime(parseInt("-1"));
    }

    public void startTimer(ActionEvent e) {
        Thread t = new Thread(this::timer);
        t.start();
    }

    public void changeSettings(ActionEvent e) {
        new SettingsGui();
    }

    public void increaseTimer(ActionEvent e) {
        Thread t = new Thread(this::screenOff);
        int actualTime = parseInt(tf_timer.getText());
        setTime(actualTime + increment);
        t.start();
    }

    public void increaseTimer() {
        Thread t = new Thread(this::screenOff);
        int actualTime = parseInt(tf_timer.getText());
        setTime(actualTime + increment);
        t.start();
    }

    public void changePlaylist(ActionEvent e) {
        var fd = new FileDialog().getFileName(root);
        if (fd != null) {
            Thread t = new Thread(this::screenOff);
            setPlaylist(String.valueOf(fd));
            OsCommands.doCommand("mediaOff", "");
            Sleep.delaySeconds(delay);
            OsCommands.doCommand("mediaOn", playlist);
            t.start();
        }
    }

    public void setIncrement(int increment) {
        this.increment = increment;
        l_inc.setText("Increase:         " + increment + " min");
    }

    public void setTime(int time) {
        this.time = time;
        tf_timer.setText(String.valueOf(time));
    }

    public void setPlaylist(String playlist) {
        MainGui.playlist = playlist;
        String clean = playlist.replaceFirst("[.][^.]+$", "");
        File path = new File(clean);
        tf_playlist.setText(" " + path.getName());
    }

    public void warning() {
        screenOn();
        acoustic(parseInt(Storage.getValueByKey("warnReps", settings)));
        showMessageDialog(null, "Sleep in " + warning + " minutes" + System.getProperty("line.separator") + "Press OK to increase time");
        increaseTimer();
    }

    public void acoustic(int reps) {
        if (!Storage.getValueByKey("acoustic", commands).isEmpty()) {
            for (int i = 0; i < reps; i++) {
                Sleep.delaySeconds(2);
                try {
                    OsCommands.doCommand("acoustic", "");
                } catch (Exception ignored) {

                }
            }
        }
    }

    // UIManager.put("TextField.font", font);
    public static Font getCustomFont() {
        var outputArea = new JEditorPane();
        var font = outputArea.getFont();
        File font_file = new File("Audiowide-Regular.ttf");
        if (font_file.exists()) {
            try {
                Font thefont = Font.createFont(Font.TRUETYPE_FONT, font_file);
                font = thefont.deriveFont(14f);
            } catch (FontFormatException | IOException e) {
                ErrorLogger.writeError("Error when loading font");
            }
        } else {
            font = font.deriveFont(14f);
        }
        return font;
    }

    private int parseInt(String str) {
        int num;
        try {
            num = Integer.parseInt(str);
        } catch (Exception e) {
            num = 0;
        }
        return num;
    }

}










