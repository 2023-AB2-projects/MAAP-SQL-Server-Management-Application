package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.service.CatalogManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class RecordDeleter {
    private RecordHandler recordHandler;
    private RecordFinder recordFinder;
    private final String databaseName, tableName;
    public RecordDeleter(String databaseName, String tableName) throws FileNotFoundException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        recordHandler = new RecordHandler(databaseName, tableName);
        recordFinder = new RecordFinder(databaseName, tableName);
    }

    public void deleteByPrimaryKey(ArrayList<String> keyValues) throws RecordNotFoundException, IOException {
        int line = recordFinder.findByPrimaryKey(keyValues);
        recordHandler.deleteLine(line);

        CatalogManager.deletedRecordLinesEnqueue(databaseName, tableName, line);

        // remove entry from index file
    }

    public void close() throws IOException {
        recordFinder.close();
        recordHandler.close();
    }
}
