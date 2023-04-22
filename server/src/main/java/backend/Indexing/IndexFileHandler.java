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
    private int nodeSize, headerSize;
    private final String databaseName, tableName, indexName;

    public IndexFileHandler(String databaseName, String tableName, String indexName) throws IOException {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.indexName = indexName;
        this.headerSize = Integer.BYTES;
        //get keyStructure from catalog
        //remove later
        keyStructure = new ArrayList<>();
        keyStructure.add("int");

        int keySize = (int)ByteConverter.sizeofStructure(keyStructure);
        nodeSize = 1 + Integer.BYTES + (2 * Consts.D) * keySize + (2 * Consts.D + 1) * Integer.BYTES;

        String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";
        io = new RandomAccessFile(filename, "rw");

        if(io.length() == 0){
            io.writeInt(0);
        }

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
        return readTreeNode(getRootPointer());
    }

    public void setRootPointer(int rootPointer) throws IOException {
        io.seek(0);
        io.writeInt(rootPointer);
    }
    public int getRootPointer() throws IOException {
        io.seek(0);
        return io.readInt();
    }

    public int getEmptyNodePointer() throws IOException {
        //get empty pointer for empty places from catalog, otherwise return pointer to end of file

        return (int) ((io.length() - headerSize) / nodeSize);
    }

    private int getOffset(int line){
        return headerSize + nodeSize * line;
    }
    public void close() throws IOException {
        io.close();
    }
}
