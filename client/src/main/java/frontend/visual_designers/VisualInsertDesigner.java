package frontend.visual_designers;

import control.ClientController;
import frontend.center_panel.CenterClientPanel;
import frontend.other_elements.SQLDocument;
import lombok.Setter;
import service.CatalogManager;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lorin
 */
public class VisualInsertDesigner extends javax.swing.JPanel {
    // References
    @Setter
    private CenterClientPanel centerClientPanel;

    @Setter
    private ClientController clientController;

    // Constants
    private final int DEFAULT_ROW_COUNT = 3;
    private final int MAX_ROW_COUNT = 20;

    // Logic
    private final DefaultTableModel tableModel;
    private final ArrayList<String> tableValues;

    public VisualInsertDesigner() {
        initComponents();

        // Init table model and other variables
        this.tableModel = new DefaultTableModel();
        this.insertTable.setModel(this.tableModel);

        this.tableValues = new ArrayList<>();
    }

    /* Setters */
    public void updateTables() {
        String currentDatabaseName = this.clientController.getCurrentDatabaseName();
        List<String> tableNames = CatalogManager.getCurrentDatabaseTableNames(currentDatabaseName);

        // Update items in combo box
        this.tableSelectComboBox.removeAllItems();
        for(final String tableName : tableNames) {
            this.tableSelectComboBox.addItem(tableName);
        }
    }

    public void setLightMode() {
        // Update document syntax highlighting
        SQLDocument doc = (SQLDocument) this.generatedCodeTextArea.getStyledDocument();
        doc.lightMode();

        // Update text
        this.generatedCodeTextArea.setText(this.generatedCodeTextArea.getText());
    }

    public void setDarkMode() {
        // Update document syntax highlighting
        SQLDocument doc = (SQLDocument) this.generatedCodeTextArea.getStyledDocument();
        doc.darkMode();

        // Update text
        this.generatedCodeTextArea.setText(this.generatedCodeTextArea.getText());
    }

    /* Logic */

    private void setColumnsAndRowCount(ArrayList<String> columnNames, int rowCount) {
        this.tableModel.setColumnIdentifiers(columnNames.toArray());
        this.tableModel.setRowCount(rowCount);
    }

    private void updateValuesList() {
        // Clear current values
        this.tableValues.clear();

        // Iterate over the data in the table
        for(int rowCount = 0; rowCount < this.insertTable.getRowCount(); ++rowCount) {
            // Iterate over the columns and check if the row is full
            boolean rowIsFull = true;
            for(int columnCount = 0; columnCount < this.insertTable.getColumnCount(); ++columnCount) {
                if(this.insertTable.getValueAt(rowCount, columnCount) == null) {
                    rowIsFull = false;
                }
            }

            // We will only put in the data where all the rows are full
            if(!rowIsFull) break;

            for(int columnCount = 0; columnCount < this.insertTable.getColumnCount(); ++columnCount) {
                // Add values to list (after conversion)
                this.tableValues.add((String) this.insertTable.getValueAt(rowCount, columnCount));
            }
        }
    }

    private String generateInsertCommand(String tableName) {
        StringBuilder commandBuilder = new StringBuilder("INSERT INTO " + tableName + "\nVALUES ");

        // Every 'columnCount' go to new line
        int columnCount = this.insertTable.getColumnCount();
        int rowCount = 0, counter = 0;
        for(final String value : this.tableValues) {
            // Add value according to where it is in the table
            if (counter % columnCount == 0) {
                // First in row case
                if(rowCount != 0) commandBuilder.append(",\n             ");
                commandBuilder.append("(").append(value);
            } else if(counter % columnCount == columnCount - 1) {
                // Last in row case
                commandBuilder.append(", ").append(value).append(")");
                rowCount++;         // Increase row count
            } else {
                // In row case
                commandBuilder.append(", ").append(value);
            }

            // Special case if there's only one column
            if(columnCount == 1) {
                commandBuilder.append(")");
                rowCount++;         // Increase row count
            }

            counter++;              // Update counter
        }

        return commandBuilder.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        generateCodeButton = new javax.swing.JButton();
        tabelNameLabel = new javax.swing.JLabel();
        splitInsertPanel = new javax.swing.JSplitPane();
        tableScrollPanel = new javax.swing.JScrollPane();
        insertTable = new javax.swing.JTable();
        generatedCodeScrollPanel = new javax.swing.JScrollPane();
        generatedCodeTextArea = new javax.swing.JTextPane();
        tableSelectComboBox = new javax.swing.JComboBox<>();
        minusRowButton = new javax.swing.JButton();
        plusRowButton = new javax.swing.JButton();
        executeButton = new javax.swing.JButton();
        explainerScrollPanel = new javax.swing.JScrollPane();
        explainerTextArea = new javax.swing.JTextArea();

        generateCodeButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        generateCodeButton.setText("Generate Code");
        generateCodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                generateCodeButtonMousePressed(evt);
            }
        });

        tabelNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabelNameLabel.setText("Table Name:");
        tabelNameLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        splitInsertPanel.setDividerLocation(300);
        splitInsertPanel.setDividerSize(3);
        splitInsertPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        insertTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        insertTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        insertTable.setGridColor(new java.awt.Color(102, 102, 102));
        insertTable.setInheritsPopupMenu(true);
        insertTable.setName(""); // NOI18N
        insertTable.setShowGrid(true);
        insertTable.getTableHeader().setReorderingAllowed(false);
        tableScrollPanel.setViewportView(insertTable);

        splitInsertPanel.setTopComponent(tableScrollPanel);

        generatedCodeTextArea.setEditable(false);
        generatedCodeTextArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        generatedCodeTextArea.setStyledDocument(new SQLDocument());
        generatedCodeScrollPanel.setViewportView(generatedCodeTextArea);

        splitInsertPanel.setRightComponent(generatedCodeScrollPanel);

        tableSelectComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "table_1", "table_2", "table_3", "table_4" }));
        tableSelectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableSelectComboBoxActionPerformed(evt);
            }
        });

        minusRowButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        minusRowButton.setText("-");
        minusRowButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                minusRowButtonMousePressed(evt);
            }
        });

        plusRowButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        plusRowButton.setText("+");
        plusRowButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                plusRowButtonMousePressed(evt);
            }
        });

        executeButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        executeButton.setText("Execute");
        executeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                executeButtonMousePressed(evt);
            }
        });

        explainerTextArea.setEditable(false);
        explainerTextArea.setColumns(20);
        explainerTextArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        explainerTextArea.setRows(5);
        explainerTextArea.setText("Welcome to the Visual Insert Designer, where we help you design your inserts!\n\nSelect a table from the dropdown in which you would like to insert data.\nIn the table below you will see each field the table has, and you can add values in each row.\nIf a row is not fully filled in or you have rows missing between two rows, those will be ignored.");
        explainerTextArea.setFocusable(false);
        explainerScrollPanel.setViewportView(explainerTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitInsertPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(explainerScrollPanel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tabelNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableSelectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minusRowButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plusRowButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(executeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(generateCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(generateCodeButton)
                        .addComponent(executeButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tabelNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tableSelectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(minusRowButton)
                        .addComponent(plusRowButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(explainerScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitInsertPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 711, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void generateCodeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_generateCodeButtonMousePressed
        // Update the arraylist that stores all the table values
        this.updateValuesList();

        // Now turn this into an SQL insert command and set command output text area
        String tableName = (String) this.tableSelectComboBox.getSelectedItem();
        this.generatedCodeTextArea.setText(this.generateInsertCommand(tableName));
    }//GEN-LAST:event_generateCodeButtonMousePressed

    private void minusRowButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minusRowButtonMousePressed
        if(this.insertTable.getRowCount() > 1) this.tableModel.setRowCount(this.insertTable.getRowCount() - 1);
    }//GEN-LAST:event_minusRowButtonMousePressed

    private void plusRowButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plusRowButtonMousePressed
        if(this.insertTable.getRowCount() < MAX_ROW_COUNT) this.tableModel.setRowCount(this.insertTable.getRowCount() + 1);
    }//GEN-LAST:event_plusRowButtonMousePressed

    private void executeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_executeButtonMousePressed
        // Update the arraylist that stores all the table values
        this.updateValuesList();

        // Now turn this into an SQL insert command and set command output text area
        String tableName = (String) this.tableSelectComboBox.getSelectedItem();
        String command = this.generateInsertCommand(tableName);
        this.generatedCodeTextArea.setText(command);

        // Set command in SQL execution area
        this.centerClientPanel.setInputTextAreaString(command);
        // Switch to that pane
        this.centerClientPanel.setCurrentPane(0);
    }//GEN-LAST:event_executeButtonMousePressed

    private void tableSelectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableSelectComboBoxActionPerformed
        if(this.tableSelectComboBox.getSelectedItem() != null) {
            String tableName = (String) this.tableSelectComboBox.getSelectedItem();

            // Update selection panel
            ArrayList<String> columnNames = (ArrayList<String>) CatalogManager.getFieldNames(this.clientController.getCurrentDatabaseName(), tableName);

            this.setColumnsAndRowCount(columnNames, this.DEFAULT_ROW_COUNT);
        }
    }//GEN-LAST:event_tableSelectComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton executeButton;
    private javax.swing.JScrollPane explainerScrollPanel;
    private javax.swing.JTextArea explainerTextArea;
    private javax.swing.JButton generateCodeButton;
    private javax.swing.JScrollPane generatedCodeScrollPanel;
    private javax.swing.JTextPane generatedCodeTextArea;
    private javax.swing.JTable insertTable;
    private javax.swing.JButton minusRowButton;
    private javax.swing.JButton plusRowButton;
    private javax.swing.JSplitPane splitInsertPanel;
    private javax.swing.JLabel tabelNameLabel;
    private javax.swing.JScrollPane tableScrollPanel;
    private javax.swing.JComboBox<String> tableSelectComboBox;
    // End of variables declaration//GEN-END:variables
}
