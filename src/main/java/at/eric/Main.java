package at.eric;

import at.eric.gui.MainGui;
import javax.swing.*;

class Main {

    public static void main(String[] args) {

        var outputArea = new JEditorPane();
        var font = outputArea.getFont();
        UIManager.put("TextField.font", font);

        new MainGui();

    }
    // Font font = new Font (fontFamily.getName(), Font.PLAIN, 12);
}




