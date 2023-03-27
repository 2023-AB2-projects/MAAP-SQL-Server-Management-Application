package backend.service;

import backend.ServerConnection;
import backend.config.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerController {
    // Components
    @Getter
    private ObjectMapper objectMapper;

    @Getter
    private JsonNode rootNode, databaseNode;

    // Variables
    @Getter
    @Setter
    private String sqlCommand, response, currentDatabaseName;

    @Getter
    @Setter
    private List<String> databaseNames;

    @Getter
    private final int port = 4444;

    /* Utility */
    private JsonNode findDatabaseNodeFromRoot(String databaseName, JsonNode rootNode) {
        // Check if database exists
        ArrayNode databasesArray = (ArrayNode) rootNode.get(Config.getDbCatalogRoot());
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("database").get("databaseName");

            if (currentDatabaseNodeValue == null) {
                log.error("ServerController -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();
            if(currentDatabaseName.equals(databaseName)) {
                return databaseNode;
            }
        }

        return null;
    }

    private void updateRootNodeAndDatabasesList() {
        // Reset databases list
        this.databaseNames.clear();

        // Json catalog -> Java JsonNode
        try {
            this.rootNode = objectMapper.readTree(Config.getCatalogFile());
        } catch (IOException exception) {
            log.error("ServerController -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(exception);
        }

        // Check if database exists
        this.databaseNode = this.findDatabaseNodeFromRoot(this.currentDatabaseName, this.rootNode);
        if(databaseNode == null) {
            log.error("CreateTableAction -> Database doesn't exits: " + this.currentDatabaseName + "!");
            throw new RuntimeException();
        }

        // Update databases list
        ArrayNode databaseTables = (ArrayNode) this.databaseNode.get("database").get("tables");
        for(final JsonNode tableNode : databaseTables) {
            this.databaseNames.add(tableNode.get("table").get("tableName").asText());
        }
    }
    /* /Utility */

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

    /* Server startup */
    private void init() {
        accessCatalog();
        initVariables();
        updateRootNodeAndDatabasesList();
    }

    private void initVariables() {
        this.databaseNames = new ArrayList<>();
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.currentDatabaseName = "master";
        this.sqlCommand = "";
        this.response = "";
    }

    private void accessCatalog() {
        File catalog = Config.getCatalogFile();
        try {
            if (catalog.createNewFile()) {
                log.info("Catalog.json Created Successfully!");
                initCatalog(catalog);
            } else {
                log.info("Catalog.json Already Exists!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initCatalog(File catalog) throws IOException {
        JsonNode rootNode = JsonNodeFactory.instance.objectNode();
        JsonNode databasesNode = JsonNodeFactory.instance.objectNode();
        JsonNode masterNode = JsonNodeFactory.instance.objectNode();

        String jsonCreate = "{\"databases\":[{\"database\":{\"databaseName\":\"master\", \"tables\":[ ]}}]}";
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
    /* /Server startup */

    /* Process SQL string given by client,
       Parser -> CommandProcessor -> Response to client
     */
    private void runCommand() {
        // Parser -> DatabaseAction
        // Process

        // Send output to client


        // Send updated databases list to client
        this.updateRootNodeAndDatabasesList();
        // Send to client database names list
    }

    private void invokeParser() {
    }
}
