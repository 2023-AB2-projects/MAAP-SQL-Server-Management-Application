package frontend.visual_designers.visual_elements;

public class FieldPanelDelete extends javax.swing.JPanel {

    public FieldPanelDelete() {
        initComponents();
    }

    /* Setters */
    public void setColumnName(String columnName) { this.columnField.setText(columnName); }
    public void setTableName(String tableName) { this.tableField.setText(tableName); }

    /* Getters */
    public boolean isSelected() { return this.selectedRadioButton.isSelected(); }
    public String getColumnName() { return this.columnField.getText(); }
    public String getConditions() {
        String conditions = "";
        // If base condition is empty -> No condition
        if(this.conditionField1.getText().equals("")) {
            return "";      // Signal that no base condition has been set -> We will ignore this attribute
        }
        // Add first condition to conditions
        conditions += this.tableField.getText() + "." + this.getColumnName() + " " + this.conditionField1.getText();

        // Check if second condition is non-empty
        if(this.conditionField2.getText().equals("")) {
            return conditions;
        }
        // Add second condition to conditions
        conditions += " OR (" + this.getColumnName() + " " + this.conditionField2.getText() + ")";

        // Check if third condition is non-empty
        if(this.conditionField3.getText().equals("")) {
            return conditions;
        }
        // Add third condition to conditions
        conditions += " OR (" + this.getColumnName() + " " + this.conditionField3.getText() + ")";

        return conditions;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectedRadioButton = new javax.swing.JRadioButton();
        divider4 = new javax.swing.JLabel();
        divider1 = new javax.swing.JLabel();
        divider2 = new javax.swing.JLabel();
        divider3 = new javax.swing.JLabel();
        tableField = new javax.swing.JTextField();
        columnField = new javax.swing.JTextField();
        conditionField1 = new javax.swing.JTextField();
        conditionField3 = new javax.swing.JTextField();
        conditionField2 = new javax.swing.JTextField();
        divider5 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1000, 50));

        selectedRadioButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        selectedRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        selectedRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedRadioButtonActionPerformed(evt);
            }
        });

        divider4.setBackground(new java.awt.Color(102, 102, 102));
        divider4.setOpaque(true);

        divider1.setBackground(new java.awt.Color(102, 102, 102));
        divider1.setOpaque(true);

        divider2.setBackground(new java.awt.Color(102, 102, 102));
        divider2.setOpaque(true);

        divider3.setBackground(new java.awt.Color(102, 102, 102));
        divider3.setOpaque(true);

        tableField.setEditable(false);
        tableField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tableField.setText("table_name");
        tableField.setFocusable(false);
        tableField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableFieldActionPerformed(evt);
            }
        });

        columnField.setEditable(false);
        columnField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        columnField.setText("column_name");
        columnField.setFocusable(false);
        columnField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnFieldActionPerformed(evt);
            }
        });

        conditionField1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        conditionField1.setEnabled(false);

        conditionField3.setEditable(false);
        conditionField3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        conditionField3.setEnabled(false);
        conditionField3.setMinimumSize(new java.awt.Dimension(68, 38));
        conditionField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conditionField3ActionPerformed(evt);
            }
        });

        conditionField2.setEditable(false);
        conditionField2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        conditionField2.setEnabled(false);
        conditionField2.setMinimumSize(new java.awt.Dimension(68, 38));
        conditionField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conditionField2ActionPerformed(evt);
            }
        });

        divider5.setBackground(new java.awt.Color(102, 102, 102));
        divider5.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectedRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(divider5, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(columnField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(divider1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(tableField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(divider2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(conditionField1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(divider3, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(conditionField2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(divider4, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(conditionField3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectedRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(divider5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(conditionField1)
                            .addComponent(divider1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(divider2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(divider3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(columnField, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                            .addComponent(conditionField2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tableField)
                            .addComponent(conditionField3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(divider4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedRadioButtonActionPerformed
        boolean selected = this.selectedRadioButton.isSelected();
        this.conditionField1.setEnabled(selected);
        this.conditionField2.setEnabled(selected);
        this.conditionField3.setEnabled(selected);
    }//GEN-LAST:event_selectedRadioButtonActionPerformed

    private void columnFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_columnFieldActionPerformed

    private void tableFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tableFieldActionPerformed

    private void conditionField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conditionField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_conditionField2ActionPerformed

    private void conditionField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conditionField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_conditionField3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField columnField;
    private javax.swing.JTextField conditionField1;
    private javax.swing.JTextField conditionField2;
    private javax.swing.JTextField conditionField3;
    private javax.swing.JLabel divider1;
    private javax.swing.JLabel divider2;
    private javax.swing.JLabel divider3;
    private javax.swing.JLabel divider4;
    private javax.swing.JLabel divider5;
    private javax.swing.JRadioButton selectedRadioButton;
    private javax.swing.JTextField tableField;
    // End of variables declaration//GEN-END:variables
}
