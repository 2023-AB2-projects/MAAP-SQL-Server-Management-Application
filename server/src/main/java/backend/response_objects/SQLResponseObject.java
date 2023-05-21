package backend.response_objects;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SQLResponseObject {
    // SQL response can contain a text message that is error or not
    private SQLTextResponse textResponse;

    // It can also contain table data -> ArrayList<ArrayList<String>>
    private ArrayList<ArrayList<String>> tableData;

    // Logic so we can tell which we sent
    private boolean isTextResponse;

    public SQLResponseObject(boolean isError, String text) {
        this.textResponse = new SQLTextResponse(isError, text);
        this.isTextResponse = true;
    }

    public SQLResponseObject(ArrayList<ArrayList<String>> tableData) {
        this.tableData = tableData;
        this.isTextResponse = false;
    }
}
