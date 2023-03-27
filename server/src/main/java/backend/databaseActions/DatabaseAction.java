package backend.databaseActions;

import backend.exceptions.*;

public interface DatabaseAction {
    Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound, AttributeCantBeNull, AttributesAreNotUnique;
}
