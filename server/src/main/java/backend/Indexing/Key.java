package backend.Indexing;

import backend.recordHandling.TypeConverter;
import lombok.Getter;

import java.util.ArrayList;

public class Key implements Comparable<Key> {

    @Getter
    private ArrayList<Object> key;

    @Getter
    private ArrayList<String> keyStructure;


    public Key(byte[] bytes, ArrayList<String> keyStructure){
        this.keyStructure = keyStructure;
        key = TypeConverter.toObjectList(keyStructure, bytes);
    }

    public Key(ArrayList<Object> key, ArrayList<String> keyStructure) {
        this.key = key;
        this.keyStructure = keyStructure;
    }

    public byte[] toBytes(){
        return TypeConverter.toBytes(keyStructure, key);
    }

    @Override
    public int compareTo(Key o) {
        ArrayList<Object> key = o.getKey();
        for (int i = 0; i < key.size(); i++) {
            int rel = TypeConverter.compare(keyStructure.get(i), this.key.get(i), key.get(i));
            if (rel < 0) {
                return -1;
            } else if (rel > 0) {
                return 1;
            }

        }
        return 0;
    }

    @Override
    public String toString() {
        return key.toString();
    }
}
