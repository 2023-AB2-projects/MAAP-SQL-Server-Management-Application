package control;

import backend.MessageModes;
import com.formdev.flatlaf.FlatDarculaLaf;
import frontend3.ClientFrame;
import frontend3.ConnectionFrame;

import java.io.IOException;
import java.util.ArrayList;
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
    }

    private void updateCurrentDatabases(String databasesString) {
        // Clear current database names
        this.databaseNames.clear();

        // Split up received string
        StringTokenizer tokenizer = new StringTokenizer(databasesString, ",");
        while (tokenizer.hasMoreTokens()) {
            this.databaseNames.add(tokenizer.nextToken());
        }

        // Update Tree node
        this.clientFrame.updateDatabaseNamesTree(this.databaseNames);

        System.out.println("Initial database names: " + this.databaseNames);
    }
    /* / Utility */

    /* Client controls */
    public void establishConnection(String ip) throws IOException {
        this.messageHandler.establishConnection(ip);

        // Update current databases list
        String databasesString = messageHandler.receiveMessage();
        this.updateCurrentDatabases(databasesString);
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
                    if(response.contains("Now using")) {
                        String currentDatabaseName = response.split("Now using ")[1];
                        log.info("Current database name: " + currentDatabaseName);
                        this.clientFrame.setCurrentDatabaseName(currentDatabaseName);
                    }
                }
                case MessageModes.refreshDatabases -> this.updateCurrentDatabases(response);

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

    /* Main */
    public static void main(String[] args) {
        new ClientController();
    }
}
