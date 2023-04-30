package backend.exceptions.databaseActionsExceptions;

public class ForeignKeyFieldNotFound extends Exception {
    public ForeignKeyFieldNotFound(String fieldName, String tableName) {
        super("Foreign key with name '" + fieldName + "' not found in table '" + tableName + "'");
    }
}
