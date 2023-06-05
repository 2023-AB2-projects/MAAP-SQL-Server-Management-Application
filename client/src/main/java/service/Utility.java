package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Utility {
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

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

    public static ImageIcon resizeIcon(ImageIcon originalIcon, int desiredWidth, int desiredHeight) {
        Image originalImage = originalIcon.getImage();          // Get the original image from the icon

        // Resize the image
        Image resizedImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);

        // Create a new ImageIcon with the resized image
        return new ImageIcon(resizedImage);
    }
}