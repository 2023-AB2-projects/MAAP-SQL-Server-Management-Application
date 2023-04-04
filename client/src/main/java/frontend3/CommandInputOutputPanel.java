package frontend3;

import javax.swing.JSplitPane;


public class CommandInputOutputPanel extends javax.swing.JPanel {

    public CommandInputOutputPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CommandSplitPane = new javax.swing.JSplitPane();
        CommandInputPane = new javax.swing.JScrollPane();
        CommandInputArea = new javax.swing.JTextArea();
        CommandOutputPane = new javax.swing.JScrollPane();
        CommandOutputArea = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(900, 1000));
        setPreferredSize(new java.awt.Dimension(900, 900));

        CommandSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        CommandInputArea.setColumns(20);
        CommandInputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        CommandInputArea.setRows(5);
        CommandInputArea.setText("COMMAND INPUT");
        CommandInputPane.setViewportView(CommandInputArea);

        CommandSplitPane.setTopComponent(CommandInputPane);

        CommandOutputArea.setColumns(20);
        CommandOutputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        CommandOutputArea.setRows(5);
        CommandOutputArea.setText("COMMAND OUTPUT");
        CommandOutputPane.setViewportView(CommandOutputArea);

        CommandSplitPane.setRightComponent(CommandOutputPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CommandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CommandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea CommandInputArea;
    private javax.swing.JScrollPane CommandInputPane;
    private javax.swing.JTextArea CommandOutputArea;
    private javax.swing.JScrollPane CommandOutputPane;
    private javax.swing.JSplitPane CommandSplitPane;
    // End of variables declaration//GEN-END:variables
}
