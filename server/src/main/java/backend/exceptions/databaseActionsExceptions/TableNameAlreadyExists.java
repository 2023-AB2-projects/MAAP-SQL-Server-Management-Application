package backend.exceptions.databaseActionsExceptions;

public class TableNameAlreadyExists extends Exception {
    public TableNameAlreadyExists(String tableName) {
        super("Table with name '" + tableName + "' already exists in database!");
    }
}
