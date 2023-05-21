package control;

import backend.MessageModes;
import backend.responseObjects.SQLResponseObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.formdev.flatlaf.FlatDarculaLaf;
import frontend.ClientFrame;
import frontend.ConnectionFrame;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.LookAndFeel;
import javax.swing.UnsupportedLookAndFeelException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import service.CatalogManager;
import service.Config;

@Slf4j
public class ClientController {
    /* ClientController has a reference to GUI, MessageHandler and ConnectionManager */
    private ClientFrame clientFrame;
    private ConnectionFrame connectionFrame;
    private MessageHandler messageHandler;

    // Other variables
    private LookAndFeel lookAndFeel;

    // Data/Logic
    private String catalogJSON;

    @Getter
    private String currentDatabaseName;

    // Connection logic
    private final ServerSocket serverSocket;

    public ClientController() {
        // Init Client side components
        this.initComponents();
        
        // Init controller variables
        this.initVariables();

        // Init server socket
        try {
            this.serverSocket = new ServerSocket(4445);
        } catch (IOException e) {
            log.error("Could not create server socket!");
            throw new RuntimeException(e);
        }
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
        // Other
        this.catalogJSON = "";
        this.currentDatabaseName = "master";
    }

    private void updateJSON(String receivedJSON) {
        // Check if catalogJSON changed
        if(!receivedJSON.equals(this.catalogJSON)) {
            // Refresh catalog JSON string
            this.catalogJSON = receivedJSON;

            // Refresh stored JSON file
            try (FileWriter writer = new FileWriter(Config.getDbCatalogPath())) {
                writer.write(this.catalogJSON);
                log.info("Updated stored JSON file!");
            } catch (IOException e) {
                log.error("Could not update stored JSON file!");
                throw new RuntimeException(e);
            }

            // Update database node
            JsonNode databaseNode = CatalogManager.findDatabaseNode(this.currentDatabaseName);
            if(databaseNode == null) {
                this.currentDatabaseName = "master";
                this.clientFrame.setCurrentDatabaseName(this.currentDatabaseName);
            }

            // Update object explorer
            this.clientFrame.updateObjectExplorer();
        }
    }
    /* / Utility */

    /* Client controls */
    public void establishConnection(String ip) throws IOException {
        this.messageHandler.establishConnection(ip);

        // Update current databases list
        String databaseJSON = messageHandler.receiveMessage();
        this.updateJSON(databaseJSON);
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

    private void stopClientSideIfNeeded(String response) {
        if (response.equals("SERVER DISCONNECTED")) {
            try {
                this.stopConnection();
            } catch (IOException e) {
                log.info("Server is no longer running");
                System.exit(0);
            }

            // Before stopping
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                log.error("Could not close server socket!");
                throw new RuntimeException(e);
            }

            log.info("Server was shut down");
            System.exit(0);
        }
    }

    private SQLResponseObject receiveSQLResponseObject() {
        // Wait for client connection
        Socket socket;
        try {
            socket = this.serverSocket.accept();
        } catch (IOException e) {
            log.error("Could not accept client connection!");
            throw new RuntimeException(e);
        }

        // Get input stream
        InputStream inputStream;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            log.error("Could not get input stream!");
            throw new RuntimeException(e);
        }

        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            log.error("Could not create object input stream!");
            throw new RuntimeException(e);
        }

        // Receive the object
        SQLResponseObject receivedObject;
        try {
            receivedObject = (SQLResponseObject) objectInputStream.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            log.error("Could not find SQL object class!");
            throw new RuntimeException(e);
        }

        // Close the streams and socket
        try {
            objectInputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            log.error("Could not close streams and socket!");
            throw new RuntimeException(e);
        }

        return receivedObject;
    }
    
    /* Message logic */
    // method receives message from server and performs action determined by mode param
    public void receiveMessageAndPerformAction(int mode) {
        switch(mode) {
            case MessageModes.setTextArea -> {
                SQLResponseObject responseObject = this.receiveSQLResponseObject();
                System.out.println(responseObject);

//                String response = null;
//                try {
//                    response = this.receiveMessage();
//                } catch (IOException e) {
//                    log.error("Could not receive message from server!");
//                    System.exit(1);
//                }

                String response = "temp";

                log.info("Updated output textArea!");
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
                String response = null;
                try {
                    response = this.receiveMessage();
                } catch (IOException e) {
                    log.error("Could not receive message from server!");
                    System.exit(1);
                }

                // Check if server was shut down
                this.stopClientSideIfNeeded(response);
                this.updateJSON(response);

                // Update tables combo boxes etc.
                this.clientFrame.update();
            }

        }
    }

    /* Setters */
    public void setClientFrameVisibility(boolean visibility) { this.clientFrame.setVisible(visibility); }

    public void setInputTextAreaString(String inputTextAreaString) { this.clientFrame.setInputTextAreaString(inputTextAreaString);}
    
    public void setOutputAreaString(String string) { this.clientFrame.setOutputAreaString(string); }

    public void increaseCenterPanelFont() { this.clientFrame.increaseCenterPanelFont(); }

    public void decreaseCenterPanelFont() { this.clientFrame.decreaseCenterPanelFont();}

    /* Getters */
    public String getInputTextAreaString() { return this.clientFrame.getInputTextAreaString(); }

    /* Main */
    public static void main(String[] args) {
        new ClientController();
    }
}
