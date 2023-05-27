package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.service.CatalogManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class RecordReader {
    private RecordHandler recordHandler;
    private final String databaseName, tableName;

    public RecordReader(String databaseName, String tableName) throws FileNotFoundException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        recordHandler = new RecordHandler(databaseName, tableName);
    }

    public ArrayList<ArrayList<Object>> scan() throws IOException {
        ArrayList<ArrayList<Object>> table = new ArrayList<>();
        for(int i = 0; i < recordHandler.getRecordCount(); i++){
            try{
                table.add(recordHandler.readLineAsObjectList(i));
            }catch (InvalidReadException ignored){}
        }
        return table;
    }

    public HashSet<Integer> getAllPointers() throws IOException {
        HashSet<Integer> pointers = new HashSet<>();
        for (int i = 0; i < recordHandler.getRecordCount(); i++){
            pointers.add(i);
        }
        ArrayDeque<Integer> deletedLines = CatalogManager.deletedRecordLinesQueue(databaseName, tableName);
        pointers.removeAll(deletedLines);
        return pointers;
    }

    public ArrayList<ArrayList<Object>> scan(ArrayList<String> columnNames) throws IOException {
        List<String> allColumnNames = CatalogManager.getFieldNames(databaseName, tableName);

        ArrayList<Integer> columns = new ArrayList<>();
        for(String columnName : columnNames){
            columns.add(allColumnNames.indexOf(columnName));
        }

        ArrayList<ArrayList<Object>> table = new ArrayList<>();
        for(int i = 0; i < recordHandler.getRecordCount(); i++){
            try{
                ArrayList<Object> record = recordHandler.readLineAsObjectList(i);
                ArrayList<Object> partialRecord = new ArrayList<>();
                for(int column : columns){
                    partialRecord.add(record.get(column));
                }
                table.add(partialRecord);
            }catch (InvalidReadException ignored){}
        }
        return table;
    }

    public ArrayList<ArrayList<Object>> scanLines(ArrayList<Integer> pointers) throws IOException {
        ArrayList<ArrayList<Object>> table = new ArrayList<>();
        for(int i : pointers){
            try {
                table.add(recordHandler.readLineAsObjectList(i));
            } catch (InvalidReadException ignored){}
        }
        return table;
    }

    public ArrayList<ArrayList<Object>> scanLines(ArrayList<Integer> pointers, ArrayList<String> columnNames) throws IOException {
        List<String> allColumnNames = CatalogManager.getFieldNames(databaseName, tableName);

        ArrayList<Integer> columns = new ArrayList<>();
        for(String columnName : columnNames){
            columns.add(allColumnNames.indexOf(columnName));
        }

        ArrayList<ArrayList<Object>> table = new ArrayList<>();
        for(int i : pointers){
            try {
                ArrayList<Object> record = recordHandler.readLineAsObjectList(i);
                ArrayList<Object> partialRecord = new ArrayList<>();
                for(int column : columns){
                    partialRecord.add(record.get(column));
                }
                table.add(partialRecord);
            } catch (InvalidReadException ignored){}
        }
        return table;
    }

    public void close() throws IOException {
        recordHandler.close();
    }
}
