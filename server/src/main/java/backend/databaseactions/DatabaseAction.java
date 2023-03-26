package backend.databaseactions;

import backend.exceptions.*;

public interface DatabaseAction {
    void actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound;
}
