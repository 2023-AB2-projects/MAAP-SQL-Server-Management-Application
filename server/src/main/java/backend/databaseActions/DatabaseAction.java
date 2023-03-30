package backend.databaseActions;

import backend.exceptions.databaseActionsExceptions.*;

public interface DatabaseAction {
    Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist;
}
