package backend.databaseactions;

import backend.exceptions.DatabaseDoesntExist;
import backend.exceptions.DatabaseNameAlreadyExists;
import backend.exceptions.TableNameAlreadyExists;

public interface DatabaseAction {
    void actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist;
}
