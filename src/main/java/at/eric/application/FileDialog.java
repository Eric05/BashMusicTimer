package at.eric.application;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileDialog extends Component {

    public File getFileName(String root) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(root));
        fileChooser.setFileSelectionMode(2);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

}



