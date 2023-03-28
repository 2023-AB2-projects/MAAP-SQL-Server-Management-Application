package backend.service;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.exceptions.*;
import backend.parser.Parser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandHandler {
    private final ServerController serverController;

    public CommandHandler(ServerController serverController){
        this.serverController = serverController;
    }

    public void processCommand() {
        //Most csak kiprobalom egy USE database actionre

/*        DatabaseAction createDatabase = new CreateDatabaseAction(new DatabaseModel("Adrienke adatbazisa", new ArrayList<>()));
        try {
            createDatabase.actionPerform();
        } catch (DatabaseNameAlreadyExists exception) {
            System.out.println("Database name already exists");
        } catch (Exception exception) {
            System.out.println("ERROR -> CreateDabaseAction should not invoke this exception!");
        }

        DatabaseAction createDatabase2 = new CreateDatabaseAction(new DatabaseModel("Adrienke adatbazisa2", new ArrayList<>()));
        try {
            createDatabase.actionPerform();
        } catch (DatabaseNameAlreadyExists exception) {
            System.out.println("Database name already exists");
        } catch (Exception exception) {
            System.out.println("ERROR -> CreateDabaseAction should not invoke this exception!");
        }*/

       /* DatabaseAction databaseAction = new UseDatabaseAction(new DatabaseModel("Adrienke adatbazisa", new ArrayList<>()));
        try {
            String databaseName = (String) databaseAction.actionPerform();
            serverController.setCurrentDatabaseName(databaseName);
            serverController.setResponse("Database Created : " + databaseName);
            updateControllerNodes(databaseAction);
            System.out.println("Databasename=" + databaseName);
        } catch (DatabaseDoesntExist e) {
            System.out.println("Database doesn't exist!");
        } catch (Exception exception) {
            System.out.println("ERROR -> UseDatabase should not invoke this exception!");
        }*/

        //FIXME
        // clear up way of getting command to parser 
        Parser parser = new Parser();
        DatabaseAction da;
        try {
            da = parser.parseInput(serverController.getSqlCommand(), "master");
        } catch (SQLParseException e) {
            log.error(e.getMessage());
            return;
        } catch (InvalidSQLCommand e) {
            log.error(e.getMessage());
            return;
        }

        try {
            Object returnValue = da.actionPerform();
            updateControllerNodes(da, returnValue);
        } catch (AttributeCantBeNull e) {
            log.error("AttributeCantBeNull");
        } catch (AttributesAreNotUnique e) {
            log.error("AttributesAreNotUnique");
        } catch (DatabaseNameAlreadyExists e) {
            log.error("DatabaseNameAlreadyExists");
        } catch (PrimaryKeyNotFound e) {
            log.error("PrimaryKeyNotFound");
        } catch (TableDoesntExist e) {
            log.error("TableDoesntExist");
        } catch (ForeignKeyNotFound e) {
            log.error("ForeignKeyNotFound");
        } catch (TableNameAlreadyExists e) {
            log.error("TableNameAlreadyExists");
        } catch (DatabaseDoesntExist e) {
            log.error("DatabaseDoesntExist");
        }


    }

    private void updateControllerNodes(DatabaseAction databaseAction, Object returnValue) {
        if (databaseAction instanceof CreateDatabaseAction) {
            serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Database Created Successfully!");
        }
        if (databaseAction instanceof DropDatabaseAction) {
            serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Database Dropped Successfully!");
        }
        if (databaseAction instanceof CreateTableAction) {
            //serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Table Created Successfully!");
        }
        if (databaseAction instanceof DropTableAction) {
            //serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Table Dropped Successfully!");
        }
        if (databaseAction instanceof UseDatabaseAction) {
            serverController.setCurrentDatabaseName((String) returnValue);
            serverController.setResponse("Now using " + (String) returnValue);
        }
    }
}
