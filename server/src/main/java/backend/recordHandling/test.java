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
        asd.add("stange");
        recordHandler.insert(asd, 1);
        recordHandler.close();
    }
}
