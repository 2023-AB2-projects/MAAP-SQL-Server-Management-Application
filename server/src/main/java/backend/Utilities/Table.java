package backend.Utilities;

import backend.Indexing.UniqueIndexManager;
import backend.databaseModels.conditions.Condition;
import backend.databaseModels.conditions.Equation;
import backend.recordHandling.RecordReader;
import backend.service.CatalogManager;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Table {
    @Getter
    private ArrayList<String> columnTypes, columnNames;
    private final String databaseName, tableName;

    private ArrayList<ArrayList<Object>> tableContent;

    public Table(String databaseName, String tableName, ArrayList<Condition> conditions) throws IOException {
        this.columnTypes = (ArrayList<String>) CatalogManager.getFieldTypes(databaseName, tableName);
        this.columnNames = (ArrayList<String>) CatalogManager.getFieldNames(databaseName, tableName);
        this.databaseName = databaseName;
        this.tableName = tableName;

        //use the indexes where able
        HashSet<Integer> wantedRecordPointers;
        RecordReader io = new RecordReader(databaseName, tableName);


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
