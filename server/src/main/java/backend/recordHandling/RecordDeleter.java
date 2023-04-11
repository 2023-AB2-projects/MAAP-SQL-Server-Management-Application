package backend.recordHandling;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RecordDeleter {
    private RecordHandler recordHandler;
    public RecordDeleter(String databaseName, String tableName) throws FileNotFoundException {
        recordHandler = new RecordHandler(databaseName, tableName);
    }

    public void deleteByPrimaryKey(ArrayList<String> keyValues){

    }
}
