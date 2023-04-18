package backend.databaseActions.createActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import lombok.Setter;

import java.util.List;

public class InsertDatabaseAction implements DatabaseAction {
    @Setter
    private String databaseName;
    @Setter
    private String tableName;
    @Setter
    private List<String> columnNames;
    @Setter
    private Object [][] values;


    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist, IndexAlreadyExists, ForeignKeyFieldNotFound {
        // the magic happens here...
        // binaris file kezeles
        // FIXME

        return null;
    }
}
