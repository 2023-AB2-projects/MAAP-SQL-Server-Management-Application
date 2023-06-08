package backend.databaseActions.dropActions;

import backend.Indexing.indexValidators.DeleteRowValidator;
import backend.Utilities.BaseTable;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.conditions.Condition;
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

    private ArrayList<Condition> conditions;

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
        DeleteRowValidator validator = new DeleteRowValidator(this.databaseName, this.tableName);

        BaseTable table = new BaseTable(databaseName, tableName, conditions);
        validator.validateTable(table);
        table.delete();

        return null;
    }
}
