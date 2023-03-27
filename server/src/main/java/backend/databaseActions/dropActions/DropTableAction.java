package backend.databaseActions.dropActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.AttributeCantBeNull;
import backend.exceptions.AttributesAreNotUnique;
import backend.exceptions.DatabaseDoesntExist;
import backend.exceptions.DatabaseNameAlreadyExists;
import backend.exceptions.ForeignKeyNotFound;
import backend.exceptions.PrimaryKeyNotFound;
import backend.exceptions.TableNameAlreadyExists;

public class DropTableAction implements DatabaseAction {

    @Override
    public void actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound, AttributeCantBeNull, AttributesAreNotUnique {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerform'");
    }
    
}
