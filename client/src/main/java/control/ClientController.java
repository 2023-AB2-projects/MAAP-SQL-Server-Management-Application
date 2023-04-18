package control;

import backend.MessageModes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.formdev.flatlaf.FlatDarculaLaf;
import frontend.ClientFrame;
import frontend.ConnectionFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.LookAndFeel;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientController {
    /* ClientController has a reference to GUI, MessageHandler and ConnectionManager */
    private ClientFrame clientFrame;
    private ConnectionFrame connectionFrame;
    private MessageHandler messageHandler;

    // Other variables
    private LookAndFeel lookAndFeel;

    // JSON logic
    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode rootNode, databaseNode;

    // Data/Logic
    private String catalogJSON; //TODO
    private String currentDatabaseName;
    private ArrayList<String> databaseNames;

    public ClientController() {
        // Init Client side components
        this.initComponents();
        
        // Init controller variables
        this.initVariables();
    }

    /* Utility */
    private void initComponents() {
        // Init GUI
        this.lookAndFeel = new FlatDarculaLaf();
        
        // Do not touch this (ffs)
        try {
            javax.swing.UIManager.setLookAndFeel(this.lookAndFeel);
        } catch (UnsupportedLookAndFeelException ex) {
            log.error("FlatLafDark is not supported!");
        }
        
        this.connectionFrame = new ConnectionFrame(this);
        this.clientFrame = new ClientFrame(this);   // Hidden by default

        // Message handler
        this.messageHandler = new MessageHandler(this);
    }

    private void initVariables() {
        // Init variables
        this.databaseNames = new ArrayList<>();

        // Other
        this.catalogJSON = "";
        this.currentDatabaseName = "master";
    }

    private void updateCurrentDatabases() {
        // Clear current database names
        this.databaseNames.clear();

        // Update databases list
        for(final JsonNode databaseNode : rootNode.get("databases")) {
            this.databaseNames.add(databaseNode.get("database").get("databaseName").asText());
        }

        // Update Tree node
        this.clientFrame.updateDatabaseNamesTree(this.databaseNames);
        System.out.println("Info -> Database names: " + this.databaseNames);
    }

    private void updateJSON(String receivedJSON) {
        // Check if catalogJSON changed
        if(!receivedJSON.equals(this.catalogJSON)) {
            // Refresh catalog JSON string
            this.catalogJSON = receivedJSON;

            // Update root node
            try {
                this.rootNode = this.objectMapper.readTree(this.catalogJSON);
            } catch (JsonProcessingException e) {
                log.error("Could not process received JSON catalog!");
            }

            // Update database node
            this.databaseNode = this.findDatabaseNodeFromRoot(this.currentDatabaseName);
            if(databaseNode == null) {
                log.error("Database doesn't exits: " + this.currentDatabaseName + "!");
                throw new RuntimeException();
            }
        }
    }

    private JsonNode findDatabaseNodeFromRoot(String databaseName) {
        // Check if database exists
        ArrayNode databasesArray = (ArrayNode) this.rootNode.get("databases");
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
    /* / Utility */

    /* Client controls */
    public void establishConnection(String ip) throws IOException {
        this.messageHandler.establishConnection(ip);

        // Update current databases list
        String databasesString = messageHandler.receiveMessage();
        System.out.println(databasesString);
//        this.updateCurrentDatabases(databasesString);
    }

    public void stopConnection() throws IOException {
        this.messageHandler.stopConnection();
    }

    public void sendCommandToServer(String command) {
        this.messageHandler.sendCommandToServer(command);
    }

    public String receiveMessage() throws IOException {
        return messageHandler.receiveMessage();
    }
    
    /* Message logic */
    // method receives message from server and performs action determined by mode param
    public void receiveMessageAndPerformAction(int mode) {
        try {
            String response = this.receiveMessage();
            log.info("Received message: " + response);
            
            if (response.equals("SERVER DISCONNECTED")) {
                this.stopConnection();
                log.info("Server was shut down");
                System.exit(0);
            }

            switch(mode) {
                case MessageModes.setTextArea -> {
                    this.setOutputAreaString(response);

                    // Check if we need to update current database
                    if(response.contains("Now using ")) {
                        String currentDatabaseName = response.split("Now using ")[1];
                        log.info("Current database name: " + currentDatabaseName);
                        this.currentDatabaseName = currentDatabaseName;
                        this.clientFrame.setCurrentDatabaseName(this.currentDatabaseName);
                    }
                }
                case MessageModes.refreshJSONCatalog -> {
                    this.updateJSON(response);
                    this.updateCurrentDatabases();

                    // Update tables combo boxes etc.
                    this.clientFrame.update();
                }

            }
            
        } catch (IOException e) {
            log.info("Server is no longer running");
            System.exit(0);
        }
    }

    /* Setters */
    public void setClientFrameVisibility(boolean visibility) { this.clientFrame.setVisible(visibility); }

    public void setInputTextAreaString(String inputTextAreaString) { this.clientFrame.setInputTextAreaString(inputTextAreaString);}
    
    public void setOutputAreaString(String string) { this.clientFrame.setOutputAreaString(string); }

    /* Getters */
    public String getInputTextAreaString() { return this.clientFrame.getInputTextAreaString(); }

    public ArrayList<String> getCurrentDatabaseTables() {
        if(this.databaseNode != null) {
            ArrayList<String> tableNames = new ArrayList<>();

            for(final JsonNode tableNode : this.databaseNode.get("database").get("tables")) {
                tableNames.add(tableNode.get("table").get("tableName").asText());
            }

            return tableNames;
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getTableAttributes(String tableName) {
        // Check if table name exists
        if(this.getCurrentDatabaseTables().contains(tableName)) {
            ArrayList<String> attributes = new ArrayList<>();
            // Find table
            for(final JsonNode tableNode : this.databaseNode.get("database").get("tables")) {
                if(tableNode.get("table").get("tableName").asText().equals(tableName)) {
                    for(final JsonNode field : tableNode.get("table").get("fields")) {
                        attributes.add(field.get("fieldName").asText());
                    }
                    break;
                }
            }

            return attributes;
        } else {
            return new ArrayList<>(){{
                add("Err: Table not found");
            }};
        }
    }

    /* Main */
    public static void main(String[] args) {
        new ClientController();
    }
}
