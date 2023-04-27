package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
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
        io.setDeletedNodePointer(Consts.nullPointer);
    }

    public int find(Key key) throws IOException, RecordNotFoundException {
        TreeNode node = io.readRoot();
        // log.info(node.toString());
        // ArrayList<Integer> path = new ArrayList<>();
        // path.add(io.getRootPointer());
        while(!node.isLeaf()){
            // path.add(node.findNextNode(key));
            node = io.readTreeNode(node.findNextNode(key));
            // log.info(node.toString());
        }
        // System.out.println("Path: " + path);
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
            node.insertInLeaf(key, pointer);
            int rightNodePointer = io.popEmptyNodePointer();
            TreeNode rightNode = node.splitLeaf(rightNodePointer);

            log.info(node.toString());
            log.info(rightNode.toString());

            int leftNodePointer = parents.pop();
            io.writeNode(node, leftNodePointer);
            io.writeNode(rightNode, rightNodePointer);

            Key smallestKey = rightNode.getSmallestKey();

            insertInParent(leftNodePointer, rightNodePointer, smallestKey, parents);

        }else {
            node.insertInLeaf(key, pointer);
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

            int newRootPointer = io.popEmptyNodePointer();
            io.writeNode(rootNode, newRootPointer);
            io.setRootPointer(newRootPointer);

            log.info(rootNode.toString());
        } else {
            int parentPointer = parents.pop();
            TreeNode parentNode = io.readTreeNode(parentPointer);

            if(parentNode.isFull()){
                parentNode.insertInNode(smallestKey, rightNodePointer);

                Key middleKey = parentNode.getMiddleKey();

                int rightParentPointer = io.popEmptyNodePointer();
                TreeNode rightParentNode = parentNode.splitNode();

                io.writeNode(parentNode, parentPointer);
                io.writeNode(rightParentNode, rightParentPointer);

                log.info(parentNode.toString());
                log.info(parentNode.toString());
                log.info(middleKey.toString());

                insertInParent(parentPointer, rightParentPointer, middleKey, parents);
            }else{
                parentNode.insertInNode(smallestKey, rightNodePointer);
                io.writeNode(parentNode, parentPointer);

                log.info(parentNode.toString());
            }
        }
    }

    public void delete(Key key) throws IOException {
        Stack<Integer> parents = new Stack<>();
        parents.add(io.getRootPointer());

        TreeNode node = io.readRoot();
        while(!node.isLeaf()){
            int nodePointer = node.findNextNode(key);
            node = io.readTreeNode(nodePointer);
            parents.add(nodePointer);
        }

        delete(node, parents.pop(), key, parents);
    }

    private void delete(TreeNode node, Integer nodePointer, Key key, Stack<Integer> parents) throws IOException {
        try{
            node.removeKey(key);
        }catch (KeyNotFoundException e){
            return;
        }
        System.out.println(node);

        //remove from root
        if(io.getRootPointer() == nodePointer){
            if(node.getKeyCount() == 0){ //root is empty
                int newRoot = node.getFirstPointer();
                if(!nullPointer(newRoot)){ //delete the root
                    io.setRootPointer(newRoot);
                    io.addEmptyNode(nodePointer);
                }else { //tree is now empty
                    io.writeNode(node, nodePointer);
                }
            }else { //removed entry from root
                io.writeNode(node, nodePointer);
            }
            return;
        }

        //
        if(node.isTooSmall()){ //node became too small
            Integer parentNodePointer = parents.pop();
            TreeNode parentNode = io.readTreeNode(parentNodePointer);

            Integer siblingPointer = parentNode.getLeftSiblingPointer(nodePointer);
            boolean isLeftSibling = true;
            if(siblingPointer == null){
                isLeftSibling = false;
                siblingPointer = parentNode.getRightSiblingPointer(nodePointer);
                if(siblingPointer == null){
                    log.warn("Something went wrong");
                    return;
                }
            }

            TreeNode siblingNode = io.readTreeNode(siblingPointer);
            Key commonKey = parentNode.getKeyBetween(nodePointer, siblingPointer);

            if(siblingNode.getKeyCount() + node.getKeyCount() < Consts.D * 2){ //join Nodes
                if(isLeftSibling){
                    TreeNode temp = node;
                    node = siblingNode;
                    siblingNode = temp;

                    Integer tempPointer = nodePointer;
                    nodePointer = siblingPointer;
                    siblingPointer = tempPointer;
                }

                System.out.println(node);
                System.out.println(siblingNode);
                if(node.isLeaf()){
                    node.joinLeaves(siblingNode);
                }else {
                    node.join(siblingNode, commonKey);
                }

                io.writeNode(node, nodePointer);
                io.addEmptyNode(siblingPointer);

                delete(parentNode, parentNodePointer, commonKey, parents);
            } else{ //unable to join, must borrow
                if(isLeftSibling) { //siblingNode is the left sibling of node
                    if(node.isLeaf()){

                    }else {

                    }
                } else{ //siblingNode is the right sibling of node
                    if(node.isLeaf()){

                    }else{

                    }
                }
            }

        }else{ //just remove the entry and write it to storage
            io.writeNode(node, nodePointer);
        }
    }
    public void printTree() throws IOException {
        System.out.println("Rootpointer: " + io.getRootPointer());
        System.out.println("EmptyPointer: " + io.getDeletedNodePointer());
        for (int i = 0; i < io.popEmptyNodePointer(); i++){
            System.out.println(i + ": " + io.readTreeNode(i));
        }
    }

    public void travelTree() throws IOException {
        TreeNode node = io.readRoot();
        System.out.println(node);
        Scanner scanner = new Scanner(System.in);
        do{
            int pointer = scanner.nextInt();
            if(pointer == -1){
                return;
            }
            node = io.readTreeNode(pointer);
            System.out.println(node);
        }while (true);
    }

    private boolean nullPointer(int pointer){
        return pointer == Consts.nullPointer;
    }

    public void close() throws IOException {
        io.close();
    }
}
