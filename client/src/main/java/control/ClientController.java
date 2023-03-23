package control;

import backend.ConnectionManager;
import frontend.GUIController;

public class ClientController {
    /* ClientController has a reference to GUI, MessageHandler and ConnectionManager */
    private GUIController guiController;
    private MessageHandler messageHandler;

    public ClientController() {
        // Init Client side components
        this.initComponents();
    }

    private void initComponents() {
        // Init GUI
        this.guiController = new GUIController(this);

        // Message handler
        this.messageHandler = new MessageHandler(this);
    }

    /* Client controls */
    public void establishConnection(String ip, String port) {
        this.messageHandler.establishConnection(ip, port);
    }

    public void sendCommandToServer(String command) {
        this.messageHandler.sendCommandToServer(command);
    }

    public void setCommandOutput(String commandOutput) {
        this.guiController.setCommandOutput(commandOutput);
    }
    /* /Client controls */

    public static void main(String[] args) {
        ClientController client = new ClientController();
    }
}
