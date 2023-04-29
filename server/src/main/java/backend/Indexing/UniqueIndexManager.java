package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.KeyAlreadyInTreeException;
import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class UniqueIndexManager {
    private final ArrayList<String> keyStructure;
    private final BPlusTree bPlusTree;

    private final String databaseName, tableName;

    public UniqueIndexManager(String databaseName, String tableName, String indexName) {
        this.databaseName = databaseName;
        this.tableName = tableName;

        // Index field types and file name
        keyStructure = (ArrayList<String>) CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
        String filename = CatalogManager.getTableIndexFilePath(databaseName, tableName, indexName);

        try {
            bPlusTree = new BPlusTree(keyStructure, filename);
        } catch (IOException e) {
            log.error("Could not load B+ tree, databaseName=" + databaseName + ", tableName=" + tableName + ", indexName=" + indexName);
            throw new RuntimeException(e);
        }
    }

    public boolean isPresent(ArrayList<String> values){
        Key key = TypeConverter.toKey(keyStructure, values);
        try{
            bPlusTree.find(key);
            return true;
        } catch (KeyNotFoundException | IOException e) {
            return false;
        }
    }

    public Integer findLocation(ArrayList<String> values) throws IOException, KeyNotFoundException {
        Key key = TypeConverter.toKey(keyStructure, values);
        return bPlusTree.find(key);
    }

    public void insert(ArrayList<String> values, Integer pointer) throws IOException, KeyAlreadyInTreeException {
        Key key = TypeConverter.toKey(keyStructure, values);
        bPlusTree.insert(key, pointer);
    }

    public void delete(ArrayList<String> values) throws IOException {
        Key key = TypeConverter.toKey(keyStructure, values);
        try {
            bPlusTree.delete(key);
        }catch (KeyNotFoundException ignored){}
    }

    public void close() throws IOException {
        bPlusTree.close();
    }

    public static void createEmptyIndex(String databaseName, String tableName, String indexName) throws IOException {
        // keyStruct = CatalogManager.getIndexStructure(databaseName, tableName, indexName);
        // String filename = CatalogManager.getIndexFileName(databaseName, tableName, indexName);

        // String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";
        // BPlusTree emptyTree = new BPlusTree(keyStruct, filename);
        // emptyTree.createEmptyTree();
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
