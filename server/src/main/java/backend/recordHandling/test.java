package backend.recordHandling;

import backend.Indexing.BPlusTree;
import backend.Indexing.IndexFileHandler;
import backend.Indexing.Key;
import backend.Indexing.TreeNode;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import com.fasterxml.jackson.databind.InjectableValues;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

@Slf4j
public class test {
    public static void main(String[] args) throws IOException, InvalidReadException, RecordNotFoundException {
//        byte[] bytes = {0,0,0,1,0,0,0,1,1};
        ArrayList<String> types = new ArrayList<>();
        types.add("int");
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
//        byte[] bytes2 = {0, 0, 0, 3};
//        Key key = new Key(bytes2, types);
//
//        node.insert(key, 1);
//        node.insert(key, 2);
//        node.insert(key, 3);
//        System.out.println(node);

//        IndexFileHandler indexFIleHandler = new IndexFileHandler("asd", "asd", "asd");
//        indexFIleHandler.writeNode(node, 0);
//        indexFIleHandler.writeNode(node, 1);
//
//        TreeNode node1 = indexFIleHandler.readTreeNode(0);
//        System.out.println(Arrays.toString(node1.toBytes()));

        BPlusTree tree = new BPlusTree("asd", "asd", "asd");
        tree.CreateEmptyTree();
        Random r = new Random(2);
        ArrayList<Integer> nums = new ArrayList<>(), pointers = new ArrayList<>();
        ArrayList<Key> keys = new ArrayList<>();
        int n = 1000;
        for(int i = 0; i < n; i++){
            ByteBuffer buffer = ByteBuffer.allocate(4);
            int num = r.nextInt() % 10000, pointer = r.nextInt() % 100;
            while(nums.contains(num)){
                num = r.nextInt() % 10000;
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

        for(int i = 0; i < n; i++){
            finds.add(tree.find(keys.get(i)));
        }

        //System.out.println(finds);

        if(pointers.equals(finds)){
            System.out.println("Nice");
        }

        //tree.printTree();

        tree.close();

    }
}
