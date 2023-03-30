package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class PrimaryKeyModel {
    @Getter
    @Setter
    private ArrayList<String> primaryKeyFields;

    public PrimaryKeyModel() {
        this.primaryKeyFields = new ArrayList<>();
    }

    public PrimaryKeyModel(ArrayList<String> primaryKeyFields) {
        this.primaryKeyFields = primaryKeyFields;
    }
}
