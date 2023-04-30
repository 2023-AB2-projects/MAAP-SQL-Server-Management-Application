package backend.databaseActions;

import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.PrimaryKeyValuesContainDuplicates;
import backend.exceptions.validatorExceptions.UniqueFieldValuesContainDuplicates;

import java.io.IOException;

public interface DatabaseAction {
    Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist,
            IndexAlreadyExists, ForeignKeyFieldNotFound, IOException, RecordNotFoundException,
            PrimaryKeyValuesContainDuplicates, UniqueFieldValuesContainDuplicates;
}
