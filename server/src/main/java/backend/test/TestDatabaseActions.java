package backend.test;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.CreateDatabaseAction;
import backend.databaseActions.createActions.CreateIndexAction;
import backend.databaseActions.createActions.CreateTableAction;
import backend.databaseActions.dropActions.DropDatabaseAction;
import backend.databaseActions.dropActions.DropTableAction;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.databaseModels.*;
import backend.exceptions.*;

import java.util.ArrayList;

public class TestDatabaseActions {
    public TableModel createPeopleTableModel() {
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
            }}, new ArrayList<>()));
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
        TableModel table = temp.createPeopleTableModel();

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
        TestDatabaseActions test = new TestDatabaseActions();

        // CreateTable
        test.createCarsTable();
//        test.createPeopleTable();

        // Use Database
//        DatabaseAction useDatabase = new UseDatabaseAction(new DatabaseModel("master", new ArrayList<>()));
//        try {
//            String databaseName = (String) useDatabase.actionPerform();
//            System.out.println("DatabaseName=" + databaseName);
//        } catch (DatabaseDoesntExist e) {
//            System.out.println("Database doesn't exist!");
//        } catch (Exception exception) {
//            System.out.println("ERROR -> UseDatabaseAction should not invoke this exception!");
//        }

        // Drop database
//        DatabaseAction dropDatabase = new DropDatabaseAction(new DatabaseModel("master", new ArrayList<>()));
//        try {
//            dropDatabase.actionPerform();
//        } catch (DatabaseDoesntExist e) {
//            System.out.println("Database doesn't exist!");
//        } catch (Exception exception) {
//            System.out.println("ERROR -> DropDatabaseAction should not invoke this exception!");
//        }

        // Drop table
//        DatabaseAction dropTable = new DropTableAction("Cars", "master");
//        try {
//            dropTable.actionPerform();
//        } catch (DatabaseDoesntExist e) {
//            System.out.println("Database doesn't exist!");
//        } catch (TableDoesntExist e) {
//            System.out.println("Table doesn't exist!");
//        } catch (Exception e) {
//            System.out.println("ERROR -> DropTableAction should not invoke this exception!");
//        }

        // Create index file
        DatabaseAction createIndex = new CreateIndexAction("Cars",  "master", new IndexFileModel(
                "indexName", 10, true, "indexType",
                new ArrayList<>()
        ));
        try {
            createIndex.actionPerform();
        } catch (DatabaseDoesntExist e) {
            System.out.println("Database doesn't exist!");
        } catch (TableDoesntExist e) {
            System.out.println("Table doesn't exist!");
        } catch (Exception exception) {
            System.out.println("ERROR -> CreateIndexAction should not invoke this exception!");
        }
    }
}
