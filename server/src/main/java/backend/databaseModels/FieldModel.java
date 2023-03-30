package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

public class FieldModel {
    @Getter
    @Setter
    private String fieldName, type;

    @Getter
    @Setter
    private int length;

    @Getter
    @Setter
    private boolean isNull, isNullable;

    public FieldModel() {
        this.fieldName = "none";
        this.type = "none";
        this.length = 0;
        this.isNull = true;
        this.isNullable = true;
    }

    public FieldModel(String fieldName, String type, int length, boolean isNull, boolean isNullable) {
        this.fieldName = fieldName;
        this.type = type;
        this.length = length;
        this.isNull = isNull;
        this.isNullable = isNullable;
    }
}
