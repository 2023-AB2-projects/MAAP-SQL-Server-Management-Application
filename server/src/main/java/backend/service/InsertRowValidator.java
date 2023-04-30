package backend.service;

import backend.Indexing.UniqueIndexManager;
import backend.databaseModels.ForeignKeyModel;

import java.util.ArrayList;
import java.util.List;

public class InsertRowValidator {
    // List of field names, PK, FK and unique
    private final List<String> tableFieldNames, primaryKeyFieldNames, uniqueFieldNames;
    private final List<ForeignKeyModel> foreignKeys;

    // Primary key has one index manager, each FK and unique fields have one manager
    private final UniqueIndexManager primaryKeyIndexManager;
    private final List<UniqueIndexManager> uniqueIndexManagers, foreignKeyIndexManagers;

    private void validateUniqueField(String uniqueFieldName, String uniqueFieldValue) {}

    private void validateForeignKey(String foreignKeyName, String foreignKeyValue) {
        // foreach foreign key
    }

    private void validatePrimaryKey(List<String> primaryKeyFieldValues) {
    }

    public InsertRowValidator(String databaseName, String tableName) {
        // Get table field names
        this.tableFieldNames = CatalogManager.getFieldNames(databaseName, tableName);

        // Find each primary key, unique field and foreign key in table
        this.primaryKeyFieldNames = CatalogManager.getPrimaryKeyFieldNames(databaseName, tableName);
        this.uniqueFieldNames = CatalogManager.getUniqueFieldNames(databaseName, tableName);
        this.foreignKeys = CatalogManager.getForeignKeys(databaseName, tableName);  // Models !!!

        // Create index for primary key
        String pKIndexName = CatalogManager.getPrimaryKeyIndexName(databaseName, tableName);
        this.primaryKeyIndexManager = new UniqueIndexManager(databaseName, tableName, pKIndexName);

        // All the unique key index names
        List<String> uniqueIndexNames = CatalogManager.getUniqueFieldIndexNames(databaseName, tableName);
        this.uniqueIndexManagers = new ArrayList<>(this.uniqueFieldNames.size());
        for (final String indexName : uniqueIndexNames) {
            this.uniqueIndexManagers.add(new UniqueIndexManager(databaseName, tableName, indexName));
        }

        // All the foreign key index names
        List<String> foreignKeyIndexNames = CatalogManager.getForeignKeyIndexNames(databaseName, tableName);
        this.foreignKeyIndexManagers = new ArrayList<>(this.foreignKeys.size());
        for (final String indexName : uniqueIndexNames) {
            this.foreignKeyIndexManagers.add(new UniqueIndexManager(databaseName, tableName, indexName));
        }
    }

    public void validateRow(List<String> row) {
        List<String> primaryKeyValues = new ArrayList<>();
        for (final String primaryKeyFieldName : primaryKeyFieldNames) {
            primaryKeyValues.add(row.get(tableFieldNames.indexOf(primaryKeyFieldName)));
        }

        List<String> uniqueFieldValues = new ArrayList<>();
        for (final String uniqueFieldName : uniqueFieldNames) {
            uniqueFieldValues.add(row.get(tableFieldNames.indexOf(uniqueFieldName)));
        }

        List<String> foreignKeyValues = new ArrayList<>();
        for (final ForeignKeyModel foreignKey : foreignKeys) {
            foreignKeyValues.add(row.get(tableFieldNames.indexOf(foreignKey.getReferencingFields().get(0))));   //TODO!!!
        }

        // Same for unique, foreign

    }
}
