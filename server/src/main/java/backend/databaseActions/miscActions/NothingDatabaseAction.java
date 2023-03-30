package backend.databaseActions.miscActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.*;

public class NothingDatabaseAction implements DatabaseAction {
    // it does nothing
    @Override
    public Object actionPerform()  {
        return null;
    }
}
