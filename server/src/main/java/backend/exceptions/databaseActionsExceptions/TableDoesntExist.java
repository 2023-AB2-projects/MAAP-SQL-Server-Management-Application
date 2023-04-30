package backend.exceptions.databaseActionsExceptions;

public class TableDoesntExist extends Exception {
    public TableDoesntExist(String tableName, String databaseName) {
        super("Table with name '" + tableName + "', doesn't exist in database '" + databaseName + "'!");
    }
}
