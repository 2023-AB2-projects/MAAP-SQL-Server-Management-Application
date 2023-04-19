package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.InvalidTypeException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RecordHandler {
    private long recordSize;
    private final ArrayList<String> tableStructure;
    private final RandomAccessFile io;
    public RecordHandler(String databaseName, String tableName) throws FileNotFoundException {
        //some json magic here
        //fileLocation = getFileLocation(databaseName, tableName)
        //remove this
        //String fileLocation = "records/testFile.bin";
        String fileLocation = System.getProperty("user.dir") + "/src/main/resources/records/testFile.bin";

        //some other json magic here :)
        //tableStructure = getTableStructure(databaseName, tableName)
        //remove this
        tableStructure = new ArrayList<>();
        tableStructure.add("int");
        tableStructure.add("float");
        tableStructure.add("long");
        tableStructure.add("char(10)");
        tableStructure.add("bool");

        recordSize = 1;
        for (String type : tableStructure) {
            recordSize += sizeof(type);
        }
        io = new RandomAccessFile(fileLocation, "rw");
    }
    public void insert(ArrayList<String> values, int line) throws IOException {
        if (values.size() != tableStructure.size()){
            log.info("Wrong length of values");
            return;
        }
        long offset = line * recordSize;
        io.seek(offset);
        if (io.length() > offset){
            boolean deletionByte = io.readBoolean();
            if(deletionByte){
                log.info("Invalid location for write");
                return;
            }
            io.seek(offset);
        }

        io.writeBoolean(true);
        for(int i = 0; i < values.size(); i++){
            io.write(toBytes(tableStructure.get(i), values.get(i)));
        }
    }
    public void deleteLine(int line) throws IOException {
        long offset = line * recordSize;

        if(offset >= io.length()){
            log.info("offset too long");
        }

        io.seek(offset);
        boolean deletionByte = io.readBoolean();
        if(!deletionByte){
            log.info("Line is not written");
            return;
        }

        io.seek(offset);
        io.writeBoolean(false);
    }
    public ArrayList<String> readLine(int line) throws IOException, InvalidReadException {
        ArrayList<String> values = new ArrayList<>();
        long offset = line * recordSize;
        if(offset >= io.length()){
            throw new InvalidReadException();
        }

        io.seek(offset);
        boolean deletionByte = io.readBoolean();
        if(!deletionByte){
            throw new InvalidReadException();
        }

        for(String type : tableStructure){
            byte[] bytes = new byte[(int)sizeof(type)];
            io.readFully(bytes);
            values.add(decode(type, bytes));
        }

        return values;
    }
    public long getRecordCount() throws IOException {
        return io.length() / recordSize;
    }
    public void close() throws IOException {
        io.close();
    }

    private long sizeof(String type){
        switch (type) {
            case "int" -> { return Integer.BYTES; }
            case "long" -> { return Long.BYTES; }
            case "float" -> { return Float.BYTES; }
            case "double" -> { return Double.BYTES; }
            case "char" -> { return Character.BYTES; }
            case "bool" -> { return 1;}
            default -> {
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
    }

    private byte[] toBytes(String type, String value){
        ByteBuffer buffer;
        switch (type) {
            case "int" -> {
                buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt(Integer.parseInt(value));
                return buffer.array();
            }
            case "long" -> {
                buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(Long.parseLong(value));
                return buffer.array();
            }
            case "float" -> {
                buffer = ByteBuffer.allocate(Float.BYTES);
                buffer.putFloat(Float.parseFloat(value));
                return buffer.array();
            }
            case "double" -> {
                buffer = ByteBuffer.allocate(Double.BYTES);
                buffer.putDouble(Double.parseDouble(value));
                return buffer.array();
            }
            case "char" -> {
                buffer = ByteBuffer.allocate(Character.BYTES);
                buffer.putChar(value.charAt(0));
                return buffer.array();
            }
            case "bool" -> {
                byte[] b = new byte[1];
                if (value.equals("true") || value.equals("1")) {
                    b[0] = 1;
                }
                return b;
            }
            default -> {
                try {
                    return RecordStandardizer.formatString(value, type).getBytes(StandardCharsets.US_ASCII);
                }catch (InvalidTypeException e){
                    return new byte[0];
                }

            }
        }
    }

    private String decode(String type, byte[] bytes){
        ByteBuffer buffer;
        switch (type) {
            case "int" -> {
                return Integer.toString(ByteBuffer.wrap(bytes).getInt());
            }
            case "long" -> {
                return Long.toString(ByteBuffer.wrap(bytes).getLong());
            }
            case "float" -> {
                return Float.toString(ByteBuffer.wrap(bytes).getFloat());
            }
            case "double" -> {
                return Double.toString(ByteBuffer.wrap(bytes).getDouble());
            }
            case "char" -> {
                return Character.toString(ByteBuffer.wrap(bytes).getChar());
            }
            case "bool" -> {
                if(bytes[0] == 0){
                    return "false";
                }
                return "true";
            }
            default -> {
                Pattern pattern = Pattern.compile("char\\((\\d+)\\)");
                Matcher matcher = pattern.matcher(type);
                if (matcher.find()) {
                    return new String(bytes, StandardCharsets.US_ASCII);
                } else {
                    return "";
                }
            }
        }
    }
}
