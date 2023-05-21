package backend.service;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.exceptions.SQLParseException;
import backend.parser.Parser;
import backend.responseObjects.SQLResponseObject;
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
            log.info("Error while performing action! (It will be sent to client)");
            log.info(e.getMessage());
            updateControllerNodes(e);
        }
    }

    private void updateControllerNodes(DatabaseAction databaseAction, Object returnValue) {
        if (databaseAction instanceof CreateDatabaseAction) {
            serverController.updateRootNodeAndNamesList();
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Database created successfully!"));
        }
        if (databaseAction instanceof DropDatabaseAction) {
            // Update server current databaseName
            ServerController.setCurrentDatabaseName("master");

            serverController.updateRootNodeAndNamesList();
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Database dropped successfully!\nSwitched back to database 'master'!"));
        }
        if (databaseAction instanceof CreateTableAction) {
            //serverController.updateRootNodeAndNamesList();
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Table created successfully!"));
        }
        if (databaseAction instanceof DropTableAction) {
            //serverController.updateRootNodeAndNamesList();
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Table dropped successfully"));
        }
        if (databaseAction instanceof UseDatabaseAction) {
            ServerController.setCurrentDatabaseName((String) returnValue);
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Now using " + returnValue));
        }
        if (databaseAction instanceof InsertIntoAction) {
            int rowCount = (int) returnValue;
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Inserted " + rowCount + " rows into table succesfully!"));
        }
        if (databaseAction instanceof DeleteFromAction) {
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Row(s) deleted successfully"));
        }

        if (databaseAction instanceof CreateIndexAction) {
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Index created successfully"));
        }
    }

    private void updateControllerNodes(Exception e) {
        // Create SQL response object to send to client
        serverController.setSqlResponseObject(new SQLResponseObject(true, e.getMessage()));
    }
}
