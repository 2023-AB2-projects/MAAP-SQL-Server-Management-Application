package backend.Indexing.indexValidators;

import backend.Indexing.UniqueIndexManager;
import backend.databaseModels.ForeignKeyModel;
import backend.exceptions.validatorExceptions.ForeignKeyValueNotFoundInParentTable;
import backend.exceptions.validatorExceptions.PrimaryKeyValueAlreadyInTable;
import backend.exceptions.validatorExceptions.UniqueValueAlreadyInTable;
import backend.service.CatalogManager;

import java.util.ArrayList;
import java.util.List;

public class InsertRowValidator {
    // List of field names, PK, FK and unique
    private final List<String> tableFieldNames, primaryKeyFieldNames, uniqueFieldNames;
    private final List<ForeignKeyModel> foreignKeys;

    // Primary key has one index manager, each FK and unique fields have one manager
    private final UniqueIndexManager primaryKeyIndexManager;
    private final List<UniqueIndexManager> uniqueIndexManagers, foreignKeyIndexManagers;

    private void validatePrimaryKey(ArrayList<String> primaryKeyFieldValues) throws PrimaryKeyValueAlreadyInTable {
        if (this.primaryKeyIndexManager.isPresent(primaryKeyFieldValues)) {
            throw new PrimaryKeyValueAlreadyInTable(primaryKeyFieldValues.toString());
        }
    }

    private void validateUniqueField(int uniqueInd, String uniqueFieldValue) throws UniqueValueAlreadyInTable {
        if (this.uniqueIndexManagers.get(uniqueInd).isPresent(new ArrayList<>(){{
            add(uniqueFieldValue);
        }})) {
            throw new UniqueValueAlreadyInTable(uniqueFieldValue);
        }
    }

    private void validateForeignKey(int foreignKeyInd, String foreignKeyValue) throws ForeignKeyValueNotFoundInParentTable {
        //TODO -> Only works for one field
        ArrayList<String> valueList = new ArrayList<>() {{
            add(foreignKeyValue);
        }};

        if (!this.foreignKeyIndexManagers.get(foreignKeyInd).isPresent(valueList)) {
            throw new ForeignKeyValueNotFoundInParentTable(foreignKeyValue);
        }
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
        List<String> foreignKeyIndexNames = CatalogManager.getForeignKeyReferencedIndexNames(databaseName, tableName);
        this.foreignKeyIndexManagers = new ArrayList<>(this.foreignKeys.size());
        int ind = 0;
        for (final ForeignKeyModel foreignKey : this.foreignKeys) {
            // The table is going to be the referenced table
            String referencedTableName = foreignKey.getReferencedTable();
            String indexName = foreignKeyIndexNames.get(ind++);

            this.foreignKeyIndexManagers.add(new UniqueIndexManager(databaseName, referencedTableName, indexName));
        }
    }

    public void validateRow(List<String> row) throws PrimaryKeyValueAlreadyInTable, UniqueValueAlreadyInTable, ForeignKeyValueNotFoundInParentTable {
        ArrayList<String> primaryKeyValues = new ArrayList<>();
        for (final String primaryKeyFieldName : primaryKeyFieldNames) {
            primaryKeyValues.add(row.get(tableFieldNames.indexOf(primaryKeyFieldName)));
        }

        ArrayList<String> uniqueFieldValues = new ArrayList<>();
        for (final String uniqueFieldName : uniqueFieldNames) {
            uniqueFieldValues.add(row.get(tableFieldNames.indexOf(uniqueFieldName)));
        }

        ArrayList<String> foreignKeyValues = new ArrayList<>();
        for (final ForeignKeyModel foreignKey : foreignKeys) {
            foreignKeyValues.add(row.get(tableFieldNames.indexOf(foreignKey.getReferencingFields().get(0))));
        }

        // Validation process
        // 1. Primary keys
        this.validatePrimaryKey(primaryKeyValues);

        // 2. Unique values
        int ind = 0;
        for (final String uniqueFieldValue : uniqueFieldValues) {
            this.validateUniqueField(ind++, uniqueFieldValue);
        }

        // 3. Foreign key values
        ind = 0;
        for (final String foreignKeyValue : foreignKeyValues) {
            this.validateForeignKey(ind++, foreignKeyValue);
        }
    }
}
