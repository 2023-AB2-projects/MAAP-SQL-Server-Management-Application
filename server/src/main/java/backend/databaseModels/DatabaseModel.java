package backend.databaseModels;

import lombok.Data;

import java.util.ArrayList;

@Data
public class DatabaseModel {
    private String databaseName;

    private ArrayList<TableModel> tables;

    public DatabaseModel() {
        this.databaseName = "none";
        this.tables = new ArrayList<>();
    }

    public DatabaseModel(String databaseName, ArrayList<TableModel> tables) {
        this.databaseName = databaseName;
        this.tables = tables;
    }
}
