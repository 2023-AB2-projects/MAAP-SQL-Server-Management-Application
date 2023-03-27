package backend.exceptions;

public class PrimaryKeyNotFound extends Exception {
    public PrimaryKeyNotFound(String tableName, String primaryKeyName) {
        super("In table=" + tableName + " there's no primary key=" + primaryKeyName);
    }
}
