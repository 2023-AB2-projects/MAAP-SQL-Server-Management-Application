package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.KeyAlreadyInTreeException;
import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NonUniqueIndexManager {
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

//    public ArrayList<Integer> findLocations(ArrayList<String> values) throws IOException, KeyNotFoundException {
//        values.add("0");
//        Key key = TypeConverter.toKey(keyStructure, values);
//        return null;
//    }

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

    public static void createIndex() {
        // keyStruct = CatalogManager.getIndexStructure(databaseName, tableName, indexName);
        // String filename = CatalogManager.getIndexFileName(databaseName, tableName, indexName);

        // String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";
        // BPlusTree emptyTree = new BPlusTree(keyStruct, filename);
        // emptyTree.createEmptyTree();

        //read lines and insert
    }
}
