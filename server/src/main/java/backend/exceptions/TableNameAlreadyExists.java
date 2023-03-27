package backend.exceptions;

public class TableNameAlreadyExists extends Exception {
    public TableNameAlreadyExists(String tableName) {
        super("Table name (=" + tableName + ") already exists in database!");
    }
}
