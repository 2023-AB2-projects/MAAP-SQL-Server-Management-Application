package backend.databaseActions.dropActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.recordHandling.RecordDeleter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;

@AllArgsConstructor
public class DeleteFromAction implements DatabaseAction {
    @Setter
    private String databaseName;
    @Setter
    private String tableName;
    @Setter
    private ArrayList<String> primaryKeys;

    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist, IndexAlreadyExists, ForeignKeyFieldNotFound, IOException, RecordNotFoundException {
        RecordDeleter recordDeleter = new RecordDeleter(databaseName, tableName);
        recordDeleter.deleteByPrimaryKey(primaryKeys);
        
        return null;
    }
}
