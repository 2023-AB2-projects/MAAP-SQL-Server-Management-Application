package backend.databaseModels;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PrimaryKeyModel {
    private ArrayList<String> primaryKeyFields;

    public PrimaryKeyModel() {
        this.primaryKeyFields = new ArrayList<>();
    }

    public PrimaryKeyModel(ArrayList<String> primaryKeyFields) {
        this.primaryKeyFields = primaryKeyFields;
    }
}
