package backend.Indexing;

import backend.recordHandling.ByteConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class TreeNode {
    private boolean isLeaf;
    private int keyCount;
    private ArrayList<Key> keys;
    private ArrayList<Integer> pointers;
    private ArrayList<String> keyStructure;
    private int keySize;
    private int nodeSize;

    //empty node
    public TreeNode(boolean isLeaf, ArrayList<String> keyStructure){
        this.isLeaf = isLeaf;
        keyCount = 0;
        keySize = (int)ByteConverter.sizeofStructure(keyStructure);

        nodeSize = 1 + Integer.BYTES + (2 * Consts.D) * keySize + (2 * Consts.D + 1) * Integer.BYTES;
        keys = new ArrayList<>();
        pointers = new ArrayList<>();
    }
    public TreeNode(boolean isLeaf, int keyCount, ArrayList<Key> keys, ArrayList<Integer> pointers, ArrayList<String> keyStructure) {
        this.isLeaf = isLeaf;
        this.keyCount = keyCount;
        this.keys = keys;
        this.pointers = pointers;
        this.keyStructure = keyStructure;

        keySize = (int)ByteConverter.sizeofStructure(keyStructure);
        nodeSize = 1 + Integer.BYTES + (2 * Consts.D) * keySize + (2 * Consts.D + 1) * Integer.BYTES;
    }
    public TreeNode(byte[] bytes, ArrayList<String> keyStructure){
        keys = new ArrayList<>();
        pointers = new ArrayList<>();

        keySize = (int)ByteConverter.sizeofStructure(keyStructure);
        nodeSize = 1 + Integer.BYTES + (2 * Consts.D) * keySize + (2 * Consts.D + 1) * Integer.BYTES;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        isLeaf = buffer.get() == 1;
        keyCount = buffer.getInt();
        pointers.add(buffer.getInt());

        for (int i = 0; i < keyCount; i++){
            byte[] keyBytes = new byte[keySize];
            buffer.get(keyBytes);
            keys.add(new Key(keyBytes, keyStructure));
            pointers.add(buffer.getInt());
        }

    }
    public byte[] toBytes(){
        ByteBuffer buffer = ByteBuffer.allocate(nodeSize);

        if(isLeaf){
            buffer.put((byte) 1);
        }else{
            buffer.put((byte) 0);
        }

        buffer.putInt(keyCount);
        for(int i = 0; i < keyCount; i++){
            buffer.putInt(pointers.get(i));
            buffer.put(keys.get(i).toBytes());
        }
        buffer.putInt(pointers.get(keyCount));

        return buffer.array();
    }

    public boolean isFull(){
        return keyCount == Consts.D * 2;
    }

    public boolean isTooSmall(){
        return keyCount < Consts.D;
    }

}
