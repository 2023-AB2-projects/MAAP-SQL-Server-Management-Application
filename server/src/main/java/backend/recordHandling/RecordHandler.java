package backend.recordHandling;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RecordHandler {
    private String tableName, fileLocation;
    private long recordSize;
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
        tableStructure.add("long");
        tableStructure.add("char(10)");

        recordSize = 0;
        for (String type : tableStructure) {
            System.out.println(sizeof(type));
            recordSize += sizeof(type);
        }
        io = new RandomAccessFile(fileLocation, "rw");
    }

    public void insert(ArrayList<String> values, int line){

        return;
    }

    public void close() throws IOException {
        io.close();
    }

    private long sizeof(String type){
        switch (type){
            case "int":
                return Integer.BYTES;
            case "long":
                return Long.BYTES;
            case "float":
                return Float.BYTES;
            case "double":
                return Double.BYTES;
            case "char":
                return Character.BYTES;
            default:
                Pattern pattern = Pattern.compile("char\\((\\d+)\\)");
                Matcher matcher = pattern.matcher(type);

                if(matcher.find()){
                    return Long.parseLong(matcher.group(1));
                }
                else{
                    return 0;
                }
        }
    }

    private byte[] toBytes(String type, String value){
        ByteBuffer buffer;
        switch (type){
            case "int":
                buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt(Integer.parseInt(value));
                return  buffer.array();
            case "long":
                buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(Long.parseLong(value));
                return  buffer.array();
            case "float":
                buffer = ByteBuffer.allocate(Float.BYTES);
                buffer.putFloat(Float.parseFloat(value));
                return  buffer.array();
            case "double":
                buffer = ByteBuffer.allocate(Double.BYTES);
                buffer.putDouble(Double.parseDouble(value));
                return  buffer.array();
            case "char":
                buffer = ByteBuffer.allocate(Character.BYTES);
                buffer.putChar(value.charAt(0));
                return  buffer.array();
            default:
                return value.getBytes(StandardCharsets.US_ASCII);
        }
    }
}
