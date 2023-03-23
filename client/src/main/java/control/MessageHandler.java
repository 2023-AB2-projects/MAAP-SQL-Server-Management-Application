package control;

import backend.ConnectionManager;


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
    public void establishConnection(String ip, String host) {
        // Connection manager
        this.connectionManager.establishConnection(ip, host);
    }

    public void sendCommandToServer(String command) {
        // Before sending processing


        // Use connection
        this.connectionManager.sendMessage(command);
    }

    public void processReceivedOutputFromServer(String commandOutput) {
        // Processing

        // Then send to client controller
        this.clientController.setCommandOutput(commandOutput);
    }
    /* /Message handler methods */
}

