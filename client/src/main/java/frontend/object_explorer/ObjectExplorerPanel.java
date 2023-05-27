package frontend.object_explorer;

import service.CatalogManager;
import service.Config;
import service.ForeignKeyModel;
import service.IndexFileModel;

import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

public class ObjectExplorerPanel extends javax.swing.JPanel {
    private DefaultMutableTreeNode databasesNode;
    private DefaultTreeModel jTreeNode;

    // Stored names
    private static HashSet<String> databaseNames = new HashSet<>();
    private static HashSet<String> tableNames = new HashSet<>(), fieldNames = new HashSet<>();
    
    public ObjectExplorerPanel() {
        // Init variables
        this.initVariables();

        initComponents();

        // Set cell renderer
        this.treeDatabases.setCellRenderer(new CustomTreeCellRendererTable());
    }
    
    private void initVariables() {
        this.databasesNode = new DefaultMutableTreeNode("Databases");
        this.jTreeNode = new DefaultTreeModel(this.databasesNode);
    }

    private void updateFieldNode(DefaultMutableTreeNode fieldNode, List<String> fieldNames, List<String> fieldTypes) {
        IntStream
                .range(0, Math.min(fieldNames.size(), fieldTypes.size()))
                .mapToObj(i -> fieldNames.get(i) + " - " + fieldTypes.get(i))
                .forEach(fieldData -> fieldNode.add(new DefaultMutableTreeNode(fieldData)));

        // Add field names to set
        ObjectExplorerPanel.fieldNames.addAll(fieldNames);
    }

    public void update() {
        // Get current databaseNames
        List<String> databaseNames = CatalogManager.getDatabaseNames();
        ObjectExplorerPanel.databaseNames = new HashSet<>(databaseNames);
        ObjectExplorerPanel.tableNames = new HashSet<>();
        ObjectExplorerPanel.fieldNames = new HashSet<>();

        // Update nodes
        this.databasesNode.removeAllChildren(); // Clear the tree

        for(final String databaseName : databaseNames) {
            DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode(databaseName);

            // Add all the tables from this database to that node
            for(final String tableName : CatalogManager.getCurrentDatabaseTableNames(databaseName)) {
                // Add table names
                tableNames.add(tableName);

                DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);

                // For all tables add a 'Fields', 'Unique Fields', 'Foreign Keys', 'IndexFiles'
                DefaultMutableTreeNode fieldsNode = new DefaultMutableTreeNode("Fields");
                DefaultMutableTreeNode primaryKeyNode = new DefaultMutableTreeNode("Primary Key");
                DefaultMutableTreeNode uniqueFieldsNode = new DefaultMutableTreeNode("Unique Fields");
                DefaultMutableTreeNode foreignKeysNode = new DefaultMutableTreeNode("Foreign Keys");
                DefaultMutableTreeNode indexFilesNode = new DefaultMutableTreeNode("Index Files");

                // Fields node
                this.updateFieldNode(fieldsNode, CatalogManager.getFieldNames(databaseName, tableName), CatalogManager.getFieldTypes(databaseName, tableName));
                // PK Fields
                this.updateFieldNode(primaryKeyNode, CatalogManager.getPrimaryKeyFieldNames(databaseName, tableName), CatalogManager.getPrimaryKeyTypes(databaseName, tableName));
                // Unique fields
                this.updateFieldNode(uniqueFieldsNode, CatalogManager.getUniqueFieldNames(databaseName, tableName), CatalogManager.getUniqueFieldTypes(databaseName, tableName));
                // Foreign Keys
                for (final ForeignKeyModel foreignKey : CatalogManager.getForeignKeys(databaseName, tableName)) {
                    DefaultMutableTreeNode foreignKeyNode = new DefaultMutableTreeNode(foreignKey.getReferencedTable());

                    // Add referenced and referencing nodes
                    foreignKeyNode.add(new DefaultMutableTreeNode("Referenced: " + foreignKey.getReferencedFields()));
                    foreignKeyNode.add(new DefaultMutableTreeNode("Referencing: " + foreignKey.getReferencingFields()));

                    foreignKeysNode.add(foreignKeyNode);
                }
                // Index files
                for (final IndexFileModel indexFile : CatalogManager.getIndexFiles(databaseName, tableName)) {
                    DefaultMutableTreeNode indexNode = new DefaultMutableTreeNode(indexFile.getIndexName());

                    // Add referenced and referencing nodes
                    indexNode.add(new DefaultMutableTreeNode("Fields: " + indexFile.getIndexFields()));
                    indexNode.add(new DefaultMutableTreeNode("Unique: " + indexFile.isUnique()));

                    indexFilesNode.add(indexNode);
                }

                // Add extra info nodes
                tableNode.add(fieldsNode); tableNode.add(primaryKeyNode); tableNode.add(uniqueFieldsNode); tableNode.add(foreignKeysNode); tableNode.add(indexFilesNode);

                // Add table to tables node
                databaseNode.add(tableNode);
            }

            this.databasesNode.add(databaseNode);
        }

        // Update the tree node
        this.jTreeNode.setRoot(this.databasesNode);
    }

    // Custom cell renderer class
    static class CustomTreeCellRendererTable extends DefaultTreeCellRenderer {
        // Override the getTreeCellRendererComponent method
        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            // Invoke the default implementation
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            // Check if the node's value is equal to "Tables"
            String valueName = value.toString();
            if (databaseNames.contains(valueName)) {
                // Set a custom icon for "Database"
                setIcon(new ImageIcon(Config.getImagesPath() + File.separator + "database_mini2.png"));
            } else if (tableNames.contains(valueName)) {
                // Set a custom icon for "Table"
                setIcon(new ImageIcon(Config.getImagesPath() + File.separator + "table_icon_mini.png"));
            } else if (fieldNames.stream().anyMatch(valueName::startsWith)) {
                // Set a custom icon for "Field"
                setIcon(new ImageIcon(Config.getImagesPath() + File.separator + "field_mini.png"));
            }
            return this;
        }
    }

    /* Setters */
    public void updateCurrentDatabase(String databaseName) {this.usingDatabaseField.setText(databaseName);}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        objectExplorerTag = new javax.swing.JLabel();
        databasesPanel = new javax.swing.JPanel();
        treeScrollPanel = new javax.swing.JScrollPane();
        treeDatabases = new javax.swing.JTree();
        usingDatabaseLabel = new javax.swing.JLabel();
        usingDatabaseField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        objectExplorerTag.setBackground(java.awt.Color.darkGray);
        objectExplorerTag.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        objectExplorerTag.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        objectExplorerTag.setLabelFor(this);
        objectExplorerTag.setText("Object Explorer");
        objectExplorerTag.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        objectExplorerTag.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        objectExplorerTag.setOpaque(true);

        treeDatabases.setBorder(null);
        treeDatabases.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        treeDatabases.setModel(jTreeNode);
        treeDatabases.setToolTipText("");
        treeScrollPanel.setViewportView(treeDatabases);

        usingDatabaseLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        usingDatabaseLabel.setText("Using database:");

        usingDatabaseField.setEditable(false);
        usingDatabaseField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        javax.swing.GroupLayout databasesPanelLayout = new javax.swing.GroupLayout(databasesPanel);
        databasesPanel.setLayout(databasesPanelLayout);
        databasesPanelLayout.setHorizontalGroup(
            databasesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(treeScrollPanel)
            .addGroup(databasesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(usingDatabaseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(usingDatabaseField, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        databasesPanelLayout.setVerticalGroup(
            databasesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databasesPanelLayout.createSequentialGroup()
                .addGroup(databasesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(usingDatabaseField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(databasesPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(usingDatabaseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treeScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 813, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(objectExplorerTag, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(databasesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(objectExplorerTag, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(databasesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel databasesPanel;
    private javax.swing.JLabel objectExplorerTag;
    private javax.swing.JTree treeDatabases;
    private javax.swing.JScrollPane treeScrollPanel;
    private javax.swing.JTextField usingDatabaseField;
    private javax.swing.JLabel usingDatabaseLabel;
    // End of variables declaration//GEN-END:variables
}
