package frontend.center_panel;

import control.ClientController;

import java.awt.*;
import java.util.ArrayList;

public class CenterClientPanel extends javax.swing.JPanel {
    private ClientController clientController;

    public CenterClientPanel() {
        initComponents();

        // Set references
        this.visualInsertDesignerPanel.setCenterClientPanel(this);
        this.visualDeleteDesignerPanel.setCenterClientPanel(this);
        this.visualSelectDesignerPanel.setCenterClientPanel(this);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        clientTabbedPane = new javax.swing.JTabbedPane();
        sqlCommandExecutionPanel = new javax.swing.JSplitPane();
        commandPanel1 = new frontend.center_panel.CommandPanel();
        commandInputOutputPanel1 = new frontend.center_panel.CommandInputOutputPanel();
        visualInsertDesignerPanel = new frontend.visual_designers.VisualInsertDesigner();
        visualDeleteDesignerPanel = new frontend.visual_designers.VisualDeleteDesigner();
        visualSelectDesignerPanel = new frontend.visual_designers.VisualSelectDesigner();

        clientTabbedPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        clientTabbedPane.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clientTabbedPane.setMinimumSize(new java.awt.Dimension(900, 900));
        clientTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                clientTabbedPaneStateChanged(evt);
            }
        });

        sqlCommandExecutionPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sqlCommandExecutionPanel.setTopComponent(commandPanel1);
        sqlCommandExecutionPanel.setRightComponent(commandInputOutputPanel1);

        clientTabbedPane.addTab("SQL Command Execution", sqlCommandExecutionPanel);
        clientTabbedPane.addTab("Visual Insert Designer", visualInsertDesignerPanel);
        clientTabbedPane.addTab("Visual Delete Designer", visualDeleteDesignerPanel);
        clientTabbedPane.addTab("Visual Select Designer", visualSelectDesignerPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void clientTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_clientTabbedPaneStateChanged
        // If we are switching to the 'Select Query tab'
        if (this.clientTabbedPane.getSelectedIndex() == 3) {
            this.visualSelectDesignerPanel.update(this.clientController.getCurrentDatabaseName());
        }
    }//GEN-LAST:event_clientTabbedPaneStateChanged


    /* Setters */
    public void setInputTextAreaString(String string) {this.commandInputOutputPanel1.setInputTextAreaString(string);}

    public void increaseFont() {
        this.commandInputOutputPanel1.increaseFont();
    }

    public void decreaseFont() {
        this.commandInputOutputPanel1.decreaseFont();
    }

    public void setOutputAreaString(String string) {
        this.commandInputOutputPanel1.setOutputAreaString(string);
    }

    public void setErrorOutputAreaString(String error) { this.commandInputOutputPanel1.setErrorOutputAreaString(error); }

    public void setOutputTableData(ArrayList<String> fieldNames, ArrayList<ArrayList<String>> data) { this.commandInputOutputPanel1.setOutputTableData(fieldNames, data); }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
        this.commandPanel1.setClientController(clientController);
        this.visualInsertDesignerPanel.setClientController(clientController);
        this.visualDeleteDesignerPanel.setClientController(clientController);
    }

    public void setCurrentPane(int index) {
        int maxIndex = this.clientTabbedPane.getComponentCount();
        if(index >= 0 && index < maxIndex) {
            if(index == 1 || index == 2) {
                this.update();
            }
            this.clientTabbedPane.setSelectedIndex(index);
        }
    }

    /* Getters */
    public String getInputAreaText() { return this.commandInputOutputPanel1.getInputAreaText(); }

    /* Others */
    public void update() {
        this.visualInsertDesignerPanel.updateTables();
        this.visualDeleteDesignerPanel.updateTables();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane clientTabbedPane;
    private frontend.center_panel.CommandInputOutputPanel commandInputOutputPanel1;
    private frontend.center_panel.CommandPanel commandPanel1;
    private javax.swing.JSplitPane sqlCommandExecutionPanel;
    private frontend.visual_designers.VisualDeleteDesigner visualDeleteDesignerPanel;
    private frontend.visual_designers.VisualInsertDesigner visualInsertDesignerPanel;
    private frontend.visual_designers.VisualSelectDesigner visualSelectDesignerPanel;
    // End of variables declaration//GEN-END:variables
}
