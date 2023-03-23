package frontend;

import javax.swing.*;
import java.awt.*;

public class ConnectionFrame extends JFrame {
    // References
    private GUIController guiController;

    // Components
    private JLabel ipLabel, portLabel;
    private JTextField ipField, portField;
    private JButton connectButton;

    public ConnectionFrame(GUIController guiController) {
        // Reference
        this.guiController = guiController;

        // Setup frame
        this.frameSetup();

        // Init frame components
        this.initComponents();

        // Add components to frame
        this.addComponents();

        // Set frame visible
        this.setVisible(true);
    }

    private void initComponents() {
        // Labels
        this.ipLabel = new JLabel("IP: ");
        this.portLabel = new JLabel("Port: ");

        // Text fields
        this.ipField = new JTextField("");
        this.portField = new JTextField("");

        // Button
        this.connectButton = new JButton("Connect");
    }

    private void addComponents() {
        // IP section
        this.add(this.ipLabel);
        this.add(this.ipField);

        // Port section
        this.add(this.portLabel);
        this.add(this.portField);

        // Connect button
        this.add(this.connectButton);
    }

    private void frameSetup() {
        // JFrame settings
        this.setSize(500, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Connect to server");
        this.setFocusable(true);

        // Layout manager
        this.setLayout(new GridLayout(3, 2, 10, 10));
    }
}
