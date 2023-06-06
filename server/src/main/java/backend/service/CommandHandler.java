package backend.service;

import backend.Utilities.Table;
import backend.Utilities.TableContentConverter;
import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.CreateDatabaseAction;
import backend.databaseActions.createActions.CreateIndexAction;
import backend.databaseActions.createActions.CreateTableAction;
import backend.databaseActions.createActions.InsertIntoAction;
import backend.databaseActions.dropActions.DeleteFromAction;
import backend.databaseActions.dropActions.DropDatabaseAction;
import backend.databaseActions.dropActions.DropTableAction;
import backend.databaseActions.miscActions.NothingDatabaseAction;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.databaseActions.themightySelectAction.SelectAction;
import backend.exceptions.SQLParseException;
import backend.parser.Parser;
import backend.responseObjects.SQLResponseObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

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
        if ( databaseAction instanceof NothingDatabaseAction) {
            serverController.setSqlResponseObject(new SQLResponseObject(true, NothingDatabaseAction.getRandomMessage()));
        }
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
            serverController.setSqlResponseObject(new SQLResponseObject(false, "Table created successfully!"));
        }
        if (databaseAction instanceof DropTableAction) {
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
        if (databaseAction instanceof SelectAction) {
            ArrayList<ArrayList<String>> rows = TableContentConverter.convert((Table) returnValue);
            ArrayList<String> headers = ((Table) returnValue).getColumnNames();
            serverController.setSqlResponseObject(new SQLResponseObject(headers, rows));
        }

    }

    private void updateControllerNodes(Exception e) {
        // Create SQL response object to send to client
        serverController.setSqlResponseObject(new SQLResponseObject(true, e.getMessage()));
    }
}
