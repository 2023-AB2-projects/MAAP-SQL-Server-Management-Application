package frontend.visual_designers;

import frontend.center_panel.CenterClientPanel;

import java.util.List;

public class VisualSelectDesigner extends javax.swing.JPanel {
    // References
    private CenterClientPanel clientPanel;

    public VisualSelectDesigner() {
        initComponents();

        // Set references
        this.selectTablesPanel.setVisualSelectDesigner(this);

        // Disable second panel first
        this.tabbedPanel.setEnabledAt(1, false);
    }

    public void switchToSelectorPanel(String databaseName, List<String> tableNames) {
        // Enable query designer
        this.tabbedPanel.setEnabledAt(1, true);

        // Update
        this.selectMainPanel.update(databaseName, tableNames);

        // Switch
        this.tabbedPanel.setSelectedIndex(1);
    }

    /* Setters */
    public void setCenterClientPanel(CenterClientPanel clientPanel) {
        this.clientPanel = clientPanel;
        this.selectMainPanel.setCenterClientPanel(this.clientPanel);
    }

    public void update(String databaseName) {
        // If no database name is set -> Set it
        if (this.selectTablesPanel.getDatabaseName() == null) {
            this.selectTablesPanel.updateDatabase(databaseName);
        }

        // Update select tables panel
        if (!this.selectTablesPanel.getDatabaseName().equals(databaseName)) {
            // Update table selection
            this.selectTablesPanel.updateDatabase(databaseName);

            // Set other panel disabled
            this.tabbedPanel.setEnabledAt(1, false);
            this.tabbedPanel.setSelectedIndex(0);

            // Clear selector panel
            this.selectMainPanel.clear();
        }
    }

    public void setLightMode() {
        this.selectMainPanel.setLightMode();
    }

    public void setDarkMode() {
        this.selectMainPanel.setDarkMode();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPanel = new javax.swing.JTabbedPane();
        selectTablesPanel = new frontend.visual_designers.visual_select.SelectTablesPanel();
        selectMainPanel = new frontend.visual_designers.visual_select.SelectMainPanel();

        tabbedPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabbedPanel.addTab("Select Tables", selectTablesPanel);
        tabbedPanel.addTab("Visual Select Designer", selectMainPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private frontend.visual_designers.visual_select.SelectMainPanel selectMainPanel;
    private frontend.visual_designers.visual_select.SelectTablesPanel selectTablesPanel;
    private javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables
}
