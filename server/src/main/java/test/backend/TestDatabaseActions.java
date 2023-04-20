package test.backend;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.CreateDatabaseAction;
import backend.databaseActions.createActions.CreateIndexAction;
import backend.databaseActions.createActions.CreateTableAction;
import backend.databaseActions.dropActions.DropDatabaseAction;
import backend.databaseActions.dropActions.DropTableAction;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.databaseModels.*;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class TestDatabaseActions {
    public TableModel createPeopleTableModel() {
        String tableName = "people", fileName = "people.data.bin";
        int rowLength = 50;
        ArrayList<FieldModel> fields = new ArrayList<>(){{
            add(new FieldModel("id", "int", false));
            add(new FieldModel("name", "char(100)", true));
            add(new FieldModel("age", "int", true));
            add(new FieldModel("height", "int", true));
        }};
        PrimaryKeyModel primaryKey = new PrimaryKeyModel(new ArrayList<>(){{ add("id"); }});
        ArrayList<ForeignKeyModel> foreignKeys = new ArrayList<>(){{
            add(new ForeignKeyModel("cars", new ArrayList<>() {{
                add("id");
            }}, new ArrayList<>(){{
                add("id");
            }}));
        }};
        ArrayList<IndexFileModel> indexFiles = new ArrayList<>();
        ArrayList<String> uniqueAttributes = new ArrayList<>(){{
            add("name");
        }};
        return new TableModel(tableName, fileName, fields, primaryKey,
                foreignKeys, uniqueAttributes, indexFiles);
    }

    public void createPeopleTable(String databaseName) {
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
        } catch (FieldCantBeNull e) {
            System.out.println("Given primary key has nullable attributes!");
        } catch (FieldsAreNotUnique e) {
            System.out.println("Attributes are not unique!");
        } catch (ForeignKeyFieldNotFound e) {
            log.error("Foreign key referencing fields are not in this table!");
        }
    }

    public void createCarsTable(String databaseName) {
        // Other table
        String tableName = "cars", fileName = "cars.data.bin";
        int rowLength = 100;
        ArrayList<FieldModel> fields = new ArrayList<>(){{
            add(new FieldModel("id", "int", false));
            add(new FieldModel("name", "char(100)", true));
        }};
        PrimaryKeyModel primaryKey = new PrimaryKeyModel(new ArrayList<>(){{ add("id"); }});
        ArrayList<ForeignKeyModel> foreignKeys = new ArrayList<>();
        ArrayList<IndexFileModel> indexFiles = new ArrayList<>();
        ArrayList<String> uniqueAttributes = new ArrayList<>();

        TableModel table = new TableModel(tableName, fileName, fields, primaryKey,
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
        } catch (FieldCantBeNull e) {
            System.out.println("Given primary key has nullable attributes!");
        } catch (FieldsAreNotUnique e) {
            System.out.println("Attributes are not unique!");
        } catch (ForeignKeyFieldNotFound e) {
            log.error("Foreign key referencing fields are not in this table!");
        }
    }

    public static void main(String[] args) {
        TestDatabaseActions test = new TestDatabaseActions();

        CreateDatabaseAction createDatabaseAction = new CreateDatabaseAction(new DatabaseModel("adatbazis_1", new ArrayList<>()));
        try {
            createDatabaseAction.actionPerform();
        } catch (DatabaseNameAlreadyExists e) {
            System.out.println("Database already exists");
        }

        UseDatabaseAction useDatabaseAction = new UseDatabaseAction(new DatabaseModel("adatbazis_1", new ArrayList<>()));
        try {
            useDatabaseAction.actionPerform();
        } catch (DatabaseDoesntExist e) {
            throw new RuntimeException(e);
        }

        test.createCarsTable("adatbazis_1");
        test.createPeopleTable("adatbazis_1");

        DatabaseAction deleteTable = new DropTableAction("people", "adatbazis_1");
        try {
            deleteTable.actionPerform();
        } catch (Exception e) {
            System.out.println("nem jo!");
        }

        DropDatabaseAction dropDatabaseAction = new DropDatabaseAction(new DatabaseModel("adatbazis_1", new ArrayList<>()));
        try {
            dropDatabaseAction.actionPerform();
        } catch (DatabaseDoesntExist e) {
            System.out.println("Database doesn't exist!");
        }


        // Create Database
//        DatabaseModel newDatabase = new DatabaseModel();
//        newDatabase.setDatabaseName("adatbaziska_1");
//        DatabaseAction createDatabase = new CreateDatabaseAction(newDatabase);
//        try {
//            createDatabase.actionPerform();
//        } catch (DatabaseNameAlreadyExists e) {
//            log.info("CreateDatabaseAction -> DatabaseAlreadyExists");
//        } catch (Exception e) {
//            log.error("ERROR -> CreateDatabaseAction should now throw this!");
//        }
//
//        newDatabase.setDatabaseName("adatbaziska_2");
//        createDatabase = new CreateDatabaseAction(newDatabase);
//        try {
//            createDatabase.actionPerform();
//        } catch (DatabaseNameAlreadyExists e) {
//            log.info("CreateDatabaseAction -> DatabaseAlreadyExists");
//        } catch (Exception e) {
//            log.error("ERROR -> CreateDatabaseAction should now throw this!");
//        }

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
//        DatabaseAction dropDatabase = new DropDatabaseAction(new DatabaseModel("akosksadfjlaskfjd", new ArrayList<>()));
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
//        DatabaseAction createIndex = new CreateIndexAction("Cars",  "master", new IndexFileModel(
//                "indexName", 10, true, "indexType",
//                new ArrayList<>()
//        ));
//        try {
//            createIndex.actionPerform();
//        } catch (DatabaseDoesntExist e) {
//            System.out.println("Database doesn't exist!");
//        } catch (TableDoesntExist e) {
//            System.out.println("Table doesn't exist!");
//        } catch (IndexAlreadyExists e) {
//            System.out.println("Index with given name already exists!");
//        } catch (Exception exception) {
//            System.out.println("ERROR -> CreateIndexAction should not invoke this exception!");
//        }
    }
}