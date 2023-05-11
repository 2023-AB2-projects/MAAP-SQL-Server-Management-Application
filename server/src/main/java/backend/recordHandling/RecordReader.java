package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidReadException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class RecordReader {
    private RecordHandler recordHandler;

    public RecordReader(String databaseName, String tableName) throws FileNotFoundException {
        recordHandler = new RecordHandler(databaseName, tableName);
    }

    public ArrayList<ArrayList<Object>> scan() throws IOException {
        ArrayList<ArrayList<Object>> table = new ArrayList<>();
        for(int i = 0; i < recordHandler.getRecordCount(); i++){
            try{
                table.add(recordHandler.readLineAsObjectList(i));
            }catch (InvalidReadException ignored){}
        }
        return table;
    }

    public ArrayList<ArrayList<Object>> scan(ArrayList<Integer> pointers) throws IOException, InvalidReadException {
        ArrayList<ArrayList<Object>> table = new ArrayList<>();
        for(int i : pointers){
            table.add(recordHandler.readLineAsObjectList(i));
        }
        return table;
    }

    public void close() throws IOException {
        recordHandler.close();
    }
}
