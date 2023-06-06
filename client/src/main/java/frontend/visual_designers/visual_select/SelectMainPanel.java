package frontend.visual_designers.visual_select;

import frontend.center_panel.CenterClientPanel;
import frontend.other_elements.SQLDocument;
import lombok.extern.slf4j.Slf4j;
import service.CatalogManager;
import service.ForeignKeyModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class SelectMainPanel extends javax.swing.JPanel {
    private String databaseName;
    private List<SelectTableFieldsPanel> tableFieldsPanels;

    // References
    private CenterClientPanel centerClientPanel;

    public SelectMainPanel() {
        initComponents();
        
        this.initVariables();
    }

    private void initVariables() {
        this.tableFieldsPanels = new ArrayList<>();

        // Make first two columns of table not editable
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Field Name", "Table Name", "Alias", "Condition"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make first two columns not editable
                return column != 0 && column != 1;
            }
        };
        this.fieldSelectorTable.setModel(model);
    }

    public void update(String databaseName, List<String> tableNames) {
        // Update database name and tables
        this.databaseName = databaseName;

        // Empty current list of panels and create new ones
        this.tableFieldsPanels.clear();
        this.tableSelectorsPanel.removeAll();

        // Create new panels
        for (final String tableName : tableNames) {
            // Create panel object and set reference
            SelectTableFieldsPanel panel = new SelectTableFieldsPanel(databaseName, tableName);
            panel.setMainPanel(this);

            // Add to list and panel
            this.tableFieldsPanels.add(panel);
            this.tableSelectorsPanel.add(panel);
        }

        // Update panels according to current theme
        if (this.centerClientPanel.getClientController().isDarkMode())
            this.setDarkMode();
        else {
            System.out.println("Light mode");
            this.setLightMode();
        }

        this.tableSelectorsPanel.revalidate();
        this.tableSelectorsPanel.repaint();
    }

    public void clear() {
        // Clear table
        DefaultTableModel model = (DefaultTableModel) this.fieldSelectorTable.getModel();
        model.setRowCount(0);

        // Clear output
        this.commandOutputTextPane.setText("");

        // Clear panels and lists
        this.tableFieldsPanels.clear();
        this.tableSelectorsPanel.removeAll();
    }

    /* Setters */
    public void setLightMode() {
        // Update each panel's theme
        for (final SelectTableFieldsPanel panel : this.tableFieldsPanels) {
            panel.setLightMode();
        }

        // Update document syntax highlighting
        SQLDocument doc = (SQLDocument) this.commandOutputTextPane.getStyledDocument();
        doc.lightMode();

        // Update text
        this.commandOutputTextPane.setText(this.commandOutputTextPane.getText());
    }

    public void setDarkMode() {
        for (final SelectTableFieldsPanel panel : this.tableFieldsPanels) {
            panel.setDarkMode();
        }

        // Update document syntax highlighting
        SQLDocument doc = (SQLDocument) this.commandOutputTextPane.getStyledDocument();
        doc.darkMode();

        // Update text
        this.commandOutputTextPane.setText(this.commandOutputTextPane.getText());
    }

    public void setCenterClientPanel(CenterClientPanel clientPanel) { this.centerClientPanel = clientPanel; }

    public void fieldIsSelected(String tableName, String fieldName) {
        // Add field into table if it doesn't exist yet
        for(int row = 0; row < this.fieldSelectorTable.getRowCount(); ++row) {
            // Get field and table name
            String currentFieldName = (String) this.fieldSelectorTable.getValueAt(row, 0);
            String currentTableName = (String) this.fieldSelectorTable.getValueAt(row, 1);

            // Check if it's already in table
            if (tableName.equals(currentTableName) && fieldName.equals(currentFieldName)) return;
        }

        // It's not in table yet -> Insert
        DefaultTableModel tableModel = (DefaultTableModel) this.fieldSelectorTable.getModel();
        tableModel.addRow(new Object[]{
                fieldName, tableName
        });
    }

    public void fieldIsDeselected(String tableName, String fieldName) {
        // Get model
        DefaultTableModel tableModel = (DefaultTableModel) this.fieldSelectorTable.getModel();

        // Remove field in table if it exists
        for(int row = 0; row < this.fieldSelectorTable.getRowCount(); ++row) {
            // Get field and table name
            String currentFieldName = (String) this.fieldSelectorTable.getValueAt(row, 0);
            String currentTableName = (String) this.fieldSelectorTable.getValueAt(row, 1);

            // Check if its in table
            if (tableName.equals(currentTableName) && fieldName.equals(currentFieldName)) {
                tableModel.removeRow(row);
                return;
            };
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableSelectorsPanel = new javax.swing.JPanel();
        fieldSelectorScrollPanel = new javax.swing.JScrollPane();
        fieldSelectorTable = new javax.swing.JTable();
        generateCodeButton = new javax.swing.JButton();
        executeButton = new javax.swing.JButton();
        explainerLabel = new javax.swing.JLabel();
        commandOutputScrollPanel = new javax.swing.JScrollPane();
        commandOutputTextPane = new javax.swing.JTextPane();

        tableSelectorsPanel.setLayout(new java.awt.GridLayout(2, 3));

        fieldSelectorTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        fieldSelectorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Field Name", "Table", "Alias", "Condition"
            }
        ));
        fieldSelectorTable.setShowGrid(true);
        fieldSelectorScrollPanel.setViewportView(fieldSelectorTable);

        generateCodeButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        generateCodeButton.setText("Generate Code");
        generateCodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                generateCodeButtonMousePressed(evt);
            }
        });
        generateCodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateCodeButtonActionPerformed(evt);
            }
        });

        executeButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        executeButton.setText("Execute");
        executeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                executeButtonMousePressed(evt);
            }
        });

        explainerLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        explainerLabel.setText("Select the fields from each table that you would like to keep!");

        commandOutputTextPane.setEditable(false);
        commandOutputTextPane.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        commandOutputTextPane.setStyledDocument(new SQLDocument());
        commandOutputScrollPanel.setViewportView(commandOutputTextPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(commandOutputScrollPanel)
                    .addComponent(tableSelectorsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldSelectorScrollPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(explainerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(executeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(generateCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(generateCodeButton)
                        .addComponent(executeButton))
                    .addComponent(explainerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableSelectorsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldSelectorScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandOutputScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public static boolean canTablesBeJoined(String databaseName, List<String> tableNames) {
        // Base case
        if (tableNames.size() == 1) return true;
        if (tableNames.size() == 0) return false;

        // Iterate over each table
        List<String> usedTables = new ArrayList<>();
        List<String> nonUsedTables = new ArrayList<>(tableNames);

        // Add/remove first item
        usedTables.add(tableNames.get(0));
        nonUsedTables.remove(tableNames.get(0));

        for (int i = 1; i < tableNames.size(); ++i) {
            // Now we need to find a table that we can join with the previous tables
            // 1. Check if any already used tables referenced non-used tables
            boolean found = false;
            for (final String usedTableName : usedTables) {
                for (final ForeignKeyModel foreignKey : CatalogManager.getForeignKeys(databaseName, usedTableName)) {
                    String referencedTable = foreignKey.getReferencedTable();
                    if (nonUsedTables.contains(referencedTable)) {
                        usedTables.add(referencedTable);
                        nonUsedTables.remove(referencedTable);

                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if (found) continue;

            // 2. Check if non-used tables are referencing used tables
            for (final String tableName : nonUsedTables) {
                // Now check if we can join it with any other previous table
                for (final ForeignKeyModel foreignKey : CatalogManager.getForeignKeys(databaseName, tableName)) {
                    String referencedTable = foreignKey.getReferencedTable();
                    if (usedTables.contains(referencedTable)) {
                        usedTables.add(tableName);
                        nonUsedTables.remove(tableName);

                        found = true;
                        break;
                    }
                }

                if (found) break;
            }

            if (found) continue;

            // If we couldn't find a way to join them -> They can't be joined
            return false;
        }

        return true;
    }

    private String tableJoinPart(List<String> tableNames) {
        // Check if we need to join
        if (tableNames.size() == 1) return "FROM " + tableNames.get(0);

        // Iterate over each table
        StringBuilder messageBuilder = new StringBuilder();
        List<String> usedTables = new ArrayList<>();
        List<String> nonUsedTables = new ArrayList<>(tableNames);
        for (String name : tableNames) {
            // First table will be added directly
            if (messageBuilder.isEmpty()) {
                messageBuilder.append("FROM ").append(name);

                // Add to used, remove from unused
                usedTables.add(name);
                nonUsedTables.remove(name);
            } else {
                // Now we need to find a table that we can join with the previous tables
                // 1. Check if any already used tables referenced non-used tables
                boolean found = false;
                for (final String usedTableName : usedTables) {
                    for (final ForeignKeyModel foreignKey : CatalogManager.getForeignKeys(this.databaseName, usedTableName)) {
                        String referencedTable = foreignKey.getReferencedTable();
                        String referencingField = foreignKey.getReferencingFields().get(0);
                        String referencedField = foreignKey.getReferencedFields().get(0);
                        if (nonUsedTables.contains(referencedTable)) {
                            messageBuilder.append("\n      ").append("INNER JOIN ").append(referencedTable).append(" ON ")
                                    .append(usedTableName).append('.').append(referencingField).append(" = ")
                                    .append(referencedTable).append('.').append(referencedField);

                            usedTables.add(referencedTable);
                            nonUsedTables.remove(referencedTable);

                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }

                if (found) continue;

                // 2. Check if non-used tables are referencing used tables
                for (final String tableName : nonUsedTables) {
                    // Now check if we can join it with any other previous table
                    for (final ForeignKeyModel foreignKey : CatalogManager.getForeignKeys(this.databaseName, tableName)) {
                        String referencedTable = foreignKey.getReferencedTable();
                        String referencingField = foreignKey.getReferencingFields().get(0);
                        String referencedField = foreignKey.getReferencedFields().get(0);
                        if (usedTables.contains(referencedTable)) {
                            messageBuilder.append("\n      ").append("INNER JOIN ").append(tableName).append(" ON ")
                                    .append(tableName).append('.').append(referencingField).append(" = ")
                                    .append(referencedTable).append('.').append(referencedField);

                            usedTables.add(tableName);
                            nonUsedTables.remove(tableName);

                            found = true;
                            break;
                        }
                    }

                    if (found) break;
                }
            }
        }

        return messageBuilder.toString();
    }

    private String getSQLSelectCommand() throws SQLException {
        StringBuilder commandBuilder = new StringBuilder("SELECT ");

        // Parse table and find all field names and aliases
        Set<String> selectedTables = new HashSet<>();
        for(int row = 0; row < this.fieldSelectorTable.getRowCount(); ++row) {
            // Get field and table name
            String fieldName = (String) this.fieldSelectorTable.getValueAt(row, 0);
            String tableName = (String) this.fieldSelectorTable.getValueAt(row, 1);
            String aliasName = (String) this.fieldSelectorTable.getValueAt(row, 2);

            // Cache selected tables
            selectedTables.add(tableName);

            // If it has alias ad it
            String tableFieldName = tableName + '.' + fieldName;
            if (row == 0) {
                if (aliasName != null) commandBuilder.append(tableFieldName).append(" AS ").append(aliasName);
                else commandBuilder.append(tableFieldName);
            } else {
                if (aliasName != null) commandBuilder.append(", ").append(tableFieldName).append(" AS ").append(aliasName);
                else commandBuilder.append(", ").append(tableFieldName);
            }
        }
        commandBuilder.append('\n');

        // JOINS
        if (selectedTables.size() > 0) {
            if (!SelectMainPanel.canTablesBeJoined(this.databaseName, selectedTables.stream().toList())) {
                String errorMessage = "Tables can't be joined together!";
                JOptionPane.showMessageDialog(new JFrame(), errorMessage, "Dialog", JOptionPane.ERROR_MESSAGE);
                throw new SQLException();
            }

            commandBuilder.append(this.tableJoinPart(selectedTables.stream().toList()));
            commandBuilder.append('\n');
        }


        // Where conditions
        boolean hasWhere = false;
        for(int row = 0; row < this.fieldSelectorTable.getRowCount(); ++row) {
            // Get field and table name
            String fieldName = (String) this.fieldSelectorTable.getValueAt(row, 0);
            String condition = (String) this.fieldSelectorTable.getValueAt(row, 3);

            // If it has condition add where
            if (condition != null) {
                if (!hasWhere) commandBuilder.append("WHERE ");

                String conditionWithField = '(' + fieldName + ' ' + condition + ')';
                if (!hasWhere) {
                    commandBuilder.append(conditionWithField);
                } else {
                    // Else add AND
                    commandBuilder.append(" AND\n").append("      ").append(conditionWithField);
                }
                if (!hasWhere) hasWhere = true;
            }
        }

        return commandBuilder.toString();
    }

    private void generateCodeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_generateCodeButtonMousePressed
        try {
            String command = this.getSQLSelectCommand();

            // Set command output
            this.commandOutputTextPane.setText(command);
        } catch (SQLException e) {
            log.info("User gave invalid SQL command!");
        }

    }//GEN-LAST:event_generateCodeButtonMousePressed

    private void executeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_executeButtonMousePressed
        // Get SQL command
        try {
            String command = this.getSQLSelectCommand();

            // Set command in SQL execution area
            this.centerClientPanel.setInputTextAreaString(command);
            // Switch to that pane
            this.centerClientPanel.setCurrentPane(0);
        } catch (SQLException e) {
            log.info("User gave invalid SQL command! Can't execute!");
        }
    }//GEN-LAST:event_executeButtonMousePressed

    private void generateCodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateCodeButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_generateCodeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane commandOutputScrollPanel;
    private javax.swing.JTextPane commandOutputTextPane;
    private javax.swing.JButton executeButton;
    private javax.swing.JLabel explainerLabel;
    private javax.swing.JScrollPane fieldSelectorScrollPanel;
    private javax.swing.JTable fieldSelectorTable;
    private javax.swing.JButton generateCodeButton;
    private javax.swing.JPanel tableSelectorsPanel;
    // End of variables declaration//GEN-END:variables
}
