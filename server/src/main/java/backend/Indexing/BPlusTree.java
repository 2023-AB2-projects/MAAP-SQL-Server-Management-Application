package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

@Slf4j
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
        Stack<Integer> pointers = new Stack<>();
        pointers.add(io.getRootPointer());

        TreeNode node = io.readRoot();
        while(!node.isLeaf()){
            int nodePointer = node.findNextNode(key);
            node = io.readTreeNode(nodePointer);
            pointers.add(nodePointer);
        }

        if(node.isAlmostFull()){
            node.insertIntoLeaf(key, pointer);
            int splitLocation = io.getEmptyNodePointer();
            TreeNode rightNode = node.splitLeaf(splitLocation);
            log.info(node.toString());
            log.info(rightNode.toString());


        }else {
            node.insertIntoLeaf(key, pointer);
            log.info(node.toString());
            io.writeNode(node, pointers.pop());
        }
    }

    public boolean nullPointer(int pointer){
        return pointer == Consts.nullPointer;
    }

    public void close() throws IOException {
        io.close();
    }
}
