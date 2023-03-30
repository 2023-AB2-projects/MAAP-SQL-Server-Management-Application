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
    private ArrayList<String> referencedAttributes, referencingAttributes;

    public ForeignKeyModel() {
        this.referencedTable = "none";
        this.referencedAttributes = new ArrayList<>();
        this.referencingAttributes = new ArrayList<>();
    }

    public ForeignKeyModel(String referencedTable, ArrayList<String> referencedAttributes, ArrayList<String> referencingAttributes) {
        this.referencedTable = referencedTable;
        this.referencedAttributes = referencedAttributes;
        this.referencingAttributes = referencingAttributes;
    }
}
