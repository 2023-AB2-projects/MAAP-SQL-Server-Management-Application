package backend.Indexing.indexValidators;

import backend.Indexing.UniqueIndexManager;
import backend.Utilities.BaseTable;
import backend.databaseModels.ForeignKeyModel;
import backend.exceptions.validatorExceptions.ForeignKeyValueIsBeingReferencedInAnotherTable;
import backend.service.CatalogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteRowValidator {
    // List of field names and foreign keys
    private final String dataBaseName, tableName;
    public DeleteRowValidator(String databaseName, String tableName) {
        this.dataBaseName = databaseName;
        this.tableName = tableName;
    }

    public void validateTable(BaseTable table) throws ForeignKeyValueIsBeingReferencedInAnotherTable {
        if (CatalogManager.getForeignKeysTableNamesReferencingThisTable(dataBaseName, tableName).size() != 0) {
            throw new ForeignKeyValueIsBeingReferencedInAnotherTable();
        }
    }
}
