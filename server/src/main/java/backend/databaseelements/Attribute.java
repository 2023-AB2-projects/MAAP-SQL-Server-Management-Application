package backend.databaseelements;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
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
