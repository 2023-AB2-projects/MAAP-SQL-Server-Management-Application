package backend;

import control.MessageHandler;

import java.io.IOException;

public class ConnectionManager {
    private MessageHandler messageHandler;
    private ClientConnection clientConnection;
    public ConnectionManager(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /* Connection methods */
    public void establishConnection(String ip, String port) throws IOException {
        clientConnection = new ClientConnection();
        clientConnection.startConnection(ip, 4444);
        //clientConnection.startConnection(ip, Integer.parseInt(port));
    }

    public void stopConnection() throws IOException {
        clientConnection.stopConnection();
    }

    public void sendMessage(String command) {
        System.out.println(command);
        clientConnection.send(command);
    }

    public String receiveMessage() throws IOException {

        String commandOutput;
        commandOutput = clientConnection.receive();
        // Delegate work to message handler
        return commandOutput;
    }
}
