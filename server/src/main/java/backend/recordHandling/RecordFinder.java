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
        keyColumnIndexes = new ArrayList<>();
        keyColumnTypes = new ArrayList<>();
        ArrayList<String> keyColumnNames, columnNames;
        keyColumnNames = (ArrayList<String>) CatalogManager.getPrimaryKeys(databaseName, tableName);
        columnNames = (ArrayList<String>) CatalogManager.getColumnNames(databaseName, tableName);



        // remove later
        keyColumnIndexes.add(0);
        keyColumnIndexes.add(3);
        keyColumnTypes.add("int");
        keyColumnTypes.add("char(10)");

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
