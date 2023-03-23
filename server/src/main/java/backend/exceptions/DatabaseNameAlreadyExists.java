package backend.exceptions;

public class DatabaseNameAlreadyExists extends Exception {
    public DatabaseNameAlreadyExists(String databaseName) {
        super("Database name (=" + databaseName + ") already exists in catalog!");
    }
}
