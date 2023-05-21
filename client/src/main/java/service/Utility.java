package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;

public class Utility {
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static ObjectMapper getObjectMapper() { return objectMapper; }

    /* Useful functions */
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] directoryFiles = directoryToBeDeleted.listFiles();
        if (directoryFiles != null) {
            for (final File file : directoryFiles) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void setJTextPaneFontColor(JTextPane textPane, Color color) {
        // Get the text pane's document
        StyledDocument doc = textPane.getStyledDocument();

        // Create a Style object
        Style style = textPane.addStyle("RedColorStyle", null);

        // Set the foreground color of the Style to red
        StyleConstants.setForeground(style, color);

        // Apply the Style to the entire document
        doc.setCharacterAttributes(0, doc.getLength(), style, false);
    }
}
