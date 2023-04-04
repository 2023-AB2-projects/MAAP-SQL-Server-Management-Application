/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package frontend3.center_panel;

/**
 *
 * @author lorin
 */
public class CommandPanel extends javax.swing.JPanel {

    /**
     * Creates new form CommandPanel
     */
    public CommandPanel() {
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

        selectComboxBox = new javax.swing.JComboBox<>();
        createAlterComboBox = new javax.swing.JComboBox<>();
        dropDeleteComboBox = new javax.swing.JComboBox<>();
        insertUpdateComboBox = new javax.swing.JComboBox<>();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        runCommandButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1000, 42));

        selectComboxBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECT" }));
        selectComboxBox.setToolTipText("");
        selectComboxBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectComboxBoxActionPerformed(evt);
            }
        });

        createAlterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CREATE/ALTER" }));
        createAlterComboBox.setToolTipText("");
        createAlterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAlterComboBoxActionPerformed(evt);
            }
        });

        dropDeleteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DROP/DELETE" }));
        dropDeleteComboBox.setToolTipText("");
        dropDeleteComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropDeleteComboBoxActionPerformed(evt);
            }
        });

        insertUpdateComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INSERT/UPDATE" }));
        insertUpdateComboBox.setToolTipText("");
        insertUpdateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertUpdateComboBoxActionPerformed(evt);
            }
        });

        runCommandButton.setText("RUN COMMAND");
        runCommandButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        runCommandButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runCommandButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                runCommandButtonMousePressed(evt);
            }
        });
        runCommandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runCommandButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(selectComboxBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(createAlterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(dropDeleteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(insertUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(415, 415, 415)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(runCommandButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(selectComboxBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(createAlterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(dropDeleteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(insertUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(runCommandButton)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectComboxBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectComboxBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectComboxBoxActionPerformed

    private void createAlterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAlterComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_createAlterComboBoxActionPerformed

    private void dropDeleteComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropDeleteComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dropDeleteComboBoxActionPerformed

    private void insertUpdateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertUpdateComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_insertUpdateComboBoxActionPerformed

    private void runCommandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runCommandButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_runCommandButtonActionPerformed

    private void runCommandButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_runCommandButtonMousePressed
        // TODO add your handling code here:
        System.out.println("Run Command Button pressed!");
    }//GEN-LAST:event_runCommandButtonMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> createAlterComboBox;
    private javax.swing.JComboBox<String> dropDeleteComboBox;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> insertUpdateComboBox;
    private javax.swing.JButton runCommandButton;
    private javax.swing.JComboBox<String> selectComboxBox;
    // End of variables declaration//GEN-END:variables
}
