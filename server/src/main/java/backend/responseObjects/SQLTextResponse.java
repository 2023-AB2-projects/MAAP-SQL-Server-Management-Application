package backend.responseObjects;

import lombok.Data;

import java.io.Serializable;

@Data
public class SQLTextResponse implements Serializable {
    private boolean isError;
    private String text;

    public SQLTextResponse(boolean isError, String text) {
        this.isError = isError;
        this.text = text;
    }
}
