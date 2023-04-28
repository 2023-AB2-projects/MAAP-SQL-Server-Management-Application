package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

@Slf4j
public class BPlusTree {
    private IndexFileHandler io;
    private ArrayList<String> keyStructure;

    public BPlusTree(ArrayList<String> keyStructure, String fileLocation) throws IOException {
        io = new IndexFileHandler(keyStructure, fileLocation);
        this.keyStructure = keyStructure;
    }

    public void createEmptyTree() throws IOException {
        TreeNode root = new TreeNode(true, keyStructure);
        io.writeNode(root, 0);
        io.setRootPointer(0);
        io.setDeletedNodePointer(Constants.nullPointer);
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
            node.insertInLeaf(key, pointer);
            int rightNodePointer = io.popEmptyNodePointer();
            TreeNode rightNode = node.splitLeaf(rightNodePointer);

            int leftNodePointer = parents.pop();
            io.writeNode(node, leftNodePointer);
            io.writeNode(rightNode, rightNodePointer);

            Key smallestKey = rightNode.getSmallestKey();

            insertInParent(leftNodePointer, rightNodePointer, smallestKey, parents);

        }else {
            node.insertInLeaf(key, pointer);
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
            TreeNode rootNode = new TreeNode(false, keys, pointers, keyStructure);

            int newRootPointer = io.popEmptyNodePointer();
            io.writeNode(rootNode, newRootPointer);
            io.setRootPointer(newRootPointer);

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

                insertInParent(parentPointer, rightParentPointer, middleKey, parents);
            }else{
                parentNode.insertInNode(smallestKey, rightNodePointer);
                io.writeNode(parentNode, parentPointer);
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
        try{
            Integer pointer = node.getValueOfKey(key);
            delete(node, parents.pop(), key, pointer, parents);
        }catch (KeyNotFoundException ignored){}
    }

    private void delete(TreeNode node, Integer nodePointer, Key deleteKey, Integer deletePointer, Stack<Integer> parents) throws IOException {
        try{
            node.removeKeyAndPointer(deleteKey, deletePointer);
        }catch (KeyNotFoundException e){
            return;
        }
        // System.out.println(node);

        //remove from root
        if(io.getRootPointer() == nodePointer){
            if(node.keyCount() == 0){ //root is empty
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

        if(node.isTooSmall()){ //node became too small
            Integer parentNodePointer = parents.pop();
            TreeNode parentNode = io.readTreeNode(parentNodePointer);

            Integer siblingPointer = parentNode.getLeftSiblingPointer(nodePointer);
            boolean isLeftSibling = true;
            if(siblingPointer == null){
                isLeftSibling = false;
                siblingPointer = parentNode.getRightSiblingPointer(nodePointer);
                if(siblingPointer == null){
                    return;
                }
            }

            TreeNode siblingNode = io.readTreeNode(siblingPointer);
            Key siblingSeparatorKey = parentNode.getKeyBetween(nodePointer, siblingPointer);

            if(siblingNode.keyCount() + node.keyCount() < Constants.D * 2){ //join Nodes
                if(isLeftSibling){
                    TreeNode temp = node;
                    node = siblingNode;
                    siblingNode = temp;

                    Integer tempPointer = nodePointer;
                    nodePointer = siblingPointer;
                    siblingPointer = tempPointer;
                }

//                System.out.println(node);
//                System.out.println(siblingNode);
                if(node.isLeaf()){
                    node.joinLeaves(siblingNode);
                }else {
                    node.join(siblingNode, siblingSeparatorKey);
                }

                io.writeNode(node, nodePointer);
                io.addEmptyNode(siblingPointer);

                delete(parentNode, parentNodePointer, siblingSeparatorKey, siblingPointer, parents);
            } else{ //unable to join, must borrow
                Key borrowedKey;
                if(isLeftSibling) { //siblingNode is the left sibling of node
                    borrowedKey = siblingNode.popBackKey();
                    if(node.isLeaf()){
                        Integer borrowedPointer = siblingNode.popSecondToLastPointer();
                        node.insertInLeaf(borrowedKey, borrowedPointer);
                    }else {
                        Integer borrowedPointer = siblingNode.popBackPointerFromNode();
                        //I know that this look stupid, but it is not an error
                        node.insertInLeaf(siblingSeparatorKey, borrowedPointer);
                    }
                    parentNode.replaceKey(siblingSeparatorKey, borrowedKey);
                } else{ //siblingNode is the right sibling of node
                    borrowedKey = siblingNode.popFrontKey();
                    if(node.isLeaf()){
                        Integer borrowedPointer = siblingNode.popFrontPointer();
                        node.insertInLeaf(borrowedKey, borrowedPointer);
                        parentNode.replaceKey(siblingSeparatorKey, siblingNode.getSmallestKey());
                    }else{
                        Integer borrowedPointer = siblingNode.popFrontPointer();

                        node.insertInNode(siblingSeparatorKey, borrowedPointer);
                        parentNode.replaceKey(siblingSeparatorKey, borrowedKey);
                    }

                }
                io.writeNode(node, nodePointer);
                io.writeNode(siblingNode, siblingPointer);
                io.writeNode(parentNode, parentNodePointer);
            }

        }else{ //just remove the entry and write it to storage
            io.writeNode(node, nodePointer);
        }
    }

    public void printTree() throws IOException {
        System.out.println("Rootpointer: " + io.getRootPointer());
        System.out.println("EmptyPointer: " + io.getDeletedNodePointer());
        for (int i = 0; i < io.getSize(); i++){
            TreeNode node = io.readTreeNode(i);
            if(node.keyCount() == 0) continue;
            System.out.println(i + ": " + node);
        }
    }

    private boolean nullPointer(int pointer){
        return pointer == Constants.nullPointer;
    }

    public void close() throws IOException {
        io.close();
    }
}
