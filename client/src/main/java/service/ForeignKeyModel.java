package service;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ForeignKeyModel {
    private String referencedTable;

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
