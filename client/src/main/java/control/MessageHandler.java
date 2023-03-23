package control;

import backend.ConnectionManager;

import java.io.IOException;


public class MessageHandler {
    private ClientController clientController;
    private ConnectionManager connectionManager;

    public MessageHandler(ClientController clientController) {
        // Reference
        this.clientController = clientController;

        // Init connection manager
        this.connectionManager = new ConnectionManager(this);
    }

    /* Message handler methods */
    public void establishConnection(String ip, String host) throws IOException {
        // Connection manager
        this.connectionManager.establishConnection(ip, host);
    }
    public void stopConnection() throws IOException {
        this.connectionManager.stopConnection();
    }
    public void sendCommandToServer(String command) {
        // Before sending processing


        // Use connection
        this.connectionManager.sendMessage(command);
    }

    public String receiveMessage() throws IOException {
        return connectionManager.receiveMessage();
    }
    /* /Message handler methods */
}

