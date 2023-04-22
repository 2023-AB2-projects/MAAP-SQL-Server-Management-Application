package backend.Indexing;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TreeNode {
    @Setter
    @Getter
    private boolean isLeaf;

    @Setter
    @Getter
    private int nr;

    @Setter
    @Getter
    private ArrayList<Object> keys;

    @Setter
    @Getter
    private ArrayList<Integer> pointers;



    public TreeNode(){
        isLeaf = true;
        nr = 0;
        keys = new ArrayList<>();
        pointers = new ArrayList<>();
    }

    public TreeNode(byte[] bytes, ArrayList<String> keyStructure){
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte isLeaf = buffer.get();
        this.isLeaf = isLeaf == 1;


    }
}
