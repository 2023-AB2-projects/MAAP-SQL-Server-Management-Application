package backend.Indexing;

import backend.config.Config;
import backend.recordHandling.ByteConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class IndexFIleHandler {
    private final RandomAccessFile io;
    private ArrayList<String> keyStructure;
    private int nodeSize, headSize, emptyNodeLocation;
    private final String databaseName, tableName, indexName;

    public IndexFIleHandler(String databaseName, String tableName, String indexName) throws FileNotFoundException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.indexName = indexName;

        //get keyStructure from catalog
        //remove later
        keyStructure = new ArrayList<>();
        keyStructure.add("int");

        int keyLength = keyStructure.size();
        long keySize = ByteConverter.sizeofStructure(keyStructure);


        String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";
        io = new RandomAccessFile(filename, "rw");
    }

    public void close() throws IOException {
        io.close();
    }
}
