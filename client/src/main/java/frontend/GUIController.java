package frontend;

import control.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class GUIController extends JFrame implements ActionListener {
    // Reference to ClientController
    private final ClientController clientController;

    // Components
    private ConnectionFrame connectionFrame;
    private MenuController menuController;
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

            try {
                String response = clientController.receiveMessage();
                outputTextArea.setText(response);
                if(response.equals("SERVER DISCONNECTED")){
                    clientController.stopConnection();
                    System.out.println("Server was shut down");
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println("Server is no longer running");
                System.exit(0);
            }

        } else if(event.getSource().equals(this.connectionButton)) {
            String ip = this.connectionFrame.getIP();
//            String port = this.connectionFrame.getPort();

//            System.out.println("Debug - 1. GUI Controller: Connect button pressed!");
//            System.out.println("Debug - 2. GUI Controller: Delegating work to client controller!");
//            System.out.println("Debug - 3. GUI Controller: IP: " + ip + " | Port: " + port);

            try {
                this.clientController.establishConnection(ip);
                // SQL window should be visible
                this.setVisible(true);
                //change this later
                connectionFrame.setVisible(false);
            } catch (IOException e) {
                //change this later maybe
                System.out.println("Server Not Running");
            }
        }
    }

    /* Setters */
    public void setInputTextAreaString(String string) { this.inputTextArea.setInputTextAreaString(string); }

}
