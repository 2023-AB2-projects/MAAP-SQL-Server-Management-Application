package backend;

import backend.model.CreateDatabaseAction;
import backend.model.DatabaseAction;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        log.info(System.getProperty("user.dir"));

        DatabaseAction databaseAction = new CreateDatabaseAction("adrienAdatbazisa");
        databaseAction.actionPerform();
    }
}
