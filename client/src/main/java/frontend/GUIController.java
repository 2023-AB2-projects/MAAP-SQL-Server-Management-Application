package frontend;

import backend.MessageModes;
import control.ClientController;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

@Slf4j
public class GUIController extends JFrame implements ActionListener, ItemListener {
    // Reference to ClientController
    private final ClientController clientController;

    // Components
    private ConnectionFrame connectionFrame;

    @Getter
    private MenuController menuController;
    private String selectedDatabase;
    private InputTextArea inputTextArea;
    private OutputTextArea outputTextArea;

    // References
    private JButton sendCommandButton;
    private JButton connectionButton;


    public GUIController(ClientController clientController) {
        // Reference
        this.clientController = clientController;
        // Setup JFrame
        this.frameSetup();

        // Init menu controller
        this.initComponents();

        // Add menu controller to frame
        this.addComponents();

        // Add components to listener (listener = this)
        this.addListeners();

//        // Set frame to visible
//        this.setVisible(true);
    }

    private void frameSetup() {
        // JFrame settings
        this.setSize(1600, 900);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    clientController.stopConnection();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                super.windowClosing(e);
                System.exit(0);
            }
        });
        this.setTitle("Client side application");
        this.setFocusable(true);

        // Layout manager
        this.setLayout(new GridLayout(2, 1, 10, 10));
    }

    private void initComponents() {
        // Connection Frame
        this.connectionFrame = new ConnectionFrame(this);
        this.connectionButton = this.connectionFrame.getConnectButton();

        // Init menu controller
        this.menuController = new MenuController(this);
        this.sendCommandButton = this.menuController.getSendCommandButton();    // Reference

        // Input/Output text areas
        this.inputTextArea = new InputTextArea("COMMAND INPUT");
        this.outputTextArea = new OutputTextArea("COMMAND OUTPUT");
    }

    private void addComponents() {
        // Add menu controller to JFrame
        this.setJMenuBar(this.menuController);

        // Add input/output text areas
        this.add(this.inputTextArea);
        this.add(this.outputTextArea);
    }

    private void addListeners() {
        this.sendCommandButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        if (event.getSource().equals(this.sendCommandButton)) {
            String command = this.inputTextArea.getText();

//            System.out.println("Debug - 1. GUI Controller: Send button pressed!");
//            System.out.println("Debug - 2. GUI Controller: Delegating work to client controller (-> MessageHandler)!");
//            System.out.println("Debug - 3. GUI Controller: Command: " + command);
            this.clientController.sendCommandToServer(command);
            this.receiveMessageAndPerformAction(MessageModes.refreshDatabases);
            this.receiveMessageAndPerformAction(MessageModes.setTextArea);


        } else if(event.getSource().equals(this.connectionButton)) {
            String ip = this.connectionFrame.getIP();

            try {
                this.clientController.establishConnection(ip);
                // SQL window should be visible
                this.setVisible(true);
                //change this later
                connectionFrame.setVisible(false);

                this.clientController.sendCommandToServer("USE master");
                this.receiveMessageAndPerformAction(MessageModes.refreshDatabases);
                this.receiveMessageAndPerformAction(MessageModes.setTextArea);

            } catch (IOException e) {
                //change this later maybe
                System.out.println("Server Not Running");
            }
        }
    }


    /* Setters */
    public void setInputTextAreaString(String string) { this.inputTextArea.setInputTextAreaString(string); }

    //method receives message from server and performs action determined by mode param
    public void receiveMessageAndPerformAction(int mode) {

        try {
            String response = clientController.receiveMessage();
            log.info(response + " received from server");
            if (response.equals("SERVER DISCONNECTED")) {
                clientController.stopConnection();
                System.out.println("Server was shut down");
                System.exit(0);
            }

            if (mode == MessageModes.setTextArea) {
                outputTextArea.setText(response);
            } else if (mode == MessageModes.refreshDatabases) {
                this.menuController.addDatabaseNames(response);
            }
        } catch (IOException e) {
            System.out.println("Server is no longer running");
            System.exit(0);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if(event.getSource().equals(menuController.getDatabaseSelector())) {
            JComboBox<String> combo = (JComboBox<String>) event.getSource();
            String selected = (String) combo.getSelectedItem();
            if(selected == null){
                return;
            }

            log.info("USE " + selected + " command sent to server");

            this.clientController.sendCommandToServer("USE " + selected);
            this.receiveMessageAndPerformAction(MessageModes.refreshDatabases);
            this.receiveMessageAndPerformAction(MessageModes.setTextArea);
        }
    }
}
