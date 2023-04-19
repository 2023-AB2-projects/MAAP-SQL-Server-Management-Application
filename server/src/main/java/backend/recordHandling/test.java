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
        /*RecordInserter recordInserter = new RecordInserter("asd", "asd");
        ArrayList<String> record = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();
        record.add("2");
        key.add("2");
        record.add("1.2");
        record.add("20");
        record.add("monkey");
        key.add("monkey");
        record.add("true");

        //recordInserter.insert(record);

        RecordHandler recordHandler = new RecordHandler("asd", "asd");
        RecordDeleter recordDeleter = new RecordDeleter("asd", "asd");


        try {
            recordDeleter.deleteByPrimaryKey(key);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


        recordInserter.close();
        recordHandler.close();
        recordDeleter.close();*/

        System.out.println(CatalogManager.getColumnNames("master", "cars"));
        System.out.println(CatalogManager.getColumnTypes("master","cars"));
        System.out.println(CatalogManager.getPrimaryKeys("master","cars"));
    }
}
