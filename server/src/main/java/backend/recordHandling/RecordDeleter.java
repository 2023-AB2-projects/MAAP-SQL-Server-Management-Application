package backend.recordHandling;

import backend.Indexing.MultipleIndexUpdater;
import backend.Indexing.UniqueIndexManager;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class RecordDeleter {
    private RecordHandler recordHandler;
    private final String databaseName, tableName;
    private final MultipleIndexUpdater multipleIndexUpdater;

    private final UniqueIndexManager primaryKeyIndexManager;
    public RecordDeleter(String databaseName, String tableName) throws FileNotFoundException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        recordHandler = new RecordHandler(databaseName, tableName);
        multipleIndexUpdater = new MultipleIndexUpdater(databaseName, tableName);
        primaryKeyIndexManager = new UniqueIndexManager(databaseName, tableName, CatalogManager.getPrimaryKeyIndexName(databaseName, tableName));
    }

    public ArrayList<String> readLineToBeDeleted(ArrayList<String> keyValues) throws IOException, KeyNotFoundException, InvalidReadException {
        int line = primaryKeyIndexManager.findLocation(keyValues);
        return recordHandler.readLine(line);
    }

    public void deleteByPrimaryKey(ArrayList<String> keyValues){
        try{
            int line = primaryKeyIndexManager.findLocation(keyValues);
            ArrayList<String> row = recordHandler.readLine(line);

            CatalogManager.deletedRecordLinesEnqueue(databaseName, tableName, line);
            recordHandler.deleteLine(line);
            multipleIndexUpdater.delete(row, line);
        }catch (Exception ignored){}
    }

    public void close() throws IOException {
        multipleIndexUpdater.close();
        primaryKeyIndexManager.close();
        recordHandler.close();
    }
}
