package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.service.CatalogManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class RecordFinder {
    private RecordHandler recordHandler;
    private ArrayList<Integer> keyColumnIndexes;
    private ArrayList<String> keyColumnTypes;
    public RecordFinder(String databaseName, String tableName) throws FileNotFoundException {
        recordHandler = new RecordHandler(databaseName, tableName);
        keyColumnIndexes = (ArrayList<Integer>) CatalogManager.getPrimaryKeyFieldIndexes(databaseName, tableName);
        keyColumnTypes = (ArrayList<String>) CatalogManager.getPrimaryKeyTypes(databaseName, tableName);
    }
    public int findByPrimaryKey(ArrayList<String> keyValues) throws RecordNotFoundException, IOException {
        // check if index exists on primaryKey
        // if it does find record via index
        // else
        return sequentialFindByPrimaryKey(keyValues);
    }
    private int sequentialFindByPrimaryKey(ArrayList<String> keyValues) throws RecordNotFoundException, IOException {
        int line = -1;

        for(int i = 0; i < recordHandler.getRecordCount(); i++){
            ArrayList<String> record;
            try {
                record = recordHandler.readLine(i);
                if(matchesPrimaryKey(record, keyValues)){
                    line = i;
                    break;
                }
            }catch (InvalidReadException ignored) {}
        }

        if(line == -1){
            throw new RecordNotFoundException();
        }else{
            return line;
        }
    }
    private boolean matchesPrimaryKey(ArrayList<String> record, ArrayList<String> keyValues){
        ArrayList<String> standardizedKeyValues = RecordStandardizer.standardizeValues(keyValues, keyColumnTypes);
        for(int i = 0; i < standardizedKeyValues.size(); i++){
            if(!standardizedKeyValues.get(i).equals(record.get(keyColumnIndexes.get(i)))){
                return false;
            }
        }
        return true;
    }

    public void close() throws IOException {
        recordHandler.close();
    }
}