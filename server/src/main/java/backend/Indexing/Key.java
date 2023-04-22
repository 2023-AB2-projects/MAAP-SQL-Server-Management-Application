package backend.Indexing;

import backend.recordHandling.ByteConverter;
import lombok.Getter;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;

public class Key implements Comparable<Key> {

    @Getter
    private ArrayList<Object> key;

    @Getter
    private ArrayList<String> keyStructure;


    public Key(byte[] bytes, ArrayList<String> keyStructure){
        this.keyStructure = keyStructure;
        key = ByteConverter.toList(keyStructure, bytes);
    }

    public byte[] toBytes(){
        return ByteConverter.toBytes(keyStructure, key);
    }

    @Override
    public int compareTo(Key o) {
        ArrayList<Object> key = o.getKey();
        for (int i = 0; i < key.size(); i++) {
            int rel = ByteConverter.compare(keyStructure.get(i), this.key.get(i), key.get(i));
            if (rel < 0) {
                return -1;
            } else if (rel > 0) {
                return 1;
            }

        }
        return 0;
    }
}
