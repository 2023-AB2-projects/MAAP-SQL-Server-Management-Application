package backend;

import backend.databaseactions.createactions.CreateDatabaseAction;
import backend.databaseactions.DatabaseAction;
import backend.service.ServerController;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        ServerController sc = new ServerController();


        // DatabaseAction databaseAction = new CreateDatabaseAction("Akoska kis bazisa");
        // databaseAction.actionPerform();
    }
}
