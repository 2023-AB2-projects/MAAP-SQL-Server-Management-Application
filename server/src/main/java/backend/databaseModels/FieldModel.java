package backend.databaseModels;

import lombok.Data;

@Data
public class FieldModel {
    private String fieldName, type;

    private boolean isNullable;

    public FieldModel() {
        this.fieldName = "none";
        this.type = "none";
        this.isNullable = true;
    }

    public FieldModel(String fieldName, String type, boolean isNullable) {
        this.fieldName = fieldName;
        this.type = type;
        this.isNullable = isNullable;
    }
}
