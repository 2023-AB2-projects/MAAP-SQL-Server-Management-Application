package backend.Indexing.indexValidators;

import backend.Indexing.UniqueIndexManager;
import backend.databaseModels.ForeignKeyModel;
import backend.exceptions.validatorExceptions.ForeignKeyValueIsBeingReferencedInAnotherTable;
import backend.service.CatalogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteRowValidator {
    // List of field names and foreign keys
    private final List<String> tableFieldNames;
    private final List<ForeignKeyModel> referencingForeignKeys;

    // Each foreign key has an index manager
    private final List<UniqueIndexManager> foreignKeyIndexManagers;

    private void validateForeignKey(int foreignKeyInd, String foreignKeyValue) throws ForeignKeyValueIsBeingReferencedInAnotherTable {
        ArrayList<String> valueList = new ArrayList<>() {{
            add(foreignKeyValue);
        }};

        if (this.foreignKeyIndexManagers.get(foreignKeyInd).isPresent(valueList)) {
            throw new ForeignKeyValueIsBeingReferencedInAnotherTable(foreignKeyValue, this.referencingForeignKeys.get(foreignKeyInd).getReferencedTable());
        }
    }

    public DeleteRowValidator(String databaseName, String tableName) {
        // Get table field names
        this.tableFieldNames = CatalogManager.getFieldNames(databaseName, tableName);
        this.referencingForeignKeys = CatalogManager.getForeignKeysReferencingThisTable(databaseName, tableName);  // Models !!!

        // All the foreign key index names
        List<String> foreignKeyIndexNames = new ArrayList<>();
        for (final ForeignKeyModel key : this.referencingForeignKeys) {
            foreignKeyIndexNames.add(CatalogManager.getFlattenedName(key.getReferencedFields()));        // We flatten the referenced fields
            // The flattened referenced fields are the names of the index files in the other table
        }

        List<String> foreignKeyTableNames = CatalogManager.getForeignKeysTableNamesReferencingThisTable(databaseName, tableName);

        this.foreignKeyIndexManagers = new ArrayList<>(this.referencingForeignKeys.size());
        int ind = 0;
        for (final ForeignKeyModel foreignKey : this.referencingForeignKeys) {
            // The table is going to be the referenced table
            String foreignKeyTableName = foreignKeyTableNames.get(ind);
            String indexName = foreignKeyIndexNames.get(ind);
            ind++;

            this.foreignKeyIndexManagers.add(new UniqueIndexManager(databaseName, foreignKeyTableName, indexName));
        }
    }

    public void validateRow(ArrayList<String> row) throws ForeignKeyValueIsBeingReferencedInAnotherTable {
        ArrayList<String> foreignKeyValues = new ArrayList<>();
        for (final ForeignKeyModel foreignKey : referencingForeignKeys) {
            String referencedField = foreignKey.getReferencedFields().get(0);
            String value = row.get(tableFieldNames.indexOf(referencedField));
            foreignKeyValues.add(value);
        }

        // Check that every foreign key value is found in parent table
        int ind = 0;
        for (final String foreignKeyValue : foreignKeyValues) {
            this.validateForeignKey(ind++, foreignKeyValue);
        }
    }
}
