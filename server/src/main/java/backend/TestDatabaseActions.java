package backend;

import backend.databaseactions.DatabaseAction;
import backend.databaseactions.createactions.CreateDatabaseAction;
import backend.exceptions.DatabaseNameAlreadyExists;

import java.io.IOException;

public class TestDatabaseActions {
    public static void main(String[] args) {
        DatabaseAction createDatabase = new CreateDatabaseAction("Adrienke teszt3");
        try {
            createDatabase.actionPerform();
        } catch (IOException exception) {
            System.out.println("Create database -> IO exception");
            throw new RuntimeException(exception);
        } catch (DatabaseNameAlreadyExists exception) {
            System.out.println("Database name already exists");
            throw new RuntimeException(exception);
        }
    }
}
