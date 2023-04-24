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
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestDatabaseActions {
    public TableModel createPeopleTableModel() {
        String tableName = "people", fileName = "people.data.bin";
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
//        TestDatabaseActions test = new TestDatabaseActions();

//        CreateDatabaseAction createDatabaseAction = new CreateDatabaseAction(new DatabaseModel("adatbazis_1", new ArrayList<>()));
//        try {
//            createDatabaseAction.actionPerform();
//        } catch (DatabaseNameAlreadyExists e) {
//            System.out.println("Database already exists");
//        }
//
//        UseDatabaseAction useDatabaseAction = new UseDatabaseAction(new DatabaseModel("adatbazis_1", new ArrayList<>()));
//        try {
//            useDatabaseAction.actionPerform();
//        } catch (DatabaseDoesntExist e) {
//            throw new RuntimeException(e);
//        }
//
//        test.createCarsTable("adatbazis_1");
//        test.createPeopleTable("adatbazis_1");
//
//        DatabaseAction deleteTable = new DropTableAction("people", "adatbazis_1");
//        try {
//            deleteTable.actionPerform();
//        } catch (Exception e) {
//            System.out.println("nem jo!");
//        }
//
//        DropDatabaseAction dropDatabaseAction = new DropDatabaseAction(new DatabaseModel("adatbazis_1", new ArrayList<>()));
//        try {
//            dropDatabaseAction.actionPerform();
//        } catch (DatabaseDoesntExist e) {
//            System.out.println("Database doesn't exist!");
//        }

        // Create index file

//        DatabaseAction createIndex = new CreateIndexAction("emberek",  "master",
//                new IndexFileModel(
//
//                        "id_index2",
//                        "emberek.index.id_index.bin",
//                        true,
//                        new ArrayList<>(){{
//                            add("id");
//                        }}
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

        String databaseName = "master", tableName = "emberek", indexName = "id_index";
        List<String> fieldNames = CatalogManager.getIndexFieldNames(databaseName, tableName, indexName);
        System.out.println(fieldNames);

        List<String> fieldTypes = CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
        System.out.println(fieldTypes);

        for(final String fieldName : fieldNames) {
            System.out.println(CatalogManager.isIndexFieldUnique(databaseName, tableName, indexName, fieldName));
        }

        List<String> indexNames = CatalogManager.getTableIndexNames(databaseName, tableName);
        System.out.println(indexNames);

        List<String> indexFileNames = CatalogManager.getTableIndexFileNames(databaseName, tableName);
        System.out.println(indexFileNames);
    }
}