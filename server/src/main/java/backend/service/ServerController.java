package backend.service;

import backend.ServerConnection;
import backend.config.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private List<String> databaseNames, tableNames;

    @Getter
    private final int port = 4444;

    private CommandHandler commandHandler;

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

    public void updateRootNodeAndNamesList() {
        // Reset databases and tables list
        this.databaseNames.clear();
        this.tableNames.clear();

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
        for(final JsonNode databaseNode : rootNode.get("databases")) {
            this.databaseNames.add(databaseNode.get("database").get("databaseName").asText());
        }

        // Update tables list
        ArrayNode databaseTables = (ArrayNode) this.databaseNode.get("database").get("tables");
        for(final JsonNode tableNode : databaseTables) {
            this.tableNames.add(tableNode.get("table").get("tableName").asText());
        }

        System.out.println("Current databases=" + this.databaseNames);
        System.out.println("Current tables=" + this.tableNames);
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
        setCurrentDatabaseName("master");       // By default, "master"
        initVariables();
        accessCatalog();
        updateRootNodeAndNamesList();
    }

    private void initVariables() {
        this.databaseNames = new ArrayList<>();
        this.tableNames = new ArrayList<>();
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.sqlCommand = "";
        this.response = "";
        this.commandHandler = new CommandHandler(this);
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

        /* Build up basic catalog structure */
        // Master node
        ObjectNode masterNode = JsonNodeFactory.instance.objectNode();
        ObjectNode masterNodeValue = JsonNodeFactory.instance.objectNode();
        masterNodeValue.put("databaseName", this.currentDatabaseName);
        masterNodeValue.set("tables", JsonNodeFactory.instance.arrayNode());
        masterNode.set("database", masterNodeValue);

        // Databases node
        ArrayNode databasesNode = JsonNodeFactory.instance.arrayNode();
        databasesNode.add(masterNode);

        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.set("databases", databasesNode);

        this.objectMapper.writeValue(catalog, rootNode);
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

                commandHandler.processCommand();

                // build a response string, send to client
                serverConnection.send(getResponse());


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
        this.updateRootNodeAndNamesList();
        // Send to client database names list
    }

    private void invokeParser() {
    }
}
