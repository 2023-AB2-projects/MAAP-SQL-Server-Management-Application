package frontend;

import javax.swing.*;
import java.awt.*;

public class OutputTextArea extends JTextArea {
    public OutputTextArea(String defaultString) {
        // Set default text
        this.setText(defaultString);

        // Text Area settings
        this.textAreaSettings();
    }

    private void textAreaSettings() {
        // Border settings
        this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 5));

        // Output area is read-only
        this.setEditable(false);

        // Font
        Font newFont = this.getFont().deriveFont(Font.PLAIN, 20);
        this.setFont(newFont);
    }
}
