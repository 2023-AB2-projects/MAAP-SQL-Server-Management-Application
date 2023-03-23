package backend;

import control.MessageHandler;

public class ConnectionManager {
    private MessageHandler messageHandler;

    public ConnectionManager(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /* Connection methods */
    public void establishConnection(String ip, String host) {
        // Magic here
    }

    public void sendMessage(String command) {
        // Magic
    }

    public void receiveMessage() {
        // Magic here
        String commandOutput = "";

        // Delegate work to message handler
        this.messageHandler.processReceivedOutputFromServer(commandOutput);
    }
}
