package backend.recordHandling;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class test {
    public static void main(String[] args) throws IOException {
        RecordHandler recordHandler = new RecordHandler("asd");
        ArrayList<String> asd = new ArrayList<>();
        asd.add("1");
        asd.add("1.2");
        asd.add("20");
        asd.add("strange");
        asd.add("true");
        //recordHandler.insert(asd, 0);
        System.out.println(recordHandler.readLine(2));
        recordHandler.close();
    }
}
