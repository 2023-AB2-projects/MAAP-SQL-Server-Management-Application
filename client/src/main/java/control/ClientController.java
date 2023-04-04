package control;

import frontend.GUIController;
import frontend.MenuController;
import frontend3.ClientFrame;

import java.io.IOException;
import java.util.ArrayList;

public class ClientController {
    /* ClientController has a reference to GUI, MessageHandler and ConnectionManager */
    private ClientFrame clientFrame;
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
        this.clientFrame = new ClientFrame(this);
//        this.menuController = guiController.getMenuController();

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

    public static void main(String[] args) {
        ClientController client = new ClientController();
    }
}
