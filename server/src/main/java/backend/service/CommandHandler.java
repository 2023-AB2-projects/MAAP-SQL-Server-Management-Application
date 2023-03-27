package backend.service;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.CreateDatabaseAction;
import backend.databaseActions.dropActions.DropDatabaseAction;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.databaseModels.DatabaseModel;
import backend.exceptions.DatabaseDoesntExist;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
@Slf4j
public class CommandHandler {
    private final ServerController serverController;

    public CommandHandler(ServerController serverController){
        this.serverController = serverController;
    }

    public void processCommand() {
        // Ide jon a parser letrehozas, majd az visszaterit egy database actiont
        //FIXME

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

        DatabaseAction databaseAction = new UseDatabaseAction(new DatabaseModel("master", new ArrayList<>())); // = parseCommand()
        try {
            Object returnValue = databaseAction.actionPerform();
            updateControllerNodes(databaseAction, returnValue);
        } catch (Exception e) {
            log.error("Command Handler Exception!");
        }


    }

    private void updateControllerNodes(DatabaseAction databaseAction, Object returValue) {
        if (databaseAction instanceof CreateDatabaseAction) {
            serverController.updateRootNodeAndNamesList();
            serverController.setResponse("Database Created Successfully!");
        }
    }
}
