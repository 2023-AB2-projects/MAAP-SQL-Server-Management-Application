package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class RecordDeleter {
    private RecordHandler recordHandler;
    private RecordFinder recordFinder;
    public RecordDeleter(String databaseName, String tableName) throws FileNotFoundException {
        recordHandler = new RecordHandler(databaseName, tableName);
        recordFinder = new RecordFinder(databaseName, tableName);
    }

    public void deleteByPrimaryKey(ArrayList<String> keyValues) throws RecordNotFoundException, IOException {
        int line = recordFinder.findByPrimaryKey(keyValues);
        recordHandler.deleteLine(line);

        // addLineToDeletedLines(line)
    }

    public void close() throws IOException {
        recordFinder.close();
        recordHandler.close();
    }
}
