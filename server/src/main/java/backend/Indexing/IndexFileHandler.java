package backend.Indexing;

import backend.config.Config;
import backend.recordHandling.ByteConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class IndexFileHandler {
    private final RandomAccessFile io;
    private ArrayList<String> keyStructure;
    private int nodeSize, headerSize, emptyNodeLocation;
    private final String databaseName, tableName, indexName;

    public IndexFileHandler(String databaseName, String tableName, String indexName) throws FileNotFoundException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.indexName = indexName;

        //get keyStructure from catalog
        //remove later
        keyStructure = new ArrayList<>();
        keyStructure.add("int");

        headerSize = 0;
        int keySize = (int)ByteConverter.sizeofStructure(keyStructure);
        nodeSize = 1 + Integer.BYTES + (2 * Consts.D) * keySize + (2 * Consts.D + 1) * Integer.BYTES;

        String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";
        io = new RandomAccessFile(filename, "rw");
    }

    public void writeNode(TreeNode node, int line) throws IOException {
        int offset = getOffset(line);
        io.seek(offset);
        io.write(node.toBytes());
    }

    public TreeNode readTreeNode(int line) throws IOException {
        int offset = getOffset(line);
        io.seek(offset);
        byte[] bytes = new byte[nodeSize];
        io.readFully(bytes);
        return new TreeNode(bytes, keyStructure);
    }

    public TreeNode readRoot() throws IOException {
        return readTreeNode(0);
    }

    private int getOffset(int line){
        return headerSize + nodeSize * line;
    }
    public void close() throws IOException {
        io.close();
    }
}
