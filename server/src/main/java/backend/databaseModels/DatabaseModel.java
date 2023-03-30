package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class DatabaseModel {
    @Getter
    @Setter
    private String databaseName;

    @Getter
    @Setter
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
