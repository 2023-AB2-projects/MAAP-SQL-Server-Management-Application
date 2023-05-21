package backend.service;

import backend.ServerConnection;
import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.CreateDatabaseAction;
import backend.databaseModels.DatabaseModel;
import backend.exceptions.databaseActionsExceptions.*;
import backend.responseObjects.SQLResponseObject;
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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private String sqlCommand;


    @Setter
    @Getter
    private static String currentDatabaseName;

    @Getter
    @Setter
    private List<String> databaseNames, tableNames;

    @Getter
    private final int port = 4444;

    // SQL response
    private CommandHandler commandHandler;
    @Setter
    private SQLResponseObject sqlResponseObject;


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
        this.databaseNode = this.findDatabaseNodeFromRoot(ServerController.currentDatabaseName, this.rootNode);
        if(databaseNode == null) {
            log.error("CreateTableAction -> Database doesn't exits: " + currentDatabaseName + "!");
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
        initRecordsFolder();
        accessCatalog();
        updateRootNodeAndNamesList();
    }

    private void initVariables() {
        this.databaseNames = new ArrayList<>();
        this.tableNames = new ArrayList<>();
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.sqlCommand = "";
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
        // Root node
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.set("databases", new ArrayNode(null));     // Base node with []

        this.objectMapper.writeValue(catalog, rootNode);

        // Add master database
        DatabaseAction createDatabaseMaster = new CreateDatabaseAction(new DatabaseModel("master", new ArrayList<>()));
        try {
            createDatabaseMaster.actionPerform();
        } catch (DatabaseNameAlreadyExists e) {
            log.error("Database master already exists! -> (Error)");
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Other exception (should not)!");
            throw new RuntimeException(e);
        }
    }

    private void initRecordsFolder() {
        // Init records folder containing records for each database and table
        try {
            Files.createDirectories(Paths.get(Config.getDbRecordsPath()));
        } catch (IOException e) {
            log.error("Could not create 'records' folder -> IO exception!");
            throw new RuntimeException(e);
        }
    }

    private void sendSQLResponseObjectToClient() throws IOException {
        // Establish socket connection
        Socket socket = new Socket("localhost", 4445);

        // Get output stream
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // Write object to stream (send object to client)
        objectOutputStream.writeObject(this.sqlResponseObject);

        // Close the streams and socket
        objectOutputStream.close();
        outputStream.close();
        socket.close();
    }

    public void start(int port) throws IOException {

        ServerConnection serverConnection = new ServerConnection(port);

        serverConnection.start();
        log.info("Client Connected");
        String shutdownMsg = "SHUTDOWN";

        String jsonText = Files.readString(Config.getCatalogFile().toPath());
        serverConnection.send(jsonText);

        // communication
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

                // Process command and save response into local object
                commandHandler.processCommand();

                // 1. Send JSON Catalog
                jsonText = Files.readString(Config.getCatalogFile().toPath());
                serverConnection.send(jsonText);

                // 2. Send message
                this.sendSQLResponseObjectToClient();

            } catch (NullPointerException e){
                serverConnection.stop();
                log.info("Client Disconnected");
                serverConnection.start();

                jsonText = Files.readString(Config.getCatalogFile().toPath());
                serverConnection.send(jsonText);

                log.info("Client Connected");
            } catch (SocketException socketException) {
                // Handled socket exception
                // Usually happens when client disconnects -> Used to crash server
                serverConnection.stop();
                log.info("Client Disconnected - Reason: Socket Error (Most likely disconnected)");
                serverConnection.start();

                jsonText = Files.readString(Config.getCatalogFile().toPath());
                serverConnection.send(jsonText);

                log.info("Client Connected");
            }
        }

        serverConnection.fullStop();
    }
    /* /Server startup */
}
