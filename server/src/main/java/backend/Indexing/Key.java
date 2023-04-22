package backend.Indexing;

import backend.recordHandling.ByteConverter;
import lombok.Getter;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Key {

    @Getter
    private ArrayList<Object> key;
    private ArrayList<String> keyStructure;


    public Key(byte[] bytes, ArrayList<String> keyStructure){
        this.keyStructure = keyStructure;
        key = ByteConverter.toList(keyStructure, bytes);
    }

    public byte[] toBytes(){
        return ByteConverter.toBytes(keyStructure, key);
    }
}
