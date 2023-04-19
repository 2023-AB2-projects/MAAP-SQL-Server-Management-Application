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
        ArrayList<String> values = new ArrayList<>();
        values.add("1");
        values.add("null");
        RecordInserter recordInserter = new RecordInserter("master", "cars");
        recordInserter.insert(values);
        recordInserter.close();

        RecordHandler recordHandler = new RecordHandler("master", "cars");
        System.out.println(recordHandler.readLine(2));
//        ArrayList<String> key = new ArrayList<>();
//        key.add("0");

//        RecordDeleter recordDeleter = new RecordDeleter("master", "cars");
//        recordDeleter.deleteByPrimaryKey(key);
//        recordDeleter.close();
    }
}
