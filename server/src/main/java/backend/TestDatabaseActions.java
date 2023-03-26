package backend;

import backend.databaseactions.DatabaseAction;
import backend.databaseactions.createactions.CreateDatabaseAction;
import backend.databaseactions.createactions.CreateTableAction;
import backend.exceptions.DatabaseDoesntExist;
import backend.exceptions.DatabaseNameAlreadyExists;
import backend.exceptions.TableNameAlreadyExists;

import java.io.IOException;
import java.util.ArrayList;

public class TestDatabaseActions {
    public static void main(String[] args) {

        // CreateDatabase
        /* DatabaseAction createDatabase = new CreateDatabaseAction("Adrienke teszt");
        try {
            createDatabase.actionPerform();
        } catch (DatabaseNameAlreadyExists exception) {
            System.out.println("Database name already exists");
        } */

        // CreateTable
        CreateTableAction createTable = new CreateTableAction("master", "tablaNev",
                "fileNev", 100, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        try {
            createTable.actionPerform();
        } catch (TableNameAlreadyExists exception) {
            System.out.println("Table name already exists!");
        } catch (DatabaseDoesntExist exception) {
            System.out.println("Database doesn't exist!");
        }
    }
}
