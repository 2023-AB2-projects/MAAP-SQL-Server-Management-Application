package backend.recordHandling;

import backend.Indexing.BPlusTree;
import backend.Indexing.IndexFileHandler;
import backend.Indexing.Key;
import backend.Indexing.TreeNode;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class test {
    public static void main(String[] args) throws IOException, InvalidReadException, RecordNotFoundException {
//        byte[] bytes = {0,0,0,1,0,0,0,1,1};
        ArrayList<String> types = new ArrayList<>();
        types.add("int");
//        types.add("float");
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
        byte[] bytes = {0, 0, 0, 3};
        Key key = new Key(bytes, types);
        tree.insert(key, 10);

        bytes = new byte[]{0, 0, 0, 1};
        key = new Key(bytes, types);
        tree.insert(key, 9);

        tree.close();
    }
}
