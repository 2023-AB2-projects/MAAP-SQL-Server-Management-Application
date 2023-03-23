package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class InputTextArea extends JTextArea implements FocusListener {
    private String defaultString;
    public InputTextArea(String defaultString) {
        // Set default text
        this.defaultString = defaultString;
        this.setText(this.defaultString);
        this.setFocusable(true);

        // TextArea settings
        this.textAreaSettings();

        // Add focus listener
        this.addListeners();
    }

    private void textAreaSettings() {
        // Border
        this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 5));

        // Font
        Font newFont = this.getFont().deriveFont(Font.PLAIN, 20);
        this.setFont(newFont);
    }

    private void addListeners() {
        this.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent event) {
        // Set text equal to "" if default text is present
        if (this.getText().equals(this.defaultString)) {
            this.setText("");
        }
    }

    @Override
    public void focusLost(FocusEvent event) {
        // Set text equal to default text if text area is empty
        if (this.getText().equals("")) {
            this.setText(this.defaultString);
        }
    }

    /* Setters */
    public void setInputTextAreaString(String string) { this.setText(string);}
}
