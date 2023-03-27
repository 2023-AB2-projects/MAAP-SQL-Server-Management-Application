package backend.exceptions;

public class TableDoesntExist extends Exception {
    public TableDoesntExist(String tableName, String databaseName) {
        super("Table=" + tableName + ", doesn't exist in database=" + databaseName + "!");
    }
}
