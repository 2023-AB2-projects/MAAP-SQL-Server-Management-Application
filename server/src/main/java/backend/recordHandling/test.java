package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class test {
    public static void main(String[] args) throws IOException, InvalidReadException {
        RecordInserter recordInserter = new RecordInserter("asd", "asd");
        ArrayList<String> asd = new ArrayList<>();
        asd.add("1");
        asd.add("1.2");
        asd.add("20");
        asd.add("monkey");
        asd.add("true");

        //recordInserter.insert(asd);

        RecordHandler recordHandler = new RecordHandler("asd", "asd");
        System.out.println(recordHandler.readLine(0));
    }
}
