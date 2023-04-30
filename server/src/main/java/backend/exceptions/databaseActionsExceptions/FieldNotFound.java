package backend.exceptions.databaseActionsExceptions;

public class FieldNotFound extends Exception {
    public FieldNotFound(String fieldName, String tableName) {
        super("Field '" + fieldName + "' not found in table '" + tableName + "'!");
    }
}
