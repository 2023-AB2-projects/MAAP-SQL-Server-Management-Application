package backend.databaseModels;

import lombok.Data;

import java.util.ArrayDeque;
import java.util.ArrayList;

@Data
public class TableModel {
    private String tableName, fileName;

    private ArrayList<FieldModel> fields;

    private ArrayDeque<Integer> deletedRecordLines;

    private PrimaryKeyModel primaryKey;

    private ArrayList<ForeignKeyModel> foreignKeys;

    private ArrayList<String> uniqueFields;

    private ArrayList<IndexFileModel> indexFiles;

    public TableModel() {
        this.tableName = "none";
        this.fileName = "none";
        this.fields = new ArrayList<>();
        this.deletedRecordLines = new ArrayDeque<>();
        this.primaryKey = new PrimaryKeyModel();
        this.foreignKeys = new ArrayList<>();
        this.uniqueFields = new ArrayList<>();
        this.indexFiles = new ArrayList<>();
    }

    public TableModel(String tableName, String fileName, ArrayList<FieldModel> fields,
                      PrimaryKeyModel primaryKey, ArrayList<ForeignKeyModel> foreignKeys,
                      ArrayList<String> uniqueFields, ArrayList<IndexFileModel> indexFiles) {
        this.tableName = tableName;
        this.fileName = fileName;
        this.fields = fields;
        this.deletedRecordLines = new ArrayDeque<>();
        this.primaryKey = primaryKey;
        this.foreignKeys = foreignKeys;
        this.uniqueFields = uniqueFields;
        this.indexFiles = indexFiles;
    }
}
