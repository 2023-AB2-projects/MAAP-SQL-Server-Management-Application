package control;

import frontend.GUIController;
import frontend.MenuController;

import java.io.IOException;

public class ClientController {
    /* ClientController has a reference to GUI, MessageHandler and ConnectionManager */
    private GUIController guiController;
    private MessageHandler messageHandler;
    private MenuController menuController;

    public ClientController() {
        // Init Client side components
        this.initComponents();
    }

    private void initComponents() {
        // Init GUI
        this.guiController = new GUIController(this);
        this.menuController = guiController.getMenuController();
        // Message handler
        this.messageHandler = new MessageHandler(this);
    }

    /* Client controls */
    public void establishConnection(String ip) throws IOException {
        this.messageHandler.establishConnection(ip);
        String databaseNames = messageHandler.receiveMessage();

        menuController.addDatabaseNames(databaseNames);
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

    public static void main(String[] args) {
        ClientController client = new ClientController();
    }
}
