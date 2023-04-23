package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;

@Slf4j
public class RecordHandler {
    private long recordSize;
    private final ArrayList<String> tableStructure;
    private final RandomAccessFile io;
    public RecordHandler(String databaseName, String tableName) throws FileNotFoundException {
        String fileLocation = CatalogManager.getTableDataPath(databaseName, tableName);

        tableStructure = (ArrayList<String>) CatalogManager.getColumnTypes(databaseName, tableName);

        recordSize = 1 + tableStructure.size();
        for (String type : tableStructure) {
            recordSize += TypeConverter.sizeof(type);
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
            if(values.get(i).equals("null")){
                io.writeBoolean(false);
                io.write(new byte[(int) TypeConverter.sizeof(tableStructure.get(i))]);
            }else{
                io.writeBoolean(true);
                io.write(TypeConverter.toBytes(tableStructure.get(i), values.get(i)));
            }

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
            boolean nullBit = io.readBoolean();
            byte[] bytes = new byte[(int) TypeConverter.sizeof(type)];
            io.readFully(bytes);
            if(nullBit){
                values.add(TypeConverter.toString(type, bytes));
            }else{
                values.add("null");
            }
        }

        return values;
    }
    public long getRecordCount() throws IOException {
        return io.length() / recordSize;
    }
    public void close() throws IOException {
        io.close();
    }

}
