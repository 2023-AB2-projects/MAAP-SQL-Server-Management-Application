package backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
}
