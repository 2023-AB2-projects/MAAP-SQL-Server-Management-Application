package backend;

import backend.databaseactions.createactions.CreateTableAction;
import backend.databaseelements.Attribute;
import backend.databaseelements.ForeignKey;
import backend.databaseelements.IndexFile;
import backend.databaseelements.PrimaryKey;
import backend.exceptions.*;

import java.util.ArrayList;

public class TestDatabaseActions {
    public void createPeopleTable() {
        String databaseName = "master", tableName = "People", fileName = "PeopleTableFile";
        int rowLength = 50;
        ArrayList<Attribute> attributes = new ArrayList<>(){{
            add(new Attribute("id", "int", 0, false));
            add(new Attribute("name", "char", 100, false));
            add(new Attribute("age", "int", 0, false));
            add(new Attribute("height", "int", 0, false));
        }};
        PrimaryKey primaryKey = new PrimaryKey(new ArrayList<>(){{ add("id"); }});
        ArrayList<ForeignKey> foreignKeys = new ArrayList<>();
        ArrayList<IndexFile> indexFiles = new ArrayList<>();
        ArrayList<String> uniqueAttributes = new ArrayList<>(){{
            add("name");
        }};

        CreateTableAction createTable = new CreateTableAction(databaseName, tableName, fileName,
                rowLength, attributes, primaryKey, foreignKeys, uniqueAttributes, indexFiles);
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

    public void createCarsTable() {
        // Other table
        String databaseName = "master", tableName = "Cars", fileName = "CarsTableFile";
        int rowLength = 100;
        ArrayList<Attribute> attributes = new ArrayList<>(){{
            add(new Attribute("id", "int", 0, false));
            add(new Attribute("name", "char", 100, false));
        }};
        PrimaryKey primaryKey = new PrimaryKey(new ArrayList<>(){{ add("id"); }});
        ArrayList<ForeignKey> foreignKeys = new ArrayList<>();
        ArrayList<IndexFile> indexFiles = new ArrayList<>();
        ArrayList<String> uniqueAttributes = new ArrayList<>();

        CreateTableAction createTable = new CreateTableAction(databaseName, tableName, fileName,
                rowLength, attributes, primaryKey, foreignKeys, uniqueAttributes, indexFiles);
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

    public static void main(String[] args) {

        // CreateDatabase
        /* DatabaseAction createDatabase = new CreateDatabaseAction("Adrienke teszt");
        try {
            createDatabase.actionPerform();
        } catch (DatabaseNameAlreadyExists exception) {
            System.out.println("Database name already exists");
        } */

        // CreateTable
        TestDatabaseActions test = new TestDatabaseActions();
        test.createPeopleTable();
//        test.createCarsTable();
    }
}
