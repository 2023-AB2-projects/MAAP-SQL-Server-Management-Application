package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class test {
    public static void main(String[] args) throws IOException, InvalidReadException, RecordNotFoundException {
        RecordFinder recordFinder = new RecordFinder("master", "cars");
        ArrayList<String> key = new ArrayList<>();
        key.add("0");
        System.out.println( recordFinder.findByPrimaryKey(key));
        recordFinder.close();
    }
}
