package backend.exceptions;

public class DatabaseDoesntExist extends Exception {
    public DatabaseDoesntExist(String databaseName) {
        super("Database " + databaseName + " doesn't exist!");
    }
}
