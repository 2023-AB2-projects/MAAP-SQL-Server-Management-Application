package backend.databaseModels;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class TableModel {
    @Getter
    @Setter
    private String tableName, fileName;

    @Getter
    @Setter
    private int rowLength;

    @Getter
    @Setter
    private ArrayList<FieldModel> fields;

    @Getter
    @Setter
    private PrimaryKeyModel primaryKey;

    @Getter
    @Setter
    private ArrayList<ForeignKeyModel> foreignKeys;

    @Getter
    @Setter
    private ArrayList<String> uniqueFields;

    @Getter
    @Setter
    private ArrayList<IndexFileModel> indexFiles;

    public TableModel() {
        this.tableName = "none";
        this.fileName = "none";
        this.rowLength = 0;
        this.fields = new ArrayList<>();
        this.primaryKey = new PrimaryKeyModel();
        this.foreignKeys = new ArrayList<>();
        this.uniqueFields = new ArrayList<>();
        this.indexFiles = new ArrayList<>();
    }

    public TableModel(String tableName, String fileName,
                      int rowLength, ArrayList<FieldModel> fields,
                      PrimaryKeyModel primaryKey, ArrayList<ForeignKeyModel> foreignKeys,
                      ArrayList<String> uniqueFields, ArrayList<IndexFileModel> indexFiles) {
        this.tableName = tableName;
        this.fileName = fileName;
        this.rowLength = rowLength;
        this.fields = fields;
        this.primaryKey = primaryKey;
        this.foreignKeys = foreignKeys;
        this.uniqueFields = uniqueFields;
        this.indexFiles = indexFiles;
    }
}
