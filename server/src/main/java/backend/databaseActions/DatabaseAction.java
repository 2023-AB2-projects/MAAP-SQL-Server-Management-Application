package backend.databaseActions;

import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.*;

import java.io.IOException;

public interface DatabaseAction {
    Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist,
            IndexAlreadyExists, ForeignKeyFieldNotFound, IOException, RecordNotFoundException,
            PrimaryKeyValuesContainDuplicates, UniqueFieldValuesContainDuplicates,
            PrimaryKeyValueAlreadyInTable, UniqueValueAlreadyInTable, ForeignKeyValueNotFoundInParentTable, InvalidReadException;
}
