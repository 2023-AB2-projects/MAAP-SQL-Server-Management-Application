package backend.Utilities;

import backend.Indexing.UniqueIndexManager;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.UndefinedQueryException;
import backend.recordHandling.RecordReader;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table {
    @Getter
    private ArrayList<String> columnTypes, columnNames;
    private final String databaseName;

    private ArrayList<ArrayList<Object>> tableContent;

    public Table(String databaseName, ArrayList<String> columnTypes, ArrayList<String> columnNames, ArrayList<ArrayList<Object>> tableContent) {
        this.databaseName = databaseName;
        this.columnTypes = columnTypes;
        this.columnNames = new ArrayList<>();
        this.tableContent = tableContent;
    }

    public Table(String databaseName, String tableName, ArrayList<ArrayList<Object>> tableContent) {
        this.columnTypes = (ArrayList<String>) CatalogManager.getFieldTypes(databaseName, tableName);
        this.columnNames = (ArrayList<String>) CatalogManager.getFieldNames(databaseName, tableName);
        this.tableContent = tableContent;
        this.databaseName = databaseName;
    }

    public void projection(ArrayList<String> wantedColumnNames){
        for(var columnName : columnNames){
            if(!wantedColumnNames.contains(columnName)){
                int i = columnNames.indexOf(columnName);
                columnTypes.remove(i);
                for(var record : tableContent){
                    record.remove(i);
                }
                columnNames.remove(columnName);
            }
        }
    }

    //TODO:
    // public void sequentialSelection(CONDITIONS for columns that don't have an index)

    public void join(String childColumnName, String tableName) throws IOException {
        // CHECK if table given as parameter is the ParentTable of this Table
        int foreignKeyColumnIndex = columnNames.indexOf(childColumnName);

        ArrayList<String> parentColumnNames = (ArrayList<String>) CatalogManager.getFieldNames(databaseName, tableName);
        columnNames.addAll(parentColumnNames);
        columnTypes.addAll(CatalogManager.getFieldTypes(databaseName, tableName));

        String primaryKeyIndexName = CatalogManager.getPrimaryKeyIndexName(databaseName, tableName);
        UniqueIndexManager indexManager = new UniqueIndexManager(databaseName, tableName, primaryKeyIndexName);
        ArrayList<Integer> pointers = new ArrayList<>();
        for(var record : tableContent){
            Object foreignKey = record.get(foreignKeyColumnIndex);

            try {
                HashMap<Integer, Object> map = indexManager.equalityQuery(foreignKey);
                pointers.addAll(map.keySet());
            } catch (Exception ignored) {}
        }
        indexManager.close();

        RecordReader recordReader = new RecordReader(databaseName, tableName);

        try{
            ArrayList<ArrayList<Object>> records = recordReader.scanLines(pointers);

            for(int i = 0; i < tableContent.size(); i++){
                tableContent.get(i).addAll(records.get(i));
            }
        } catch (Exception ignored) {}

        recordReader.close();
    }

    public void join(String childColumnName, String tableName, ArrayList<String> wantedColumNames) throws IOException {
        int foreignKeyColumnIndex = columnNames.indexOf(childColumnName);

        ArrayList<String> parentColumnNames = (ArrayList<String>) CatalogManager.getFieldNames(databaseName, tableName);
        columnNames.addAll(parentColumnNames);
        columnTypes.addAll(CatalogManager.getFieldTypes(databaseName, tableName));

        String primaryKeyIndexName = CatalogManager.getPrimaryKeyIndexName(databaseName, tableName);
        UniqueIndexManager indexManager = new UniqueIndexManager(databaseName, tableName, primaryKeyIndexName);
        ArrayList<Integer> pointers = new ArrayList<>();
        for(var record : tableContent){
            Object foreignKey = record.get(foreignKeyColumnIndex);

            try {
                HashMap<Integer, Object> map = indexManager.equalityQuery(foreignKey);
                pointers.addAll(map.keySet());
            } catch (Exception ignored) {}
        }
        indexManager.close();

        RecordReader recordReader = new RecordReader(databaseName, tableName);

        try{
            ArrayList<ArrayList<Object>> records = recordReader.scanLines(pointers, wantedColumNames);

            for(int i = 0; i < tableContent.size(); i++){
                tableContent.get(i).addAll(records.get(i));
            }
        } catch (Exception ignored) {}

        recordReader.close();
    }

    public void printState(){
        System.out.println(columnNames);
        System.out.println(columnTypes);

        for(var record : tableContent) {
            System.out.println(record);
        }
    }
}
