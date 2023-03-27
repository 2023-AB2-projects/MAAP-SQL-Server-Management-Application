package backend;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.CreateDatabaseAction;
import backend.databaseActions.createActions.CreateTableAction;
import backend.databaseModels.*;
import backend.exceptions.*;

import java.util.ArrayList;

public class TestDatabaseActions {
    public TableModel createPeopeTableModel() {
        String tableName = "People", fileName = "PeopleTableFile";
        int rowLength = 50;
        ArrayList<AttributeModel> attributes = new ArrayList<>(){{
            add(new AttributeModel("id", "int", 0, false, false));
            add(new AttributeModel("name", "char", 100, false, true));
            add(new AttributeModel("age", "int", 0, false, true));
            add(new AttributeModel("height", "int", 0, false, true));
        }};
        PrimaryKeyModel primaryKey = new PrimaryKeyModel(new ArrayList<>(){{ add("id"); }});
        ArrayList<ForeignKeyModel> foreignKeys = new ArrayList<>(){{
            add(new ForeignKeyModel("Cars", new ArrayList<>() {{
                add("id");
            }}));
        }};
        ArrayList<IndexFileModel> indexFiles = new ArrayList<>();
        ArrayList<String> uniqueAttributes = new ArrayList<>(){{
            add("name");
        }};
        return new TableModel(tableName, fileName, rowLength, attributes, primaryKey,
                foreignKeys, uniqueAttributes, indexFiles);
    }

    public void createPeopleTable() {
        String databaseName = "master";
        TestDatabaseActions temp = new TestDatabaseActions();
        TableModel table = temp.createPeopeTableModel();

        CreateTableAction createTable = new CreateTableAction(table, databaseName);
        try {
            createTable.actionPerform();
        } catch (TableNameAlreadyExists exception) {
            System.out.println("Table name already exists!");
        } catch (DatabaseDoesntExist exception) {
            System.out.println("Database doesn't exist!");
        } catch (PrimaryKeyNotFound e) {
            System.out.println("Primary key is not found in table attributes!");
        } catch (ForeignKeyNotFound e) {
            System.out.println("Foreign key is not found in table attributes!");
        } catch (AttributeCantBeNull e) {
            System.out.println("Given primary key has nullable attributes!");
        } catch (AttributesAreNotUnique e) {
            System.out.println("Attributes are not unique!");
        }
    }

    public void createCarsTable() {
        // Other table
        String databaseName = "master", tableName = "Cars", fileName = "CarsTableFile";
        int rowLength = 100;
        ArrayList<AttributeModel> attributes = new ArrayList<>(){{
            add(new AttributeModel("id", "int", 0, false, false));
            add(new AttributeModel("name", "char", 100, false, true));
        }};
        PrimaryKeyModel primaryKey = new PrimaryKeyModel(new ArrayList<>(){{ add("id"); }});
        ArrayList<ForeignKeyModel> foreignKeys = new ArrayList<>();
        ArrayList<IndexFileModel> indexFiles = new ArrayList<>();
        ArrayList<String> uniqueAttributes = new ArrayList<>();

        TableModel table = new TableModel(tableName, fileName, rowLength, attributes, primaryKey,
                foreignKeys, uniqueAttributes, indexFiles);

        CreateTableAction createTable = new CreateTableAction(table, databaseName);
        try {
            createTable.actionPerform();
        } catch (TableNameAlreadyExists exception) {
            System.out.println("Table name already exists!");
        } catch (DatabaseDoesntExist exception) {
            System.out.println("Database doesn't exist!");
        } catch (PrimaryKeyNotFound e) {
            System.out.println("Primary key is not found in table attributes!");
        } catch (ForeignKeyNotFound e) {
            System.out.println("Foreign key is not found in table attributes!");
        } catch (AttributeCantBeNull e) {
            System.out.println("Given primary key has nullable attributes!");
        } catch (AttributesAreNotUnique e) {
            System.out.println("Attributes are not unique!");
        }
    }

    public static void main(String[] args) {
        TestDatabaseActions temp = new TestDatabaseActions();


        // CreateDatabase
        DatabaseAction createDatabase = new CreateDatabaseAction(new DatabaseModel("Adrienke adatbazisa", new ArrayList<>() {{
            add(temp.createPeopeTableModel());
        }}));
        try {
            createDatabase.actionPerform();
        } catch (DatabaseNameAlreadyExists exception) {
            System.out.println("Database name already exists");
        } catch (Exception exception) {
            System.out.println("ERROR -> CreateDabaseAction should not invoke this exception!");
        }

        // CreateTable
//        TestDatabaseActions test = new TestDatabaseActions();
//        test.createCarsTable();
//        test.createPeopleTable();
    }
}
