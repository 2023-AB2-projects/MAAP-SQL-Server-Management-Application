package backend.recordHandling;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
public class RecordHandler {
    private String tableName, fileLocation;
    private ArrayList<String> tableStructure;
    private RandomAccessFile io;

    public RecordHandler(String tableName) throws IOException {
        this.tableName = tableName;
        //fileLocation = getFileLocation(tableName)
        fileLocation = "records/testFile.bin";

        //tableStructure = getTableStructure(tableName)
        tableStructure = new ArrayList<>();
        tableStructure.add("int");
        tableStructure.add("float");
        tableStructure.add("char(10)");

        io = new RandomAccessFile(fileLocation, "rw");

        io.seek(1);
        io.write("asdfg".getBytes(StandardCharsets.UTF_8));
    }

    public void insert(ArrayList<String> values, int line){
        return;
    }

    public void close() throws IOException {
        io.close();
    }
}
