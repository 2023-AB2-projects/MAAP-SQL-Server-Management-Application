package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

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
        Stack<Integer> parents = new Stack<>();
        parents.add(io.getRootPointer());

        TreeNode node = io.readRoot();
        while(!node.isLeaf()){
            int nodePointer = node.findNextNode(key);
            node = io.readTreeNode(nodePointer);
            parents.add(nodePointer);
        }

        if(node.isAlmostFull()){
            node.insert(key, pointer);
            int rightNodePointer = io.getEmptyNodePointer();
            TreeNode rightNode = node.splitLeaf(rightNodePointer);

            log.info(node.toString());
            log.info(rightNode.toString());

            int leftNodePointer = parents.pop();
            io.writeNode(node, leftNodePointer);
            io.writeNode(rightNode, rightNodePointer);

            Key smallestKey = rightNode.getSmallestKey();

            insertInParent(leftNodePointer, rightNodePointer, smallestKey, parents);

        }else {
            node.insert(key, pointer);
            log.info(node.toString());
            io.writeNode(node, parents.pop());
        }
    }

    private void insertInParent(int leftNodePointer, int rightNodePointer, Key smallestKey, Stack<Integer> parents) throws IOException {
        if(parents.empty()){
            ArrayList<Key> keys = new ArrayList<>();
            keys.add(smallestKey);
            ArrayList<Integer> pointers = new ArrayList<>();
            pointers.add(leftNodePointer);
            pointers.add(rightNodePointer);
            TreeNode rootNode = new TreeNode(false, 1, keys, pointers, keyStructure);

            int newRootPointer = io.getEmptyNodePointer();
            io.writeNode(rootNode, newRootPointer);
            io.setRootPointer(newRootPointer);
        } else {
            int parentPointer = parents.pop();
            TreeNode parentNode = io.readTreeNode(parentPointer);

            if(parentNode.isFull()){
                parentNode.insert(smallestKey, rightNodePointer);

                Key middleKey = parentNode.getMiddleKey();

                int rightParentPointer = io.getEmptyNodePointer();
                TreeNode rightParentNode = parentNode.splitNode();

                io.writeNode(parentNode, parentPointer);
                io.writeNode(rightParentNode, rightParentPointer);

                insertInParent(parentPointer, rightParentPointer, middleKey, parents);
            }else{
                parentNode.insert(smallestKey, rightNodePointer);
                io.writeNode(parentNode, parentPointer);
            }
        }
    }

    public boolean nullPointer(int pointer){
        return pointer == Consts.nullPointer;
    }

    public void close() throws IOException {
        io.close();
    }
}
