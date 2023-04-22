package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class BPlusTree {
    private IndexFileHandler io;
    private ArrayList<String> keyStructure;

    public BPlusTree(String databaseName, String tableName, String indexName) throws IOException {
        io = new IndexFileHandler(databaseName, tableName, indexName);

        //get keyStructure from catalog
        //remove later
        keyStructure = new ArrayList<>();
        keyStructure.add("int");
    }

    public void CreateEmptyTree() throws IOException {
        TreeNode root = new TreeNode(true, keyStructure);
        io.writeNode(root, 0);
        io.setRootPointer(0);
    }

    public int find(Key key) throws IOException, RecordNotFoundException {
        TreeNode node = io.readRoot();
        while(!node.isLeaf()){
            node = io.readTreeNode(node.findNextNode(key));
        }
        return node.findKeyInLeaf(key);
    }

    public void insert(Key key, int pointer) throws IOException {
        TreeNode node = io.readRoot();
        while(!node.isLeaf()){
            node = io.readTreeNode(node.findNextNode(key));
        }

        if(node.isAlmostFull()){
            node.insertIntoLeaf(key, pointer);
            TreeNode rightNode = node.splitLeaf(1);

        }else {
            node.insertIntoLeaf(key, pointer);
            //change later
            io.writeNode(node, 0);
        }
    }

    public boolean nullPointer(int pointer){
        return pointer == Consts.nullPointer;
    }

    public void close() throws IOException {
        io.close();
    }
}
