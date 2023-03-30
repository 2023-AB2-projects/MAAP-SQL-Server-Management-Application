package backend.service;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import backend.parser.Parser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandHandler {
    private final ServerController serverController;
    private final Parser parser;

    public CommandHandler(ServerController serverController){
        this.serverController = serverController;
        this.parser = new Parser();
    }

    public void processCommand() {

        DatabaseAction databaseAction = null;
        try {
            databaseAction = parser.parseInput(serverController.getSqlCommand(), serverController.getCurrentDatabaseName());
        } catch (Exception exception) {
            System.out.println("ERROR -> CreateDabaseAction should not invoke this exception!");
        }
        try {
            Object returnValue = databaseAction.actionPerform();
            updateControllerNodes(databaseAction, returnValue);
        } catch (FieldCantBeNull e) {
            log.error("AttributeCantBeNull");
        } catch (FieldsAreNotUnique e) {
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
            serverController.setResponse("Now using " + returnValue);
        }
    }
}
