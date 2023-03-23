package frontend;

import control.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIController extends JFrame implements ActionListener {
    // Reference to ClientController
    private ClientController clientController;

    // Components
    private ConnectionFrame connectionFrame;
    private MenuController menuController;
    private InputTextArea inputTextArea;
    private OutputTextArea outputTextArea;

    // References
    private JButton sendCommandButton;

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

        // Set frame to visible
        this.setVisible(true);
    }

    private void frameSetup() {
        // JFrame settings
        this.setSize(1600, 900);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Client side application");
        this.setFocusable(true);

        // Layout manager
        this.setLayout(new GridLayout(2, 1, 10, 10));
    }

    private void initComponents() {
        // Connection Frame
        this.connectionFrame = new ConnectionFrame(this);

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
            String command = this.sendCommandButton.getText();

            System.out.println("Debug - 1. GUI Controller: Send button pressed!");
            System.out.println("Debug - 2. GUI Controller: Delegating work to client controller (-> MessageHandler)!");
            System.out.println("Debug - 3. GUI Controller: Command: " + command);

            this.clientController.sendCommandToServer(command);
        }
    }

    /* Setters */
    public void setInputTextAreaString(String string) { this.inputTextArea.setInputTextAreaString(string); }

    public void setCommandOutput(String commandOutput) { this.outputTextArea.setText(commandOutput) ;}
}
