package backend.recordHandling;

import java.io.IOException;
import java.util.ArrayList;

public class RecordInserter {
    private final RecordHandler recordHandler;
    private String databaseName, tableName;

    public RecordInserter(String databaseName, String tableName) throws IOException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        recordHandler = new RecordHandler(databaseName, tableName);
    }

    public void insert(ArrayList<String> values) throws IOException {
//        ArrayList<Integer> emptyLines = getEmptyLines(databaseName, tableName);
//        if(emptyLines.size() != 0){
//            recordHandler.insert(values, emptyLines.get(0));
//            removeEmptyLine(databaseName, tableName, emptyLines.ger(0));
//            // append index file later
//            return;
//        }
        // insert at the end of the file
        recordHandler.insert(values, (int)recordHandler.getRecordCount());
        // append index file later

    }

    public void close() throws IOException {
        recordHandler.close();
    }
}
