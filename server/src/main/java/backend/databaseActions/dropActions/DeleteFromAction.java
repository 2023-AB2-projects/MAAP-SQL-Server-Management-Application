package backend.databaseActions.dropActions;

import backend.Indexing.indexValidators.DeleteRowValidator;
import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.ForeignKeyValueIsBeingReferencedInAnotherTable;
import backend.recordHandling.RecordDeleter;
import backend.service.CatalogManager;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@AllArgsConstructor
public class DeleteFromAction implements DatabaseAction {
    @Setter
    private String databaseName;
    @Setter
    private String tableName;
    @Setter
    private ArrayList<String> primaryKeys;

    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist, IndexAlreadyExists, ForeignKeyFieldNotFound, IOException, RecordNotFoundException, InvalidReadException, ForeignKeyValueIsBeingReferencedInAnotherTable {
        // ----------------------------------- CHECK DB, TABLE NAME ------------------------------------------------- //
        if (!CatalogManager.getDatabaseNames().contains(this.databaseName)) {
            throw new DatabaseDoesntExist(this.databaseName);
        }

        if (!CatalogManager.getCurrentDatabaseTableNames().contains(this.tableName)) {
            throw new TableDoesntExist(this.tableName, this.databaseName);
        }
        // ---------------------------------- / CHECK DB, TABLE NAME ------------------------------------------------ //
        RecordDeleter recordDeleter = new RecordDeleter(this.databaseName, this.tableName);
        DeleteRowValidator validator = new DeleteRowValidator(this.databaseName, this.tableName);

        ArrayList<String> row;
        for (final String pKValue : this.primaryKeys) {
            ArrayList<String> pKValueList = new ArrayList<>(){{ add(pKValue); }};

            try {
                row = recordDeleter.readLineToBeDeleted(pKValueList);
            } catch (KeyNotFoundException e) {
                log.error("Primary key value not found in table");
                throw new RuntimeException(e);
            }

            // Validate row
            validator.validateRow(row);
        }

        for (final String pKValue : this.primaryKeys) {
            ArrayList<String> pKValueList = new ArrayList<>() {{ add(pKValue); }};
            // Delete row
            recordDeleter.deleteByPrimaryKey(pKValueList);
        }

        return null;
    }
}
