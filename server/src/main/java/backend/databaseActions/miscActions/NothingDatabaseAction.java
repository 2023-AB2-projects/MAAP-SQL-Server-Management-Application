package backend.databaseActions.miscActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.*;

public class NothingDatabaseAction implements DatabaseAction {
    // it does nothing
    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, AttributeCantBeNull, AttributesAreNotUnique, TableDoesntExist {
        return null;
    }
}
