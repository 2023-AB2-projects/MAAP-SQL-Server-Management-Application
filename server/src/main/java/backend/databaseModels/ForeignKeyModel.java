package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ForeignKeyModel {
    @Getter
    @Setter
    private String referencedTable;

    @Getter
    @Setter
    private ArrayList<String> referencedFields, referencingFields;

    public ForeignKeyModel() {
        this.referencedTable = "none";
        this.referencedFields = new ArrayList<>();
        this.referencingFields = new ArrayList<>();
    }

    public ForeignKeyModel(String referencedTable, ArrayList<String> referencedFields, ArrayList<String> referencingFields) {
        this.referencedTable = referencedTable;
        this.referencedFields = referencedFields;
        this.referencingFields = referencingFields;
    }
}
