package backend.Indexing;

import backend.config.Config;
import backend.exceptions.recordHandlingExceptions.KeyAlreadyInTreeException;
import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.recordHandling.TypeConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UniqueIndexManager {
    private final ArrayList<String> keyStructure;
    private final BPlusTree bPlusTree;

    private final String databaseName, tableName;

    public UniqueIndexManager(String databaseName, String tableName, String indexName) throws IOException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        // keyStructure = CatalogManager.getIndexStructure(databaseName, tableName, indexName);

        //remove later
        keyStructure = new ArrayList<>();
        keyStructure.add("int");

        // String filename = CatalogManager.getIndexFileName(databaseName, tableName, indexName);
        //remove later
        String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";

        bPlusTree = new BPlusTree(keyStructure, filename);
    }

    public void removeLater() throws IOException {
        bPlusTree.createEmptyTree();
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
