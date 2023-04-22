package backend.recordHandling;

import backend.Indexing.IndexFIleHandler;
import backend.Indexing.Key;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
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
        byte[] bytes = {0,0,0,1,0,0,0,1};
        ArrayList<String> types = new ArrayList<>();
        types.add("int");
        types.add("float");

        Key key = new Key(bytes, types);
        System.out.println(Arrays.toString(key.toBytes()));
    }
}
