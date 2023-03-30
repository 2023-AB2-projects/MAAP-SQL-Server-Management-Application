package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

public class AttributeModel {
    @Getter
    @Setter
    private String attributeName, type;

    @Getter
    @Setter
    private int length;

    @Getter
    @Setter
    private boolean isNull, isNullable;

    public AttributeModel() {
        this.attributeName = "none";
        this.type = "none";
        this.length = 0;
        this.isNull = true;
        this.isNullable = true;
    }

    public AttributeModel(String attributeName, String type, int length, boolean isNull, boolean isNullable) {
        this.attributeName = attributeName;
        this.type = type;
        this.length = length;
        this.isNull = isNull;
        this.isNullable = isNullable;
    }
}
