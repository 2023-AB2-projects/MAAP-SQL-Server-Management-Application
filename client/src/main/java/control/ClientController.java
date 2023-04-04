package control;

import backend.MessageModes;
import frontend3.ClientFrame;
import frontend3.ConnectionFrame;

import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientController {
    /* ClientController has a reference to GUI, MessageHandler and ConnectionManager */
    private ClientFrame clientFrame;
    private ConnectionFrame connectionFrame;
    private MessageHandler messageHandler;
//    private MenuController menuController;

    // Other variables
    private ArrayList<String> databaseNames;

    public ClientController() {
        // Init Client side components
        this.initComponents();

        // Init controler variables
        this.initVariables();
    }

    private void initComponents() {
        // Init GUI
        this.clientFrame = new ClientFrame(this);   // Hidden by default
        this.connectionFrame = new ConnectionFrame(this);

        // Message handler
        this.messageHandler = new MessageHandler(this);
    }

    private void initVariables() {
        // Init variables
        this.databaseNames = new ArrayList<>();
    }

    /* Client controls */
    public void establishConnection(String ip) throws IOException {
        this.messageHandler.establishConnection(ip);
        String databaseNames = messageHandler.receiveMessage();

        System.out.println("Initial database names: " + databaseNames);
        this.databaseNames.add(databaseNames);
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
    //method receives message from server and performs action determined by mode param
    public void receiveMessageAndPerformAction(int mode) {
        try {
            String response = this.receiveMessage();
            log.info(response + " received from server");
            
            if (response.equals("SERVER DISCONNECTED")) {
                this.stopConnection();
                log.info("Server was shut down");
                System.exit(0);
            }

            switch(mode) {
                case MessageModes.setTextArea:
                    
                    break;
                   
                case MessageModes.refreshDatabases:
                    this.databaseNames.clear();
                    this.databaseNames.add(response);
                    break;
            }
            
        } catch (IOException e) {
            log.info("Server is no longer running");
            System.exit(0);
        }
    }

    /* Setters */
    public void setClientFrameVisibility(boolean visibility) {
        this.clientFrame.setVisible(visibility);
    }
    
    public static void main(String[] args) {
        new ClientController();
    }
}
