package frontend.visual_designers.visual_elements;

public class fieldPanelDelete extends javax.swing.JPanel {

    public fieldPanelDelete() {
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
        conditions += "(" + this.getColumnName() + this.conditionField1.getText() + ")";

        // Check if second condition is non-empty
        if(this.conditionField2.getText().equals("")) {
            return conditions;
        }
        // Add second condition to conditions
        conditions += " OR (" + this.getColumnName() + this.conditionField2.getText() + ")";

        // Check if third condition is non-empty
        if(this.conditionField3.getText().equals("")) {
            return conditions;
        }
        // Add third condition to conditions
        conditions += " OR (" + this.getColumnName() + this.conditionField3.getText() + ")";

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
        tableField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableField.setText("table_name");
        tableField.setFocusable(false);

        columnField.setEditable(false);
        columnField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        columnField.setText("column_name");
        columnField.setFocusable(false);

        conditionField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        conditionField3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        conditionField3.setMinimumSize(new java.awt.Dimension(68, 38));

        conditionField2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        conditionField2.setMinimumSize(new java.awt.Dimension(68, 38));

        divider5.setBackground(new java.awt.Color(102, 102, 102));
        divider5.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectedRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(divider5, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(columnField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(divider1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(divider2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(conditionField1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(divider3, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(conditionField2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(divider4, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    }//GEN-LAST:event_selectedRadioButtonActionPerformed


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
