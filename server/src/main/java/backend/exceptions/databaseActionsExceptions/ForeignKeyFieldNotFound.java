package backend.exceptions.databaseActionsExceptions;

public class ForeignKeyFieldNotFound extends Exception {
    public ForeignKeyFieldNotFound(String fieldName, String tableName) {
        super("Foreign key fieldName=" + fieldName + " not found in table=" + tableName);
    }
}
