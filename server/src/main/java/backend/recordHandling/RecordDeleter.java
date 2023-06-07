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
    private final RecordHandler recordHandler;
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

    public void deleteRecords(ArrayList<Integer> pointers) {
        CatalogManager.deletedRecordLinesEnqueueN(databaseName, tableName, pointers);
        for(var pointer : pointers){
            try{
                ArrayList<String> record = recordHandler.readLine(pointer);

                recordHandler.deleteLine(pointer);
                multipleIndexUpdater.delete(record, pointer);
            }catch (Exception ignored){}
        }
    }

    public void deleteByPrimaryKey(ArrayList<String> keyValues){
        try{
            int pointer = primaryKeyIndexManager.findLocation(keyValues);
            ArrayList<String> record = recordHandler.readLine(pointer);

            CatalogManager.deletedRecordLinesEnqueue(databaseName, tableName, pointer);
            recordHandler.deleteLine(pointer);
            multipleIndexUpdater.delete(record, pointer);
        }catch (Exception ignored){}
    }

    public void close() throws IOException {
        multipleIndexUpdater.close();
        primaryKeyIndexManager.close();
        recordHandler.close();
    }
}
