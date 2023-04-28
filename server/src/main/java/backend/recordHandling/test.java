package backend.recordHandling;

import backend.Indexing.BPlusTree;
import backend.Indexing.Key;
import backend.Indexing.TreeNode;
import backend.config.Config;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

@Slf4j
public class test {
    public static ArrayList<String> types;
    public static void TreeNodeTest(){
        TreeNode node = new TreeNode(true, types);
        byte[] bytes1 = {0, 0, 0, 1};
        byte[] bytes2 = {0, 0, 0, 2};
        byte[] bytes3 = {0, 0, 0, 3};

        Key key1 = new Key(bytes1, types);
        Key key2 = new Key(bytes2, types);
        Key key3 = new Key(bytes3, types);

        node.insertInLeaf(key1, 1);

        TreeNode node1 = new TreeNode(true, types);
        node1.insertInLeaf(key3, 3);

        System.out.println(node);
        System.out.println(node1);

        node.join(node1, key2);
        System.out.println(node);

        System.out.println(node.popBackKey());
        System.out.println(node.popSecondToLastPointer());
        System.out.println(node);
//        try{
//            node.removeKey(key2);
//            node.removeKey(key3);
//            node.removeKey(key3);
//            node.removeKey(key1);
//        }catch (Exception e){
//            System.out.println("nope");
//        }
//        System.out.println(node);
    }

    public static void BtreeDeleteTest() throws IOException, RecordNotFoundException {
        String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";
        System.out.println(filename);
        BPlusTree tree = new BPlusTree(types, filename);
        tree.createEmptyTree();
        Random r = new Random();
        ArrayList<Integer> nums = new ArrayList<>(), pointers = new ArrayList<>();
        ArrayList<Key> keys = new ArrayList<>();
        int n = 1000;
        for(int i = 0; i < n; i++){
            ByteBuffer buffer = ByteBuffer.allocate(4);
            int num = r.nextInt() % 10000, pointer = r.nextInt() % 10000;
            while(nums.contains(num)){
                num = r.nextInt() % 10000;
            }
            while(pointers.contains(pointer)){
                pointer = r.nextInt() % 10000;
            }
            nums.add(num);
            pointers.add(pointer);
            buffer.putInt(num);
            Key key = new Key(buffer.array(), types);
            keys.add(key);
            tree.insert(key, pointer);
            //tree.printTree();
        }

        //System.out.println(nums);
        //System.out.println(pointers);

        ArrayList<Integer> finds = new ArrayList<>();

        tree.printTree();

        for(int i = 0; i < n; i++){
            tree.delete(keys.get(i));
            //tree.printTree();
        }

//        for(int i = 0; i < n; i++){
//            finds.add(tree.find(keys.get(i)));
//        }
//
//        System.out.println(finds);
//
//        if(pointers.equals(finds)){
//            System.out.println("Nice");
//        }

        //System.out.println(keys);
        //System.out.println(pointers);
        tree.insert(keys.get(0), pointers.get(0));
        tree.printTree();

        tree.close();
    }
    public static void main(String[] args) throws IOException, InvalidReadException, RecordNotFoundException {
//        byte[] bytes = {0,0,0,1,0,0,0,1,1};
        types = new ArrayList<>();
        types.add("int");
        //TreeNodeTest();
        BtreeDeleteTest();
//        types.add("bit");
//
//        Key key = new Key(bytes, types);
//        System.out.println(Arrays.toString(key.toBytes()));
//
//        Object a = "asg", b = "asd";
//        byte b1 = 1, b2 = 1;
//        System.out.println(ByteConverter.compare("bit", b1, b2));

//        byte[] bytes = {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//
//        TreeNode node = new TreeNode(bytes, types);

//        System.out.println(node);

//        IndexFileHandler indexFIleHandler = new IndexFileHandler("asd", "asd", "asd");
//        indexFIleHandler.writeNode(node, 0);
//        indexFIleHandler.writeNode(node, 1);
//
//        TreeNode node1 = indexFIleHandler.readTreeNode(0);
//        System.out.println(Arrays.toString(node1.toBytes()));




//        tree.close();


//        BPlusTree tree = new BPlusTree("asd", "asd", "asd");
//        tree.CreateEmptyTree();
//        tree.close();
//        IndexFileHandler handler = new IndexFileHandler("asd", "asd", "asd");
//        System.out.println(handler.popEmptyNodePointer());
//        System.out.println(handler.getDeletedNodePointer());
////        System.out.println(handler.readTreeNode(0));
////        System.out.println(handler.readTreeNode(1));
////        System.out.println(handler.readTreeNode(2));
//        handler.close();


    }
}
