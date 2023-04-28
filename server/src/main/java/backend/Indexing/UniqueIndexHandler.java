package backend.Indexing;

import backend.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UniqueIndexHandler {
    private ArrayList<String> keyStructure;
    private BPlusTree bPlusTree;

    private String databaseName, tableName;

    public UniqueIndexHandler(String databaseName, String tableName, String indexName) throws IOException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        // keyStructure = CatalogManager.getIndexStructure(databaseName, tableName, indexName);

        //remove later
        keyStructure = new ArrayList<>();
        keyStructure.add("int");

        // String filename = CatalogManager.getIndexFileName(databaseName, tableName, indexName);
        //remove later
        String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";

        bPlusTree = new BPlusTree(keyStructure, indexName);
    }

    public void createEmptyIndex() throws IOException {
        bPlusTree.createEmptyTree();
    }

    public void createIndex() {

    }

}
