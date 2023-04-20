package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

public class FieldModel {
    @Getter
    @Setter
    private String fieldName, type;

    @Getter
    @Setter
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
