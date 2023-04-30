package backend.service;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.exceptions.SQLParseException;
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
            serverController.setResponse("Database created successfully!");
        }
        if (databaseAction instanceof DropDatabaseAction) {
            // Update server current databaseName
            serverController.setCurrentDatabaseName("master");

            serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Database dropped successfully!\nSwitched back to database 'master'!");
        }
        if (databaseAction instanceof CreateTableAction) {
            //serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Table created successfully!");
        }
        if (databaseAction instanceof DropTableAction) {
            //serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Table dropped successfully");
        }
        if (databaseAction instanceof UseDatabaseAction) {
            ServerController.setCurrentDatabaseName((String) returnValue);
            serverController.setResponse("Now using " + returnValue);
        }
        if (databaseAction instanceof InsertIntoAction) {
            int rowCount = (int) returnValue;
            serverController.setResponse("Inserted " + rowCount + " rows into table succesfully!");
        }
        if (databaseAction instanceof DeleteFromAction) {
            serverController.setResponse("DeleteAction parsed succesfully");
        }
    }

    private void updateControllerNodes(Exception e) {
        serverController.setResponse(e.getMessage());
    }
}
