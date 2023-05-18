package frontend;

import backend.MessageModes;
import control.ClientController;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionFrame extends javax.swing.JFrame {
    // References
    private final ClientController clientController;
    
    public ConnectionFrame(ClientController clientController) {
        // Reference
        this.clientController = clientController;

        // Look and feel
//        try {
//            javax.swing.UIManager.setLookAndFeel(this.clientController.getLookAndFeel());
//        } catch (UnsupportedLookAndFeelException ex) {
//            log.error("FlatLafDark is not supported!");
//        }
        
        initComponents();

        /* Create and display the form */
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ipTextField = new javax.swing.JTextField();
        sQLLabel = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        ipLabel = new javax.swing.JLabel();
        portLabel = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Connect to Server");

        ipTextField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ipTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipTextFieldActionPerformed(evt);
            }
        });

        sQLLabel.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        sQLLabel.setText("SQL Server Connection");
        sQLLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        portTextField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        portTextField.setEnabled(false);
        portTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portTextFieldActionPerformed(evt);
            }
        });

        ipLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ipLabel.setText("IP Address:");

        portLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        portLabel.setText("Port:");

        connectButton.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        connectButton.setText("Connect To Server");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(sQLLabel)
                        .addGap(77, 77, 77))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(portLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(portTextField))
                        .addGap(42, 42, 42))))
            .addGroup(layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addComponent(connectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(sQLLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(connectButton)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ipTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipTextFieldActionPerformed

    private void portTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portTextFieldActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        // TODO add your handling code here:
        String ip = this.ipTextField.getText();

        try {
            this.clientController.establishConnection(ip);

            // Main SQL window should be visible
            this.clientController.setClientFrameVisibility(true);

            // change this later? (why)
            this.setVisible(false);

            this.clientController.sendCommandToServer("USE master");
            this.clientController.receiveMessageAndPerformAction(MessageModes.refreshJSONCatalog);
            this.clientController.receiveMessageAndPerformAction(MessageModes.setTextArea);

        } catch (IOException e) {
            //change this later maybe
            log.error("Connection button pressed -> Couldn't connect to server!");
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel ipLabel;
    private javax.swing.JTextField ipTextField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField portTextField;
    private javax.swing.JLabel sQLLabel;
    // End of variables declaration//GEN-END:variables
}
