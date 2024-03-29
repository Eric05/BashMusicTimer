package at.eric.gui;

import at.eric.application.App;
import at.eric.application.FileDialog;
import at.eric.application.FileOperation;
import at.eric.application.Storage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsGui extends JFrame {

    private final List<String> settings;
    private final FileOperation fo = new FileOperation("Settings.txt");
    JLabel l_playlist, l;
    JTextField tf_playlist, tf;
    JButton b_browse, b_save;
    JComboBox<String> combo = new JComboBox<>();
    Font font = MainGui.getCustomFont();

    public SettingsGui() {
        super("Music Player");
        settings = fo.getSettings();
        createGui();
    }

    private void createGui() {
        l_playlist = new JLabel("playlist");
        l_playlist.setBounds(20, 50, 150, 20);
        tf_playlist = new JTextField(Storage.getValueByKey("playlist", settings));
        tf_playlist.setBounds(150, 50, 170, 20);
        tf_playlist.setFont(font);
        l_playlist.setForeground(Color.green);
        b_browse = new JButton(" ...");
        b_browse.setBounds(320, 50, 30, 20);
        b_browse.addActionListener(this::changePlaylist);

        b_save = new JButton("SAVE");
        b_save.setBounds(150, 440, 100, 30);
        b_save.setHorizontalAlignment(SwingConstants.CENTER);
        b_save.setHorizontalTextPosition(SwingConstants.CENTER);
        b_save.addActionListener(this::saveSettings);

        createTable();
        add(b_browse);
        add(l_playlist);
        add(tf_playlist);
        add(b_save);

        combo.addItem("list");
        combo.addItem("browse");
        combo.addItem("off");
        combo.addItem("shuffle");
        combo.setSelectedItem(Storage.getValueByKey("mode", settings));
        combo.setEditable(true);
        combo.setBounds(10, 450, 100, 20);
        add(combo);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        getContentPane().setBackground(new Color(26, 26, 26));
        pack();
        setSize(400, 520);
        App.changeFont(combo, font);
        setLayout(null);
        setVisible(true);
    }

    private void createTable( int ... maxRows) {
        int counter = 0;
        String key;
        String val;
        int rows = 0;
        int posY = 80;
        if(maxRows.length <= 0){
           rows = 8;
        }
        for (String setting : settings) {
           counter++;
            if (setting.length() < 3) {
                continue;
            }
            if (!setting.contains(";") && !setting.contains(",")) {
                key = setting;
                val = "";
            } else {
                var arr = setting.split("[;|,]");
                key = arr[0];
                if (arr.length < 2) {
                    val = "";
                } else {
                    val = setting.split(";")[1];
                }
            }
            if (key.contains("play")) {
                continue;
            }

            l = new JLabel(key);
            l.setBounds(20, posY, 150, 20);
            tf = new JTextField(val);
            tf.setBounds(150, posY, 200, 20);
            tf.setFont(font);
            l.setForeground(Color.white);
            add(l);
            add(tf);
            if ( counter < rows ){
                posY+= 30;
            } else {
                posY += 15;
            }
        }
    }

    private void saveSettings(ActionEvent e) {
        var settings = createSettingsList();
        fo.writeSettings(settings);
        // close
        this.dispose();
    }

    private void changePlaylist(ActionEvent e) {
        var root = Storage.getValueByKey("root", settings);
        var f = new File(root);
        if (!f.exists()) {
            f = new File(System.getProperty("user.dir"));
        }
        var fd = new FileDialog();
        var path = fd.getFileName(String.valueOf(f));
        if (path != null)
            tf_playlist.setText(String.valueOf(path));
    }

    private List<String> createSettingsList() {
        List<String> settings = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        Component[] components = this.getContentPane().getComponents();
        for (Component c : components) {
            if (c instanceof JLabel) {
                sb.append(((JLabel) c).getText());
            }
            if (c instanceof JTextField) {
                if (((JTextField) c).getText().startsWith("list") ||
                        ((JTextField) c).getText().startsWith("browse") ||
                        ((JTextField) c).getText().startsWith("off") ||
                        ((JTextField) c).getText().startsWith("shuffle")) {
                    sb.append(";").append(combo.getSelectedItem());
                } else {
                    sb.append(";").append(((JTextField) c).getText());
                }
                settings.add(sb.toString());
                sb.setLength(0);
            }
        }
        return settings;
    }
}



