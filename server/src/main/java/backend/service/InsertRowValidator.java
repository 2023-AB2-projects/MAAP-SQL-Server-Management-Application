package backend.service;

import java.util.ArrayList;

public class InsertRowValidator {
    private ArrayList<String> primaryKeyFieldNames, uniqueFieldNames, foreignKeyFieldNames;
    private ArrayList<String> tableFieldNames;

    private void validateUniqueField(String uniqueFieldName, String uniqueFieldValue) {}

    private void validateForeignKey(String foreignKeyName, String foreignKeyValue) {
        // foreach foreign key
        // UniqueKeyIndexManager manager = new...(databaseName, tableName, indexName)
    }

    private void validatePrimaryKey(ArrayList<String> primaryKeyFieldValues) {
        //UniqueIndexManager manager = new (datababse, table, "primaryKeyIndex")
    }

    public InsertRowValidator(String databaseName, String tableName) {
        // Get field names
        // PrimaryKeyIndexManager manager = new ...(databaseName, tableName)
    }


    public void validateRow(ArrayList<String> row) {
        // 'alma', 'dio', 1
        ArrayList<String> primaryKeyValues = new ArrayList<>();
        for (final String primaryKeyFieldName : primaryKeyFieldNames) {
            primaryKeyValues.add(row.get(tableFieldNames.indexOf(primaryKeyFieldName)));
        }

        // Same for unique, foreign

    }
}
