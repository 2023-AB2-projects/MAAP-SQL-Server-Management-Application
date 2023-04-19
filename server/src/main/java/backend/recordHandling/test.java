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
        /*RecordHandler recordHandler = new RecordHandler("master", "cars");
        ArrayList<String> asd = new ArrayList<>();
        asd.add("0");
        asd.add("a");
        //recordHandler.insert(asd, 0);
        System.out.println(recordHandler.readLine(0));
        recordHandler.close();*/

        System.out.println(CatalogManager.getPrimaryKeyTypes("master","cars"));
    }
}
