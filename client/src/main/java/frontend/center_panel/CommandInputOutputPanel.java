package frontend.center_panel;

import lombok.Setter;

public class CommandInputOutputPanel extends javax.swing.JPanel {
    @Setter
    private CenterClientPanel centerClientPanel;

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

        commandSplitPane = new javax.swing.JSplitPane();
        inputScrollPanel = new javax.swing.JScrollPane();
        inputArea = new frontend.visual_designers.visual_elements.SQLTextPane();
        outputScrollPanel = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(900, 1000));
        setPreferredSize(new java.awt.Dimension(900, 900));

        commandSplitPane.setDividerLocation(450);
        commandSplitPane.setDividerSize(3);
        commandSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        inputArea.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        inputArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputAreaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputAreaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inputAreaKeyTyped(evt);
            }
        });
        inputScrollPanel.setViewportView(inputArea);

        commandSplitPane.setTopComponent(inputScrollPanel);

        outputArea.setEditable(false);
        outputArea.setColumns(20);
        outputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        outputArea.setRows(5);
        outputArea.setText("COMMAND OUTPUT");
        outputScrollPanel.setViewportView(outputArea);

        commandSplitPane.setRightComponent(outputScrollPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void inputAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputAreaKeyTyped
    }//GEN-LAST:event_inputAreaKeyTyped

    private void inputAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputAreaKeyReleased
        // TODO add your handling code here:
        String text = this.inputArea.getText();
        this.inputArea.setTextSQL(text);
    }//GEN-LAST:event_inputAreaKeyReleased

    private void inputAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputAreaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputAreaKeyPressed

    /* Setters */
    public void setInputTextAreaString(String string) { this.inputArea.setText(string); }

    public void setOutputAreaString(String string) { this.outputArea.setText(string); }

    /* Getters */
    public String getInputAreaText() { return this.inputArea.getText(); }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane commandSplitPane;
    private frontend.visual_designers.visual_elements.SQLTextPane inputArea;
    private javax.swing.JScrollPane inputScrollPanel;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JScrollPane outputScrollPanel;
    // End of variables declaration//GEN-END:variables
}
