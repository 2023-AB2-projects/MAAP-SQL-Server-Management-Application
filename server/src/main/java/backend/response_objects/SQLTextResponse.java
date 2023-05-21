package backend.response_objects;

import lombok.Data;

@Data
public class SQLTextResponse {
    private boolean isError;
    private String text;

    public SQLTextResponse(boolean isError, String text) {
        this.isError = isError;
        this.text = text;
    }
}
