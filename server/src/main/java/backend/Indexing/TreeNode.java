package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.recordHandling.ByteConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class TreeNode {
    @Getter
    @Setter
    private boolean isLeaf;
    private int keyCount;
    private ArrayList<Key> keys;
    private ArrayList<Integer> pointers;
    @Getter
    private ArrayList<String> keyStructure;
    private int keySize;
    private int nodeSize;

    //empty node
    public TreeNode(boolean isLeaf, ArrayList<String> keyStructure){
        this.isLeaf = isLeaf;
        keyCount = 0;
        this.keyStructure = keyStructure;

        keySize = (int)ByteConverter.sizeofStructure(keyStructure);

        nodeSize = 1 + Integer.BYTES + (2 * Consts.D) * keySize + (2 * Consts.D + 1) * Integer.BYTES;
        keys = new ArrayList<>();
        pointers = new ArrayList<>();
        pointers.add(Consts.nullPointer);
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
        this.keyStructure = keyStructure;
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
    public int findNextNode(Key key){
        for(int i = 0; i < keyCount; i++){
            if(key.compareTo(keys.get(i)) < 0){
                return pointers.get(i);
            }
        }
        return pointers.get(keyCount);
    }
    public int findKeyInLeaf(Key key) throws RecordNotFoundException {
        for(int i = 0; i < keyCount; i++){
            if(key.compareTo(keys.get(i)) == 0){
                return pointers.get(i);
            }
        }
        throw new RecordNotFoundException();
    }
    public void insertIntoLeaf(Key key, int pointer){
        ArrayList<Key> newKeys = new ArrayList<>();
        ArrayList<Integer> newPointers = new ArrayList<>();

        int i = 0;
        while(i < keyCount && key.compareTo(keys.get(i)) > 0){
            newKeys.add(keys.get(i));
            newPointers.add(pointers.get(i));
            i++;
        }
        newKeys.add(key);
        newPointers.add(pointer);
        while(i < keyCount){
            newKeys.add(keys.get(i));
            newPointers.add(pointers.get(i));
            i++;
        }
        newPointers.add(pointers.get(keyCount));

        keys = newKeys;
        pointers = newPointers;
        keyCount++;
    }
    public TreeNode splitLeaf(int splitLocation){
        ArrayList<Key> leftKeys = new ArrayList<>(), rightKeys = new ArrayList<>();
        ArrayList<Integer> leftPointers = new ArrayList<>(), rightPointers = new ArrayList<>();

        for(int i = 0; i < Consts.D; i++){
            leftKeys.add(keys.get(i));
            leftPointers.add(pointers.get(i));
            rightKeys.add(keys.get(Consts.D + i));
            rightPointers.add(pointers.get(Consts.D + i));
        }
        leftPointers.add(splitLocation);
        rightPointers.add(pointers.get(keyCount));


        TreeNode node = new TreeNode(true, Consts.D, rightKeys, rightPointers, keyStructure);
        keys = leftKeys;
        pointers = leftPointers;
        keyCount = Consts.D;
        return node;
    }
    public boolean isAlmostFull(){
        return keyCount == Consts.D * 2 - 1;
    }
    public boolean isTooSmall(){
        return keyCount < Consts.D;
    }
    public Key getSmallestKey(){
        return keys.get(0);
    }
    @Override
    public String toString() {
        return "TreeNode{" +
                "isLeaf=" + isLeaf +
                ", keyCount=" + keyCount +
                ", keys=" + keys +
                ", pointers=" + pointers +
                '}';
    }
}
