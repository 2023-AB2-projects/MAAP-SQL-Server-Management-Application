package backend.exceptions.databaseActionsExceptions;

public class DatabaseNameAlreadyExists extends Exception {
    public DatabaseNameAlreadyExists(String databaseName) {
        super("Database with name '" + databaseName + "' already exists in catalog!");
    }
}
