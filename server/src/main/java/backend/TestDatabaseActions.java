package backend;

import backend.databaseactions.DatabaseAction;
import backend.databaseactions.createactions.CreateDatabaseAction;
import backend.databaseactions.createactions.CreateTableAction;
import backend.databaseelements.Attribute;
import backend.databaseelements.IndexFile;
import backend.exceptions.*;

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
        String databaseName = "master", tableName = "People", fileName = "PeopleTableFile";
        int rowLength = 50;
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Name", "char", 100, false));
        attributes.add(new Attribute("Age", "int", 0, false));
        attributes.add(new Attribute("Height", "int", 0, false));
        ArrayList<String> pKAttributes = new ArrayList<>(), fKAttributes = new ArrayList<>();
        pKAttributes.add("Name");
        fKAttributes.add("Height");
        ArrayList<IndexFile> indexFiles = new ArrayList<>();

        CreateTableAction createTable = new CreateTableAction(databaseName, tableName, fileName,
                rowLength, attributes, pKAttributes, fKAttributes, indexFiles);
        try {
            createTable.actionPerform();
        } catch (TableNameAlreadyExists exception) {
            System.out.println("Table name already exists!");
        } catch (DatabaseDoesntExist exception) {
            System.out.println("Database doesn't exist!");
        } catch (PrimaryKeyNotFound e) {
            System.out.println("Primary key is not found in table attributes!");
            throw new RuntimeException(e);
        } catch (ForeignKeyNotFound e) {
            System.out.println("Foreign key is not found in table attributes!");
            throw new RuntimeException(e);
        }
    }
}
