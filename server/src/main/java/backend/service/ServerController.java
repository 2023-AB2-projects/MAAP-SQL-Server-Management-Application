package backend.service;

import backend.ServerConnection;
import backend.config.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerController {
    //sql command set from
    @Getter
    @Setter
    private String sqlCommand;

    @Getter
    @Setter
    private String response;

    @Getter
    @Setter
    private String currentDatabase;

    @Getter
    @Setter
    private List<String> databaseNames;

    @Getter
    private final int port = 4444;

    public ServerController() {
        log.info("Server Started!");

        // init catalog and set default database
        init();
        try {
            // start connection and send Catalog info to client
            start(port);
        } catch (IOException e) {
            log.error("ServerController->start() : Connection error");
            throw new RuntimeException(e);
        }


    }

    private void init() {
        accessCatalog();
        loadDatabases();
        setDefaultDatabase();
    }

    private void setDefaultDatabase() {
        setCurrentDatabase("master");
    }

    private void loadDatabases() {
        databaseNames = new ArrayList<>();
        databaseNames.add("master");
        databaseNames.add("master2");
        databaseNames.add("akoska");
    }

    private void accessCatalog() {
        File catalog = Config.getCatalogFile();
        try {
            if (catalog.createNewFile()) {
                log.info("Catalog.json Created Succesfully!");
                initCatalog(catalog);
            } else {
                log.info("Catalog.json Already Exists!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initCatalog(File catalog) throws IOException {
        String jsonCreate = "{\"Databases\":[{\"Database\":{\"databaseName\":\"master\"}}]}";
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        Object jsonObject = mapper.readValue(jsonCreate, Object.class);
        mapper.writeValue(catalog, jsonObject);
    }

    public void start(int port) throws IOException {

        ServerConnection serverConnection = new ServerConnection(port);

        serverConnection.start();
        log.info("Client Connected");
        String shutdownMsg = "SHUTDOWN";

        serverConnection.send(databaseNames.toString());

        while(true){
            try{
                String msg = serverConnection.receive();

                // check if client disconnected
                if(msg.equals(shutdownMsg)){
                    serverConnection.send("SERVER DISCONNECTED");
                    serverConnection.stop();
                    log.info("Server Shutting Down");
                    break;
                }

                // pass message to parser and receive the answer
                setSqlCommand(msg);         // if the client message is a sql command, then execute it
                invokeParser();             // parse the command, and perform the db_actions

                // build a response string, send to client
                serverConnection.send(msg);


            }catch (NullPointerException e){
                serverConnection.stop();
                log.info("Client Disconnected");
                serverConnection.start();
                serverConnection.send(databaseNames.toString());
                log.info("Client Connected");
            }
        }

        serverConnection.fullStop();
    }

    private void invokeParser() {
    }
}
