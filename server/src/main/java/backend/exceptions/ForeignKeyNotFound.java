package backend.exceptions;

public class ForeignKeyNotFound extends Exception {
    public ForeignKeyNotFound(String tableName, String foreignKeyName) {
        super("In table=" + tableName + " there's no foreign key=" + foreignKeyName);
    }
}