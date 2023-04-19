package backend.databaseActions.dropActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.recordHandling.RecordDeleter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DeleteAction implements DatabaseAction {
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
