package backend.databaseActions.dropActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import lombok.Setter;

public class DeleteDatabaseAction implements DatabaseAction {
    @Setter
    private String databaseName;
    @Setter
    private String tableName;

    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist, IndexAlreadyExists, ForeignKeyFieldNotFound {
        // itt jon a binaris filekezeles

        return null;
    }
}
