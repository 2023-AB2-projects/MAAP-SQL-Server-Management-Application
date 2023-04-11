package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class test {
    public static void main(String[] args) throws IOException, InvalidReadException, RecordNotFoundException {
        RecordInserter recordInserter = new RecordInserter("asd", "asd");
        ArrayList<String> asd = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();
        asd.add("2");
        key.add("2");
        asd.add("1.2");
        key.add("1.2");
        asd.add("20");
        asd.add("monkey");
        asd.add("true");

        recordInserter.insert(asd);

        RecordHandler recordHandler = new RecordHandler("asd", "asd");
        RecordFinder recordFinder = new RecordFinder("asd", "Asd");
        System.out.println(recordFinder.findByPrimaryKey(key));
        System.out.println(recordHandler.readLine(0));

        recordFinder.close();
        recordInserter.close();
        recordHandler.close();
    }
}
