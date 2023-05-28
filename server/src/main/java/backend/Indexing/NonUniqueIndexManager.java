package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.KeyAlreadyInTreeException;
import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.exceptions.recordHandlingExceptions.UndefinedQueryException;
import backend.recordHandling.RecordReader;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
public class NonUniqueIndexManager implements Queryable{
    private final ArrayList<String> keyStructure;
    private final BPlusTree bPlusTree;

    private final String databaseName, tableName;
    public NonUniqueIndexManager(String databaseName, String tableName, String indexName) {
        this.databaseName = databaseName;
        this.tableName = tableName;

        // Index field types and file name
        keyStructure = (ArrayList<String>) CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
        keyStructure.add("int");
        String filename = CatalogManager.getTableIndexFilePath(databaseName, tableName, indexName);

        try {
            bPlusTree = new BPlusTree(keyStructure, filename);
        } catch (IOException e) {
            log.error("Could not load B+ tree, databaseName=" + databaseName + ", tableName=" + tableName + ", indexName=" + indexName);
            throw new RuntimeException(e);
        }
    }

    public boolean isPresent(ArrayList<String> values, Integer pointer){
        values.add(pointer.toString());
        Key key = TypeConverter.toKey(keyStructure, values);
        try{
            bPlusTree.find(key);
            return true;
        } catch (KeyNotFoundException | IOException e) {
            return false;
        }
    }

    public HashMap<Integer, Object> equalityQuery(Object key) throws IOException, UndefinedQueryException {
        return rangeQuery(key, key, true, true);
    }

    public HashMap<Integer, Object> rangeQuery(Object lowerBound, Object upperBound, boolean allowEqualityLower, boolean allowEqualityUpper) throws UndefinedQueryException, IOException {
        HashMap<Integer, Object> result = new HashMap<>();
        if(keyStructure.size() != 2){
            throw new UndefinedQueryException();
        }

        int lowerCompareValue, upperCompareValue, lowerPointerValue, upperPointerValue;
        if(allowEqualityLower){
            lowerCompareValue = 1;
            lowerPointerValue = Integer.MIN_VALUE;
        } else {
            lowerCompareValue = 0;
            lowerPointerValue = Integer.MAX_VALUE;
        }

        if(allowEqualityUpper){
            upperCompareValue = 1;
            upperPointerValue = Integer.MAX_VALUE;
        } else {
            upperCompareValue = 0;
            upperPointerValue = Integer.MIN_VALUE;
        }

        ArrayList<Object> lowerObjectList = new ArrayList<>(), upperObjectList = new ArrayList<>();
        lowerObjectList.add(lowerBound);
        lowerObjectList.add(lowerPointerValue);
        upperObjectList.add(upperBound);
        upperObjectList.add(upperPointerValue);
        Key lowerKey = new Key(lowerObjectList, keyStructure);
        Key upperKey = new Key(upperObjectList, keyStructure);

        return bPlusTree.rangeQuery(lowerKey, upperKey, lowerCompareValue, upperCompareValue);
    }

    public HashMap<Integer, Object> lesserQuery(Object upperBound, boolean allowEquality) throws UndefinedQueryException, IOException {
        if(keyStructure.size() != 2){
            throw new UndefinedQueryException();
        }

        int upperCompareValue, upperPointerValue;

        if(allowEquality){
            upperCompareValue = 1;
            upperPointerValue = Integer.MAX_VALUE;
        } else {
            upperCompareValue = 0;
            upperPointerValue = Integer.MIN_VALUE;
        }

        ArrayList<Object> upperObjectList = new ArrayList<>();
        upperObjectList.add(upperBound);
        upperObjectList.add(upperPointerValue);
        Key upperKey = new Key(upperObjectList, keyStructure);
        return bPlusTree.rangeQuery(TypeConverter.smallestKey(keyStructure), upperKey, 0, upperCompareValue);
    }

    public HashMap<Integer, Object> greaterQuery(Object lowerBound, boolean allowEquality) throws UndefinedQueryException, IOException {
        if(keyStructure.size() != 2){
            throw new UndefinedQueryException();
        }
        int lowerCompareValue, lowerPointerValue;
        if(allowEquality){
            lowerCompareValue = 1;
            lowerPointerValue = Integer.MIN_VALUE;
        } else {
            lowerCompareValue = 0;
            lowerPointerValue = Integer.MAX_VALUE;
        }


        ArrayList<Object> lowerObjectList = new ArrayList<>();
        lowerObjectList.add(lowerBound);
        lowerObjectList.add(lowerPointerValue);
        Key lowerKey = new Key(lowerObjectList, keyStructure);

        return bPlusTree.rangeQuery(lowerKey, lowerKey, lowerCompareValue, 2);
    }

    public void insert(ArrayList<String> values, Integer pointer) throws IOException, KeyAlreadyInTreeException {
        values.add(pointer.toString());
        Key key = TypeConverter.toKey(keyStructure, values);
        bPlusTree.insert(key, pointer);
    }

    public void delete(ArrayList<String> values, Integer pointer) throws IOException {
        values.add(pointer.toString());
        Key key = TypeConverter.toKey(keyStructure, values);
        try {
            bPlusTree.delete(key);
        }catch (KeyNotFoundException ignored){}
    }

    @Override
    public void close() throws IOException {
        bPlusTree.close();
    }

    public static void createEmptyIndex(String databaseName, String tableName, String indexName) throws IOException {
        List<String> keyStruct = CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
        keyStruct.add("int");
        String filename = CatalogManager.getTableIndexFilePath(databaseName, tableName, indexName);

        BPlusTree emptyTree = new BPlusTree((ArrayList<String>) keyStruct, filename);
        emptyTree.createEmptyTree();
        emptyTree.close();
    }

    public static void createIndex(String databaseName, String tableName, String indexName) throws IOException {
        ArrayList<String> keyStruct = (ArrayList<String>) CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
        keyStruct.add("int");
        ArrayList<String> keyColumnNames = (ArrayList<String>) CatalogManager.getIndexFieldNames(databaseName, tableName, indexName);

        String filename = CatalogManager.getTableIndexFilePath(databaseName, tableName, indexName);

        BPlusTree tree = new BPlusTree(keyStruct, filename);
        tree.createEmptyTree();

        RecordReader reader = new RecordReader(databaseName, tableName);

        ArrayList<ArrayList<Object>> table = reader.scan(keyColumnNames);

        for(int i = 0; i < table.size(); i++){
            try{
                ArrayList<Object> key = table.get(i);
                key.add(i);
                tree.insert(new Key(key, keyStruct), i);
            }catch (KeyAlreadyInTreeException ignored){}
        }

        tree.close();
    }
}
