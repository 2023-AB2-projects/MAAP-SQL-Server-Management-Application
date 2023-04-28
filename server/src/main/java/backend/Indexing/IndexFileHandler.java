package backend.Indexing;

import backend.config.Config;
import backend.recordHandling.TypeConverter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class IndexFileHandler {
    private final RandomAccessFile io;
    private ArrayList<String> keyStructure;
    private int nodeSize, headerSize;

    private final static long deletePointerOffset = Integer.BYTES;
    public IndexFileHandler(ArrayList<String> keyStructure, String fileLocation) throws IOException {
        this.headerSize = 2 * Integer.BYTES;

        this.keyStructure = keyStructure;

        int keySize = (int) TypeConverter.sizeofStructure(keyStructure);
        nodeSize = 1 + Integer.BYTES + (2 * Constants.D) * keySize + (2 * Constants.D + 1) * Integer.BYTES;

        io = new RandomAccessFile(fileLocation, "rw");

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

    public void setDeletedNodePointer(int pointer) throws IOException {
        io.seek(Integer.BYTES);
        io.writeInt(pointer);
    }

    public int getDeletedNodePointer() throws IOException {
        io.seek(deletePointerOffset);
        return io.readInt();
    }

    public void addEmptyNode(int line) throws IOException {
        TreeNode emptyNode = TreeNode.createDeletedNode(getDeletedNodePointer(), keyStructure);
        setDeletedNodePointer(line);
        writeNode(emptyNode, line);
    }

    public int popEmptyNodePointer() throws IOException {
        int pointer = getDeletedNodePointer();
        if(pointer != Constants.nullPointer){
            TreeNode node = readTreeNode(pointer);
            setDeletedNodePointer(node.getFirstPointer());
            return pointer;
        }
        //end of file
        return (int) ((io.length() - headerSize) / nodeSize);
    }

    public int getSize() throws IOException {
        return (int) ((io.length() - headerSize) / nodeSize);
    }

    private int getOffset(int line){
        return headerSize + nodeSize * line;
    }

    public void close() throws IOException {
        io.close();
    }
}
