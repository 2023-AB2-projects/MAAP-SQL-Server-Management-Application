package backend.service;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.exceptions.SQLParseException;
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
            databaseAction = parser.parseInput(serverController.getSqlCommand(), ServerController.getCurrentDatabaseName());
        } catch (SQLParseException e) {
            log.error(e.getMessage());
            updateControllerNodes(e);
            return;
        }
        try {
            Object returnValue = databaseAction.actionPerform();
            updateControllerNodes(databaseAction, returnValue);
            
        } catch (Exception e) {
            log.error(e.getMessage());
            updateControllerNodes(e);
            return;
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
            ServerController.setCurrentDatabaseName((String) returnValue);
            serverController.setResponse("Now using " + returnValue);
        }
        if (databaseAction instanceof InsertAction) {
            serverController.setResponse("InsertAction parsed sucessfully");
        }
        if (databaseAction instanceof DeleteAction) {
            serverController.setResponse("DeleteAction parsed succesfully");
        }
    }

    private void updateControllerNodes(Exception e) {
        serverController.setResponse(e.getMessage());
    }
}
