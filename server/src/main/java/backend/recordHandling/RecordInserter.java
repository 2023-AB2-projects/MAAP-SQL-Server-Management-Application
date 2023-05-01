package backend.recordHandling;

import backend.Indexing.MultipleIndexUpdater;
import backend.exceptions.recordHandlingExceptions.DeletedRecordLinesEmpty;
import backend.exceptions.recordHandlingExceptions.KeyAlreadyInTreeException;
import backend.service.CatalogManager;

import java.io.IOException;
import java.util.ArrayList;

public class RecordInserter {
    private final RecordHandler recordHandler;
    private final MultipleIndexUpdater multipleIndexUpdater;
    private String databaseName, tableName;

    public RecordInserter(String databaseName, String tableName) throws IOException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        recordHandler = new RecordHandler(databaseName, tableName);
        multipleIndexUpdater = new MultipleIndexUpdater(databaseName, tableName);
    }

    public void insert(ArrayList<String> values) throws IOException {
        Integer pointer;
        try{
            pointer = CatalogManager.deletedRecordLinesPop(databaseName, tableName);
        }catch (Exception e){
            pointer = (int)recordHandler.getRecordCount();
        }

        recordHandler.insert(values, pointer);
        multipleIndexUpdater.insert(values, pointer);
    }

    public void close() throws IOException {
        recordHandler.close();
    }
}
