package backend.recordHandling;

import backend.Indexing.IndexFIleHandler;
import backend.Indexing.Key;
import backend.Indexing.TreeNode;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.InvalidTypeException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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

        byte[] bytes = {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        TreeNode node = new TreeNode(bytes, types);

        IndexFIleHandler indexFIleHandler = new IndexFIleHandler("asd", "asd", "asd");
        indexFIleHandler.writeNode(node, 0);
        indexFIleHandler.writeNode(node, 1);

        TreeNode node1 = indexFIleHandler.readTreeNode(0);
        System.out.println(Arrays.toString(node1.toBytes()));

        indexFIleHandler.close();
    }
}
