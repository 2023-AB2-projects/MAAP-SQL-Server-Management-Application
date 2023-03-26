package backend.databaseelements;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Attribute {
    @JsonProperty
    private String attributeName, type;

    @JsonProperty
    private int length;

    @JsonProperty
    private boolean isNull;

    public Attribute(String attributeName, String type, int length, boolean isNull) {
        this.attributeName = attributeName;
        this.type = type;
        this.length = length;
        this.isNull = isNull;
    }
}
