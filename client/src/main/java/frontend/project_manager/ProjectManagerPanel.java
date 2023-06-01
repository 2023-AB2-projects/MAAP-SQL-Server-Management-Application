package frontend.project_manager;

import control.ClientController;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import service.Config;
import service.Utility;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.*;

@Slf4j
public class ProjectManagerPanel extends javax.swing.JPanel {
    // References
    @Setter
    private ClientController clientController;

    // File system logic
    private DefaultMutableTreeNode projectsRootNode;
    private DefaultTreeModel documentJTreeMutable;

    // File logic
    private String currentFileName;
    private File currentFile;

    public ProjectManagerPanel() {
        initComponents();

        // Init root node
        this.projectsRootNode = new DefaultMutableTreeNode("User Projects");
        this.documentJTreeMutable = new DefaultTreeModel(this.projectsRootNode);
        this.documentJTree.setModel(this.documentJTreeMutable);

        this.initLabelIcons();
        this.createRootFolderIfNotExists();

        // Init default file
        this.initDefaultFile();
        this.currentFileField.setText(this.currentFileName);

        // Update file system
        this.update();
    }

    private void initLabelIcons() {
        // Set SQL Query Label Icon (resize it to 32x32)
        sqlQueryLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "sql_file_icon.png"),
                32, 32
        ));

        // Set Project Label Icon (resize it to 32x32)
        projectLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "sql_folder_icon.png"),
                32, 32
        ));

        // Save file label
        saveFileLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "save_file_icon.png"),
                32, 32
        ));

        // Delete label
        deleteLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "delete_file_icon.png"),
                32, 32
        ));
    }

    private void createRootFolderIfNotExists() {
        // Create root folder if it doesn't exist
        File rootFolder = new File(Config.getUserScriptsPath());

        // If the root folder doesn't exist, create it
        if (!rootFolder.exists()) {
            if (!rootFolder.mkdir()) {
                log.error("Failed to create user scripts directory");
            }
        }
    }

    private void initDefaultFile() {
        this.currentFileName = "New SQL query.sql";

        // Check if file already exists in root folder
        this.currentFile = new File(Config.getUserScriptsPath() + File.separator + this.currentFileName);
        if (!this.currentFile.exists()) {
            // If it doesn't exist -> Create a new file
            try {
                if (this.currentFile.createNewFile()) {
                    log.info("Created new file: " + this.currentFileName);
                }
            } catch (IOException e) {
                log.error("Failed to create new file");
                throw new RuntimeException(e);
            }
        }
    }

    private void update() {
        // Clear out the current JTree
        this.projectsRootNode.removeAllChildren();

        // Read all the files and directories in the user scripts directory recursively
        File rootFolder = new File(Config.getUserScriptsPath());

        // If the root folder doesn't exist, create it
        if (!rootFolder.exists()) {
            if (!rootFolder.mkdir()) {
                log.error("Failed to create user scripts directory");
            }
        } else {
            // If the directory exists -> Parse it recursively (After skipping the root folder)
            File[] files = rootFolder.listFiles();
            if (files != null) {
                for (final File subFile : files) {
                    this.parseUserScripts(subFile, this.projectsRootNode);
                }
            }
        }
    }

    private void parseUserScripts(File file, DefaultMutableTreeNode parentNode) {
        // If it's directory -> Recursive case
        if (file.isDirectory()) {
            // Create a TreeNode for the directory
            DefaultMutableTreeNode directoryNode = new DefaultMutableTreeNode(file.getName());

            // Add the directory node to the parent node
            parentNode.add(directoryNode);

            // Parse every file in the directory
            File[] files = file.listFiles();
            if (files != null) {
                for (final File subFile : files) {
                    this.parseUserScripts(subFile, directoryNode);
                }
            }
        } else {
            // It's a file -> Base case
            // Create a TreeNode for the file
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());

            // Add the file node to the parent node
            parentNode.add(fileNode);
        }
    }

    /* Setters */
    public void readCurrentFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.currentFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // Set the text area text
            this.clientController.setInputTextAreaString(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
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

        projectManagerTag = new javax.swing.JLabel();
        sqlQueryLabel = new javax.swing.JLabel();
        newProjectButton = new javax.swing.JButton();
        projectLabel = new javax.swing.JLabel();
        newQueryButton = new javax.swing.JButton();
        documentTreeScrollPanel = new javax.swing.JScrollPane();
        documentJTree = new javax.swing.JTree();
        saveFileButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        saveFileLabel = new javax.swing.JLabel();
        deleteLabel = new javax.swing.JLabel();
        currentFileLabel = new javax.swing.JLabel();
        currentFileField = new javax.swing.JTextField();

        projectManagerTag.setBackground(java.awt.Color.darkGray);
        projectManagerTag.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        projectManagerTag.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        projectManagerTag.setText("Project Manager");
        projectManagerTag.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        projectManagerTag.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        projectManagerTag.setOpaque(true);

        newProjectButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        newProjectButton.setText("New Project");
        newProjectButton.setMargin(new java.awt.Insets(2, 7, 3, 7));
        newProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectButtonActionPerformed(evt);
            }
        });

        newQueryButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        newQueryButton.setText("New Query");
        newQueryButton.setMargin(new java.awt.Insets(2, 7, 3, 7));
        newQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newQueryButtonActionPerformed(evt);
            }
        });

        documentJTree.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        documentTreeScrollPanel.setViewportView(documentJTree);

        saveFileButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        saveFileButton.setText("Save");
        saveFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        deleteButton.setText("Delete");

        currentFileLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        currentFileLabel.setText("Current file:");
        currentFileLabel.setFocusable(false);

        currentFileField.setEditable(false);
        currentFileField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        currentFileField.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(projectManagerTag, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(documentTreeScrollPanel)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sqlQueryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saveFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(saveFileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newQueryButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(currentFileLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentFileField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(projectManagerTag, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(currentFileField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sqlQueryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(projectLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(newQueryButton)
                        .addComponent(newProjectButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveFileButton)
                    .addComponent(deleteButton)
                    .addComponent(deleteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(documentTreeScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void newProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newProjectButtonActionPerformed

    private void newQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newQueryButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newQueryButtonActionPerformed

    private void saveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileButtonActionPerformed
//        // Find the currently selected Tree node
//        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) documentJTree.getLastSelectedPathComponent();
//
//        // If nothing is selected -> Use the root node
//        if (selectedNode == null) selectedNode = this.projectsRootNode;
//
//        // Reconstruct path to current node (In file system)
//        StringBuilder pathBuilder = new StringBuilder();
//        for (final TreeNode pathNode : selectedNode.getPath()) {
//            String pathElementString = pathNode.toString();
//            if (pathElementString.equals("User Projects")) continue;
//            pathBuilder.append(File.separator).append(pathElementString);
//        }
//
//        String pathToFile = Config.getUserScriptsPath() + pathBuilder.toString();
//        System.out.println(pathToFile);

        // Get the text from the editor
        String currentSQLText = this.clientController.getInputTextAreaString();

        // Save SQL text to file
        try {
            FileWriter fileWriter = new FileWriter(this.currentFile);
            fileWriter.write(currentSQLText);
            fileWriter.close();
        } catch (IOException e) {
            log.error("Error saving file: " + this.currentFileName);
            e.printStackTrace();
        }

    }//GEN-LAST:event_saveFileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField currentFileField;
    private javax.swing.JLabel currentFileLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel deleteLabel;
    private javax.swing.JTree documentJTree;
    private javax.swing.JScrollPane documentTreeScrollPanel;
    private javax.swing.JButton newProjectButton;
    private javax.swing.JButton newQueryButton;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JLabel projectManagerTag;
    private javax.swing.JButton saveFileButton;
    private javax.swing.JLabel saveFileLabel;
    private javax.swing.JLabel sqlQueryLabel;
    // End of variables declaration//GEN-END:variables
}
