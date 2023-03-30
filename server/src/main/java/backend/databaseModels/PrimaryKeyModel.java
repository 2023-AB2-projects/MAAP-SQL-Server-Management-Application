package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class PrimaryKeyModel {
    @Getter
    @Setter
    private ArrayList<String> primaryKeyAttributes;

    public PrimaryKeyModel() {
        this.primaryKeyAttributes = new ArrayList<>();
    }

    public PrimaryKeyModel(ArrayList<String> primaryKeyAttributes) {
        this.primaryKeyAttributes = primaryKeyAttributes;
    }
}
