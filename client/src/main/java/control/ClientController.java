package control;

import backend.MessageModes;
import backend.responseObjects.SQLResponseObject;
import backend.responseObjects.SQLTextResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import frontend.ClientFrame;
import frontend.ConnectionFrame;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import service.CatalogManager;
import service.Config;

@Slf4j
public class ClientController {
    /* ClientController has a reference to GUI, MessageHandler and ConnectionManager */
    @Getter
    private ClientFrame clientFrame;
    private ConnectionFrame connectionFrame;
    private MessageHandler messageHandler;

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

        // Set image icon for JFrame
        ImageIcon logo = new ImageIcon(Config.getImagesPath() + File.separator + "logo_square.png");

        // Set the icon for the taskbar
        Taskbar taskbar = Taskbar.getTaskbar();
        if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
            taskbar.setIconImage(logo.getImage());
        }

        // Init server socket
        try {
            this.serverSocket = new ServerSocket(4445);
        } catch (IOException e) {
            log.error("Could not create server socket!");
            throw new RuntimeException(e);
        }

        // Read initial file contents
        this.clientFrame.readCurrentFile();
    }

    /* Utility */
    private void initComponents() {
        // Do not touch this (ffs)
        this.setDarkMode();
        
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

    private SQLResponseObject receiveSQLResponseObject() throws IOException {
        // Wait for client connection
        Socket socket =  this.serverSocket.accept();

        // Get input stream
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        // Receive the object
        SQLResponseObject receivedObject;
        try {
            receivedObject = (SQLResponseObject) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            log.error("Could not find SQL object class!");
            throw new RuntimeException(e);
        }

        // Close the streams and socket
        objectInputStream.close();
        inputStream.close();
        socket.close();

        return receivedObject;
    }
    
    /* Message logic */
    // method receives message from server and performs action determined by mode param
    public void receiveMessageAndPerformAction(int mode) {
        switch(mode) {
            case MessageModes.setTextArea -> {
                SQLResponseObject responseObject;
                try {
                    responseObject = this.receiveSQLResponseObject();
                } catch (IOException e) {
                    log.error(e.getMessage());
                    log.error("Failed to receive SQL response object!");
                    throw new RuntimeException(e);
                }

                // Check response contents
                // If it's text content -> Can be output or error
                boolean isTextResponse = responseObject.getIsTextResponse();
                if (isTextResponse) {
                    SQLTextResponse textResponse = responseObject.getTextResponse();

                    // Check if it's error message
                    if (textResponse.isError()) {
                        String errorMessage = textResponse.getText();

                        // Set output error string
                        this.clientFrame.setErrorOutputAreaString(errorMessage);

                    } else {
                        String commandOutput = textResponse.getText();
                        this.setOutputAreaString(commandOutput);

                        // Check if we need to update current database
                        if(commandOutput.contains("Now using ")) {
                            String currentDatabaseName = commandOutput.split("Now using ")[1];
                            log.info("Current database name: " + currentDatabaseName);
                            this.currentDatabaseName = currentDatabaseName;
                            this.clientFrame.setCurrentDatabaseName(this.currentDatabaseName);
                        }
                        log.info("Updated output textArea (Non-error)!");
                    }
                } else {
                    // We received table data
                    this.setOutputTableData(responseObject.getFieldNames(), responseObject.getTableData());
                    log.info("Updated output table data!");
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
    public void setLightMode() {
        // Set look and feel
        LookAndFeel lookAndFeel = new FlatLightLaf();
        try {
            javax.swing.UIManager.setLookAndFeel(lookAndFeel);
        } catch (UnsupportedLookAndFeelException ex) {
            log.error(lookAndFeel.getName() + " is not supported!");
        }

        // Update theme
        this.updateTheme(false);
    }

    public void setDarkMode() {
        // Set look and feel
        LookAndFeel lookAndFeel = new FlatDarkLaf();
        try {
            javax.swing.UIManager.setLookAndFeel(lookAndFeel);
        } catch (UnsupportedLookAndFeelException ex) {
            log.error(lookAndFeel.getName() + " is not supported!");
        }

        // Update theme
        this.updateTheme(true);
    }

    public void updateTheme(boolean isDarkMode) {
        // Update client frame and recursively update all frames and panels
        if (this.clientFrame != null) {
            // Update all frames and panels
            SwingUtilities.invokeLater(() -> {
                Enumeration<Object> keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);
                    if (value instanceof javax.swing.plaf.FontUIResource) {
                        UIManager.put(key, new javax.swing.plaf.FontUIResource("Segoe UI", Font.PLAIN, 14));
                    }
                }

                updateUIRecursively(Frame.getFrames());
            });

            // Update other stuff
            if (isDarkMode) {
                this.clientFrame.setDarkMode();
            } else {
                this.clientFrame.setLightMode();
            }
        }
    }

    private static void updateUIRecursively(Window[] windows) {
        for (Window window : windows) {
            if (window instanceof Frame || window instanceof Dialog) {
                SwingUtilities.updateComponentTreeUI(window);
            }

            if (window instanceof Frame) {
                updateUIRecursively(window.getOwnedWindows());
            }
        }
    }

    public void setClientFrameVisibility(boolean visibility) { this.clientFrame.setVisible(visibility); }

    public void setInputTextAreaString(String inputTextAreaString) { this.clientFrame.setInputTextAreaString(inputTextAreaString);}
    
    public void setOutputAreaString(String string) { this.clientFrame.setOutputAreaString(string); }

    public void setOutputTableData(ArrayList<String> fieldNames, ArrayList<ArrayList<String>> data) { this.clientFrame.setOutputTableData(fieldNames, data); }

    public void increaseCenterPanelFont() { this.clientFrame.increaseCenterPanelFont(); }

    public void decreaseCenterPanelFont() { this.clientFrame.decreaseCenterPanelFont();}

    public void inputAreaChanged() { this.clientFrame.inputAreaChanged(); }

    public void saveCurrentFile() { this.clientFrame.saveCurrentFile(); }

    /* Getters */
    public String getInputTextAreaString() { return this.clientFrame.getInputTextAreaString(); }

    /* Main */
    public static void main(String[] args) {
        new ClientController();
    }
}
